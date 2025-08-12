package com.crowdfunding.tecendoarte.services.implementations;

import com.crowdfunding.tecendoarte.config.JwtUtil;
import com.crowdfunding.tecendoarte.dto.AdministradorDTO.AdminLoginRequestDTO;
import com.crowdfunding.tecendoarte.dto.AdministradorDTO.AdminLoginResponseDTO;
import com.crowdfunding.tecendoarte.models.Administrador;
import com.crowdfunding.tecendoarte.repositories.AdministradorRepository;
import com.crowdfunding.tecendoarte.services.interfaces.AdministradorAuthServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class AdministradorAuthService implements AdministradorAuthServiceInterface {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AdminLoginResponseDTO login(AdminLoginRequestDTO request) {
        Administrador administrador = administradorRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Administrador não encontrado."));

        if (!passwordEncoder.matches(request.getSenha(), administrador.getSenha())) {
            throw new IllegalArgumentException("Senha inválida.");
        }

        String token = jwtUtil.generateTokenForAdmin(administrador.getId(), administrador.getEmail());

        return AdminLoginResponseDTO.builder()
                .id(administrador.getId())
                .nome(administrador.getNome())
                .email(administrador.getEmail())
                .permissoes(administrador.getPermissoes())
                .token(token)
                .build();
    }
}
