package com.taskflow.taskflow.dto;

import com.taskflow.taskflow.entity.Status;

public class TaskResponseDTO {

    private Long id;
    private String title;
    private Status status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}