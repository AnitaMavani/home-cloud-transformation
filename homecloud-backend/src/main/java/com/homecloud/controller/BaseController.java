package com.homecloud.controller;

import com.homecloud.entity.enums.Constants;
import com.homecloud.entity.enums.ResponseCodeEnum;
import com.homecloud.entity.domain.UserInfo;
import com.homecloud.entity.vo.PaginationResult;
import com.homecloud.entity.vo.R;
import com.homecloud.utils.CopyUtil;
import com.homecloud.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;


public class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
    private static final String STATUS_SUCCESS = "success";
    private static final int BUFFER_SIZE = 1024;
    protected static final String STATUS_ERROR = "error";

    protected <T> R getSuccessR(T data) {
        R<T> response = new R<>();
        response.setStatus(STATUS_SUCCESS);
        response.setCode(ResponseCodeEnum.CODE_200.getCode());
        response.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        response.setData(data);
        return response;
    }

    protected <S, T> PaginationResult<T> convert2Pagination(PaginationResult<S> source, Class<T> targetClass) {
        PaginationResult<T> target = new PaginationResult<>();
        target.setList(CopyUtil.copyList(source.getList(), targetClass));
        target.setPageNo(source.getPageNo());
        target.setPageSize(source.getPageSize());
        target.setPageTotal(source.getPageTotal());
        target.setTotalCount(source.getTotalCount());
        return target;
    }

    protected UserInfo getUserInfoFromSession(HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute(Constants.SESSION_KEY);
        return userInfo;
    }


}
