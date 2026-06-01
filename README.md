# equipo2-todo-back

Construido con **Quarkus 3** (Java 17), arquitectura limpia, Firebase Auth y MySQL.

---

## Links deployados


| Servicio | URL |
|----------|-----|
| **API (producción)** | https://equipo2-todo-crs-66787588163.us-central1.run.app |
| **Health check** | https://equipo2-todo-crs-66787588163.us-central1.run.app/status |

---

## Tecnologías

- [Quarkus 3](https://quarkus.io) — framework Java
- Java 17
- Hibernate ORM + Panache — persistencia
- MySQL — base de datos
- Firebase Admin SDK — validación de tokens JWT
- JAX-RS (RESTEasy Jackson) — API REST
- Maven — build

---

## Requisitos previos

- Java 17+
- Maven 3.9+ (o usar el wrapper `./mvnw`)
- MySQL 8+ corriendo localmente
- Archivo JSON de Firebase Admin SDK (ver abajo)

---

## Setup de la base de datos

Quarkus/Hibernate crea las tablas automáticamente — solo necesitas crear el esquema vacío primero.

```sql
-- Ejecuta esto una vez en tu cliente MySQL (TablePlus, DBeaver, mysql CLI, etc.)
CREATE DATABASE todogrupo2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**Primera ejecución (con datos semilla):**  
Agrega esta variable en tu `.env` para que Hibernate recree el esquema y cargue `import.sql`:

```env
DB_SCHEMA_STRATEGY=drop-and-create
```

**Ejecuciones posteriores:**  
Cambia a `update` para conservar los datos entre reinicios:

```env
DB_SCHEMA_STRATEGY=update
```

> Si no defines `DB_SCHEMA_STRATEGY`, el valor por defecto es `update`.

---

## Configuración de Firebase Admin SDK

1. Ve a [Firebase Console](https://console.firebase.google.com) → tu proyecto → ⚙️ Configuración del proyecto → **Cuentas de servicio**
2. Haz clic en **Generar nueva clave privada** → descarga el `.json`
3. Coloca el archivo en `src/main/resources/` (nombre libre, p. ej. `firebase-adminsdk.json`)
4. Actualiza `FIREBASE_CREDENTIALS_PATH` en tu `.env`:

```env
FIREBASE_CREDENTIALS_PATH=src/main/resources/firebase-adminsdk.json
```

> El archivo `.json` está en `.gitignore` y nunca se sube al repositorio.

---

## Variables de entorno

Copia `.env.example` y completa con tus valores:

```bash
cp .env.example .env
```

```env
# Base de datos MySQL
DB_USERNAME=root
DB_PASSWORD=tu_password
DB_URL=jdbc:mysql://localhost:3306/todogrupo2

# Ruta al JSON de Firebase Admin SDK
# Descárgalo desde: Firebase Console > Configuración del proyecto > Cuentas de servicio
FIREBASE_CREDENTIALS_PATH=src/main/resources/tu-firebase-adminsdk.json
```

> El archivo `.env` está en `.gitignore` y nunca se sube al repositorio.

---

## Instalación y ejecución

```bash
# Modo desarrollo con live reload
./mvnw quarkus:dev

# Build y ejecución en producción
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

La API queda disponible en `http://localhost:8080`.  
Dev UI disponible en `http://localhost:8080/q/dev/` (solo modo dev).

---

## Endpoints principales

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `POST` | `/users` | Registrar usuario | No |
| `GET` | `/status` | Health check | No |
| `GET` | `/todos` | Listar todas las listas | Sí |
| `POST` | `/todos` | Crear lista o tarea | Sí |
| `GET` | `/todos/{id}` | Obtener lista por ID | Sí |
| `GET` | `/todos/{id}/tasks` | Tareas de una lista | Sí |
| `GET` | `/todos/search?q=...` | Buscar listas y tareas | Sí |
| `PUT` | `/todos/{id}` | Actualizar lista/tarea | Sí |
| `DELETE` | `/todos/{id}` | Eliminar lista/tarea | Sí |
| `GET` | `/categories` | Listar categorías | Sí |
| `POST` | `/categories` | Crear categoría | Sí |

Todos los endpoints protegidos requieren header:  
`Authorization: Bearer <Firebase ID Token>`

---

## Usuarios de prueba

| Email | Contraseña | Descripción |
|-------|-----------|-------------|
| `test@example.com` | `Test1234!` | Usuario de prueba general |

> Crea el usuario primero con `POST /users` o desde la pantalla de registro.

---

## Estructura del proyecto

```
src/main/java/org/acme/
├── interfaces/rest/      # Controladores REST (recursos)
├── application/
│   ├── dto/             # DTOs de entrada
│   └── usecase/         # Casos de uso (lógica de negocio)
├── domain/
│   ├── models/          # Entidades de dominio
│   └── repository/      # Interfaces de repositorio
└── infrastructure/
    ├── entities/         # Entidades JPA
    ├── repository/       # Implementaciones de repositorio
    ├── security/         # Filtro Firebase + AuthContext
    └── config/           # Configuración Firebase
```
