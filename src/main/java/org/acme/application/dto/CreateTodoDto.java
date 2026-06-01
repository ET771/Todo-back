package org.acme.application.dto;

import org.acme.domain.models.Priority;
import java.time.LocalDateTime;
import java.util.UUID;

public class CreateTodoDto {
    private String title;
    private String description;
    private UUID parentId;
    private Priority priority;
    private LocalDateTime dueDate;

    public CreateTodoDto() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
}
