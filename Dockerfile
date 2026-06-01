# Dockerfile para desplegar el backend Quarkus en Railway.
#
# Multi-stage:
#   1) build   -> compila el JAR con Maven (no requiere ./mvnw package previo)
#   2) runtime -> ejecuta el JAR sobre el runtime OpenJDK 17 de Red Hat
#
# El JSON de credenciales de Firebase NO se incluye en la imagen (está en
# .gitignore). Se inyecta en runtime mediante la variable FIREBASE_CREDENTIALS_JSON
# y se escribe a /deployments/firebase.json justo antes de arrancar la app.

# ---- Build: compila el JAR ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ---- Runtime ----
FROM registry.access.redhat.com/ubi9/openjdk-17-runtime:1.24
ENV LANGUAGE='en_US:en'

# Cuatro capas para reaprovechar caché cuando solo cambia el código de la app
COPY --from=build --chown=185 /app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build --chown=185 /app/target/quarkus-app/*.jar /deployments/
COPY --from=build --chown=185 /app/target/quarkus-app/app/ /deployments/app/
COPY --from=build --chown=185 /app/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185

ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

# Escribe el JSON de Firebase desde la variable secreta y luego arranca Quarkus.
# Se escribe en /tmp porque el usuario 185 sí tiene permiso de escritura ahí.
# FIREBASE_CREDENTIALS_PATH debe valer /tmp/firebase.json en las variables de Railway.
# Quarkus lee el puerto de la variable PORT que Railway inyecta automáticamente.
ENTRYPOINT ["/bin/sh","-c","printf '%s' \"$FIREBASE_CREDENTIALS_JSON\" > /tmp/firebase.json && JAVA_APP_JAR=/deployments/quarkus-run.jar /opt/jboss/container/java/run/run-java.sh"]
