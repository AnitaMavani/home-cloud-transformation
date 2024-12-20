package com.homecloud.service.impl;

import java.io.File;
import java.io.IOException;


import com.homecloud.utils.FfmpegUtil;
import com.homecloud.utils.FileEncryptionUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import com.homecloud.entity.domain.UserInfo;
import com.homecloud.entity.vo.UploadStatusVo;
import com.homecloud.config.AppConfig;
import com.homecloud.entity.enums.*;
import com.homecloud.entity.domain.*;
import com.homecloud.entity.query.FileInfoQuery;
import org.springframework.transaction.annotation.Transactional;
import com.homecloud.entity.query.SimplePage;
import com.homecloud.entity.vo.PaginationResult;
import com.homecloud.mappers.FileInfoMapper;
import com.homecloud.utils.DateUtil;
import com.homecloud.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

import com.homecloud.service.FileInfoService;
import org.apache.commons.io.FileUtils;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.homecloud.exception.BusinessException;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.*;

import static com.homecloud.utils.FileEncryptionUtil.decrypt;

@Service("fileInfoService")
public class FileInfoServiceImpl implements FileInfoService {

    private static final Logger logger = LoggerFactory.getLogger(FileInfoServiceImpl.class);

    @Resource
    private AppConfig appConfig;

    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    @Resource
    @Lazy
    FileInfoServiceImpl fileInfoService;

    @Override
    public List<FileInfo> findListByParam(FileInfoQuery param) {
        return fileInfoMapper.selectList(param);
    }

    @Override
    public Integer findCountByParam(FileInfoQuery param) {
        return fileInfoMapper.selectCount(param);
    }

    @Override
    public PaginationResult<FileInfo> findListByPage(FileInfoQuery param) {
        int count = findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE10.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);

        List<FileInfo> list = findListByParam(param);
        return new PaginationResult<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadStatusVo uploadFile(UserInfo userInfo, String fileIdOut, MultipartFile file, String fileName,
                                     String filePid, String fileMd5, Integer chunkIndex, Integer chunks) {
        File tempFileFolder = null;
        boolean uploadSuccess = true;
        String fileId;
        try {
            UploadStatusVo uploadResult = new UploadStatusVo();
            fileId = (StringUtil.isEmpty(fileIdOut)) ? StringUtil.getRandomString(Constants.LENGTH_10) : fileIdOut;
            uploadResult.setFileId(fileId);
            Date curDate = new Date();
            if (chunkIndex == 0 && isFileExisting(fileMd5)) {
                return handleSecondUpload(userInfo, fileId, filePid, fileName, fileMd5, curDate);
            }
            tempFileFolder = saveChunkToFile(file, userInfo, fileId, chunkIndex);
            if (chunkIndex < chunks - 1) {
                uploadResult.setStatus(UploadStatusEnums.UPLOADING.getCode());
                return uploadResult;
            }

            UploadStatusVo uploadStatus = handleLastChunk(userInfo, fileId, fileName, filePid, fileMd5, curDate);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    fileInfoService.transferFile(fileId, userInfo);
                }
            });
            return uploadStatus;
        } catch (Exception e) {
            uploadSuccess = false;
            logger.error("File upload failed", e);
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            } else if (e instanceof IOException) {
                throw new RuntimeException("IO exception", e);
            } else {
                throw new BusinessException("File upload failed");
            }
        } finally {
            deleteTempDirOnFailure(tempFileFolder, uploadSuccess);
        }
    }

    private boolean isFileExisting(String fileMd5) {
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setFileMd5(fileMd5);
        infoQuery.setSimplePage(new SimplePage(0, 1));
        infoQuery.setStatus(FileStatusEnums.USING.getStatus());
        return !fileInfoMapper.selectList(infoQuery).isEmpty();
    }

    private UploadStatusVo handleSecondUpload(UserInfo userInfo, String fileId, String filePid, String fileName, String fileMd5, Date curDate) {
        UploadStatusVo resultDto = new UploadStatusVo();

        // 从数据库中获取文件信息
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setFileMd5(fileMd5);
        infoQuery.setSimplePage(new SimplePage(0, 1));
        infoQuery.setStatus(FileStatusEnums.USING.getStatus());
        List<FileInfo> dbFileList = fileInfoMapper.selectList(infoQuery);
        FileInfo dbFile = dbFileList.get(0);
        dbFile.setFileId(fileId);
        dbFile.setFilePid(filePid);
        dbFile.setUserId(userInfo.getUserId());
        dbFile.setFileMd5(null);
        dbFile.setCreateTime(curDate);
        dbFile.setLastUpdateTime(curDate);
        dbFile.setStatus(FileStatusEnums.USING.getStatus());
        dbFile.setDelFlag(FileDelFlagEnums.USING.getFlag());
        dbFile.setFileMd5(fileMd5);
        fileName = autoRename(filePid, userInfo.getUserId(), fileName);
        dbFile.setFileName(fileName);
        fileInfoMapper.insert(dbFile);
        resultDto.setStatus(UploadStatusEnums.UPLOAD_SECONDS.getCode());
        return resultDto;
    }


    private File saveChunkToFile(MultipartFile file, UserInfo userInfo, String fileId, Integer chunkIndex) throws Exception {

        String tempFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
        String currentUserFolderName = userInfo.getUserId() + fileId;

        // create the temporary folder
        File tempFileFolder = new File(tempFolderName + currentUserFolderName);
        if (!tempFileFolder.exists()) {
            tempFileFolder.mkdirs();
        }

        File newFile = new File(tempFileFolder.getPath() + "/" + chunkIndex);
        file.transferTo(newFile);

        return tempFileFolder;
    }

    private UploadStatusVo handleLastChunk(UserInfo userInfo, String fileId, String fileName, String filePid, String fileMd5, Date curDate) {
        UploadStatusVo uploadStatus = new UploadStatusVo();
        String month = DateUtil.format(curDate, DateTimePatternEnum.YYYYMM.getPattern());
        String fileSuffix = StringUtil.getFileSuffix(fileName);
        String realFileName = userInfo.getUserId() + fileId + fileSuffix;
        FileTypeEnums fileTypeEnum = FileTypeEnums.getFileTypeBySuffix(fileSuffix);
        fileName = autoRename(filePid, userInfo.getUserId(), fileName);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId(fileId);
        fileInfo.setUserId(userInfo.getUserId());
        fileInfo.setFileMd5(fileMd5);
        fileInfo.setFileName(fileName);
        fileInfo.setFilePath(month + "/" + realFileName);
        fileInfo.setFilePid(filePid);

        fileInfo.setCreateTime(curDate);
        fileInfo.setLastUpdateTime(curDate);
        fileInfo.setFileCategory(fileTypeEnum.getCategory().getCategory());
        fileInfo.setFileType(fileTypeEnum.getType());
        fileInfo.setStatus(FileStatusEnums.TRANSFER.getStatus());
        fileInfo.setFolderType(FileFolderTypeEnums.FILE.getType());
        fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
        fileInfoMapper.insert(fileInfo);
        uploadStatus.setStatus(UploadStatusEnums.UPLOAD_FINISH.getCode());
        return uploadStatus;
    }

    private void deleteTempDirOnFailure(File tempFileFolder, boolean uploadSuccess) {
        if (tempFileFolder != null && !uploadSuccess) {
            try {
                FileUtils.deleteDirectory(tempFileFolder);
            } catch (IOException e) {
                logger.error("Failed to delete the temporary directory");
            }
        }
    }

    private String autoRename(String filePid, String userId, String fileName) {
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setFileName(fileName);
//        fileInfoQuery.setFileMd5(fileMd5);
        Integer count = fileInfoMapper.selectCount(fileInfoQuery);
        if (count > 0) {
            return StringUtil.rename(fileName, count);
        }
        return fileName;
    }

    @Async
    public void transferFile(String fileId, UserInfo userInfo) {
        Boolean transferSuccess = true;
        String cover = null;
        FileTypeEnums fileTypeEnum = null;
        String destFilePath = null;
        FileInfo fileInfo = fileInfoMapper.selectByFileIdAndUserId(fileId, userInfo.getUserId());
        try {
            if (fileInfo == null || !FileStatusEnums.TRANSFER.getStatus().equals(fileInfo.getStatus())) {
                return;
            }
            String tempFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
            String tempUserFolder = userInfo.getUserId() + fileId;
            File tempFileFolder = new File(tempFolder + tempUserFolder);
            if (!tempFileFolder.exists()) {
                tempFileFolder.mkdirs();
            }

            String suffix = StringUtil.getFileSuffix(fileInfo.getFileName());
            String month = DateUtil.format(fileInfo.getCreateTime(), DateTimePatternEnum.YYYYMM.getPattern());
            String targetFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
            File destFolder = new File(targetFolderName + "/" + month);
            if (!destFolder.exists()) {
                destFolder.mkdirs();
            }
            destFilePath = destFolder.getPath() + "/" + tempUserFolder + suffix;
            merge(tempFileFolder.getPath(), destFilePath, fileInfo.getFileName(), true);
            fileTypeEnum = FileTypeEnums.getFileTypeBySuffix(suffix);
            if (FileTypeEnums.VIDEO == fileTypeEnum) {

                File destFile = new File(destFilePath);
                byte[] fileData = Files.readAllBytes(destFile.toPath());
                byte[] decryptedData = decrypt(fileData);
                Files.write(destFile.toPath(), decryptedData);

                videoSegmentation(fileId, destFilePath);

            }
        } catch (Exception e) {
            logger.error("file transfer failed");
            transferSuccess = false;
        } finally {
            FileInfo updateInfo = new FileInfo();
            updateInfo.setFileCover(cover);
            updateInfo.setStatus(transferSuccess ? FileStatusEnums.USING.getStatus() : FileStatusEnums.TRANSFER_FAIL.getStatus());
            updateInfo.setFileSize(new File(destFilePath).length());
            fileInfoMapper.updateFileStatusWithOldStatus(FileStatusEnums.TRANSFER.getStatus(), updateInfo, fileId, userInfo.getUserId());
        }
    }

    private static void merge(String dirPath, String destFilePath, String fileName, boolean delSource) throws BusinessException {
        File dir = new File(dirPath);

        File destFile = new File(destFilePath);

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFilePath))) {
            byte[] buffer = new byte[1024 * 10];
            int bytesRead;
            File[] fileList = dir.listFiles();
            Arrays.sort(fileList, Comparator.comparingInt(file -> Integer.parseInt(file.getName())));

            for (File chunkFile : fileList) {
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(chunkFile))) {
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    logger.error("Failed reading chunk from file: {}", chunkFile.getAbsolutePath(), e);
                    throw new BusinessException("Failed reading chunk from file: " + chunkFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to merge files into: {}", fileName);
            throw new BusinessException("Failed to merge files into: " + fileName);
        }

        try {
            byte[] fileData = Files.readAllBytes(destFile.toPath());
            byte[] encryptedData = FileEncryptionUtil.encrypt(fileData);
            Files.write(destFile.toPath(), encryptedData);
        } catch (Exception e) {
            logger.error("Failed to encrypt file: {}", destFilePath, e);
            throw new BusinessException("Failed to encrypt file: " + destFilePath);
        }

        if (delSource) {
            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
                logger.error("Failed to delete directory after merging: {}", dirPath);
            }
        }
    }


    private void videoSegmentation(String fileId, String videoFilePath) {
        File tsFolder = new File(videoFilePath.substring(0, videoFilePath.lastIndexOf(".")));
        if (!tsFolder.exists()) {
            tsFolder.mkdirs();
        }
        final String TO_TS = "ffmpeg -y -i %s  -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s";
        final String CUT_TS = "ffmpeg -i %s -c copy -map 0 -f segment -segment_list %s -segment_time 15 %s/%s_%%4d.ts";

        String tsPath = tsFolder + "/" + Constants.TS;
        String cmd = String.format(TO_TS, videoFilePath, tsPath);
        FfmpegUtil.executeCmd(cmd, false);
        cmd = String.format(CUT_TS, tsPath, tsFolder.getPath() + "/" + Constants.M3U8, tsFolder.getPath(), fileId);
        FfmpegUtil.executeCmd(cmd, false);
        new File(tsPath).delete();

    }


    public void previewFile(HttpServletResponse response, String fileId, String userId) {
        String filePath = determineFilePath(fileId, userId);
        if (filePath == null || !new File(filePath).exists()) {
            return;
        }
        readFile(response, filePath);
    }


    private String determineFilePath(String fileId, String userId) {
        FileInfo fileInfo;
        if (fileId.endsWith(".ts")) {
            String realFileId = fileId.split("_")[0];
            fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(realFileId, userId);
            if (fileInfo == null) {
                return null;
            }
            String fileName = StringUtil.getFileNameNoSuffix(fileInfo.getFilePath()) + "/" + fileId;
            return appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileName;
        } else {
            fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
            if (fileInfo == null) {
                return null;
            }
            if (FileCategoryEnums.VIDEO.getCategory().equals(fileInfo.getFileCategory())) {
                String fileNameNoSuffix = StringUtil.getFileNameNoSuffix(fileInfo.getFilePath());
                return appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileNameNoSuffix + "/" + Constants.M3U8;
            } else {
                String path = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath();
                File destFile = new File(path);
                byte[] fileData = new byte[0];
                try {
                    fileData = Files.readAllBytes(destFile.toPath());
                    byte[] decryptedData = FileEncryptionUtil.decrypt(fileData);
                    Files.write(destFile.toPath(), decryptedData);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return path;
            }
        }
    }

    public void readFile(HttpServletResponse response, String filePath) {
        if (!StringUtil.pathCorrect(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            byte[] byteData = new byte[1024];
            int len;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }
            if ((!filePath.endsWith(".ts")) && (!filePath.endsWith(".m3u8"))) {
                File destFile = new File(filePath);
                byte[] fileData = Files.readAllBytes(destFile.toPath());
                byte[] encryptedData = FileEncryptionUtil.encrypt(fileData);
                Files.write(destFile.toPath(), encryptedData);
            }

            out.flush();
        } catch (Exception e) {
            logger.error("Error reading the file", e);
        }
    }



    @Override
    public FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId) {
        return this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
    }

    @Override
    public FileInfo rename(String fileId, String userId, String fileName) {
        FileInfo fileInfo = this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
        if (fileInfo == null) {
            throw new BusinessException("file doesn't exist");
        }
        if (fileInfo.getFileName().equals(fileName)) {
            return fileInfo;
        }
        if (FileFolderTypeEnums.FILE.getType().equals(fileInfo.getFolderType())) {
            fileName = fileName + StringUtil.getFileSuffix(fileInfo.getFileName());
        }
        FileInfoQuery query = new FileInfoQuery();
        query.setFileName(fileName);
        query.setUserId(userId);
        query.setFolderType(fileInfo.getFolderType());
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        query.setFilePid(fileInfo.getFilePid());
        Integer count = this.fileInfoMapper.selectCount(query);
        if (count > 0) {
            throw new BusinessException("A file with the same name already exists in this directory. Please rename the file.");
        }

        FileInfo updateInfo = new FileInfo();
        updateInfo.setFileName(fileName);
        updateInfo.setLastUpdateTime(new Date());
        this.fileInfoMapper.updateByFileIdAndUserId(updateInfo, fileId, userId);
        return this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
    }

    @Override
    public FileInfo newFolder(String userId, String filePid, String folderName) {
        validateFolderName(userId, filePid, folderName, FileFolderTypeEnums.FOLDER.getType());
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(folderName);
        fileInfo.setFilePid(filePid);
        fileInfo.setUserId(userId);
        Date date = new Date();
        fileInfo.setLastUpdateTime(date);
        fileInfo.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        fileInfo.setFileId(StringUtil.getRandomNumber(Constants.LENGTH_10));
        fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
        fileInfo.setCreateTime(date);
        fileInfo.setStatus(FileStatusEnums.USING.getStatus());
        this.fileInfoMapper.insert(fileInfo);
        return fileInfo;
    }


    private void validateFolderName(String userId, String filePid, String folderName, Integer folderType) {
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFileName(folderName);
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
        fileInfoQuery.setFolderType(folderType);
        Integer count = this.fileInfoMapper.selectCount(fileInfoQuery);
        if (count > 0) {
            throw new BusinessException("Duplicated folder name, please rename.");
        }
    }

    @Override
    public List<FileInfo> moveTo(String userId, String filePid, String sourceFileIds) {
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        fileInfoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
        if (sourceFileIds != null) {
            fileInfoQuery.setExcludeFileIdArray(sourceFileIds.split(","));
        }
        fileInfoQuery.setOrderBy("file_name");
        List<FileInfo> fileInfoList = fileInfoMapper.selectList(fileInfoQuery);
        return fileInfoList;
    }

    @Override
    public void moveToHere(String userId, String filePid, String sourceFileIds) {
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setFilePid(filePid);
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(query);
        String[] fileIdArray = sourceFileIds.split(",");
        for (int i = 0; i < fileIdArray.length; i++) {
            FileInfo fileInfo = this.fileInfoMapper.selectByFileIdAndUserId(fileIdArray[i], userId);
            String fileName = fileInfo.getFileName();
            for (int j = 0; j < fileInfoList.size(); j++) {
                if (fileName.equals(fileInfoList.get(j).getFileName())) {
                    fileName = autoRename(filePid, userId, fileName);
                }
            }
            FileInfo updateInfo = new FileInfo();
            updateInfo.setFilePid(filePid);
            updateInfo.setFileName(fileName);
            this.fileInfoMapper.updateByFileIdAndUserId(updateInfo, fileIdArray[i], userId);
        }
    }

    @Override
    public void download(String userId, String fileId, HttpServletRequest request, HttpServletResponse response) {
        FileInfo fileInfo = fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
        if (fileInfo == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        String filePath = fileInfo.getFilePath();
        String realDownloadPath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + filePath;

        if (!(filePath.endsWith(".ts") || filePath.endsWith(".m3u8") || FileCategoryEnums.VIDEO.getCategory().equals(fileInfo.getFileCategory()))) {
            File path = new File(realDownloadPath);
            byte[] fileData = new byte[0];
            try {
                fileData = Files.readAllBytes(path.toPath());
                byte[] decryptedData = FileEncryptionUtil.decrypt(fileData);
                Files.write(path.toPath(), decryptedData);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        String fileName = fileInfo.getFileName();
        response.setContentType("application/x-msdownload; charset=UTF-8");
        if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0) {
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        readFile(response, realDownloadPath);

    }

    @Override
    public void batchDelete2Recyclebin(String userId, String fileIds) {
        String[] array = fileIds.split(",");
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setFileIdArray(array);
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(query);
        if (fileInfoList == null) {
            return;
        }
        for (FileInfo fileInfo : fileInfoList) {
            FileInfo updateInfo = new FileInfo();
            updateInfo.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
            updateInfo.setLastUpdateTime(new Date());
            this.fileInfoMapper.updateByFileIdAndUserId(updateInfo, fileInfo.getFileId(), fileInfo.getUserId());
            if (fileInfo.getFolderType() == 1) {
                findContent(userId, fileInfo.getFileId());
            }

        }
    }


    @Override
    public void batchRestore(String userId, String fileIds) {
        String[] array = fileIds.split(",");
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setFileIdArray(array);
        query.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(query);
        if (fileInfoList == null) {
            return;
        }
        for (FileInfo fileInfo : fileInfoList) {
            FileInfo updateInfo = new FileInfo();
            updateInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
            updateInfo.setRecoveryTime(new Date());
            this.fileInfoMapper.updateByFileIdAndUserId(updateInfo, fileInfo.getFileId(), fileInfo.getUserId());
            if (fileInfo.getFolderType() == 1) {
                findContent2(userId, fileInfo.getFileId());
            }
        }
    }

    @Override
    public void batchDeleteFromRecycleBin(String userId, String fileIds) {
        String[] array = fileIds.split(",");
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setFileIdArray(array);
        query.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(query);
        if (fileInfoList == null) {
            return;
        }
        for (FileInfo fileInfo : fileInfoList) {
            this.fileInfoMapper.deleteByFileIdAndUserId(fileInfo.getFileId(), fileInfo.getUserId());
            if (fileInfo.getFolderType() == 1) {
                findContent3(userId, fileInfo.getFileId());
            }
        }

    }

    void findContent(String userId, String filePid) {
        FileInfoQuery query = new FileInfoQuery();
        query.setFilePid(filePid);
        query.setUserId(userId);
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(query);
        if (fileInfoList.isEmpty()) {
            return;
        }
        for (FileInfo fileInfo : fileInfoList) {
            FileInfo updateInfo = new FileInfo();
            updateInfo.setDelFlag(FileDelFlagEnums.DEL.getFlag());
            this.fileInfoMapper.updateByFileIdAndUserId(updateInfo, fileInfo.getFileId(), fileInfo.getUserId());
            if (fileInfo.getFolderType() == 1) {
                findContent(userId, fileInfo.getFileId());
            }
        }
    }

    void findContent2(String userId, String filePid) {
        FileInfoQuery query = new FileInfoQuery();
        query.setFilePid(filePid);
        query.setUserId(userId);
        query.setDelFlag(FileDelFlagEnums.DEL.getFlag());
        List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(query);
        if (fileInfoList.isEmpty()) {
            return;
        }
        for (FileInfo fileInfo : fileInfoList) {
            FileInfo updateInfo = new FileInfo();
            updateInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
            this.fileInfoMapper.updateByFileIdAndUserId(updateInfo, fileInfo.getFileId(), fileInfo.getUserId());
            if (fileInfo.getFolderType() == 1) {
                findContent(userId, fileInfo.getFileId());
            }
        }
    }

    void findContent3(String userId, String filePid) {
        FileInfoQuery query = new FileInfoQuery();
        query.setFilePid(filePid);
        query.setUserId(userId);
        query.setDelFlag(FileDelFlagEnums.DEL.getFlag());
        List<FileInfo> fileInfoList = this.fileInfoMapper.selectList(query);
        if (fileInfoList.isEmpty()) {
            return;
        }
        for (FileInfo fileInfo : fileInfoList) {
            this.fileInfoMapper.deleteByFileIdAndUserId(fileInfo.getFileId(), fileInfo.getUserId());
            if (fileInfo.getFolderType() == 1) {
                findContent(userId, fileInfo.getFileId());
            }
        }
    }


}



