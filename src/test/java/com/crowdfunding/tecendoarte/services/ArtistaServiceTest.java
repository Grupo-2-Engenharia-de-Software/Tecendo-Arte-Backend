package com.crowdfunding.tecendoarte.services;

import com.crowdfunding.tecendoarte.config.JwtUtil;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaLoginRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaLoginResponseDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaResponseDTO;
import com.crowdfunding.tecendoarte.models.Artista;
import com.crowdfunding.tecendoarte.models.Conta;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.crowdfunding.tecendoarte.repositories.ArtistaRepository;
import com.crowdfunding.tecendoarte.repositories.ContaRepository;
import com.crowdfunding.tecendoarte.services.implementations.ArtistaService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistaServiceTest {

    @Mock
    private ArtistaRepository artistaRepository;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private ArtistaService artistaService;

    private Conta conta;
    private Artista artista;
    private ArtistaRequestDTO artistaRequestDTO;
    private ArtistaLoginRequestDTO artistaLoginRequestDTO;

    @BeforeEach
    void setUp() {
        conta = Conta.builder()
                .nome("Artista Teste")
                .email("artista@test.com")
                .senha("senha123")
                .build();

        artista = Artista.builder()
                .id(1L)
                .conta(conta)
                .descricao("Descrição do artista")
                .categorias(List.of(TipoArte.PINTURA))
                .projetos(List.of())
                .build();

        artistaRequestDTO = ArtistaRequestDTO.builder()
                .contaId(1L)
                .descricao("Descrição do artista")
                .categorias(List.of("PINTURA"))
                .build();

        artistaLoginRequestDTO = ArtistaLoginRequestDTO.builder()
                .email("artista@test.com")
                .senha("senha123")
                .build();
    }

    // Testes para o método cadastrarArtista
    @Test
    @DisplayName("Deve cadastrar um artista com sucesso")
    void deveCadastrarArtistaComSucesso() {
        when(contaRepository.findById(anyLong())).thenReturn(Optional.of(conta));
        when(artistaRepository.findByContaId(anyLong())).thenReturn(Optional.empty());
        when(artistaRepository.save(any(Artista.class))).thenReturn(artista);

        ArtistaResponseDTO response = artistaService.cadastrarArtista(artistaRequestDTO);

        assertNotNull(response);
        assertEquals("Artista Teste", response.getNome());
        verify(artistaRepository, times(1)).save(any(Artista.class));
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar artista com conta inexistente")
    void deveFalharAoCadastrarComContaInexistente() {
        when(contaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> artistaService.cadastrarArtista(artistaRequestDTO));
        verify(artistaRepository, never()).save(any(Artista.class));
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar artista com conta já existente")
    void deveFalharAoCadastrarComContaExistente() {
        when(contaRepository.findById(anyLong())).thenReturn(Optional.of(conta));
        when(artistaRepository.findByContaId(anyLong())).thenReturn(Optional.of(artista));

        assertThrows(IllegalArgumentException.class, () -> artistaService.cadastrarArtista(artistaRequestDTO));
        verify(artistaRepository, never()).save(any(Artista.class));
    }

    // Testes para o método login
    @Test
    @DisplayName("Deve realizar login do artista com sucesso")
    void deveRealizarLoginComSucesso() {
        when(artistaRepository.findByContaEmail(anyString())).thenReturn(Optional.of(artista));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateTokenForArtista(anyLong(), anyString())).thenReturn("mocked_token");

        ArtistaLoginResponseDTO response = artistaService.login(artistaLoginRequestDTO);

        assertNotNull(response);
        assertEquals("artista@test.com", response.getEmail());
        assertEquals("mocked_token", response.getToken());
    }

    @Test
    @DisplayName("Deve falhar o login com artista inexistente")
    void deveFalharLoginComArtistaInexistente() {
        when(artistaRepository.findByContaEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> artistaService.login(artistaLoginRequestDTO));
    }

    @Test
    @DisplayName("Deve falhar o login com senha invalida")
    void deveFalharLoginComSenhaInvalida() {
        when(artistaRepository.findByContaEmail(anyString())).thenReturn(Optional.of(artista));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> artistaService.login(artistaLoginRequestDTO));
    }

    // Teste para o método buscarPorNome
    @Test
    @DisplayName("Deve buscar artista por nome com sucesso")
    void deveBuscarArtistaPorNomeComSucesso() {
        when(artistaRepository.findByContaNomeContainingIgnoreCase(anyString())).thenReturn(Optional.of(artista));

        ArtistaResponseDTO response = artistaService.buscarPorNome("Artista Teste");

        assertNotNull(response);
        assertEquals("Artista Teste", response.getNome());
    }

    @Test
    @DisplayName("Deve falhar ao buscar artista inexistente")
    void deveFalharAoBuscarArtistaInexistente() {
        when(artistaRepository.findByContaNomeContainingIgnoreCase(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> artistaService.buscarPorNome("Nome Inexistente"));
    }

    // Teste para o método listarArtistas
    @Test
    @DisplayName("Deve listar todos os artistas com sucesso")
    void deveListarTodosOsArtistasComSucesso() {
        when(artistaRepository.findAll()).thenReturn(List.of(artista));

        List<ArtistaResponseDTO> response = artistaService.listarArtistas();

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
    }

    @Test
    @DisplayName("Deve falhar ao listar se não houver artistas")
    void deveFalharAoListarQuandoNaoHouverArtistas() {
        when(artistaRepository.findAll()).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> artistaService.listarArtistas());
    }

    // Testes para o método atualizarArtista
    @Test
    @DisplayName("Deve atualizar artista com sucesso")
    void deveAtualizarArtistaComSucesso() {
        when(artistaRepository.findByContaNomeContainingIgnoreCase(anyString())).thenReturn(Optional.of(artista));
        when(artistaRepository.save(any(Artista.class))).thenReturn(artista);

        ArtistaResponseDTO response = artistaService.atualizarArtista("Artista Teste", artistaRequestDTO);

        assertNotNull(response);
        assertEquals("Descrição do artista", response.getDescricao());
    }

    @Test
    @DisplayName("Deve falhar ao atualizar artista inexistente")
    void deveFalharAoAtualizarArtistaInexistente() {
        when(artistaRepository.findByContaNomeContainingIgnoreCase(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> artistaService.atualizarArtista("Nome Inexistente", artistaRequestDTO));
    }

    // Testes para o método deletarArtista
    @Test
    @DisplayName("Deve deletar artista com sucesso")
    void deveDeletarArtistaComSucesso() {
        when(artistaRepository.findByContaNomeContainingIgnoreCase(anyString())).thenReturn(Optional.of(artista));

        artistaService.deletarArtista("Artista Teste");

        verify(artistaRepository, times(1)).delete(any(Artista.class));
    }

    @Test
    @DisplayName("Deve falhar ao deletar artista inexistente")
    void deveFalharAoDeletarArtistaInexistente() {
        when(artistaRepository.findByContaNomeContainingIgnoreCase(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> artistaService.deletarArtista("Nome Inexistente"));
    }
}