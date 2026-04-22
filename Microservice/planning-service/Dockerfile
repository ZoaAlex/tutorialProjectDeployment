# ── Stage 1 : Build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN apk add --no-cache maven && mvn package -DskipTests -q

# ── Stage 2 : Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=builder /app/target/*.jar app.jar
USER spring
EXPOSE 8087
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
