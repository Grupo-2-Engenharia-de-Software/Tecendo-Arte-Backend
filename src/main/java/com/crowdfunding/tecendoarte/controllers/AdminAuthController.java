package com.crowdfunding.tecendoarte.controllers;

import com.crowdfunding.tecendoarte.dto.AdministradorDTO.AdminLoginRequestDTO;
import com.crowdfunding.tecendoarte.dto.AdministradorDTO.AdminLoginResponseDTO;
import com.crowdfunding.tecendoarte.services.interfaces.AdministradorAuthServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Tag(name = "Administradores", description = "Operações relacionadas a administradores")
@RestController
@RequestMapping(value = "/api/admin", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdministradorAuthServiceInterface administradorAuthService;

    @Operation(summary = "Login de administrador", description = "Realiza autenticação de administrador. Rota pública, não requer autenticação.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso", content = @Content(schema = @Schema(implementation = AdminLoginResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios não preenchidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content),
        @ApiResponse(responseCode = "404", description = "Administrador não encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdminLoginRequestDTO request) {
        try {
            AdminLoginResponseDTO response = administradorAuthService.login(request);
            return ResponseEntity.ok(response);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro interno no servidor. Tente novamente mais tarde."));
        }
    }
}
