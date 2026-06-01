package org.acme.infrastructure.messaging.rabbitmq;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.infrastructure.messaging.TodoEvent;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class TodoCreatedProducer {

    @Channel("todo-created-out")
    Emitter<TodoEvent> emitter;

    public void publish(TodoEvent event) {
        emitter.send(event);
    }
}
