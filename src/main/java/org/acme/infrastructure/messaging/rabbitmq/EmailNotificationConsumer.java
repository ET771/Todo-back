package org.acme.infrastructure.messaging.rabbitmq;

import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.infrastructure.messaging.TodoEvent;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class EmailNotificationConsumer {

    @Incoming("email-notifications-in")
    public void onTodoCreated(JsonObject json) {
        TodoEvent event = json.mapTo(TodoEvent.class);
        // Aquí iría SendGrid / SES / Mailgun
        System.out.println("[Email] -> " + event.title() + " " + event.userEmail());
    }
}
