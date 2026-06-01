package org.acme.domain.repository;

import org.acme.domain.models.Todo;
import org.acme.domain.models.demo.TodoView;

import java.util.List;

public interface TodoDemoRepository {

    List<Todo> lazyOk();

    List<Todo> eagerSimulation();

    List<Todo> nPlusOne();

    List<Todo> joinFetch();

    List<Todo> entityGraph();

    List<TodoView> projection();

    String lazyInitException();
}