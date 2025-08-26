package com.crowdfunding.tecendoarte.controllers;

import org.springframework.web.bind.annotation.*;
import com.crowdfunding.tecendoarte.services.implementations.ArtistaService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.*;
import jakarta.persistence.EntityNotFoundException;
import io.swagger.v3.oas.annotations.*;

@Tag(name = "Artistas", description = "Operações relacionadas a artistas")
@RestController
@RequestMapping(
        value = "api/artistas",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ArtistaController {

    private final ArtistaService artistaService;

    public ArtistaController(ArtistaService artistaService) {
        this.artistaService = artistaService;
    }

    @Operation(
        summary = "Cadastrar novo artista", 
        description = "Cadastra um novo artista no sistema. Rota pública, não requer autenticação.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Artista cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios não preenchidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        }
    )
    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody ArtistaRequestDTO request) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(this.artistaService.cadastrarArtista(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @Operation(
        summary = "Login de artista", 
        description = "Realiza autenticação de artista. Rota pública, não requer autenticação.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios não preenchidos"),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody ArtistaLoginRequestDTO request) {
        try {
            ArtistaLoginResponseDTO response = this.artistaService.login(request);
            return ResponseEntity
                    .ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @Operation(
        summary = "Buscar artista por nome", 
        description = "Retorna um artista que corresponde ao nome informado. Requer autenticação.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Artista encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios não preenchidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        }
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorNome(@Valid @RequestParam String nome) {
        try {
            return ResponseEntity
                    .ok(this.artistaService.buscarPorNome(nome));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @Operation(
        summary = "Listar artistas", 
        description = "Lista todos os artistas cadastrados. Requer autenticação.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Artistas encontrados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "404", description = "Nenhum artista encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        }
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/listar")
    public ResponseEntity<?> listar() {
        try {
            return ResponseEntity
                    .ok(this.artistaService.listarArtistas());
        } catch (SecurityException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @Operation(
        summary = "Atualizar artista", 
        description = "Atualiza um artista cadastrado. Requer autenticação.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Artista atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios não preenchidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizar(@Valid @RequestParam String nome, @RequestBody ArtistaRequestDTO request) {
        try {
            return ResponseEntity
                    .ok(this.artistaService.atualizarArtista(nome, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @Operation(
        summary = "Deletar artista", 
        description = "Deleta um artista cadastrado. Requer autenticação.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Artista deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios não preenchidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        }
    )
    @DeleteMapping("/deletar")
    public ResponseEntity<?> deletar(@Valid @RequestParam String nome) {
        try {
            this.artistaService.deletarArtista(nome);
            return ResponseEntity
                    .noContent()
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
    
}
