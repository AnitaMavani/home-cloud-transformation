package com.homecloud.service;

import com.homecloud.entity.domain.UserInfo;
import com.homecloud.entity.query.UserInfoQuery;

import java.util.List;


public interface UserInfoService {


    UserInfo login(String email, String password);

    void register(String email, String nickName, String password);
    Integer updateUserInfoByUserId(UserInfo bean, String userId);
}




