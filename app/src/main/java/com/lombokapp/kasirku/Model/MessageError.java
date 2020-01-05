package com.lombokapp.kasirku.Model;

import com.google.gson.annotations.SerializedName;

public class MessageError {
    @SerializedName("error")
    private boolean err;

    @SerializedName("message")
    private String msg;

    public MessageError(boolean err, String msg) {
        this.err = err;
        this.msg = msg;
    }

    public boolean isErr() {
        return err;
    }

    public String getMsg() {
        return msg;
    }
}
