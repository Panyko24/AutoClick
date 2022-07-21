package com.panyko.autoclick.enums;

public enum  TypeEnum {
    TYPE_COMMON(0,"TYPE_COMMON"),
    TYPE_GGS_LOGIN(1,"TYPE_GGS_LOGIN");
    private int code;
    private String msg;

    TypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
