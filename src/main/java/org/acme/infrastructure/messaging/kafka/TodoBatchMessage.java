package org.acme.infrastructure.messaging.kafka;

import org.acme.infrastructure.messaging.TodoEvent;

public record TodoBatchMessage(String batchId, TodoEvent todo) {
}
