package com.crowdfunding.tecendoarte.controllers;

import org.springframework.web.bind.annotation.*;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaRequestDTO;
import com.crowdfunding.tecendoarte.services.implementations.ArtistaService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaLoginRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaLoginResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

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

    @Operation(summary = "Buscar artista por nome", description = "Retorna um ou mais artistas que correspondam ao nome informado.")
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
