package com.crowdfunding.tecendoarte.controllers;

import org.springframework.web.bind.annotation.*;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaRequestDTO;
import com.crowdfunding.tecendoarte.dto.ContaDTO.*;
import com.crowdfunding.tecendoarte.services.implementations.ContaService;

import jakarta.validation.Valid;
import org.springframework.http.*;

@RestController
@RequestMapping(value = "/api/conta", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContaController {
    private final ContaService contaService;

    public ContaController(ContaService contaService){
        this.contaService = contaService;
    }

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

@PutMapping("/{id}")
    public ResponseEntity<?> atualizarConta(@PathVariable Long id, @Valid @RequestBody ContaRequestDTO contaDTO) {
        try {
            ContaResponseDTO response = contaService.atualizarConta(id, contaDTO);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirConta(@PathVariable Long id) {
        try {
            contaService.excluirConta(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

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
