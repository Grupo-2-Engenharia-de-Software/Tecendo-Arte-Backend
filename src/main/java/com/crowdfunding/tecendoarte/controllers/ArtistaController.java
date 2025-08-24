package com.crowdfunding.tecendoarte.controllers;

import org.springframework.web.bind.annotation.*;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaRequestDTO;
import com.crowdfunding.tecendoarte.services.implementations.ArtistaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.*;

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

    @GetMapping("/{nome}")
    public ResponseEntity<?> consultar(@Valid @PathVariable String nome) {
        try {
            return ResponseEntity
                    .ok(this.artistaService.consultarArtista(nome));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            return ResponseEntity
                    .ok(this.artistaService.listarArtistas());
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PutMapping("/{nome}")
    public ResponseEntity<?> atualizar(@Valid @PathVariable String nome, @RequestBody ArtistaRequestDTO dto) {
        try {
            return ResponseEntity
                    .ok(this.artistaService.atualizarArtista(nome, dto));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }

    @DeleteMapping("/{nome}")
    public ResponseEntity<?> deletar(@Valid @PathVariable String nome) {
        try {
            this.artistaService.deletarArtista(nome);
            return ResponseEntity
                    .noContent()
                    .build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        }
    }

}
