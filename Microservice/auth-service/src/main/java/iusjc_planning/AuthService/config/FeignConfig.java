package iusjc_planning.AuthService.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "iusjc_planning.AuthService.feign")
public class FeignConfig {
}
