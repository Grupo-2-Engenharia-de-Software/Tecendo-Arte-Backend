package com.crowdfunding.tecendoarte.services;

import com.crowdfunding.tecendoarte.dto.ContaDTO.ContaRequestDTO;
import com.crowdfunding.tecendoarte.dto.ContaDTO.ContaResponseDTO;
import com.crowdfunding.tecendoarte.models.Conta;
import com.crowdfunding.tecendoarte.models.Usuario;
import com.crowdfunding.tecendoarte.models.enums.TipoConta;
import com.crowdfunding.tecendoarte.repositories.ArtistaRepository;
import com.crowdfunding.tecendoarte.repositories.ContaRepository;
import com.crowdfunding.tecendoarte.repositories.UsuarioRepository;
import com.crowdfunding.tecendoarte.services.implementations.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ArtistaRepository artistaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ContaService contaService;

    private ContaRequestDTO contaRequestDTO;
    private Conta contaUsuario;
    private Conta contaArtista;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        contaService = new ContaService(contaRepository, passwordEncoder, usuarioRepository, artistaRepository);

        contaRequestDTO = ContaRequestDTO.builder()
                .email("teste@teste.com")
                .nome("Teste Nome")
                .senha("senha123")
                .tipoConta(TipoConta.USUARIO)
                .build();

        contaUsuario = Conta.builder()
                .idConta(1L)
                .email("teste@teste.com")
                .nome("Teste Nome")
                .senha("senhaCriptografada")
                .tipoConta(TipoConta.USUARIO)
                .build();

        contaArtista = Conta.builder()
                .idConta(2L)
                .email("artista@teste.com")
                .nome("Artista Nome")
                .senha("senhaCriptografada")
                .tipoConta(TipoConta.ARTISTA)
                .build();

        usuario = Usuario.builder()
                .conta(contaUsuario)
                .interesses(new ArrayList<>())
                .build();
    }

    @Test
    void testCadastrarContaComSucesso() {
        when(contaRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("senhaCriptografada");
        when(contaRepository.save(any(Conta.class))).thenReturn(contaUsuario);

        ContaResponseDTO responseDTO = contaService.cadastrar(contaRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(contaRequestDTO.getEmail(), responseDTO.getEmail());
        assertEquals(contaRequestDTO.getNome(), responseDTO.getNome());
        verify(contaRepository, times(1)).save(any(Conta.class));
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testCadastrarContaComEmailExistenteDeveLancarExcecao() {
        when(contaRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> contaService.cadastrar(contaRequestDTO));
        verify(contaRepository, never()).save(any(Conta.class));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testAtualizarContaComSucesso() {
        when(contaRepository.findById(anyLong())).thenReturn(Optional.of(contaUsuario));
        when(passwordEncoder.encode(anyString())).thenReturn("novaSenhaCriptografada");
        when(contaRepository.save(any(Conta.class))).thenReturn(contaUsuario);

        ContaRequestDTO atualizacaoDTO = ContaRequestDTO.builder()
                .nome("Novo Nome")
                .email("novo@email.com")
                .senha("novaSenha")
                .tipoConta(TipoConta.USUARIO)
                .build();

        ContaResponseDTO responseDTO = contaService.atualizarConta(1L, atualizacaoDTO);

        assertNotNull(responseDTO);
        assertEquals(atualizacaoDTO.getNome(), responseDTO.getNome());
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    void testAtualizarContaNaoExistenteDeveLancarExcecao() {
        when(contaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> contaService.atualizarConta(99L, contaRequestDTO));
    }

    @Test
    void testAtualizarTipoDeContaDeUsuarioParaArtista() {
        ContaRequestDTO atualizacaoDTO = ContaRequestDTO.builder()
                .nome("Artista Novo")
                .email("artista@novo.com")
                .senha("senhaForte")
                .tipoConta(TipoConta.ARTISTA)
                .build();

        when(contaRepository.findById(anyLong())).thenReturn(Optional.of(contaUsuario));
        when(passwordEncoder.encode(anyString())).thenReturn("senhaCriptografada");
        when(contaRepository.save(any(Conta.class))).thenReturn(contaArtista);
        when(usuarioRepository.findByConta(any(Conta.class))).thenReturn(Optional.of(usuario));

        ContaResponseDTO responseDTO = contaService.atualizarConta(1L, atualizacaoDTO);

        assertNotNull(responseDTO);
        assertEquals(TipoConta.ARTISTA, responseDTO.getTipoConta());
        verify(usuarioRepository, times(1)).delete(any(Usuario.class));
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    void testExcluirContaComSucesso() {
        when(contaRepository.findById(anyLong())).thenReturn(Optional.of(contaUsuario));
        when(usuarioRepository.findByConta(any(Conta.class))).thenReturn(Optional.of(usuario));
        doNothing().when(contaRepository).delete(any(Conta.class));
        doNothing().when(usuarioRepository).delete(any(Usuario.class));

        boolean resultado = contaService.excluirConta(1L);

        assertTrue(resultado);
        verify(usuarioRepository, times(1)).delete(any(Usuario.class));
        verify(contaRepository, times(1)).delete(any(Conta.class));
    }

    @Test
    void testExcluirContaNaoExistenteDeveLancarExcecao() {
        when(contaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> contaService.excluirConta(99L));
        verify(contaRepository, never()).delete(any(Conta.class));
    }

    @Test
    void testBuscarPorIdComSucesso() {
        when(contaRepository.findById(anyLong())).thenReturn(Optional.of(contaUsuario));

        ContaResponseDTO responseDTO = contaService.buscarPorId(1L);

        assertNotNull(responseDTO);
        assertEquals(contaUsuario.getIdConta(), responseDTO.getIdConta());
        assertEquals(contaUsuario.getEmail(), responseDTO.getEmail());
    }

    @Test
    void testBuscarPorIdComContaNaoExistenteDeveLancarExcecao() {
        when(contaRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> contaService.buscarPorId(99L));
    }
}