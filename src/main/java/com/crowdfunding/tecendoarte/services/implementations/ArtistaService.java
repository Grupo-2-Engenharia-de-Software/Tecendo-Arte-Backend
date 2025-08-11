package com.crowdfunding.tecendoarte.services.implementations;

import org.springframework.stereotype.Service;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.*;
import com.crowdfunding.tecendoarte.models.Artista;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.crowdfunding.tecendoarte.repositories.ArtistaRepository;
import com.crowdfunding.tecendoarte.services.interfaces.ArtistaServiceInterface;
import jakarta.persistence.EntityNotFoundException;
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

    public ArtistaResponseDTO consultarArtista(String nome) {
        Artista artista = this.artistaRepository.findByNome(nome)
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado com nome: " + nome));
        return ArtistaResponseDTO.builder()
                .nome(artista.getNome())
                .email(artista.getEmail())
                .tiposArte(artista.getTiposArte().stream()
                    .map(TipoArte::name)
                    .collect(Collectors.toList()))
                .build();
    };

    public List<ArtistaResponseDTO> listarArtistas() {
        List<ArtistaResponseDTO> artistas = this.artistaRepository.findAll()
                .stream()
                .map(artista -> ArtistaResponseDTO.builder()
                .nome(artista.getNome())
                .email(artista.getEmail())
                .tiposArte(artista.getTiposArte().stream()
                    .map(TipoArte::name)
                    .collect(Collectors.toList()))
                .build()
                )
                .collect(Collectors.toList());

        if (artistas.isEmpty()) {
            throw new EntityNotFoundException("Nenhum artista encontrado.");
        }

        return artistas;
    }

    public Artista atualizarArtista(String nome, ArtistaRequestDTO dto) {
        Artista artista = this.artistaRepository.findByNome(nome)
            .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado com nome: " + nome));

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            artista.setNome(dto.getNome());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(artista.getEmail())) {
            boolean emailRepetido = this.artistaRepository.existsByEmail(dto.getEmail());
            if (emailRepetido) {
                throw new IllegalArgumentException("O e-mail informado já está em uso.");
            }
            artista.setEmail(dto.getEmail());
        }

        if (!dto.getSenha().equals(dto.getConfirmacaoSenha())) {
            throw new IllegalArgumentException("As senhas não coincidem. Tente novamente.");
        }

        List<TipoArte> tiposArte = dto.getTiposArte().stream()
                .map(tipo -> {
                    try {
                        return TipoArte.valueOf(tipo.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Tipo de arte inválido: " + tipo);
                    }
                })
                .collect(Collectors.toList());

        if (tiposArte != null && !tiposArte.isEmpty()) {
            artista.setTiposArte(tiposArte);
        }

        return this.artistaRepository.save(artista);
    }

}
