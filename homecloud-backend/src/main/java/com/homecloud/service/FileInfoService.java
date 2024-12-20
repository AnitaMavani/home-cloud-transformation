package com.homecloud.service;


import com.homecloud.entity.vo.UploadStatusVo;
import com.homecloud.entity.domain.FileInfo;
import com.homecloud.entity.domain.UserInfo;
import com.homecloud.entity.query.FileInfoQuery;
import com.homecloud.entity.vo.PaginationResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public interface FileInfoService {

    List<FileInfo> findListByParam(FileInfoQuery param);

    Integer findCountByParam(FileInfoQuery param);

    PaginationResult<FileInfo> findListByPage(FileInfoQuery param);


    UploadStatusVo uploadFile(UserInfo userInfo, String fileId, MultipartFile file, String fileName, String filePid, String fileMd5, Integer chunkIndex,
                              Integer chunks);

    FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId);

    void previewFile(HttpServletResponse response, String fileId, String userId);

    FileInfo rename(String fileId, String userId, String fileName);

    FileInfo newFolder(String userId, String filePid, String folderName);

    public List<FileInfo> moveTo(String userId, String filePid, String sourceFileIds);

    void moveToHere(String userId, String filePid, String sourceFileIds);

    void download(String userId, String fileId, HttpServletRequest request, HttpServletResponse response);

    void batchDelete2Recyclebin(String userId, String fileIds);

    void batchRestore(String userId, String fileIds);

    void batchDeleteFromRecycleBin(String userId, String fileIds);
}