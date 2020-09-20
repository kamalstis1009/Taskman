package com.subra.taskman.models;

import java.util.ArrayList;

public class TaskModel {

    private String title;
    private String date;
    private String description;
    private String status;
    private String priority;
    private ArrayList<String> participants;
    private ArrayList<FileModel> attachments;
    private ArrayList<FileModel> records;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    public ArrayList<FileModel> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<FileModel> attachments) {
        this.attachments = attachments;
    }

    public ArrayList<FileModel> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<FileModel> records) {
        this.records = records;
    }
}
