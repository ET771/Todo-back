package org.acme.infrastructure.messaging.kafka;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class TodoBatchMessageDeserializer extends ObjectMapperDeserializer<TodoBatchMessage> {
    public TodoBatchMessageDeserializer() {
        super(TodoBatchMessage.class);
    }
}
