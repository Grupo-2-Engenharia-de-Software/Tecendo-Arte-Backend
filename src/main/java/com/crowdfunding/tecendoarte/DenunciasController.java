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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/admin/denuncias", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DenunciasController {

    private final DenunciaServiceInterface denunciaService;

    @GetMapping
    public ResponseEntity<List<ListarDenunciaResponseDTO>> listar() {
        List<ListarDenunciaResponseDTO> resposta = denunciaService.listarTodas().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resposta);
    }

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
                .referenciaId(d.getReferenciaId())
                .descricao(d.getDescricao())
                .status(d.getStatus())
                .criadoEm(d.getCriadoEm())
                .atualizadoEm(d.getAtualizadoEm())
                .build();
    }
}
