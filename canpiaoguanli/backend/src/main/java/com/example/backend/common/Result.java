package com.example.backend.common;

public class Result {
    private String code;
    private String msg;
    private Object data;

    public static Result success() {
        Result result = new Result();
        result.setCode("200");
        result.setMsg("success");
        return result;
    }
    public static Result success(Object data) {
        Result result = success();
        result.setData(data);
        return result;
    }
    public static Result error(String msg) {
        Result result = new Result();
        result.setCode("500");
        result.setMsg("mistake");
        return result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
