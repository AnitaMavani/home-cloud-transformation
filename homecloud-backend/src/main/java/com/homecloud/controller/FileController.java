package com.homecloud.controller;

import com.homecloud.aspect.annotation.Interceptor;
import com.homecloud.aspect.annotation.ValidateParam;
import com.homecloud.entity.domain.FileInfo;
import com.homecloud.entity.vo.UploadStatusVo;
import com.homecloud.entity.enums.FileCategoryEnums;
import com.homecloud.entity.enums.FileDelFlagEnums;
import com.homecloud.entity.domain.UserInfo;
import com.homecloud.entity.query.FileInfoQuery;
import com.homecloud.entity.vo.PaginationResult;
import com.homecloud.entity.vo.R;
import com.homecloud.service.FileInfoService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;


@RestController("fileController")
@RequestMapping("/file")
public class FileController extends BaseController {

    @Resource
    private FileInfoService fileInfoService;


    @RequestMapping("/loadDataList")
    @Interceptor(checkInput = true)
    public R loadDataList(HttpSession session, FileInfoQuery query, String category) {
        FileCategoryEnums categoryEnum = FileCategoryEnums.getByCode(category);
        if (null != categoryEnum) {
            query.setFileCategory(categoryEnum.getCategory());
        }
        query.setUserId(getUserInfoFromSession(session).getUserId());
        query.setOrderBy("last_update_time desc");
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        PaginationResult result = fileInfoService.findListByPage(query);
        return getSuccessR(convert2Pagination(result, FileInfo.class));
    }

    @RequestMapping("/uploadFile")
    @Interceptor(checkInput = true)
    public R uploadFile(HttpSession session,
                        String fileId,
                        MultipartFile file,
                        @ValidateParam(required = true) String fileName,
                        @ValidateParam(required = true) String filePid,
                        @ValidateParam(required = true) String fileMd5,
                        @ValidateParam(required = true) Integer chunkIndex,
                        @ValidateParam(required = true) Integer chunks) {

        UserInfo userInfo = getUserInfoFromSession(session);
        UploadStatusVo uploadResult = fileInfoService.uploadFile(userInfo, fileId, file, fileName, filePid, fileMd5, chunkIndex, chunks);
        return getSuccessR(uploadResult);
    }


    @RequestMapping("/getFile/{fileId}")
    public void preview(HttpServletResponse response, HttpSession session, @PathVariable("fileId") @ValidateParam(required = true) String fileId) {
        UserInfo userInfo = getUserInfoFromSession(session);
        fileInfoService.previewFile(response, fileId, userInfo.getUserId());
    }

    @RequestMapping("/rename")
    @Interceptor(checkInput = true)
    public R rename(HttpSession session, @ValidateParam(required = true) String fileId, @ValidateParam(required = true) String fileName) {
        UserInfo userInfo = getUserInfoFromSession(session);
        FileInfo fileInfo = fileInfoService.rename(fileId, userInfo.getUserId(), fileName);
        return getSuccessR(fileInfo);
    }

    @RequestMapping("/newFolder")
    @Interceptor(checkInput = true)
    public R newFolder(HttpSession session, @ValidateParam(required = true) String filePid,
                       @ValidateParam(required = true) String fileName) {
        UserInfo userInfo = getUserInfoFromSession(session);
        FileInfo fileInfo = fileInfoService.newFolder(userInfo.getUserId(), filePid, fileName);
        return getSuccessR(fileInfo);
    }

    @RequestMapping("/moveTo")
    @Interceptor(checkInput = true)
    public R moveTo(HttpSession session, @ValidateParam(required = true) String filePid, String currentFileIds) {
        UserInfo userInfo = getUserInfoFromSession(session);
        List<FileInfo> fileInfoList = fileInfoService.moveTo(userInfo.getUserId(), filePid, currentFileIds);
        return getSuccessR(fileInfoList);
    }

    @RequestMapping("/moveToHere")
    @Interceptor(checkInput = true)
    public R moveToHere(HttpSession session, @ValidateParam(required = true) String filePid, String fileIds) {
        UserInfo userInfo = getUserInfoFromSession(session);
        fileInfoService.moveToHere(userInfo.getUserId(), filePid, fileIds);
        return getSuccessR(null);
    }

    @RequestMapping("/download/{fileId}")
    @Interceptor(checkInput = true)
    public void download(HttpSession session, HttpServletRequest request, HttpServletResponse response,
                         @PathVariable("fileId") @ValidateParam(required = true) String fileId) {
        UserInfo userInfo = getUserInfoFromSession(session);
        fileInfoService.download(userInfo.getUserId(), fileId, request, response);
    }


    @RequestMapping("/deleteFile")
    @Interceptor(checkInput = true)
    public R deleteFile(HttpSession session, @ValidateParam(required = true) String fileIds) {
        UserInfo userInfo = getUserInfoFromSession(session);
        fileInfoService.batchDelete2Recyclebin(userInfo.getUserId(), fileIds);
        return getSuccessR(null);

    }
}

