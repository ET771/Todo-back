package org.acme.infrastructure.messaging.kafka;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class BatchProcessingStats {

    public record Snapshot(String batchId, String startedAt, int enqueued,
                           int inProgress, int processed, int failed,
                           String lastProcessedAt, String lastTodoTitle,
                           boolean done) {}

    private static final class PerBatch {
        final String batchId;
        final String startedAt = Instant.now().toString();
        final AtomicInteger enqueued = new AtomicInteger();
        final AtomicInteger inProgress = new AtomicInteger();
        final AtomicInteger processed = new AtomicInteger();
        final AtomicInteger failed = new AtomicInteger();
        final AtomicReference<String> lastProcessedAt = new AtomicReference<>("never");
        final AtomicReference<String> lastTodoTitle = new AtomicReference<>("-");

        PerBatch(String batchId) { this.batchId = batchId; }

        Snapshot snapshot() {
            int enq = enqueued.get(), prog = inProgress.get(),
                done = processed.get(), fail = failed.get();
            boolean finished = enq > 0 && prog == 0 && (done + fail) >= enq;
            return new Snapshot(batchId, startedAt, enq, prog, done, fail,
                    lastProcessedAt.get(), lastTodoTitle.get(), finished);
        }
    }

    private final ConcurrentMap<String, PerBatch> batches = new ConcurrentHashMap<>();

    public void startBatch(String batchId, int n) {
        batches.computeIfAbsent(batchId, PerBatch::new).enqueued.addAndGet(n);
    }

    public void onStart(String batchId) {
        get(batchId).inProgress.incrementAndGet();
    }

    public void onSuccess(String batchId, String title) {
        PerBatch b = get(batchId);
        b.inProgress.decrementAndGet();
        b.processed.incrementAndGet();
        b.lastProcessedAt.set(Instant.now().toString());
        b.lastTodoTitle.set(title);
    }

    public void onFailure(String batchId) {
        PerBatch b = get(batchId);
        b.inProgress.decrementAndGet();
        b.failed.incrementAndGet();
    }

    public Snapshot snapshot(String batchId) {
        PerBatch b = batches.get(batchId);
        return b == null ? null : b.snapshot();
    }

    public Map<String, Snapshot> allSnapshots() {
        Map<String, Snapshot> out = new LinkedHashMap<>();
        batches.forEach((id, b) -> out.put(id, b.snapshot()));
        return out;
    }

    private PerBatch get(String batchId) {
        return batches.computeIfAbsent(batchId, PerBatch::new);
    }
}
