package com.homecloud.entity.enums;


public enum FileFolderTypeEnums {
    FILE(0, "file"),
    FOLDER(1, "folder");

    private Integer type;
    private String desc;

    FileFolderTypeEnums(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
