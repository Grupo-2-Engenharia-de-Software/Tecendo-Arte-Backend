package com.crowdfunding.tecendoarte.dto.ArtistaDTO;

import java.util.List;
import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistaRequestDTO {

    @NotBlank(message = "Nome obrigatorio!")
    private String nome;

    @NotBlank(message = "Conta obrigatoria!")
    private Conta conta;

    @NotBlank(message = "Descricao obrigatoria!")
    private String descricao;

    @NotEmpty(message = "Categorias obrigatorias!")
    private List<@NotBlank String> categorias;

    private List<ProjetoResponseDTO> projetos;

    // A modelagem indica a necessidade do atributo "recompensas". 
    // Não é necessário, pois são os projetos que possuem recompensas.

}
