package com.kaiyu.wiki.exception;

public enum BusinessExceptionCode {

    USER_LOGIN_NAME_EXIST("The login name already exists"),
    LOGIN_USER_ERROR("The username does not exist or the password is incorrect"),
    VOTE_REPEAT("You have already liked this"),
    ;

    private String desc;

    BusinessExceptionCode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
