package com.homecloud.entity.query;

public class FileInfoQuery extends BasePage {

    private String fileIdFuzzy;
    private String userIdFuzzy;
    private String fileMd5Fuzzy;
    private String filePidFuzzy;
    private String fileNameFuzzy;
    private String fileCoverFuzzy;
    private String filePathFuzzy;


    private String fileId;

    private String userId;

    private String fileMd5;

    private String filePid;

    private Long fileSize;

    private String fileName;

    private String fileCover;

    private String filePath;

    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;

    private String lastUpdateTime;

    private String lastUpdateTimeStart;

    private String lastUpdateTimeEnd;

    private Integer folderType;

    private Integer fileCategory;

    private Integer fileType;

    private Integer status;

    private String recoveryTime;

    private String recoveryTimeStart;

    private String recoveryTimeEnd;

    private Integer delFlag;

    private String[] fileIdArray;

    private String[] filePidArray;

    private String[] excludeFileIdArray;

    private Boolean queryExpire;

    private Boolean queryNickName;


    public Boolean getQueryNickName() {
        return queryNickName;
    }

    public void setQueryNickName(Boolean queryNickName) {
        this.queryNickName = queryNickName;
    }

    public Boolean getQueryExpire() {
        return queryExpire;
    }

    public void setQueryExpire(Boolean queryExpire) {
        this.queryExpire = queryExpire;
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

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTimeStart(String createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public String getCreateTimeStart() {
        return this.createTimeStart;
    }

    public void setCreateTimeEnd(String createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public String getCreateTimeEnd() {
        return this.createTimeEnd;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTimeStart(String lastUpdateTimeStart) {
        this.lastUpdateTimeStart = lastUpdateTimeStart;
    }

    public String getLastUpdateTimeStart() {
        return this.lastUpdateTimeStart;
    }

    public void setLastUpdateTimeEnd(String lastUpdateTimeEnd) {
        this.lastUpdateTimeEnd = lastUpdateTimeEnd;
    }

    public String getLastUpdateTimeEnd() {
        return this.lastUpdateTimeEnd;
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

    public void setRecoveryTime(String recoveryTime) {
        this.recoveryTime = recoveryTime;
    }

    public String getRecoveryTime() {
        return this.recoveryTime;
    }

    public void setRecoveryTimeStart(String recoveryTimeStart) {
        this.recoveryTimeStart = recoveryTimeStart;
    }

    public String getRecoveryTimeStart() {
        return this.recoveryTimeStart;
    }

    public void setRecoveryTimeEnd(String recoveryTimeEnd) {
        this.recoveryTimeEnd = recoveryTimeEnd;
    }

    public String getRecoveryTimeEnd() {
        return this.recoveryTimeEnd;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() {
        return this.delFlag;
    }

    public String[] getFileIdArray() {
        return fileIdArray;
    }

    public void setFileIdArray(String[] fileIdArray) {
        this.fileIdArray = fileIdArray;
    }

    public String[] getExcludeFileIdArray() {
        return excludeFileIdArray;
    }

    public void setExcludeFileIdArray(String[] excludeFileIdArray) {
        this.excludeFileIdArray = excludeFileIdArray;
    }

    public String[] getFilePidArray() {
        return filePidArray;
    }

    public void setFilePidArray(String[] filePidArray) {
        this.filePidArray = filePidArray;
    }

    public String getFileIdFuzzy() {
        return fileIdFuzzy;
    }

    public void setFileIdFuzzy(String fileIdFuzzy) {
        this.fileIdFuzzy = fileIdFuzzy;
    }

    public String getUserIdFuzzy() {
        return userIdFuzzy;
    }

    public void setUserIdFuzzy(String userIdFuzzy) {
        this.userIdFuzzy = userIdFuzzy;
    }

    public String getFileMd5Fuzzy() {
        return fileMd5Fuzzy;
    }

    public void setFileMd5Fuzzy(String fileMd5Fuzzy) {
        this.fileMd5Fuzzy = fileMd5Fuzzy;
    }

    public String getFilePidFuzzy() {
        return filePidFuzzy;
    }

    public void setFilePidFuzzy(String filePidFuzzy) {
        this.filePidFuzzy = filePidFuzzy;
    }

    public String getFileNameFuzzy() {
        return fileNameFuzzy;
    }

    public void setFileNameFuzzy(String fileNameFuzzy) {
        this.fileNameFuzzy = fileNameFuzzy;
    }

    public String getFileCoverFuzzy() {
        return fileCoverFuzzy;
    }

    public void setFileCoverFuzzy(String fileCoverFuzzy) {
        this.fileCoverFuzzy = fileCoverFuzzy;
    }

    public String getFilePathFuzzy() {
        return filePathFuzzy;
    }

    public void setFilePathFuzzy(String filePathFuzzy) {
        this.filePathFuzzy = filePathFuzzy;
    }
}
