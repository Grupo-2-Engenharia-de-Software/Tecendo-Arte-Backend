package com.crowdfunding.tecendoarte;

import com.crowdfunding.tecendoarte.dto.DenunciaDTO.AnaliseDenunciaRequestDTO;
import com.crowdfunding.tecendoarte.dto.DenunciaDTO.ListarDenunciaResponseDTO;
import com.crowdfunding.tecendoarte.models.Denuncia;
import com.crowdfunding.tecendoarte.services.interfaces.DenunciaServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Denúncias", description = "Operações relacionadas a denúncias de conteúdo")
@RestController
@RequestMapping(value = "/api/admin/denuncias", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DenunciasController {

    private final DenunciaServiceInterface denunciaService;

    @Operation(summary = "Listar denúncias", description = "Lista todas as denúncias cadastradas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Denúncias listadas com sucesso", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ListarDenunciaResponseDTO.class)))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ListarDenunciaResponseDTO>> listar() {
        List<ListarDenunciaResponseDTO> resposta = denunciaService.listarTodas().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Analisar denúncia", description = "Analisa uma denúncia e atualiza seu status.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Denúncia analisada com sucesso", content = @Content(schema = @Schema(implementation = ListarDenunciaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou campos obrigatórios não preenchidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Denúncia não encontrada", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PostMapping(path = "/{id}/analise", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ListarDenunciaResponseDTO> analisar(@PathVariable Long id,
                                                              @Valid @RequestBody AnaliseDenunciaRequestDTO request) {
        Denuncia analisada = denunciaService.analisar(id, request.getResultado());
        return ResponseEntity.ok(toResponse(analisada));
    }

    private ListarDenunciaResponseDTO toResponse(Denuncia d) {
        return ListarDenunciaResponseDTO.builder()
                .id(d.getId())
                .tipo(d.getTipo())
                .idAlvo(d.getIdAlvo())
                .descricao(d.getDescricao())
                .status(d.getStatus())
                .criadoEm(d.getCriadoEm())
                .atualizadoEm(d.getAtualizadoEm())
                .nomeAutor(d.getAutor().getNome())
                .emailAutor(d.getAutor().getEmail())
                .build();
    }
}
