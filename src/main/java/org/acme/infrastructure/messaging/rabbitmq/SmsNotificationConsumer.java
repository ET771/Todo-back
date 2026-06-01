package org.acme.infrastructure.messaging.rabbitmq;

import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.infrastructure.messaging.TodoEvent;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class SmsNotificationConsumer {

    @Incoming("sms-notifications-in")
    public void onTodoCreated(JsonObject json) {
        TodoEvent event = json.mapTo(TodoEvent.class);
        // Aquí iría Twilio / SNS
        System.out.println("[SMS] -> +52*** " + event.title());
    }
}
