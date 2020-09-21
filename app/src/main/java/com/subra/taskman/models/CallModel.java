package com.subra.taskman.models;

import java.sql.Timestamp;

public class CallModel {

    private String status;
    private ContactModel contact;
    private String date;
    private String subject;
    private String type;
    private String purpose;
    private String result;
    private String createdAt = String.valueOf(new Timestamp(System.currentTimeMillis()));

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ContactModel getContact() {
        return contact;
    }

    public void setContact(ContactModel contact) {
        this.contact = contact;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
