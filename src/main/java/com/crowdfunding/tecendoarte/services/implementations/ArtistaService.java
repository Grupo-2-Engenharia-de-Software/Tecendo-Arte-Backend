package com.crowdfunding.tecendoarte.services.implementations;

import org.springframework.stereotype.Service;
import com.crowdfunding.tecendoarte.models.Artista;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.crowdfunding.tecendoarte.repositories.ArtistaRepository;
import com.crowdfunding.tecendoarte.services.interfaces.ArtistaServiceInterface;
import java.util.List;
import java.util.stream.Collectors;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.*;
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

    @Override
    public ArtistaResponseDTO cadastrarArtista(ArtistaRequestDTO request) {

        if (request == null) {
            throw new IllegalArgumentException("Requisição não pode ser nula.");
        }
        if (this.isBlank(request.getNome())) {
            throw new IllegalArgumentException("Nome obrigatório.");
        }
        if (this.isBlank(request.getConta()) || isBlank(request.getConta().getEmail()) || isBlank(request.getConta().getSenha())) {
            throw new IllegalArgumentException("Dados da conta obrigatórios.");
        }
        if (this.isBlank(request.getDescricao())) {
            throw new IllegalArgumentException("Descrição obrigatória.");
        }
        if (this.isBlank(request.getCategorias())) {
            throw new IllegalArgumentException("Pelo menos uma categoria deve ser informada.");
        }
        this.artistaRepository.findByNomeContainingIgnoreCase(request.getNome()).ifPresent(artista -> {
            throw new IllegalArgumentException("Artista com este nome já cadastrado.");
        });
        this.artistaRepository.findByContaEmail(request.getEmail()).ifPresent(artista -> {
            throw new IllegalArgumentException("Artista com este e-mail já cadastrado.");
        });

        List<TipoArte> categorias = this.parseTiposArte(request.getCategorias());
        
        Artista artista = Artista.builder()
                .nome(request.getNome())
                .conta(request.getConta(this.passwordEncoder))
                .descricao(request.getDescricao())
                .categorias(categorias)
                .projetos(List.of())
                .build();

        Artista salvo = this.artistaRepository.save(artista);

        return ArtistaResponseDTO.builder()
                .nome(salvo.getNome())
                .descricao(salvo.getDescricao())
                .categorias(salvo.getCategorias().stream().map(TipoArte::name).collect(Collectors.toList()))
                .projetos(List.of())
                .build();
    }
    
    @Override
    public ArtistaLoginResponseDTO login(ArtistaLoginRequestDTO request) {

        if (request == null || this.isBlank(request.getEmail()) || this.isBlank(request.getSenha())) {
            throw new IllegalArgumentException("E-mail e senha obrigatórios.");
        }

        Artista artista = this.artistaRepository.findByContaEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado."));

        if (!this.passwordEncoder.matches(request.getSenha(), artista.getSenha())) {
            throw new IllegalArgumentException("Senha inválida.");
        }

        String token = this.jwtUtil.generateTokenForArtista(artista.getId(), artista.getEmail());

        return ArtistaLoginResponseDTO.builder()
                .nome(artista.getNome())
                .email(artista.getEmail())
                .token(token)
                .build();
    }

    @Override
    public ArtistaResponseDTO buscarPorNome(String nome) {

        if (this.isBlank(nome)) {
            throw new IllegalArgumentException("Nome para busca obrigatório.");
        }

        Artista artista = this.artistaRepository.findByNomeIgnoreCase(nome)
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado."));

        return ArtistaResponseDTO.builder()
                .nome(artista.getNome())
                .descricao(artista.getDescricao())
                .categorias(artista.getCategorias().stream().map(TipoArte::name).collect(Collectors.toList()))
                .projetos(List.of())
                .build();
    }

    @Override
    public List<ArtistaResponseDTO> listarArtistas() {

        List<Artista> artistas = this.artistaRepository.findAll();

        if (artistas.isEmpty()) {
            throw new EntityNotFoundException("Nenhum artista encontrado.");
        }
        
        return artistas.stream()
                .map(artista -> ArtistaResponseDTO.builder()
                        .nome(artista.getNome())
                        .descricao(artista.getDescricao())
                        .categorias(artista.getCategorias().stream().map(TipoArte::name).collect(Collectors.toList()))
                        .projetos(List.of())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ArtistaResponseDTO atualizarArtista(String nome, ArtistaRequestDTO request) {

        if (this.isBlank(nome)) {
            throw new IllegalArgumentException("Nome para busca obrigatório.");
        }
        if (request == null) {
            throw new IllegalArgumentException("Requisição não pode ser nula.");
        }
        if (this.isBlank(request.getNome())) {
            throw new IllegalArgumentException("Nome obrigatório.");
        }
        if (this.isBlank(request.getConta()) || isBlank(request.getConta().getEmail()) || isBlank(request.getConta().getSenha())) {
            throw new IllegalArgumentException("Dados da conta obrigatórios.");
        }
        this.artistaRepository.findByNomeContainingIgnoreCase(request.getNome()).ifPresent(artista -> {
            throw new IllegalArgumentException("Artista com este nome já cadastrado.");
        });
        this.artistaRepository.findByContaEmail(request.getEmail()).ifPresent(artista -> {
            throw new IllegalArgumentException("Artista com este e-mail já cadastrado.");
        });

        Artista artista = this.artistaRepository.findByNomeIgnoreCase(nome)
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado."));

        artista.setNome(request.getNome());

        if (!this.isBlank(request.getDescricao())) {
            artista.setDescricao(request.getDescricao());
        }
        if (request.getCategorias() != null && !request.getCategorias().isEmpty()) {
            artista.setCategorias(this.parseTiposArte(request.getCategorias()));
        }
        if (request.getProjetos() != null && !request.getProjetos().isEmpty()) {
            artista.setProjetos(List.of());
        }

        Artista atualizado = this.artistaRepository.save(artista);

        return ArtistaResponseDTO.builder()
                .nome(atualizado.getNome())
                .descricao(atualizado.getDescricao())
                .categorias(atualizado.getCategorias().stream().map(TipoArte::name).collect(Collectors.toList()))
                .projetos(List.of())
                .build();
    }

    public void deletarArtista(String nome) {

        if (this.isBlank(nome)) {
            throw new IllegalArgumentException("Nome obrigatório.");
        }

        Artista artista = this.artistaRepository.findByNomeIgnoreCase(nome)
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado."));

        this.artistaRepository.delete(artista);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private List<TipoArte> parseTiposArte(List<String> tipos) {
        if (tipos == null || tipos.isEmpty()) {
            throw new IllegalArgumentException("Pelo menos um tipo de arte deve ser informado.");
        }
        try {
            return tipos.stream()
                    .map(t -> TipoArte.valueOf(t.trim().toUpperCase()))
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Tipo de arte inválido.", ex);
        }
    }

}
