package com.crowdfunding.tecendoarte.dto.AdministradorDTO;

import com.crowdfunding.tecendoarte.models.enums.PermissaoAdministrador;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLoginResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private Set<PermissaoAdministrador> permissoes;
}
