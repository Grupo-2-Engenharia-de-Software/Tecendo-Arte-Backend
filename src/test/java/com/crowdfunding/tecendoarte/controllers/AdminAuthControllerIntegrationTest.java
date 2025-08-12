package com.crowdfunding.tecendoarte.controllers;

import com.crowdfunding.tecendoarte.models.Administrador;
import com.crowdfunding.tecendoarte.repositories.AdministradorRepository;
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

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdminAuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        administradorRepository.deleteAll();
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
        Administrador admin = Administrador.builder()
                .nome("Admin")
                .email("admin@example.com")
                .senha(passwordEncoder.encode("secret"))
                .build();
        administradorRepository.save(admin);

        String payload = buildRequestJson("admin@example.com", "secret");

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andExpect(jsonPath("$.nome").value("Admin"))
                .andExpect(jsonPath("$.token").exists())
                .andReturn();
    }

    @Test
    void deveFalharComEmailNaoEncontrado() throws Exception {
        String payload = buildRequestJson("naoexiste@example.com", "secret");

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Administrador não encontrado."));
    }

    @Test
    void deveFalharComSenhaInvalida() throws Exception {
        Administrador admin = Administrador.builder()
                .nome("Admin")
                .email("admin@example.com")
                .senha(passwordEncoder.encode("secret"))
                .build();
        administradorRepository.save(admin);

        String payload = buildRequestJson("admin@example.com", "errada");

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Senha inválida."));
    }

    @Test
    void deveFalharComBodyJsonVazio() throws Exception {
        mockMvc.perform(post("/api/admin/login")
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
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("email=admin"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.error").value("Unsupported Media Type"))
                .andExpect(jsonPath("$.message").value("Content-Type nao suportado. Use application/json."));
    }

    @Test
    void deveFalharComJsonMalFormatado() throws Exception {
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Body invalido ou JSON mal formatado."));
    }
}
