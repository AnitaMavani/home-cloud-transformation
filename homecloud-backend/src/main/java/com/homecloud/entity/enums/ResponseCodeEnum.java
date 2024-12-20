package com.homecloud.entity.enums;


public enum ResponseCodeEnum {
    CODE_200(200, "Request successful"),
    CODE_404(404, "Request URL not found"),
    CODE_600(600, "Request parameter error"),
    CODE_601(601, "Information already exists"),
    CODE_500(500, "Server returned an error, please contact the administrator"),
    CODE_901(901, "Login timeout, please login again"),
    CODE_902(902, "Shared link does not exist or has expired"),
    CODE_903(903, "Shared verification expired, please verify again");

    private Integer code;

    private String msg;

    ResponseCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
