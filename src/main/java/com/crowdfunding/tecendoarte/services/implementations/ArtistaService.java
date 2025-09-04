package com.crowdfunding.tecendoarte.services.implementations;

import org.springframework.stereotype.Service;
import com.crowdfunding.tecendoarte.models.Artista;
import com.crowdfunding.tecendoarte.models.Conta;
import com.crowdfunding.tecendoarte.models.Imagem;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.crowdfunding.tecendoarte.repositories.ArtistaRepository;
import com.crowdfunding.tecendoarte.repositories.ContaRepository;
import com.crowdfunding.tecendoarte.services.interfaces.ArtistaServiceInterface;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.*;
import com.crowdfunding.tecendoarte.dto.ImagemDTO.ImagemRequestDTO;
import com.crowdfunding.tecendoarte.dto.ImagemDTO.ImagemResponseDTO;
import com.crowdfunding.tecendoarte.config.JwtUtil;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArtistaService implements ArtistaServiceInterface {

    private final ArtistaRepository artistaRepository;
    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public ArtistaResponseDTO cadastrarArtista(ArtistaRequestDTO request) {
        Conta conta = contaRepository.findById(request.getContaId())
                .orElseThrow(() -> new EntityNotFoundException("Conta nao encontrada"));

        this.artistaRepository.findByContaId(request.getContaId()).ifPresent(artista -> {
            throw new IllegalArgumentException("Artista com esta conta já cadastrado.");
        });

        List<TipoArte> categorias = this.parseTiposArte(request.getCategorias());

        Artista artista = Artista.builder()
                .conta(conta)
                .descricao(request.getDescricao())
                .categorias(categorias)
                .projetos(List.of())
                .build();

        Artista salvo = this.artistaRepository.save(artista);

        return ArtistaResponseDTO.builder()
                .nome(conta.getNome())
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

        if (!this.passwordEncoder.matches(request.getSenha(), artista.getConta().getSenha())) {
            throw new IllegalArgumentException("Senha inválida.");
        }

        Conta conta = artista.getConta();

        String token = this.jwtUtil.generateTokenForArtista(artista.getId(), artista.getConta().getEmail());

        return ArtistaLoginResponseDTO.builder()
                .nome(conta.getNome())
                .email(artista.getConta().getEmail())
                .token(token)
                .build();
    }

    @Override
    public ArtistaResponseDTO buscarPorNome(String nome) {

        if (this.isBlank(nome)) {
            throw new IllegalArgumentException("Nome para busca obrigatório.");
        }

        Artista artista = this.artistaRepository.findByContaNomeContainingIgnoreCase(nome)
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado."));

        Conta conta = artista.getConta();

        return ArtistaResponseDTO.builder()
                .nome(conta.getNome())
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
                        .nome(artista.getConta().getNome())
                        .descricao(artista.getDescricao())
                        .categorias(artista.getCategorias().stream().map(TipoArte::name).collect(Collectors.toList()))
                        .projetos(List.of())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ArtistaResponseDTO atualizarArtista(String nome, ArtistaRequestDTO request) {

        Artista artista = this.artistaRepository.findByContaNomeContainingIgnoreCase(nome)
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado."));

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
                .nome(atualizado.getConta().getNome())
                .descricao(atualizado.getDescricao())
                .categorias(atualizado.getCategorias().stream().map(TipoArte::name).collect(Collectors.toList()))
                .projetos(List.of())
                .build();
    }

    public void deletarArtista(String nome) {

        if (this.isBlank(nome)) {
            throw new IllegalArgumentException("Nome obrigatório.");
        }

        Artista artista = this.artistaRepository.findByContaNomeContainingIgnoreCase(nome)
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

    @Transactional
    public void adicionarImagensAoPerfil(Long idArtista, List<ImagemRequestDTO> imagensDto) {
        Artista artista = artistaRepository.findById(idArtista)
            .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado."));
        for (ImagemRequestDTO dto : imagensDto) {
            byte[] dadosImagem = Base64.getDecoder().decode(dto.getDadosImagemBase64());
            Imagem imagem = Imagem.builder()
                .dadosImagem(dadosImagem)
                .descricao(dto.getDescricao())
                .artista(artista)
                .build();
            artista.getImagensPortifolio().add(imagem);
        }
        artistaRepository.save(artista);
    }

    @Transactional(readOnly = true)
    public List<ImagemResponseDTO> listarImagensPortifolio(Long idArtista) {
        Artista artista = artistaRepository.findById(idArtista)
            .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado."));
        return artista.getImagensPortifolio().stream()
            .map(imagem -> ImagemResponseDTO.builder()
                .idImagem(imagem.getIdImagem())
                .dadosImagemBase64(Base64.getEncoder().encodeToString(imagem.getDadosImagem())) // CORRIGIDO AQUI
                .descricao(imagem.getDescricao())
                .projetoId(imagem.getProjeto() != null ? imagem.getProjeto().getIdProjeto() : null)
                .build())
            .toList();
    }

    public Long getIdArtistaAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        return artistaRepository.findByContaEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado."))
                .getId();
    }
}