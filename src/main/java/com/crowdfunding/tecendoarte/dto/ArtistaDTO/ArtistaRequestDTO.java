package com.crowdfunding.tecendoarte.dto.ArtistaDTO;

import java.util.List;
import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoResponseDTO;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistaRequestDTO {

    @NotNull(message = "Id da conta obrigatorio!")
    private Long contaId;

    @NotBlank(message = "Descricao obrigatoria!")
    private String descricao;

    @NotEmpty(message = "Pelo menos uma categoria deve ser informada.")
    private List<@NotBlank String> categorias;

    private List<ProjetoResponseDTO> projetos;

}