package com.crowdfunding.tecendoarte.dto.DenunciaDTO;

import com.crowdfunding.tecendoarte.models.enums.TipoDenuncia;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DenunciaRequestDTO {

    @JsonProperty("tipo_denuncia")
    private TipoDenuncia tipoDenuncia;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("autor_id")
    private Long autorId;

    @JsonProperty("id_alvo")
    private Long idAlvo;
}