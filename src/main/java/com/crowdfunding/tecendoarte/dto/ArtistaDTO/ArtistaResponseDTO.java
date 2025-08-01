package com.crowdfunding.tecendoarte.dto.ArtistaDTO;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistaResponseDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Nome de Artista Obrigatorio!")
    private String nome;

    @JsonProperty("email")
    @NotBlank(message = "Email de Artista Obrigatorio!")
    private String email;

    @JsonProperty("tiposArte")
    @NotBlank(message = "Tipos de Arte Obrigatorios!")
    private List<String> tiposArte;
    
}
