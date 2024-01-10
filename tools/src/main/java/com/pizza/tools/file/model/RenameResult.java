package com.pizza.tools.file.model;

/**
 * @author BoWei
 * 2023/8/28 09:39
 *
 */
public class RenameResult {
  private boolean  isSuccess;
  private String msg;

    public RenameResult(boolean isSuccess, String msg) {
        this.isSuccess = isSuccess;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMsg() {
        return msg == null ? "" : msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}