package com.crowdfunding.tecendoarte.dto.DenunciaDTO;

import com.crowdfunding.tecendoarte.models.enums.StatusDenuncia;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnaliseDenunciaRequestDTO {
    @NotNull(message = "Resultado obrigatorio.")
    private StatusDenuncia resultado;
}
