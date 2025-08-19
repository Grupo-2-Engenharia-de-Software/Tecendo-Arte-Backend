package com.crowdfunding.tecendoarte.controllers;

import com.crowdfunding.tecendoarte.models.Artista;
import com.crowdfunding.tecendoarte.repositories.ArtistaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ArtistaControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ArtistaRepository artistaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        artistaRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private String buildRequestJson(String email, String senha) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "email", email,
                "senha", senha
        ));
    }

    @Test
    void deveLogarComSucesso() throws Exception {
        Artista artista = Artista.builder()
                .nome("Artista Teste")
                .email("artista@example.com")
                .senha(passwordEncoder.encode("secret"))
                .confirmacaoSenha(passwordEncoder.encode("secret"))
                .tiposArte(List.of())
                .build();
        artistaRepository.save(artista);

        String payload = buildRequestJson("artista@example.com", "secret");

        mockMvc.perform(post("/api/artistas/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("artista@example.com"))
                .andExpect(jsonPath("$.nome").value("Artista Teste"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void deveFalharComEmailNaoEncontrado() throws Exception {
        String payload = buildRequestJson("naoexiste@example.com", "secret");

        mockMvc.perform(post("/api/artistas/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Artista não encontrado."));
    }

    @Test
    void deveFalharComSenhaInvalida() throws Exception {
        Artista artista = Artista.builder()
                .nome("Artista Teste")
                .email("artista@example.com")
                .senha(passwordEncoder.encode("secret"))
                .confirmacaoSenha(passwordEncoder.encode("secret"))
                .tiposArte(List.of())
                .build();
        artistaRepository.save(artista);

        String payload = buildRequestJson("artista@example.com", "errada");

        mockMvc.perform(post("/api/artistas/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Senha inválida."));
    }

    @Test
    void deveFalharComBodyJsonVazio() throws Exception {
        mockMvc.perform(post("/api/artistas/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Falha de validacao nos campos."))
                .andExpect(jsonPath("$.fields.email").value("Email obrigatorio."))
                .andExpect(jsonPath("$.fields.senha").value("Senha obrigatoria."));
    }

    @Test
    void deveFalharComContentTypeNaoJson() throws Exception {
        mockMvc.perform(post("/api/artistas/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("email=artista"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.error").value("Unsupported Media Type"))
                .andExpect(jsonPath("$.message").value("Content-Type nao suportado. Use application/json."));
    }

    @Test
    void deveFalharComJsonMalFormatado() throws Exception {
        mockMvc.perform(post("/api/artistas/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Body invalido ou JSON mal formatado."));
    }
} 