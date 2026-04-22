package org.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

        @Bean
        public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
                return builder.routes()
                                // AUTH SERVICE
                                .route("auth-service", r -> r.path("/api/auth/**")
                                                .uri("http://localhost:8082"))

                                // USER SERVICE
                                // Maps multiple paths to user-service
                                .route("user-service",
                                                r -> r.path("/api/users/**",
                                                                "/api/enseignants/**")
                                                                .uri("http://localhost:8081"))

                                // DISPONIBILITE SERVICE
                                .route("disponibilite-service",
                                                r -> r.path("/api/disponibilites", "/api/disponibilites/**")
                                                                .uri("http://localhost:8083"))

                                // SALLES SERVICE
                                .route("salles-service",
                                                r -> r.path("/api/salles/**", "/api/materiels/**",
                                                                "/api/reservations/**")
                                                                .uri("http://localhost:8084"))

                                // COURS ET CLASSES SERVICE
                                .route("coursclasseservice",
                                                r -> r.path("/api/classes/**", "/api/cours/**", "/api/ecoles/**",
                                                                "/api/etudiants/**", "/api/filieres/**",
                                                                "/api/groupes-etudiants/**", "/api/ues/**","/api/import/**")
                                                                .uri("http://localhost:8085"))

                                // Speciale Event SERVICE
                                .route("SpecialEvent-service", r -> r.path("/api/demandes/**", "/api/events/**")
                                                .uri("http://localhost:8086"))
                                // PLANNING SERVICE (Edmonds-Karp)
                                .route("planning-service", r -> r.path("/api/generation/**")
                                                .uri("http://localhost:8087"))

                                .build();
        }
}
