package com.crowdfunding.tecendoarte.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tecendo Arte API")
                        .description("Documentação da API do projeto Tecendo Arte")
                        .version("1.0.0"));
    }
}
