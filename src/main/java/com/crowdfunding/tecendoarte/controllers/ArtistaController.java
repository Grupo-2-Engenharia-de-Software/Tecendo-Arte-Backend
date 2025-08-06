package com.crowdfunding.tecendoarte.controllers;

import org.springframework.web.bind.annotation.*;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaRequestDTO;
import com.crowdfunding.tecendoarte.services.implementations.ArtistaService;
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

}
