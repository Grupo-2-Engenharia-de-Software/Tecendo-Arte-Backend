package com.crowdfunding.tecendoarte.dto.ArtistaDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistaLoginRequestDTO {
    @NotBlank(message = "Email obrigatorio.")
    @Email(message = "Email invalido.")
    private String email;

    @NotBlank(message = "Senha obrigatoria.")
    private String senha;
} 