package com.crowdfunding.tecendoarte.services;

import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoRequestDTO;
import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoResponseDTO;
import com.crowdfunding.tecendoarte.models.Artista;
import com.crowdfunding.tecendoarte.models.Conta;
import com.crowdfunding.tecendoarte.models.Projeto;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.crowdfunding.tecendoarte.repositories.ArtistaRepository;
import com.crowdfunding.tecendoarte.repositories.ProjetoRepository;
import com.crowdfunding.tecendoarte.services.implementations.ProjetoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetoServiceTest {
    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private ArtistaRepository artistaRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProjetoService projetoService;

    private Artista artista;
    private Projeto projeto;
    private ProjetoRequestDTO projetoRequestDTO;

    @BeforeEach
    void setUp() {
        Conta conta = new Conta();
        conta.setIdConta(1L);
        conta.setEmail("artista@test.com");
        conta.setNome("Artista Teste");

        artista = new Artista();
        artista.setId(1L); 
        artista.setConta(conta);

        projeto = new Projeto();
        projeto.setIdProjeto(1L);
        projeto.setTitulo("Projeto de Teste");
        projeto.setArtista(artista);
        projeto.setDataCriacao(LocalDate.now());

        projetoRequestDTO = ProjetoRequestDTO.builder()
                .titulo("Novo Projeto de Teste")
                .descricaoProjeto("Nova descrição para o projeto de teste.")
                .meta(1500.0)
                .tipoArte(TipoArte.PINTURA)
                .build();
    }

    @Test
    void testCadastraProjetoComSucesso() {
        when(artistaRepository.findById(anyLong())).thenReturn(Optional.of(artista));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);

        ProjetoResponseDTO result = projetoService.cadastraProjeto(projetoRequestDTO, artista.getId());

        assertNotNull(result);
        assertEquals(projeto.getTitulo(), result.getTitulo());
        verify(projetoRepository, times(1)).save(any(Projeto.class));
    }

    @Test
    void testCadastraProjetoComArtistaInexistente() {
        when(artistaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projetoService.cadastraProjeto(projetoRequestDTO, 99L));
    }

    @Test
    void testBuscarPorIdComSucesso() {
        when(projetoRepository.findById(anyLong())).thenReturn(Optional.of(projeto));

        ProjetoResponseDTO result = projetoService.buscarPorId(1L);

        assertNotNull(result);
        assertEquals(projeto.getTitulo(), result.getTitulo());
    }

    @Test
    void testBuscarPorIdComProjetoInexistente() {
        when(projetoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projetoService.buscarPorId(99L));
    }

    @Test
    void testAtualizaProjetoComSucesso() {
        when(projetoRepository.findById(anyLong())).thenReturn(Optional.of(projeto));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);

        ProjetoResponseDTO result = projetoService.atualizaProjeto(1L, projetoRequestDTO, artista.getId());

        assertNotNull(result);
        assertEquals(projetoRequestDTO.getTitulo(), result.getTitulo());
        assertEquals(projetoRequestDTO.getDescricaoProjeto(), result.getDescricaoProjeto());
        verify(projetoRepository, times(1)).save(any(Projeto.class));
    }

    @Test
    void testAtualizaProjetoComAcessoNegado() {
        Artista outroArtista = new Artista();
        Conta outraConta = new Conta();
        outraConta.setIdConta(2L);
        outroArtista.setId(2L);
        outroArtista.setConta(outraConta);

        when(projetoRepository.findById(anyLong())).thenReturn(Optional.of(projeto));

        assertThrows(IllegalArgumentException.class,
                () -> projetoService.atualizaProjeto(1L, projetoRequestDTO, outroArtista.getId()));
    }

    @Test
    void testDeletaProjetoComSucesso() {
        when(projetoRepository.findById(anyLong())).thenReturn(Optional.of(projeto));
        doNothing().when(projetoRepository).delete(any(Projeto.class));

        assertDoesNotThrow(() -> projetoService.deletaProjeto(1L, artista.getId()));
        verify(projetoRepository, times(1)).delete(any(Projeto.class));
    }

    @Test
    void testDeletaProjetoComProjetoInexistente() {
        when(projetoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projetoService.deletaProjeto(99L, artista.getId()));
    }

    @Test
    void testDeletaProjetoComAcessoNegado() {
        Artista outroArtista = new Artista();
        Conta outraConta = new Conta();
        outraConta.setIdConta(2L);
        outroArtista.setId(2L);
        outroArtista.setConta(outraConta);

        when(projetoRepository.findById(anyLong())).thenReturn(Optional.of(projeto));

        assertThrows(IllegalArgumentException.class, () -> projetoService.deletaProjeto(1L, outroArtista.getId()));
    }

    @Test
    void testConsultarProjetosPorArtistaComSucesso() {
        when(artistaRepository.findByContaNomeContainingIgnoreCase(anyString())).thenReturn(Optional.of(artista));
        when(projetoRepository.findByArtistaId(anyLong())).thenReturn(List.of(projeto));

        List<ProjetoResponseDTO> result = projetoService.consultarProjetosPorArtista("Artista Teste");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(projeto.getTitulo(), result.get(0).getTitulo());
    }

    @Test
    void testConsultarProjetosPorArtistaComArtistaInexistente() {
        when(artistaRepository.findByContaNomeContainingIgnoreCase(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> projetoService.consultarProjetosPorArtista("Artista Inexistente"));
    }

    @Test
    void testGetIdArtistaAutenticadoComSucesso() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(artista.getConta().getEmail());
        when(artistaRepository.findByContaEmail(anyString())).thenReturn(Optional.of(artista));

        Long id = projetoService.getIdArtistaAutenticado();

        assertEquals(artista.getId(), id);
    }

    @Test
    void testGetIdArtistaAutenticadoComArtistaInexistente() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn("naoexiste@test.com");
        when(artistaRepository.findByContaEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projetoService.getIdArtistaAutenticado());
    }
}
