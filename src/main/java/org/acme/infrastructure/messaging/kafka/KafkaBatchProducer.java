package org.acme.infrastructure.messaging.kafka;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.infrastructure.messaging.TodoEvent;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class KafkaBatchProducer {

    @Channel("todos-batch-out")
    Emitter<TodoBatchMessage> emitter;

    @Inject
    BatchProcessingStats stats;

    public String publishBatch(List<TodoEvent> todos) {
        String batchId = UUID.randomUUID().toString();
        stats.startBatch(batchId, todos.size());
        for (TodoEvent t : todos) {
            emitter.send(new TodoBatchMessage(batchId, t));
        }
        return batchId;
    }
}
