package com.crowdfunding.tecendoarte.services.implementations;

import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoRequestDTO;
import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoResponseDTO;
import com.crowdfunding.tecendoarte.models.Artista;
import com.crowdfunding.tecendoarte.models.Projeto;
import com.crowdfunding.tecendoarte.models.enums.StatusProjeto;
import com.crowdfunding.tecendoarte.repositories.ArtistaRepository;
import com.crowdfunding.tecendoarte.repositories.ProjetoRepository;
import com.crowdfunding.tecendoarte.services.interfaces.ProjetoServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProjetoService implements ProjetoServiceInterface {

    private final ProjetoRepository projetoRepository;
    private final ArtistaRepository artistaRepository;

    @Transactional
    public ProjetoResponseDTO cadastraProjeto(ProjetoRequestDTO dto, Long idArtistaAutenticado) {
        Artista artista = artistaRepository.findById(idArtistaAutenticado)
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado."));

        if (dto.getTitulo() == null || dto.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Título do projeto é obrigatório.");
        }
        if (dto.getDescricaoProjeto() == null || dto.getDescricaoProjeto().isBlank()) {
            throw new IllegalArgumentException("Descrição do projeto é obrigatória.");
        }
        if (dto.getMeta() == null || dto.getMeta() <= 0) {
            throw new IllegalArgumentException("Meta do projeto deve ser maior que zero.");
        }
        if (dto.getTipoArte() == null) {
            throw new IllegalArgumentException("Tipo de arte é obrigatório.");
        }

        Projeto projeto = Projeto.builder()
                .titulo(dto.getTitulo())
                .descricaoProjeto(dto.getDescricaoProjeto())
                .meta(dto.getMeta())
                .tipoArte(dto.getTipoArte())
                .artista(artista)
                .status(StatusProjeto.AGUARDANDO_AVALIACAO)
                .dataCriacao(LocalDate.now())
                .valorArrecadado(0.0)
                .build();

        projeto = projetoRepository.save(projeto);

        return ProjetoResponseDTO.builder()
                .idProjeto(projeto.getIdProjeto())
                .titulo(projeto.getTitulo())
                .descricaoProjeto(projeto.getDescricaoProjeto())
                .meta(projeto.getMeta())
                .valorArrecadado(projeto.getValorArrecadado())
                .dataCriacao(projeto.getDataCriacao())
                .status(projeto.getStatus())
                .tipoArte(projeto.getTipoArte())
                .nomeArtista(artista.getNome())
                .build();
    }

    @Transactional
    public ProjetoResponseDTO deletaProjeto(Long idProjeto, Long idArtistaAutenticado) {
        Artista artista = artistaRepository.findById(idArtistaAutenticado)
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado."));

        Projeto projeto = projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado."));

        if (!projeto.getArtista().getId().equals(idArtistaAutenticado)) {
            throw new IllegalArgumentException("Você só pode deletar projetos do seu próprio usuário.");
        }

        projetoRepository.delete(projeto);

        return ProjetoResponseDTO.builder()
                .idProjeto(projeto.getIdProjeto())
                .titulo(projeto.getTitulo())
                .descricaoProjeto(projeto.getDescricaoProjeto())
                .meta(projeto.getMeta())
                .valorArrecadado(projeto.getValorArrecadado())
                .dataCriacao(projeto.getDataCriacao())
                .status(projeto.getStatus())
                .tipoArte(projeto.getTipoArte())
                .nomeArtista(artista.getNome())
                .build();
    }

    @Transactional
    public ProjetoResponseDTO atualizaProjeto(Long idProjeto, ProjetoRequestDTO dto, Long idArtistaAutenticado) {
        Artista artista = artistaRepository.findById(idArtistaAutenticado)
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado."));

        Projeto projeto = projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado."));

        if (!projeto.getArtista().getId().equals(idArtistaAutenticado)) {
            throw new IllegalArgumentException("Você só pode atualizar projetos do seu próprio usuário.");
        }
        if (dto.getTitulo() != null && !dto.getTitulo().isBlank()) {
            projeto.setTitulo(dto.getTitulo());
        }
        if (dto.getDescricaoProjeto() != null && !dto.getDescricaoProjeto().isBlank()) {
            projeto.setDescricaoProjeto(dto.getDescricaoProjeto());
        }
        if (dto.getMeta() != null && dto.getMeta() > 0) {
            projeto.setMeta(dto.getMeta());
        }
        if (dto.getTipoArte() != null) {
            projeto.setTipoArte(dto.getTipoArte());
        }

        projeto = projetoRepository.save(projeto);

        return ProjetoResponseDTO.builder()
                .idProjeto(projeto.getIdProjeto())
                .titulo(projeto.getTitulo())
                .descricaoProjeto(projeto.getDescricaoProjeto())
                .meta(projeto.getMeta())
                .valorArrecadado(projeto.getValorArrecadado())
                .dataCriacao(projeto.getDataCriacao())
                .status(projeto.getStatus())
                .tipoArte(projeto.getTipoArte())
                .nomeArtista(artista.getNome())
                .build();
    }
}