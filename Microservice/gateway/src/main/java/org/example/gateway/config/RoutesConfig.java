package org.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RoutesConfig {

    // ── Profil LOCAL (défaut) ────────────────────────────────────────────────
    @Bean
    @Profile("!docker")
    public RouteLocator localRoutes(RouteLocatorBuilder builder) {
        return buildRoutes(builder,
                "http://localhost:8081",
                "http://localhost:8082",
                "http://localhost:8083",
                "http://localhost:8084",
                "http://localhost:8085",
                "http://localhost:8086",
                "http://localhost:8087"
        );
    }

    // ── Profil DOCKER ────────────────────────────────────────────────────────
    @Bean
    @Profile("docker")
    public RouteLocator dockerRoutes(RouteLocatorBuilder builder) {
        return buildRoutes(builder,
                "http://user-service:8081",
                "http://auth-service:8082",
                "http://disponibilite-service:8083",
                "http://salle-service:8084",
                "http://coursclasse-service:8085",
                "http://specialevent-service:8086",
                "http://planning-service:8087"
        );
    }

    // ── Routes communes ──────────────────────────────────────────────────────
    private RouteLocator buildRoutes(RouteLocatorBuilder builder,
                                     String userServiceUrl,
                                     String authServiceUrl,
                                     String disponibiliteServiceUrl,
                                     String salleServiceUrl,
                                     String coursClasseServiceUrl,
                                     String specialEventServiceUrl,
                                     String planningServiceUrl) {
        return builder.routes()
                // AUTH SERVICE
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri(authServiceUrl))

                // USER SERVICE
                .route("user-service", r -> r
                        .path("/api/users/**", "/api/enseignants/**")
                        .uri(userServiceUrl))

                // DISPONIBILITE SERVICE
                .route("disponibilite-service", r -> r
                        .path("/api/disponibilites", "/api/disponibilites/**")
                        .uri(disponibiliteServiceUrl))

                // SALLES SERVICE
                .route("salles-service", r -> r
                        .path("/api/salles/**", "/api/materiels/**", "/api/reservations/**")
                        .uri(salleServiceUrl))

                // COURS ET CLASSES SERVICE
                .route("coursclasse-service", r -> r
                        .path("/api/classes/**", "/api/cours/**", "/api/ecoles/**",
                                "/api/etudiants/**", "/api/filieres/**",
                                "/api/groupes-etudiants/**", "/api/ues/**", "/api/import/**")
                        .uri(coursClasseServiceUrl))

                // SPECIAL EVENT SERVICE
                .route("specialevent-service", r -> r
                        .path("/api/demandes/**", "/api/events/**")
                        .uri(specialEventServiceUrl))

                // PLANNING SERVICE
                .route("planning-service", r -> r
                        .path("/api/generation/**")
                        .uri(planningServiceUrl))

                .build();
    }
}
