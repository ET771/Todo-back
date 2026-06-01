# Guía de pruebas con Postman

Base URL: `http://localhost:8080`

Todos los endpoints requieren el header:
```
Authorization: Bearer <firebase-id-token>
```

---

## Usuarios

### Registrar usuario
```
POST /users/register
Content-Type: application/json

{
  "email": "usuario@example.com",
  "fullName": "Nombre Apellido",
  "firebaseUuid": "uid-de-firebase"
}
```
Respuesta esperada: `200 OK` con el usuario creado.

---

## Todos

### Crear todo
```
POST /todos
Content-Type: application/json

{
  "title": "Estudiar Quarkus",
  "description": "Leer la documentación oficial"
}
```
Respuesta esperada: `200 OK` con el todo creado (incluye `id`, `createdAt`, `owner`).

### Obtener todos los todos
```
GET /todos
```
Respuesta esperada: `200 OK` con array de todos.

### Obtener todo por ID
```
GET /todos/{id}
```
Respuesta esperada: `200 OK` con el todo (incluye `categories` y `comments`).
Devuelve `404` si no existe.

### Editar todo
```
PUT /todos/{id}
Content-Type: application/json

{
  "title": "Nuevo título",
  "description": "Nueva descripción",
  "completed": true,
  "priority": "HIGH",
  "dueDate": "2026-05-01T23:59:00"
}
```
Todos los campos son opcionales — solo se actualizan los que se envían.
`priority` acepta: `LOW`, `MEDIUM`, `HIGH`.
Respuesta esperada: `200 OK` con el todo actualizado.

### Eliminar todo
```
DELETE /todos/{id}
```
Respuesta esperada: `204 No Content`.
Devuelve `404` si no existe.

---

## Categorías

### Crear categoría
```
POST /categories
Content-Type: application/json

{
  "name": "Trabajo",
  "description": "Tareas laborales",
  "color": "#4A90D9",
  "icon": "briefcase"
}
```
Respuesta esperada: `201 Created` con la categoría creada.

### Obtener todas las categorías
```
GET /categories
```
Respuesta esperada: `200 OK` con array de categorías.

### Obtener categoría por ID
```
GET /categories/{id}
```
Respuesta esperada: `200 OK` con la categoría.
Devuelve `404` si no existe.

### Eliminar categoría
```
DELETE /categories/{id}
```
Respuesta esperada: `204 No Content`.
Devuelve `404` si no existe.

---

## Vincular Todos ↔ Categorías

### Agregar categoría a un todo
```
POST /todos/{todoId}/categories/{categoryId}
Content-Type: application/json
```
Respuesta esperada: `204 No Content`.
Devuelve `404` si el todo o la categoría no existen.

### Quitar categoría de un todo
```
DELETE /todos/{todoId}/categories/{categoryId}
```
Respuesta esperada: `204 No Content`.
Devuelve `404` si el todo o la categoría no existen.

---

## Comentarios

### Obtener comentarios de un todo
```
GET /todos/{todoId}/comments
```
Respuesta esperada: `200 OK` con array de comentarios.

### Agregar comentario
```
POST /todos/{todoId}/comments
Content-Type: application/json

{
  "content": "Este todo es muy importante"
}
```
Respuesta esperada: `201 Created` con el comentario (incluye `id`, `authorEmail`, `createdAt`).

### Eliminar comentario
```
DELETE /todos/{todoId}/comments/{commentId}
```
Respuesta esperada: `204 No Content`.
Devuelve `404` si no existe.

---

## Flujo de prueba sugerido

1. Registrar un usuario → guardar el `id`
2. Crear 2 categorías (ej. "Trabajo" y "Personal") → guardar sus `id`
3. Crear un todo → guardar su `id`
4. Editar el todo con `priority: HIGH` y una `dueDate`
5. Vincular el todo con una categoría → `GET /todos/{id}` y verificar que aparece en `categories`
6. Agregar 2 comentarios al todo → `GET /todos/{todoId}/comments`
7. Borrar un comentario → verificar que ya no aparece
8. Desvincular la categoría del todo
9. Borrar el todo → verificar `204`
