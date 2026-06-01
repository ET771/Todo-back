package org.acme.interfaces.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.infrastructure.messaging.TodoEvent;
import org.acme.infrastructure.messaging.kafka.BatchProcessingStats;
import org.acme.infrastructure.messaging.kafka.KafkaBatchProducer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

@Path("/demo/kafka")
@Produces(MediaType.APPLICATION_JSON)
public class KafkaDemoResource {

    @Inject
    KafkaBatchProducer producer;

    @Inject
    BatchProcessingStats stats;

    @POST
    @Path("/batch")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submitBatch(List<TodoEvent> todos) {
        String batchId = producer.publishBatch(todos);
        return Response.accepted(Map.of("batchId", batchId, "enqueued", todos.size())).build();
    }

    @POST
    @Path("/batch/generate/{n}")
    public Response generate(@PathParam("n") int n) {
        List<TodoEvent> todos = IntStream.range(0, n)
                .mapToObj(i -> new TodoEvent(
                        UUID.randomUUID().toString(),
                        "Tarea sintética #" + i,
                        "user" + i + "@example.com"))
                .toList();
        String batchId = producer.publishBatch(todos);
        return Response.accepted(Map.of("batchId", batchId, "enqueued", n)).build();
    }

    @GET
    @Path("/status/{batchId}")
    public Response statusOf(@PathParam("batchId") String batchId) {
        BatchProcessingStats.Snapshot snap = stats.snapshot(batchId);
        if (snap == null) return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", "batch no encontrado", "batchId", batchId)).build();
        return Response.ok(snap).build();
    }

    @GET
    @Path("/status")
    public Map<String, BatchProcessingStats.Snapshot> allStatus() {
        return stats.allSnapshots();
    }
}
