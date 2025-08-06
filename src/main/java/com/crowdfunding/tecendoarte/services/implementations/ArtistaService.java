package com.crowdfunding.tecendoarte.services.implementations;

import org.springframework.stereotype.Service;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.*;
import com.crowdfunding.tecendoarte.models.Artista;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.crowdfunding.tecendoarte.repositories.ArtistaRepository;
import com.crowdfunding.tecendoarte.services.interfaces.ArtistaServiceInterface;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtistaService implements ArtistaServiceInterface {

    private final ArtistaRepository artistaRepository;
    
    public ArtistaService(ArtistaRepository artistaRepository) {
        this.artistaRepository = artistaRepository;
    }

    public Artista cadastrarArtista(ArtistaRequestDTO dto) {

        if (!dto.getSenha().equals(dto.getConfirmacaoSenha())) {
            throw new IllegalArgumentException("As senhas não coincidem. Tente novamente.");
        }

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
                .senha(dto.getSenha())
                .confirmacaoSenha(dto.getConfirmacaoSenha())
                .tiposArte(tiposArte)
                .build();

        return this.artistaRepository.save(artista);
    }
    
}
