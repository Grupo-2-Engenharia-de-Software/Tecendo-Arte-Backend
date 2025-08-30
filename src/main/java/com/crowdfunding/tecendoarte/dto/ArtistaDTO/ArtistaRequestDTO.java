package com.crowdfunding.tecendoarte.dto.ArtistaDTO;

import java.util.List;
import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoResponseDTO;
import com.crowdfunding.tecendoarte.models.Conta;
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

}