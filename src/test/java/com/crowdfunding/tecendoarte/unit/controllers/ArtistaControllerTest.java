package com.crowdfunding.tecendoarte.unit.controllers;

import com.crowdfunding.tecendoarte.controllers.ArtistaController;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaResponseDTO;
import com.crowdfunding.tecendoarte.models.Artista;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.crowdfunding.tecendoarte.services.implementations.ArtistaService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ArtistaControllerTest {

    @Mock
    private ArtistaService artistaService;

    @InjectMocks
    private ArtistaController artistaController;

    private Artista artista;
    private ArtistaRequestDTO requestDTO;
    private ArtistaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.requestDTO = new ArtistaRequestDTO();
        this.requestDTO.setNome("João");
        this.requestDTO.setEmail("joao@email.com");
        this.requestDTO.setSenha("123456");
        this.requestDTO.setConfirmacaoSenha("123456");
        this.requestDTO.setTiposArte(List.of("PINTURA", "ESCULTURA"));

        this.responseDTO = ArtistaResponseDTO.builder()
            .nome(this.requestDTO.getNome())
            .email(this.requestDTO.getEmail())
            .tiposArte(this.requestDTO.getTiposArte())
            .build();

        this.artista = Artista.builder()
            .id(1L)
            .nome(this.requestDTO.getNome())
            .email(this.requestDTO.getEmail())
            .senha(this.requestDTO.getSenha())
            .confirmacaoSenha(this.requestDTO.getConfirmacaoSenha())
            .tiposArte(this.requestDTO.getTiposArte().stream()
                .map(String::toUpperCase)
                .map(tipo -> {
                    try {
                        return TipoArte.valueOf(tipo.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Tipo de arte inválido: " + tipo);
                    }
                })
                .filter(Objects::nonNull)
                .toList())
            .build();
    }

    @Test
    void cadastrarArtista_sucesso() {
        // Arrange
        when(this.artistaService.cadastrarArtista(any()))
                .thenReturn(this.artista);

        // Act
        ResponseEntity<?> response = this.artistaController.cadastrar(this.requestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(this.artista, response.getBody());
        assertNotNull(response);
    }

    @Test
    void cadastrarArtista_erro() {
        // Arrange
        when(this.artistaService.cadastrarArtista(any()))
                .thenThrow(new RuntimeException("Erro"));

        // Act
        ResponseEntity<?> response = artistaController.cadastrar(requestDTO);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro", response.getBody());
        assertNotNull(response);
    }

    @Test
    void consultarArtista_sucesso() {
        // Arrange
        when(this.artistaService.consultarArtista("João"))
                .thenReturn(this.responseDTO);

        // Act
        ResponseEntity<?> response = this.artistaController.consultar("João");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.responseDTO, response.getBody());
        assertNotNull(response);
    }

    @Test
    void consultarArtista_erro() {
        // Arrange
        when(this.artistaService.consultarArtista("João"))
                .thenThrow(new EntityNotFoundException("Não encontrado"));

        // Act
        ResponseEntity<?> response = artistaController.consultar("João");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Não encontrado", response.getBody());
        assertNotNull(response);
    }

    @Test
    void listarArtistas_sucesso() {
        // Arrange
        List<ArtistaResponseDTO> lista = Arrays.asList(this.responseDTO);
        when(this.artistaService.listarArtistas())
                .thenReturn(lista);

        // Act
        ResponseEntity<?> response = this.artistaController.listar();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(lista, response.getBody());
        assertNotNull(response);
    }

    @Test
    void listarArtistas_erro() {
        // Arrange
        when(this.artistaService.listarArtistas())
                .thenThrow(new RuntimeException("Erro"));

        // Act
        ResponseEntity<?> response = this.artistaController.listar();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());
        assertNotNull(response);
    }

    @Test
    void atualizarArtista_sucesso() {
        // Arrange
        when(this.artistaService.atualizarArtista(eq("João"), any()))
                .thenReturn(this.artista);

        // Act
        ResponseEntity<?> response = this.artistaController.atualizar("João", this.requestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.artista, response.getBody());
        assertNotNull(response);
    }

    @Test
    void atualizarArtista_erro() {
        // Arrange
        when(this.artistaService.atualizarArtista(eq("João"), any()))
                .thenThrow(new EntityNotFoundException("Não encontrado"));
        
        // Act
        ResponseEntity<?> response = this.artistaController.atualizar("João", this.requestDTO);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Não encontrado", response.getBody());
        assertNotNull(response);
    }

    @Test
    void deletarArtista_sucesso() {
        // Arrange
        doNothing().when(this.artistaService).deletarArtista("João");

        // Act
        ResponseEntity<?> response = this.artistaController.deletar("João");

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        assertNotNull(response);
    }

    @Test
    void deletarArtista_erro() {
        // Arrange
        doThrow(new EntityNotFoundException("Não encontrado"))
                .when(this.artistaService).deletarArtista("João");
        
        // Act
        ResponseEntity<?> response = this.artistaController.deletar("João");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Não encontrado", response.getBody());
        assertNotNull(response);
    }

}
