package com.crowdfunding.tecendoarte.dto.DenunciaDTO;

import com.crowdfunding.tecendoarte.models.enums.StatusDenuncia;
import com.crowdfunding.tecendoarte.models.enums.TipoDenuncia;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ListarDenunciaResponseDTO {
    private Long id;
    private TipoDenuncia tipo;
    private Long idAlvo;
    private String descricao;
    private StatusDenuncia status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private String nomeAutor;
    private String emailAutor;
}
