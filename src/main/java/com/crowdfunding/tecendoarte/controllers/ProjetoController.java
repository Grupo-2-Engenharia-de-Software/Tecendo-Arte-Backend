package com.crowdfunding.tecendoarte.controllers;

import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoRequestDTO;
import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoResponseDTO;
import com.crowdfunding.tecendoarte.services.implementations.ProjetoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(name = "Projetos", description = "Operações relacionadas a projetos")
@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
public class ProjetoController {

    private final ProjetoService projetoService;

    @Operation(
        summary = "Cadastrar novo projeto",
        description = "Cria um novo projeto para o artista autenticado.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Projeto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios não preenchidos"),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<?> cadastrarProjeto(@RequestBody @Valid ProjetoRequestDTO dto) {
        try {
            Long idArtistaAutenticado = projetoService.getIdArtistaAutenticado();
            ProjetoResponseDTO response = projetoService.cadastraProjeto(dto, idArtistaAutenticado);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Buscar projeto por ID",
        description = "Busca um projeto específico pelo ID.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Projeto encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        }
    )
    @GetMapping("/{idProjeto}")
    public ResponseEntity<?> buscarProjetoPorId(@PathVariable Long idProjeto) {
        try {
            ProjetoResponseDTO response = projetoService.buscarPorId(idProjeto);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Atualizar projeto",
        description = "Atualiza os dados de um projeto existente do artista autenticado.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Projeto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios não preenchidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - projeto não pertence ao artista autenticado"),
            @ApiResponse(responseCode = "404", description = "Projeto ou artista não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{idProjeto}")
    public ResponseEntity<?> atualizarProjeto(@PathVariable Long idProjeto,
                                              @RequestBody @Valid ProjetoRequestDTO dto) {
        try {
            Long idArtistaAutenticado = projetoService.getIdArtistaAutenticado();
            ProjetoResponseDTO response = projetoService.atualizaProjeto(idProjeto, dto, idArtistaAutenticado);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Deletar projeto",
        description = "Remove um projeto do artista autenticado.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Projeto deletado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - projeto não pertence ao artista autenticado"),
            @ApiResponse(responseCode = "404", description = "Projeto ou artista não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        }
    )
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{idProjeto}")
    public ResponseEntity<?> deletarProjeto(@PathVariable Long idProjeto) {
        try {
            Long idArtistaAutenticado = projetoService.getIdArtistaAutenticado();
            projetoService.deletaProjeto(idProjeto, idArtistaAutenticado);
            return ResponseEntity.ok(
                java.util.Map.of(
                    "message", "Projeto deletado com sucesso.",
                    "idProjeto", idProjeto
                )
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(java.util.Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @Operation(
    summary = "Consultar projetos de um artista pelo nome",
    description = "Permite ao público geral consultar todos os projetos de um artista pelo nome.",
    responses = {
        @ApiResponse(responseCode = "200", description = "Lista de projetos do artista"),
        @ApiResponse(responseCode = "404", description = "Artista não encontrado")
    }
    )
    @GetMapping("/consultar-por-artista")
    public ResponseEntity<?> consultarProjetosPorArtista(@RequestParam String nomeArtista) {
        try {
            List<ProjetoResponseDTO> projetos = projetoService.consultarProjetosPorArtista(nomeArtista);
            return ResponseEntity.ok(projetos);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("message", "Artista não encontrado"));
        }
    }
}