package com.shencai.eil.constants;

import com.baomidou.mybatisplus.extension.api.IErrorCode;

/**
 * @author fujl
 */
public enum ResultStatus implements IErrorCode {

    /**
     * business validate error
     */
    VALIDATE_ERROR(-4,"business validate error"),

    /**
     * system error
     */
    ERROR(-3,"system error"),

    /**
     * no permission
     */
    NO_PERMISSION(-2,"no permission"),

    /**
     * login overtime/no login
     */
    LOGIN_OUT(-1, "login overtime/no login"),

    /**
     * Frequent operation
     */
    FREQUENTLY(2, "Frequent operation"),

    /**
     * failure
     */
    FAILED(0, "Failure"),

    /**
     * success
     */
    SUCCESS(1, "Success"),


    /**
     * user name or password error
     */
    USERNAME_PASSWORD_ERROR(500, "user name or password error");


    private final long code;
    private final String msg;

    ResultStatus(final long code, final String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ResultStatus fromCode(long code) {
        ResultStatus[] ecs = ResultStatus.values();
        for (ResultStatus ec : ecs) {
            if (ec.getCode() == code) {
                return ec;
            }
        }
        return SUCCESS;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return String.format(" ErrorCode:{code=%s, msg=%s} ", code, msg);
    }
}