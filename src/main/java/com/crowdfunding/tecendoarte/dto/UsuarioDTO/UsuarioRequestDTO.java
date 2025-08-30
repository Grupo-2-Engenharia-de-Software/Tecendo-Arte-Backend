package com.crowdfunding.tecendoarte.dto.UsuarioDTO;

import lombok.Data;
import java.util.List;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import jakarta.validation.constraints.NotNull;

@Data
public class UsuarioRequestDTO {
    @NotNull
    private Long contaId;

    private List<TipoArte> interesses;
}
