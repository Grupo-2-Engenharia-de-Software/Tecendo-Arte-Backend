package com.crowdfunding.tecendoarte.services;

import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioRequestDTO;
import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioResponseDTO;
import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioLoginRequestDTO;
import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioLoginResponseDTO;
import com.crowdfunding.tecendoarte.services.implementations.UsuarioService;
import com.crowdfunding.tecendoarte.models.Conta;
import com.crowdfunding.tecendoarte.models.Usuario;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.crowdfunding.tecendoarte.models.enums.TipoConta;
import com.crowdfunding.tecendoarte.repositories.ContaRepository;
import com.crowdfunding.tecendoarte.repositories.UsuarioRepository;
import com.crowdfunding.tecendoarte.config.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Conta conta;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        conta = new Conta();
        conta.setIdConta(1L);

        usuario = Usuario.builder()
                .id(1L)
                .conta(conta)
                .interesses(List.of(TipoArte.ESCULTURA))
                .build();
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setContaId(1L);
        dto.setInteresses(List.of(TipoArte.ESCULTURA));

        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponseDTO response = usuarioService.criar(dto);

        assertNotNull(response);
        assertEquals(1L, response.getContaId());
        assertTrue(response.getInteresses().contains(TipoArte.ESCULTURA));

        verify(contaRepository).findById(1L);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveLancarErroQuandoContaNaoExistirAoCriar() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setContaId(99L);

        when(contaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> usuarioService.criar(dto));
    }

    @Test
    void deveBuscarUsuarioPorId() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response = usuarioService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void deveLancarErroAoBuscarUsuarioInexistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> usuarioService.buscarPorId(99L));
    }

    @Test
    void deveAtualizarInteressesDoUsuario() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setInteresses(List.of(TipoArte.FOTOGRAFIA));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response = usuarioService.atualizar(1L, dto);

        assertTrue(response.getInteresses().contains(TipoArte.FOTOGRAFIA));
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void deveDeletarUsuarioExistente() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        usuarioService.deletar(1L);

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void deveLancarErroAoDeletarUsuarioInexistente() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> usuarioService.deletar(99L));
    }

    @Test
    void login_ComCredenciaisValidas_DeveRetornarToken() {
        // Arrange
        UsuarioLoginRequestDTO request = new UsuarioLoginRequestDTO();
        request.setEmail("usuario@teste.com");
        request.setSenha("senha123");

        Conta conta = Conta.builder()
                .idConta(1L)
                .email("usuario@teste.com")
                .senha("senha_criptografada")
                .nome("Usuário Teste")
                .tipoConta(TipoConta.USUARIO)
                .build();

        Usuario usuario = Usuario.builder()
                .id(1L)
                .conta(conta)
                .build();

        when(contaRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(conta));
        when(passwordEncoder.matches("senha123", "senha_criptografada")).thenReturn(true);
        when(usuarioRepository.findByConta(conta)).thenReturn(Optional.of(usuario));
        when(jwtUtil.generateTokenForUsuario(1L, "usuario@teste.com")).thenReturn("token_jwt");

        // Act
        UsuarioLoginResponseDTO response = usuarioService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("token_jwt", response.getToken());
        assertEquals("usuario@teste.com", response.getEmail());
        assertEquals("Usuário Teste", response.getNome());
        assertEquals(1L, response.getUsuarioId());
        assertEquals("USUARIO", response.getTipoConta());

        verify(contaRepository).findByEmail("usuario@teste.com");
        verify(passwordEncoder).matches("senha123", "senha_criptografada");
        verify(usuarioRepository).findByConta(conta);
        verify(jwtUtil).generateTokenForUsuario(1L, "usuario@teste.com");
    }

    @Test
    void login_ComEmailInexistente_DeveLancarExcecao() {
        // Arrange
        UsuarioLoginRequestDTO request = new UsuarioLoginRequestDTO();
        request.setEmail("inexistente@teste.com");
        request.setSenha("senha123");

        when(contaRepository.findByEmail("inexistente@teste.com")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.login(request);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(contaRepository).findByEmail("inexistente@teste.com");
        verifyNoInteractions(passwordEncoder, usuarioRepository, jwtUtil);
    }

    @Test
    void login_ComSenhaIncorreta_DeveLancarExcecao() {
        // Arrange
        UsuarioLoginRequestDTO request = new UsuarioLoginRequestDTO();
        request.setEmail("usuario@teste.com");
        request.setSenha("senha_errada");

        Conta conta = Conta.builder()
                .idConta(1L)
                .email("usuario@teste.com")
                .senha("senha_criptografada")
                .nome("Usuário Teste")
                .tipoConta(TipoConta.USUARIO)
                .build();

        when(contaRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(conta));
        when(passwordEncoder.matches("senha_errada", "senha_criptografada")).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.login(request);
        });

        assertEquals("Senha incorreta", exception.getMessage());
        verify(contaRepository).findByEmail("usuario@teste.com");
        verify(passwordEncoder).matches("senha_errada", "senha_criptografada");
        verifyNoInteractions(usuarioRepository, jwtUtil);
    }

    @Test
    void login_ComUsuarioNaoVinculado_DeveLancarExcecao() {
        // Arrange
        UsuarioLoginRequestDTO request = new UsuarioLoginRequestDTO();
        request.setEmail("usuario@teste.com");
        request.setSenha("senha123");

        Conta conta = Conta.builder()
                .idConta(1L)
                .email("usuario@teste.com")
                .senha("senha_criptografada")
                .nome("Usuário Teste")
                .tipoConta(TipoConta.USUARIO)
                .build();

        when(contaRepository.findByEmail("usuario@teste.com")).thenReturn(Optional.of(conta));
        when(passwordEncoder.matches("senha123", "senha_criptografada")).thenReturn(true);
        when(usuarioRepository.findByConta(conta)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.login(request);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(contaRepository).findByEmail("usuario@teste.com");
        verify(passwordEncoder).matches("senha123", "senha_criptografada");
        verify(usuarioRepository).findByConta(conta);
        verifyNoInteractions(jwtUtil);
    }
}