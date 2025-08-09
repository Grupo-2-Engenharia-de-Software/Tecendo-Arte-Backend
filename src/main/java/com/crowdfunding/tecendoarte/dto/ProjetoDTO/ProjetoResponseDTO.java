package com.crowdfunding.tecendoarte.dto.ProjetoDTO;

import java.time.LocalDate;

import com.crowdfunding.tecendoarte.models.enums.StatusProjeto;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjetoResponseDTO {

    @JsonProperty("idProjeto")
    private Long idProjeto;

    @JsonProperty("titulo")
    private String titulo;

    @JsonProperty("descricaoProjeto")
    private String descricaoProjeto;

    @JsonProperty("meta")
    private Double meta;

    @JsonProperty("valorArrecadado")
    private Double valorArrecadado;

    @JsonProperty("dataCriacao")
    private LocalDate dataCriacao;

    @JsonProperty("status")
    private StatusProjeto status;

    @JsonProperty("tipoArte")
    private TipoArte tipoArte;

    @JsonProperty("nomeArtista")
    private String nomeArtista;
}
