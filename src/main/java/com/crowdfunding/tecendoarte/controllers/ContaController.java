package com.crowdfunding.tecendoarte.controllers;

import org.springframework.web.bind.annotation.*;
import com.crowdfunding.tecendoarte.dto.ContaDTO.*;
import com.crowdfunding.tecendoarte.services.implementations.ContaService;

import jakarta.validation.Valid;
import org.springframework.http.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(name = "Contas", description = "Operações relacionadas a contas")
@RestController
@RequestMapping(value = "/api/conta", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Conta", description = "Gerenciamento de contas de usuários no sistema")
public class ContaController {
    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @Operation(summary = "Cadastrar nova conta", description = "Cadastra uma nova conta no sistema. Rota pública, não requer autenticação.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Conta cadastrada com sucesso", content = @Content(schema = @Schema(implementation = ContaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios não preenchidos", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody ContaRequestDTO contaDTO) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(this.contaService.cadastrar(contaDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno no servidor. Tente novamente mais tarde.");
        }
    }

    @Operation(summary = "Atualizar conta", description = "Atualiza os dados de uma conta existente. Requer autenticação.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso", content = @Content(schema = @Schema(implementation = ContaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios não preenchidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado - autenticação necessária", content = @Content),
        @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarConta(@PathVariable Long id, @Valid @RequestBody ContaRequestDTO contaDTO) {
        try {
            ContaResponseDTO response = contaService.atualizarConta(id, contaDTO);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Excluir conta", description = "Remove uma conta do sistema. Requer autenticação.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Conta excluída com sucesso", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acesso negado - autenticação necessária", content = @Content),
        @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirConta(@PathVariable Long id) {
        try {
            contaService.excluirConta(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Buscar conta por ID", description = "Busca uma conta específica pelo ID. Requer autenticação.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conta encontrada com sucesso", content = @Content(schema = @Schema(implementation = ContaResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Acesso negado - autenticação necessária", content = @Content),
        @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarConta(@PathVariable Long id) {
        try {
            ContaResponseDTO conta = contaService.buscarPorId(id);
            return ResponseEntity.ok(conta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
