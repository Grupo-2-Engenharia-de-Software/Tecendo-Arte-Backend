package com.crowdfunding.tecendoarte.dto.ArtistaDTO;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistaRequestDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Nome de Artista Obrigatorio!")
    private String nome;

    @JsonProperty("email")
    @NotBlank(message = "Email de Artista Obrigatorio!")
    private String email;

    @JsonProperty("senha")
    @NotBlank(message = "Senha de Artista Obrigatoria!")
    private String senha;

    @JsonProperty("confirmacaoSenha")
    @NotBlank(message = "Confirmacao de Senha de Artista Obrigatoria!")
    private String confirmacaoSenha;

    @JsonProperty("tiposArte")
    @NotEmpty(message = "Tipos de Arte Obrigatorios!")
    private List<@NotBlank String> tiposArte;

}
