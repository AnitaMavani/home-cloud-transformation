package com.homecloud.entity.domain;

import com.homecloud.entity.enums.DateTimePatternEnum;
import com.homecloud.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;



public class FileInfo implements Serializable {


    private String fileId;
    private String userId;
    private Long fileSize;
    private String fileMd5;
    private String filePid;
    private String filePath;
    private String fileName;
    private String fileCover;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;
    /**
     * 1:vedio 2:music  3:image 4:doc 5:others
     */
    private Integer fileCategory;
    /**
     * 0:file 1:folder
     */
    private Integer folderType;

    /**
     * 1:vedio 2:music  3:image 4:pdf 5:doc 6:excel 7:txt 8:code 9:zip 10:others
     */
    private Integer fileType;

    /**
     * 0:transfer 1 transfer_fail 2:using
     */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recoveryTime;

    /**
     *  0:deleted  1:recyble bin  2:using
     */
    private Integer delFlag;

    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return this.fileId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFileMd5() {
        return this.fileMd5;
    }

    public void setFilePid(String filePid) {
        this.filePid = filePid;
    }

    public String getFilePid() {
        return this.filePid;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getFileSize() {
        return this.fileSize;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileCover(String fileCover) {
        this.fileCover = fileCover;
    }

    public String getFileCover() {
        return this.fileCover;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Date getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setFolderType(Integer folderType) {
        this.folderType = folderType;
    }

    public Integer getFolderType() {
        return this.folderType;
    }

    public void setFileCategory(Integer fileCategory) {
        this.fileCategory = fileCategory;
    }

    public Integer getFileCategory() {
        return this.fileCategory;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public Integer getFileType() {
        return this.fileType;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setRecoveryTime(Date recoveryTime) {
        this.recoveryTime = recoveryTime;
    }

    public Date getRecoveryTime() {
        return this.recoveryTime;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() {
        return this.delFlag;
    }

    @Override
    public String toString() {
        return "fileIdID:" + (fileId == null ? "empty" : fileId) + "，userID:" + (userId == null ? "empty" : userId) + "，md5:" + (fileMd5 == null ? "empty" : fileMd5) + "，filePidID:" + (filePid == null ? "empty" : filePid) + "，fileSize:" + (fileSize == null ? "empty" : fileSize) + "，fileName:" + (fileName == null ? "empty" : fileName) + "，fileCover:" + (fileCover == null ? "empty" : fileCover) + "，filePath:" + (filePath == null ? "empty" : filePath) + "，createTime:" + (createTime == null ? "empty" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + "，lastUpdateTime:" + (lastUpdateTime == null ? "empty" : DateUtil.format(lastUpdateTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + "，0:file 1:folder:" + (folderType == null ? "empty" : folderType) + "，1:video 2:music  3:image 4:doc 5:others:" + (fileCategory == null ? "empty" : fileCategory) + "， 1:video 2:music  3:image 4:pdf 5:doc 6:excel 7:txt 8:code 9:zip 10:others:" + (fileType == null ? "empty" : fileType) + "，0:TRANSFER 1:TRANSFER_FAIL 2:USING:" + (status == null ? "empty" : status) + "，recoveryTime:" + (recoveryTime == null ? "empty" : DateUtil.format(recoveryTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + "，delFlag 0:DEL  1:RECYCLE  2:USING:" + (delFlag == null ? "empty" : delFlag);
    }
}
