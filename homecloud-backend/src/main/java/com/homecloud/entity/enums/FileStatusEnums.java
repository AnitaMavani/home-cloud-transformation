package com.homecloud.entity.enums;


public enum FileStatusEnums {
    TRANSFER(0, "transfer"),
    TRANSFER_FAIL(1, "transfer_fail"),
    USING(2, "using");

    private Integer status;
    private String desc;

    FileStatusEnums(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
