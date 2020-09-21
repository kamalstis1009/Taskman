package com.subra.taskman.models;

public class CallModel {

    private String type;
    private ContactModel contact;
    private String subject;
    private String callType;
    private String callPurpose;
    private String callResult;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ContactModel getContact() {
        return contact;
    }

    public void setContact(ContactModel contact) {
        this.contact = contact;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallPurpose() {
        return callPurpose;
    }

    public void setCallPurpose(String callPurpose) {
        this.callPurpose = callPurpose;
    }

    public String getCallResult() {
        return callResult;
    }

    public void setCallResult(String callResult) {
        this.callResult = callResult;
    }
}
