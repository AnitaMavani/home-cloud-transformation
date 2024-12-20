package com.homecloud.entity.domain;


import java.util.Date;
import com.homecloud.entity.enums.DateTimePatternEnum;
import com.homecloud.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;



public class UserInfo implements Serializable {


    private String userId;


    private String nickName;

    private String email;

    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date joinTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    private Integer status;

    private Long useSpace;

    private Long totalSpace;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return this.nickName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public Date getJoinTime() {
        return this.joinTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getLastLoginTime() {
        return this.lastLoginTime;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setUseSpace(Long useSpace) {
        this.useSpace = useSpace;
    }

    public Long getUseSpace() {
        return this.useSpace;
    }

    public void setTotalSpace(Long totalSpace) {
        this.totalSpace = totalSpace;
    }

    public Long getTotalSpace() {
        return this.totalSpace;
    }

    @Override
    public String toString() {
        return "UserID:" + (userId == null ? "empty" : userId) + "，nickName:" + (nickName == null ? "empty" : nickName) + "，email:" + (email == null ? "empty" : email) + "，password:" + (password == null ? "empty" : password) + "，joinTime:" + (joinTime == null ? "empty" : DateUtil.format(joinTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + "，lastLoginTime:" + (lastLoginTime == null ? "empty" : DateUtil.format(lastLoginTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + "，0:禁用 1:正常:" + (status == null ? "empty" : status) + "，useSpace:" + (useSpace == null ? "empty" : useSpace) + "，totalSpace:" + (totalSpace == null ? "empty" : totalSpace);
    }
}
