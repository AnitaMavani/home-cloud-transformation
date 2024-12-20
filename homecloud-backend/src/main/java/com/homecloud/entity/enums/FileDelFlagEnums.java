package com.homecloud.entity.enums;


public enum FileDelFlagEnums {
    DEL(0, "delete"),
    RECYCLE (1, "recycle"),
    USING(2, "using");;

    private Integer flag;
    private String desc;

    FileDelFlagEnums(Integer flag, String desc) {
        this.flag = flag;
        this.desc = desc;
    }


    public Integer getFlag() {
        return flag;
    }

    public String getDesc() {
        return desc;
    }
}
