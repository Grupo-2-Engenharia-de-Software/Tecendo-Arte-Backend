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
public class ArtistaResponseDTO {

    private String nome;
    private String descricao;
    private List<String> categorias;
    private List<ProjetoResponseDTO> projetos;

    // A modelagem indica a necessidade do atributo "recompensas". 
    // Não é necessário, pois são os projetos que possuem recompensas.
    
}
