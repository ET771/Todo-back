package org.acme.infrastructure.messaging.rabbitmq;

import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.infrastructure.messaging.TodoEvent;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class PushNotificationConsumer {

    @Incoming("push-notifications-in")
    public void onTodoCreated(JsonObject json) {
        TodoEvent event = json.mapTo(TodoEvent.class);
        // Aquí iría Firebase Cloud Messaging / APNs
        String deviceToken = "device-" + Integer.toHexString(event.id().hashCode());
        System.out.println("[Push] -> " + deviceToken + " " + event.title());
    }
}
