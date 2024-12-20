package com.homecloud.entity.enums;


public enum FileCategoryEnums {
    VIDEO(1, "video"),
    MUSIC(2, "music"),
    IMAGE(3, "image"),
    DOC(4, "doc"),
    OTHERS(5, "others");

    private Integer category;
    private String code;

    FileCategoryEnums(Integer category, String code) {
        this.category = category;
        this.code = code;

    }

    public static FileCategoryEnums getByCode(String code) {
        for (FileCategoryEnums item : FileCategoryEnums.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public Integer getCategory() {
        return category;
    }

    public String getCode() {
        return code;
    }
}
