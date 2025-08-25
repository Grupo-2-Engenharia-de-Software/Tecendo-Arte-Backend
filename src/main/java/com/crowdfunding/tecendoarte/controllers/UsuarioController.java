package com.crowdfunding.tecendoarte.controllers;

import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioRequestDTO;
import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioResponseDTO;
import com.crowdfunding.tecendoarte.services.implementations.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuario", description = "Gerenciamento de usuarios no sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Criar usuario", description = "Cadastra um novo usuario no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuaario criado com sucesso", content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados invalidos ou usuario ja cadastrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody @Valid UsuarioRequestDTO dto) {
        try {
            UsuarioResponseDTO response = usuarioService.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Buscar usuario por ID", description = "Recupera os dados de um usuario pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario nao encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            UsuarioResponseDTO response = usuarioService.buscarPorId(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Atualizar usuario", description = "Atualiza os dados de um usuario existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario atualizado com sucesso", content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario nao encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioRequestDTO dto) {
        try {
            UsuarioResponseDTO response = usuarioService.atualizar(id, dto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Deletar usuario", description = "Remove um usuario existente pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuario nao encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            usuarioService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
