package com.homecloud.service.impl;

import com.homecloud.entity.enums.Constants;
import com.homecloud.entity.domain.UserInfo;
import com.homecloud.entity.query.*;
import com.homecloud.exception.BusinessException;
import com.homecloud.mappers.*;
import com.homecloud.service.UserInfoService;
import com.homecloud.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.Date;

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);


    @Override
    public UserInfo login(String email, String password) {
        UserInfo user = this.userInfoMapper.selectByEmail(email);
        if (null == user || !user.getPassword().equals(password)) {
            throw new BusinessException("email or password error");
        }
        UserInfo updateInfo = new UserInfo();
        updateInfo.setLastLoginTime(new Date());
        this.userInfoMapper.updateByUserId(updateInfo, user.getUserId());
        return user;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(String email, String nickName, String password) {
        UserInfo user = this.userInfoMapper.selectByEmail(email);
        if (null != user) {
            throw new BusinessException("email already exists");
        }
        UserInfo nickNameUser = this.userInfoMapper.selectByNickName(nickName);
        if (null != nickNameUser) {
            throw new BusinessException("nickName already exists");
        }

        String userId = StringUtil.getRandomNumber(Constants.LENGTH_10);
        user = new UserInfo();
        user.setEmail(email);
        user.setUserId(userId);
        user.setPassword(StringUtil.encodeByMD5(password));
        user.setNickName(nickName);
        user.setJoinTime(new Date());
        this.userInfoMapper.insert(user);
    }
    @Override
    public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
        return this.userInfoMapper.updateByUserId(bean, userId);
    }

}