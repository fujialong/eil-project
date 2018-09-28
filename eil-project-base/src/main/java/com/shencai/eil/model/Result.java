package com.shencai.eil.model;

import com.shencai.eil.constants.ResultStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanghj on 2018/1/30.
 */
public class Result<T> implements Serializable{

    private static final long serialVersionUID = -7268548945184612165L;

    private ResultStatus status = ResultStatus.FAILED;

    private T data;

    private List<String> infoList = new ArrayList<>();

    private String msg;

    private long code;

    public static Result ok() {
        Result r = new Result();
        r.setStatus(ResultStatus.SUCCESS);
        r.setCode(ResultStatus.SUCCESS.getCode());
        return r;
    }

    public static Result ok(Object data) {
        Result r = new Result();
        r.setData(data);
        r.setStatus(ResultStatus.SUCCESS);
        r.setCode(ResultStatus.SUCCESS.getCode());
        return r;
    }

    public Result(){
        super();
    }

    public Result(T data){
        super();
        this.data = data;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<String> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<String> infoList) {
        this.infoList = infoList;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }
}
