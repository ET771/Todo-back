package org.acme.application.usecase.demo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.acme.domain.models.Todo;
import org.acme.domain.models.demo.TodoDemoResult;
import org.acme.domain.models.demo.TodoView;
import org.acme.domain.repository.TodoDemoRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ApplicationScoped
public class TodoFetchDemoUseCase {

    @Inject
    TodoDemoRepository repository;

    @Inject
    EntityManager em;

    public TodoDemoResult lazyOk() {
        return measureTodos(
                "lazyOk",
                "Carga LAZY pura. Solo selecciona las columnas escalares de la tabla todos. Una sola query, sin tocar relaciones.",
                repository::lazyOk
        );
    }

    public TodoDemoResult eagerSimulation() {
        return measureTodos(
                "eagerSimulation",
                "Simula FetchType.EAGER: trae los todos y luego accede a owner, categories y comments uno por uno. Hibernate dispara queries extras por cada relacion (1 + N + N + N).",
                repository::eagerSimulation
        );
    }

    public TodoDemoResult nPlusOne() {
        return measureTodos(
                "nPlusOne",
                "Problema clasico N+1: 1 query para los todos + 1 query mas por cada todo cuando accedes a sus comments. Si tienes 8 todos, terminas con 9 queries.",
                repository::nPlusOne
        );
    }

    public TodoDemoResult joinFetch() {
        return measureTodos(
                "joinFetch",
                "JPQL con LEFT JOIN FETCH: una sola query con JOINs trae el padre y las relaciones en una pasada. Adios N+1.",
                repository::joinFetch
        );
    }

    public TodoDemoResult entityGraph() {
        return measureTodos(
                "entityGraph",
                "@NamedEntityGraph declarativo. Hibernate convierte el grafo en JOINs en una sola query. Reusable y limpio.",
                repository::entityGraph
        );
    }

    public TodoDemoResult projection() {
        Statistics stats = stats();
        stats.clear();
        long start = System.nanoTime();
        List<TodoView> rows;
        TodoDemoResult result = new TodoDemoResult(
                "projection",
                "Proyeccion DTO: SELECT new TodoView(...). Trae solo las columnas necesarias, sin entidades ni relaciones. Es la opcion mas barata cuando no necesitas el grafo completo."
        );
        try {
            rows = repository.projection();
            result.setMillis((System.nanoTime() - start) / 1_000_000);
            result.setQueryCount(stats.getPrepareStatementCount());
            result.setRows(rows.size());
            result.setSample(rows.stream()
                    .limit(5)
                    .map(v -> v.getTitle() + " <- " + v.getOwnerEmail())
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            result.setError(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        return result;
    }

    public TodoDemoResult lazyInitException() {
        Statistics stats = stats();
        stats.clear();
        long start = System.nanoTime();
        TodoDemoResult result = new TodoDemoResult(
                "lazyInitException",
                "Cargar entidades y luego cerrar la sesion (em.clear). Cuando intentas tocar una coleccion LAZY fuera de la sesion, Hibernate lanza LazyInitializationException."
        );
        String message = repository.lazyInitException();
        result.setMillis((System.nanoTime() - start) / 1_000_000);
        result.setQueryCount(stats.getPrepareStatementCount());
        result.setRows(0);
        if (message.startsWith("LazyInitializationException")) {
            result.setError(message);
        } else {
            result.setSample(List.of(message));
        }
        return result;
    }

    private TodoDemoResult measureTodos(String strategy, String explanation, Supplier<List<Todo>> action) {
        Statistics stats = stats();
        stats.clear();
        long start = System.nanoTime();
        TodoDemoResult result = new TodoDemoResult(strategy, explanation);
        try {
            List<Todo> todos = action.get();
            result.setMillis((System.nanoTime() - start) / 1_000_000);
            result.setQueryCount(stats.getPrepareStatementCount());
            result.setRows(todos.size());
            result.setSample(todos.stream()
                    .limit(5)
                    .map(t -> {
                        String ownerEmail = t.getOwner() != null ? t.getOwner().getEmail() : "?";
                        int comments = t.getComments() != null ? t.getComments().size() : 0;
                        int cats = t.getCategories() != null ? t.getCategories().size() : 0;
                        return t.getTitle() + " (owner=" + ownerEmail + ", cats=" + cats + ", comments=" + comments + ")";
                    })
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            result.setError(e.getClass().getSimpleName() + ": " + e.getMessage());
            result.setMillis((System.nanoTime() - start) / 1_000_000);
            result.setQueryCount(stats.getPrepareStatementCount());
        }
        return result;
    }

    private Statistics stats() {
        Session session = em.unwrap(Session.class);
        SessionFactory sf = session.getSessionFactory();
        Statistics statistics = sf.getStatistics();
        statistics.setStatisticsEnabled(true);
        return statistics;
    }
}