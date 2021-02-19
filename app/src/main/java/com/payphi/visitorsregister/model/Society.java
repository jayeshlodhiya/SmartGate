package com.payphi.visitorsregister.model;

import java.io.Serializable;

public class Society implements Serializable {
    private String name;
    private String mainAmnt;
    private String mid;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainAmnt() {
        return mainAmnt;
    }

    public void setMainAmnt(String mainAmnt) {
        this.mainAmnt = mainAmnt;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
