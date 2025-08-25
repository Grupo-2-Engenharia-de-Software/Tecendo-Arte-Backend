package com.crowdfunding.tecendoarte.controllers;

import org.springframework.web.bind.annotation.*;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaResponseDTO;
import com.crowdfunding.tecendoarte.services.implementations.ArtistaService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaLoginRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaLoginResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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

    @Operation(summary = "Cadastrar novo artista", description = "Cadastra um novo artista no sistema. Rota pública, não requer autenticação.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Artista cadastrado com sucesso", content = @Content(schema = @Schema(implementation = ArtistaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios não preenchidos", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody ArtistaRequestDTO dto) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(this.artistaService.cadastrarArtista(dto));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ex.getMessage());
        }
    }

    @Operation(summary = "Login de artista", description = "Realiza autenticação de artista. Rota pública, não requer autenticação.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso", content = @Content(schema = @Schema(implementation = ArtistaLoginResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios não preenchidos", content = @Content),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content),
        @ApiResponse(responseCode = "404", description = "Artista não encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody ArtistaLoginRequestDTO request) {
        try {
            ArtistaLoginResponseDTO response = artistaService.login(request);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(java.util.Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("message", "Erro interno no servidor. Tente novamente mais tarde."));
        }
    }

    @Operation(summary = "Buscar artista por nome", description = "Retorna um ou mais artistas que correspondam ao nome informado. Requer autenticação.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Artistas encontrados com sucesso", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArtistaResponseDTO.class)))),
        @ApiResponse(responseCode = "403", description = "Acesso negado - autenticação necessária", content = @Content),
        @ApiResponse(responseCode = "404", description = "Nenhum artista encontrado com o nome especificado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorNome(
            @Parameter(description = "Nome do artista a ser buscado", example = "Maria")
            @RequestParam("nome") String nome) {
        try {
            return ResponseEntity.ok(artistaService.buscarPorNome(nome));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(java.util.Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("message", "Erro interno no servidor. Tente novamente mais tarde."));
        }
    }
}
