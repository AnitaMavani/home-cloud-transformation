package com.homecloud.entity.enums;


public enum UploadStatusEnums {
    UPLOAD_SECONDS("upload_seconds"),
    UPLOADING("uploading"),
    UPLOAD_FINISH("upload_finish");

    private String code;


    UploadStatusEnums(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
