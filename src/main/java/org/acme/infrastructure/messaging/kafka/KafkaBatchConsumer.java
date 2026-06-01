package org.acme.infrastructure.messaging.kafka;

import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.infrastructure.messaging.rabbitmq.TodoCreatedProducer;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class KafkaBatchConsumer {

    // Tiempo simulado de procesamiento por todo (ms).
    // En producción aquí irían llamadas a APIs externas, OCR, indexación, etc.
    private static final long PROCESSING_MS = 2000;

    @Inject
    BatchProcessingStats stats;

    @Inject
    TodoCreatedProducer notifications;

    @Incoming("todos-batch-in")
    @Blocking
    public void process(TodoBatchMessage msg) {
        String batchId = msg.batchId();
        stats.onStart(batchId);
        System.out.println("[Kafka batch] batch=" + batchId + " procesando " + msg.todo().id() + " - " + msg.todo().title());
        try {
            Thread.sleep(PROCESSING_MS);
            stats.onSuccess(batchId, msg.todo().title());
            notifications.publish(msg.todo());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            stats.onFailure(batchId);
            System.out.println("[Kafka batch] failure batch=" + batchId);
        }
    }
}
