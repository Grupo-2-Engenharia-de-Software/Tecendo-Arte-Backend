package com.crowdfunding.tecendoarte.dto.DenunciaDTO;

import com.crowdfunding.tecendoarte.models.enums.StatusDenuncia;
import com.crowdfunding.tecendoarte.models.enums.TipoDenuncia;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DenunciaResponseDTO {

    @JsonProperty("id_denuncia")
    private Long idDenuncia;

    @JsonProperty("tipo_denuncia")
    private TipoDenuncia tipoDenuncia;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("status_denuncia")
    private StatusDenuncia statusDenuncia;

    @Builder.Default
    @JsonProperty("autor")
    private String autor = "Anônimo";

    @JsonProperty("id_alvo")
    private Long idAlvo;
}