package org.acme.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Comment {
    private UUID id;
    private String content;
    private LocalDateTime createdAt;
    private String authorEmail;

    public Comment() {
    }

    public Comment(UUID id, String content, LocalDateTime createdAt, String authorEmail) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.authorEmail = authorEmail;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getAuthorEmail() { return authorEmail; }
    public void setAuthorEmail(String authorEmail) { this.authorEmail = authorEmail; }
}
