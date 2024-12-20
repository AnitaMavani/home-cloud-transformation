package com.homecloud.controller;

import com.homecloud.aspect.annotation.Interceptor;
import com.homecloud.aspect.annotation.ValidateParam;
import com.homecloud.entity.enums.Constants;
import com.homecloud.entity.enums.VerifyRegexEnum;
import com.homecloud.entity.domain.UserInfo;
import com.homecloud.entity.vo.R;
import com.homecloud.service.UserInfoService;
import com.homecloud.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RestController;
@RestController("userController")
public class UserController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserInfoService userInfoService;

    @RequestMapping("/register")
    @Interceptor(checkInput = true)
    public R register(HttpSession session,
                      @ValidateParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
                      @ValidateParam(required = true, max = 20) String nickName,
                      @ValidateParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password
                              ) {
            userInfoService.register(email, nickName, password);
            return getSuccessR(null);

    }

@RequestMapping("/login")
@Interceptor(checkInput = false)
public R login(HttpSession session, HttpServletRequest request,
               @ValidateParam(required = true) String email,
               @ValidateParam(required = true) String password
) {
    UserInfo userInfo = userInfoService.login(email, password);
    session.setAttribute(Constants.SESSION_KEY, userInfo);
    return getSuccessR(userInfo);
}

    @RequestMapping("/getUserInfo")
    public R getUserInfo(HttpSession session) {
        UserInfo userInfo = getUserInfoFromSession(session);
        return getSuccessR(userInfo);
    }

    @RequestMapping("/logout")
    public R logout(HttpSession session) {
        session.invalidate();
        return getSuccessR(null);
    }

    @RequestMapping("/updatePassword")
    @Interceptor(checkInput = true)
    public R updatePassword(HttpSession session,
                                     @ValidateParam(regex = VerifyRegexEnum.PASSWORD, required = true, min = 8, max = 18) String password) {
        UserInfo userInfo = getUserInfoFromSession(session);
        UserInfo updateUserInfo = new UserInfo();
        updateUserInfo.setPassword(StringUtil.encodeByMD5(password));
        userInfoService.updateUserInfoByUserId(updateUserInfo, userInfo.getUserId());
        return getSuccessR(null);
    }
}
