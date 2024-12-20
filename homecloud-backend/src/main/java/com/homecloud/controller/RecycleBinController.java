package com.homecloud.controller;


import com.homecloud.aspect.annotation.Interceptor;
import com.homecloud.aspect.annotation.ValidateParam;
import com.homecloud.entity.domain.UserInfo;
import com.homecloud.entity.enums.FileDelFlagEnums;
import com.homecloud.entity.query.FileInfoQuery;
import com.homecloud.entity.vo.PaginationResult;
import com.homecloud.entity.vo.R;
import com.homecloud.service.FileInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController("recycleBinController")
@RequestMapping("/recycle")
public class RecycleBinController extends BaseController {

    @Resource
    private FileInfoService fileInfoService;

    @RequestMapping("/loadRecycleList")
    @Interceptor(checkInput = true)
    public R loadRecycleList(HttpSession session, Integer pageNo, Integer pageSize) {
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(getUserInfoFromSession(session).getUserId());
        query.setOrderBy("recovery_time desc");
        query.setPageNo(pageNo);
        query.setPageSize(pageSize);
        query.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        PaginationResult result = fileInfoService.findListByPage(query);
        return getSuccessR(result);
    }

    @RequestMapping("/restore")
    @Interceptor(checkInput = true)
    public R Restore(HttpSession session, @ValidateParam(required = true) String fileIds) {
        UserInfo userInfo = getUserInfoFromSession(session);
        fileInfoService.batchRestore(userInfo.getUserId(), fileIds);
        return getSuccessR(null);
    }

    @RequestMapping("/deleteFile")
    @Interceptor(checkInput = true)
    public R deleteFile(HttpSession session, @ValidateParam(required = true) String fileIds) {
        UserInfo userInfo = getUserInfoFromSession(session);
        fileInfoService.batchDeleteFromRecycleBin(userInfo.getUserId(), fileIds);
        return getSuccessR(null);

    }
}
