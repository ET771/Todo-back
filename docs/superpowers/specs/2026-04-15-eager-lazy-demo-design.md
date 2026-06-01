# Eager vs Lazy Loading & N+1 Demo — Design

**Date:** 2026-04-15
**Project:** HelloWorldQuarkusGrupo2 / code-with-quarkus
**Goal:** Extend existing clean-architecture Quarkus project so students can see, click, and measure the difference between eager loading, lazy loading, the N+1 problem, and mitigations (JOIN FETCH, `@NamedEntityGraph`, DTO projection). Ship an interactive HTML guide served by Quarkus itself.

## Motivation

Teach in class the real-world impact of fetch strategies. Current project has flat `Todo` + `User` with no relationships — nothing to N+1. Need relationships (owner, categories, comments) plus side-by-side runnable endpoints with live query counts.

## Architecture (clean, mirrors existing layout)

Package root: `org.acme`.

```
domain/
  models/
    User.java            (existing, unchanged)
    Todo.java            (extend: owner, categories, comments)
    Category.java        (new)
    Comment.java         (new)
    demo/
      TodoView.java      (new, projection DTO)
      TodoDemoResult.java (new, wraps strategy + metrics + rows + explanation)
  repository/
    TodoRepository.java      (existing)
    UserRepository.java      (existing)
    TodoDemoRepository.java  (new port, 7 methods)

application/
  usecase/
    demo/
      TodoFetchDemoUseCase.java (new — orchestrates each strategy, measures)

infrastructure/
  entities/
    UserEntity.java      (existing, unchanged)
    TodoEntity.java      (extend: @ManyToOne owner, @OneToMany comments, @ManyToMany categories, @NamedEntityGraph x2)
    CategoryEntity.java  (new)
    CommentEntity.java   (new)
  mapper/
    TodoMapper.java       (extend)
    TodoViewMapper.java   (new)
    CategoryMapper.java   (new)
    CommentMapper.java    (new)
  repository/
    TodoRepositoryImpl.java      (existing)
    TodoDemoRepositoryImpl.java  (new — implements 7 strategies)
  security/
    FirebaseAuthFilter.java      (modify: skip /todo/demo/* and static guide)

interfaces/rest/
  TodoResource.java        (existing)
  TodoDemoResource.java    (new — 7 GET endpoints, public)
```

## Domain relationships

- `Todo` N—1 `User` (owner, required)
- `Todo` 1—N `Comment` (comments, cascade delete)
- `Todo` N—N `Category` (categories, join table `todo_category`)

All `@ManyToOne` / `@OneToMany` / `@ManyToMany` declared with **`fetch = FetchType.LAZY`** — the whole point of the demo is that LAZY is the default and you *control* eager access via queries.

`TodoEntity` declares two named entity graphs:
- `Todo.full` → owner + categories + comments
- `Todo.notSoFull` → owner + categories

## 7 Demo Strategies

`TodoDemoRepository` port, implemented in `TodoDemoRepositoryImpl`:

| # | Method | What it does | Expected SQL |
|---|--------|--------------|--------------|
| 1 | `lazyOk()` | `SELECT t FROM TodoEntity t`, touch only scalar fields (title/desc) | **1** query |
| 2 | `eagerSimulation()` | Load all todos, then for each access owner+categories+comments — simulates what `FetchType.EAGER` would produce | **many** queries |
| 3 | `nPlusOne()` | Load all todos, loop and call `t.getComments().size()` | **1 + N** queries |
| 4 | `joinFetch()` | JPQL `SELECT DISTINCT t FROM TodoEntity t LEFT JOIN FETCH t.owner LEFT JOIN FETCH t.categories` | **1** query |
| 5 | `entityGraph()` | `em.createQuery(...).setHint("jakarta.persistence.loadgraph", graph("Todo.full"))` | **1** query |
| 6 | `projection()` | `SELECT new TodoView(t.id, t.title, o.email) FROM TodoEntity t JOIN t.owner o` | **1** lean query |
| 7 | `lazyInitException()` | Load inside tx, return detached, touch collection outside session — catch + report `LazyInitializationException` | **exception** |

Each returns `TodoDemoResult`:
```java
{
  strategy: "joinFetch",
  queryCount: 1,
  millis: 14,
  rows: 8,
  explanation: "One SQL with LEFT JOIN FETCH pulls parent + children in a single round trip.",
  sample: [ ...first few rows as strings... ],
  error: null
}
```

Query count comes from Hibernate `Statistics` (`sessionFactory.getStatistics()`). Reset before each call, read after.

## Use case

`TodoFetchDemoUseCase` — single `@ApplicationScoped` bean. Injects `TodoDemoRepository`. Each method:
1. Reset statistics
2. `System.nanoTime()` start
3. Call strategy
4. Capture `queryCount = stats.getQueryExecutionCount() + stats.getPrepareStatementCount()` (use prepared statement count — more accurate than query count)
5. Return `TodoDemoResult`

## REST

`TodoDemoResource` at `/todo/demo`, all `@GET` `produces(JSON)`:

```
GET /todo/demo/lazy-ok
GET /todo/demo/eager-simulation
GET /todo/demo/n-plus-1
GET /todo/demo/join-fetch
GET /todo/demo/entity-graph
GET /todo/demo/projection
GET /todo/demo/lazy-init-exception
GET /todo/demo/all          (run all 7, return array — lets HTML render a comparison table)
```

All public. `FirebaseAuthFilter` must skip path prefixes:
- `/todo/demo/`
- `/guia-eager-lazy.html`
- `/q/` (Quarkus dev UI, already skipped if applicable)

## Config (`application.properties` additions)

```properties
quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.hibernate-orm.sql-load-script=import.sql
quarkus.hibernate-orm.log.sql=true
quarkus.hibernate-orm.log.format-sql=true
quarkus.hibernate-orm.statistics=true
quarkus.http.cors=true
quarkus.http.cors.origins=*
```

`drop-and-create` wipes DB on each restart so `import.sql` re-seeds cleanly — acceptable for a classroom demo project (NOT production).

## Seed data (`src/main/resources/import.sql`)

- 4 users (owners)
- 5 categories (Work, Personal, Urgent, Learning, Home)
- 8 todos, each assigned 1-2 categories and 0-3 comments
- Uses MySQL `UNHEX(REPLACE(uuid, '-', ''))` to store UUID as `BINARY(16)` — matches how Hibernate persists `java.util.UUID` by default.

## Interactive HTML guide

**Path:** `src/main/resources/META-INF/resources/guia-eager-lazy.html`
**URL:** `http://localhost:8080/guia-eager-lazy.html`

Single self-contained file (inline CSS + JS, no external deps). Sections:

1. **Intro** — what is fetch? JPA default per association type.
2. **Lazy vs Eager** — visual diagram + code snippets.
3. **The N+1 problem** — explain, then `[Ejecutar N+1]` button → shows query count jumping.
4. **JOIN FETCH fix** — button, compare query count side-by-side with N+1 result.
5. **@NamedEntityGraph** — button + explanation of reusable graph hint.
6. **DTO Projection** — button, explain why it is the leanest path when you don't need the full entity.
7. **LazyInitializationException** — button, shows captured exception message + *why it happens*.
8. **Why LAZY by default?** — 4 reasons:
   - Avoid over-fetching data you won't use
   - Keep queries predictable and tunable per use case
   - Better Hibernate L1/L2 cache reuse
   - Escape hatches exist (JOIN FETCH, entity graph, projection) when you DO need it
9. **Comparison table** — "Ejecutar todos" button → populates a table with strategy / query count / ms / rows for all 7 strategies in one shot.
10. **Quiz** — 3 multiple-choice questions, client-side validation.

Each button calls `fetch('/todo/demo/...')`, renders the JSON result into a card showing query count (big number), time, explanation, sample rows.

Dark theme, color-coded cards (green = good, red = N+1 / exception, blue = neutral info). Mirror the visual style of the reference project's `guia-eager-lazy.html`.

## Tests

Out of scope for this class demo. Manual verification via the HTML guide is the acceptance criterion.

## Acceptance criteria

1. `./mvnw quarkus:dev` starts cleanly, DB drops + reseeds, 4 users / 8 todos / etc. present.
2. Opening `http://localhost:8080/guia-eager-lazy.html` loads the guide.
3. Every button in the guide returns a 200 with sensible numbers:
   - `lazy-ok` → 1 query
   - `n-plus-1` → >= 9 queries (1 + 8)
   - `join-fetch` / `entity-graph` / `projection` → 1–2 queries
   - `lazy-init-exception` → error field populated with `LazyInitializationException` text
4. Existing authenticated endpoints (`POST /todos`, `POST /users`, `GET /users/test`) still require Firebase token.
5. Server logs show real SQL (format-sql on) so students can read the actual statements during class.

## Non-goals

- No new auth changes beyond filter path-skip.
- No production hardening (drop-and-create wipes data; fine for demo).
- No automated tests.
- No frontend framework — vanilla HTML/CSS/JS only.
