# syntax=docker/dockerfile:1.7
# ===================================================================
# RetailFlow — Multi-stage Dockerfile
# Stage 1: build fat JAR dengan Maven 3.9 + JDK 21
# Stage 2: runtime minimal dengan Eclipse Temurin JRE 21 (Alpine)
# ===================================================================

# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Cache repository Maven dengan BuildKit agar dependency tetap reusable antar-build
COPY pom.xml ./

# Copy source dan build (skip tests — test dijalankan di CI pipeline, bukan saat build image)
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
  mvn -B -e -ntp -DskipTests clean package

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jre-alpine AS runtime

# Install curl untuk healthcheck + create non-root user untuk security
RUN apk add --no-cache curl tini && \
    addgroup -S retailflow && adduser -S retailflow -G retailflow

WORKDIR /app

# Copy artifact dari build stage
COPY --from=build /workspace/target/toko-online-1.0.0.jar /app/app.jar

# Copy Flyway migration & seed files (jar di-dikembangkan tetapi redundant copy memastikan
# fallback bila Flyway perlu baca dari filesystem, misal untuk repeatable seed)
COPY --from=build /workspace/src/main/resources/db /app/db

# Ownership
RUN chown -R retailflow:retailflow /app

USER retailflow

# Expose port Spring Boot
EXPOSE 8080

# Healthcheck via actuator/health (butuh starter-actuator) — fallback ke root jika belum ada
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -fsS http://localhost:8080/actuator/health || curl -fsS http://localhost:8080/api/v1/products || exit 1

# JVM tuning untuk container (heap detection, OOM exit, UTF-8)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom" \
    SPRING_PROFILES_ACTIVE=prod

# tini: PID 1 reaper untuk graceful shutdown signal handling
ENTRYPOINT ["/sbin/tini", "--", "sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
