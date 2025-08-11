package com.crowdfunding.tecendoarte.dto.ProjetoDTO;

import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjetoRequestDTO {

    @JsonProperty("titulo")
    private String titulo;

    @JsonProperty("descricaoProjeto")
    private String descricaoProjeto;

    @JsonProperty("meta")
    private Double meta;

    @JsonProperty("tipoArte")
    private TipoArte tipoArte;
}