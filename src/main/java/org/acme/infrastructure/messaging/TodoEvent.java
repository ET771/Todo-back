package org.acme.infrastructure.messaging;

public record TodoEvent(String id, String title, String userEmail) {
}
