# ── Stage 1 : Build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 true
COPY src ./src
# Pas de wrapper mvnw dans ce service — on utilise Maven embarqué
RUN apk add --no-cache maven && mvn package -DskipTests -q

# ── Stage 2 : Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=builder /app/target/*.jar app.jar
USER spring
EXPOSE 8083
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
