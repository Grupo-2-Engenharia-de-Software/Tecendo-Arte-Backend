package com.crowdfunding.tecendoarte.dto.ArtistaDTO;

import java.util.List;
import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoResponseDTO;
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
    
}