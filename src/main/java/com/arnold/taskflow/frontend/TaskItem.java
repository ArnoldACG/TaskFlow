package com.arnold.taskflow.frontend;

public class TaskItem {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String createdAt;

    public TaskItem(Long id, String title, String description, String status, String priority, String createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public String getCreatedAt() { return createdAt; }
}