package com.crowdfunding.tecendoarte.dto.ProjetoDTO;

import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjetoRequestDTO {

    @JsonProperty("titulo")
    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @JsonProperty("descricaoProjeto")
    @NotBlank(message = "A descrição do projeto é obrigatória")
    private String descricaoProjeto;

    @JsonProperty("meta")
    @NotNull(message = "A meta é obrigatória")
    @Positive(message = "A meta deve ser maior que zero")
    private Double meta;

    @JsonProperty("tipoArte")
    @NotNull(message = "O tipo de arte é obrigatório")
    private TipoArte tipoArte;
}