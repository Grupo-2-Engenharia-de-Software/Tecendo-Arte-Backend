package com.crowdfunding.tecendoarte.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Tag(name = "Home", description = "Operações básicas do sistema")
@RestController
public class HomeController {

    @Operation(
        summary = "Página inicial", 
        description = "Endpoint de teste para verificar se o sistema está funcionando. Rota pública, não requer autenticação.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Sistema funcionando normalmente")
        }
    )
    @GetMapping("/")
    public String home() {
        return "Olá, Spring Boot está funcionando!";
    }
}
