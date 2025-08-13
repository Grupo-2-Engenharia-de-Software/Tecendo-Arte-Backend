package com.crowdfunding.tecendoarte.controllers;

import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoRequestDTO;
import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoResponseDTO;
import com.crowdfunding.tecendoarte.services.implementations.ProjetoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
public class ProjetoController {

    /*
    ATENÇÃO: Este controller utiliza a obtenção manual do id do artista para testes,
    sem autenticação JWT configurada. Após implementar autenticação do artista,
    refatorar para extrair o id do token via SecurityContext/@AuthenticationPrincipal e
    remover a notação @RequestHeader(...).
    */

    private final ProjetoService projetoService;

    @PostMapping
    public ResponseEntity<?> cadastrarProjeto(@RequestBody @Valid ProjetoRequestDTO dto,
                                              @RequestHeader("Artista-Id") Long idArtistaAutenticado) {
        try {
            ProjetoResponseDTO response = projetoService.cadastraProjeto(dto, idArtistaAutenticado);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{idProjeto}")
    public ResponseEntity<?> buscarProjetoPorId(@PathVariable Long idProjeto) {
        try {
            ProjetoResponseDTO response = projetoService.buscarPorId(idProjeto);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{idProjeto}")
    public ResponseEntity<?> atualizarProjeto(@PathVariable Long idProjeto,
                                              @RequestBody @Valid ProjetoRequestDTO dto,
                                              @RequestHeader("Artista-Id") Long idArtistaAutenticado) {
        try {
            ProjetoResponseDTO response = projetoService.atualizaProjeto(idProjeto, dto, idArtistaAutenticado);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{idProjeto}")
    public ResponseEntity<?> deletarProjeto(@PathVariable Long idProjeto,
                                            @RequestHeader("Artista-Id") Long idArtistaAutenticado) {
        try {
            projetoService.deletaProjeto(idProjeto, idArtistaAutenticado);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}