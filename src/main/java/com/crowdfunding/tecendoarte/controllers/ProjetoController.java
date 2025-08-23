package com.crowdfunding.tecendoarte.controllers;

import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoRequestDTO;
import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoResponseDTO;
import com.crowdfunding.tecendoarte.repositories.ArtistaRepository;
import com.crowdfunding.tecendoarte.services.implementations.ProjetoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
public class ProjetoController {

    private final ProjetoService projetoService;
    private final ArtistaRepository artistaRepository;

    @PostMapping
    public ResponseEntity<?> cadastrarProjeto(@RequestBody @Valid ProjetoRequestDTO dto) {
        try {
            Long idArtistaAutenticado = getIdArtistaAutenticado();
            ProjetoResponseDTO response = projetoService.cadastraProjeto(dto, idArtistaAutenticado);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{idProjeto}")
    public ResponseEntity<?> atualizarProjeto(@PathVariable Long idProjeto,
                                              @RequestBody @Valid ProjetoRequestDTO dto) {
        try {
            Long idArtistaAutenticado = getIdArtistaAutenticado();
            ProjetoResponseDTO response = projetoService.atualizaProjeto(idProjeto, dto, idArtistaAutenticado);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{idProjeto}")
    public ResponseEntity<?> deletarProjeto(@PathVariable Long idProjeto) {
        try {
            Long idArtistaAutenticado = getIdArtistaAutenticado();
            projetoService.deletaProjeto(idProjeto, idArtistaAutenticado);
            return ResponseEntity.ok(
                java.util.Map.of(
                    "message", "Projeto deletado com sucesso.",
                    "idProjeto", idProjeto
                )
            );
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(java.util.Map.of("message", e.getMessage()));
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

    // Método auxiliar para obter o id do artista autenticado
    private Long getIdArtistaAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        return artistaRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado."))
                .getId();
    }
}