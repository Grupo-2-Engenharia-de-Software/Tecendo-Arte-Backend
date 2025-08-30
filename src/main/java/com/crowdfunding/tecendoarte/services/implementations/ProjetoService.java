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
        return toResponseDTO(projeto);
    }

    @Transactional
    public ProjetoResponseDTO deletaProjeto(Long idProjeto, Long idArtistaAutenticado) {
        Projeto projeto = buscarProjetoPorId(idProjeto);

        if (!projeto.getArtista().getId().equals(idArtistaAutenticado)) {
            throw new IllegalArgumentException("Você só pode deletar projetos do seu próprio usuário.");
        }

        projetoRepository.delete(projeto);
        return toResponseDTO(projeto);
    }

    @Transactional
    public ProjetoResponseDTO atualizaProjeto(Long idProjeto, ProjetoRequestDTO dto, Long idArtistaAutenticado) {
        Projeto projeto = buscarProjetoPorId(idProjeto);

        if (!projeto.getArtista().getId().equals(idArtistaAutenticado)) {
            throw new IllegalArgumentException("Você só pode atualizar projetos do seu próprio usuário.");
        }

        projeto.setTitulo(dto.getTitulo());
        projeto.setDescricaoProjeto(dto.getDescricaoProjeto());
        projeto.setMeta(dto.getMeta());
        projeto.setTipoArte(dto.getTipoArte());

        projeto = projetoRepository.save(projeto);
        return toResponseDTO(projeto);
    }

    @Transactional(readOnly = true)
    public ProjetoResponseDTO buscarPorId(Long idProjeto) {
        Projeto projeto = buscarProjetoPorId(idProjeto);
        return toResponseDTO(projeto);
    }

    private Projeto buscarProjetoPorId(Long idProjeto) {
        return projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado."));
    }

    private ProjetoResponseDTO toResponseDTO(Projeto projeto) {
        return ProjetoResponseDTO.builder()
                .idProjeto(projeto.getIdProjeto())
                .titulo(projeto.getTitulo())
                .descricaoProjeto(projeto.getDescricaoProjeto())
                .meta(projeto.getMeta())
                .valorArrecadado(projeto.getValorArrecadado())
                .dataCriacao(projeto.getDataCriacao())
                .status(projeto.getStatus())
                .tipoArte(projeto.getTipoArte())
                .nomeArtista(projeto.getArtista().getConta().getNome())
                .build();
    }
}