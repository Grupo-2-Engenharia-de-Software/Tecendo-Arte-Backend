package com.crowdfunding.tecendoarte.dto.ArtistaDTO;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistaLoginRequestDTO {

    @NotBlank(message = "Email obrigatorio.")
    @Email(message = "Email invalido.")
    private String email;

    @NotBlank(message = "Senha obrigatoria.")
    private String senha;

}