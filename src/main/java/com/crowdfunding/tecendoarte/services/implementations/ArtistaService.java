package com.crowdfunding.tecendoarte.services.implementations;

import org.springframework.stereotype.Service;
import com.crowdfunding.tecendoarte.models.Artista;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.crowdfunding.tecendoarte.repositories.ArtistaRepository;
import com.crowdfunding.tecendoarte.services.interfaces.ArtistaServiceInterface;
import java.util.List;
import java.util.stream.Collectors;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaLoginRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaLoginResponseDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaResponseDTO;
import com.crowdfunding.tecendoarte.config.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ArtistaService implements ArtistaServiceInterface {

    private final ArtistaRepository artistaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public ArtistaService(ArtistaRepository artistaRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.artistaRepository = artistaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public ArtistaResponseDTO cadastrarArtista(ArtistaRequestDTO dto) {

        artistaRepository.findByEmail(dto.getEmail()).ifPresent(artista -> {
            throw new IllegalArgumentException("Artista com este e-mail já cadastrado.");
        });

        List<TipoArte> tiposArte = dto.getTiposArte().stream()
                .map(tipo -> {
                    try {
                        return TipoArte.valueOf(tipo.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Tipo de arte inválido: " + tipo);
                    }
                })
                .collect(Collectors.toList());

        Artista artista = Artista.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .tiposArte(tiposArte)
                .build();

        Artista salvo = this.artistaRepository.save(artista);
        return ArtistaResponseDTO.builder()
                .nome(salvo.getNome())
                .email(salvo.getEmail())
                .tiposArte(salvo.getTiposArte().stream().map(TipoArte::name).collect(Collectors.toList()))
                .build();
    }
    
    @Override
    public ArtistaLoginResponseDTO login(ArtistaLoginRequestDTO request) {
        Artista artista = artistaRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado."));

        if (!passwordEncoder.matches(request.getSenha(), artista.getSenha())) {
            throw new IllegalArgumentException("Senha inválida.");
        }

        String token = jwtUtil.generateTokenForArtista(artista.getId(), artista.getEmail());

        return ArtistaLoginResponseDTO.builder()
                .id(artista.getId())
                .nome(artista.getNome())
                .email(artista.getEmail())
                .token(token)
                .build();
    }
}
