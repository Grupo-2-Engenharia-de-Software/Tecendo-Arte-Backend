package com.crowdfunding.tecendoarte.services;

import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioRequestDTO;
import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioResponseDTO;
import com.crowdfunding.tecendoarte.services.implementations.UsuarioService;
import com.crowdfunding.tecendoarte.models.Conta;
import com.crowdfunding.tecendoarte.models.Usuario;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.crowdfunding.tecendoarte.repositories.ContaRepository;
import com.crowdfunding.tecendoarte.repositories.UsuarioRepository;
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
}