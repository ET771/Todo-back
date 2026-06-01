package org.acme.domain.models.demo;

import java.util.UUID;

public class TodoView {
    private UUID id;
    private String title;
    private String ownerEmail;

    public TodoView() {
    }

    public TodoView(UUID id, String title, String ownerEmail) {
        this.id = id;
        this.title = title;
        this.ownerEmail = ownerEmail;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
}
