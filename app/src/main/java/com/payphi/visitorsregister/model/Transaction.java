package com.payphi.visitorsregister.model;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String amt;
    private String status;
    private String date;
    private String transId;
    private String name;
    private String flatno;
    private String merchatRefNo;
    private String respCode;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlatno() {
        return flatno;
    }

    public void setFlatno(String flatno) {
        this.flatno = flatno;
    }

    public String getMerchatRefNo() {
        return merchatRefNo;
    }

    public void setMerchatRefNo(String merchatRefNo) {
        this.merchatRefNo = merchatRefNo;
    }
}
