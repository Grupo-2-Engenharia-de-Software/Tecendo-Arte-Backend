package com.crowdfunding.tecendoarte.controllers;

import com.crowdfunding.tecendoarte.models.Denuncia;
import com.crowdfunding.tecendoarte.models.enums.StatusDenuncia;
import com.crowdfunding.tecendoarte.models.enums.TipoDenuncia;
import com.crowdfunding.tecendoarte.repositories.DenunciaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdminDenunciasControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DenunciaRepository denunciaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        denunciaRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void deveListarDenunciasPendentes() throws Exception {
        Denuncia d1 = Denuncia.builder()
                .tipo(TipoDenuncia.PROJETO)
                .referenciaId(1L)
                .descricao("Conteúdo impróprio")
                .status(StatusDenuncia.PENDENTE)
                .build();
        Denuncia d2 = Denuncia.builder()
                .tipo(TipoDenuncia.USUARIO)
                .referenciaId(2L)
                .descricao("Usuário spammer")
                .status(StatusDenuncia.PENDENTE)
                .build();
        denunciaRepository.save(d1);
        denunciaRepository.save(d2);

        mockMvc.perform(get("/api/admin/denuncias").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].tipo").exists())
                .andExpect(jsonPath("$[0].descricao").exists())
                .andExpect(jsonPath("$[0].status").value("PENDENTE"))
                .andExpect(jsonPath("$[1].status").value("PENDENTE"));
    }

    @Test
    void deveAnalisarDenunciaComoProcedente() throws Exception {
        Denuncia d = Denuncia.builder()
                .tipo(TipoDenuncia.ARTISTA)
                .referenciaId(7L)
                .descricao("Violação de termos")
                .status(StatusDenuncia.PENDENTE)
                .build();
        d = denunciaRepository.save(d);

        String payload = objectMapper.writeValueAsString(Map.of("resultado", "PROCEDENTE"));

        mockMvc.perform(post("/api/admin/denuncias/" + d.getId() + "/analise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/denuncias").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(d.getId()))
                .andExpect(jsonPath("$[0].status").value("PROCEDENTE"));
    }

    @Test
    void deveAnalisarDenunciaComoImprocedente() throws Exception {
        Denuncia d = Denuncia.builder()
                .tipo(TipoDenuncia.PROJETO)
                .referenciaId(9L)
                .descricao("Sem fundamento")
                .status(StatusDenuncia.PENDENTE)
                .build();
        d = denunciaRepository.save(d);

        String payload = objectMapper.writeValueAsString(Map.of("resultado", "IMPROCEDENTE"));

        mockMvc.perform(post("/api/admin/denuncias/" + d.getId() + "/analise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/denuncias").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(d.getId()))
                .andExpect(jsonPath("$[0].status").value("IMPROCEDENTE"));
    }

    @Test
    void deveFalharAoAnalisarComIdInexistente() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of("resultado", "PROCEDENTE"));

        mockMvc.perform(post("/api/admin/denuncias/9999/analise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Denúncia não encontrada."));
    }

    @Test
    void deveFalharAoAnalisarSemResultado() throws Exception {
        Denuncia d = Denuncia.builder()
                .tipo(TipoDenuncia.USUARIO)
                .referenciaId(10L)
                .descricao("Teste")
                .status(StatusDenuncia.PENDENTE)
                .build();
        d = denunciaRepository.save(d);

        mockMvc.perform(post("/api/admin/denuncias/" + d.getId() + "/analise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Falha de validacao nos campos."))
                .andExpect(jsonPath("$.fields.resultado").value("Resultado obrigatorio."));
    }

    @Test
    void deveFalharAoReanalisarDenuncia() throws Exception {
        Denuncia d = Denuncia.builder()
                .tipo(TipoDenuncia.PROJETO)
                .referenciaId(3L)
                .descricao("Algo")
                .status(StatusDenuncia.PENDENTE)
                .build();
        d = denunciaRepository.save(d);

        String payload = objectMapper.writeValueAsString(Map.of("resultado", "PROCEDENTE"));
        mockMvc.perform(post("/api/admin/denuncias/" + d.getId() + "/analise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCEDENTE"));

        mockMvc.perform(post("/api/admin/denuncias/" + d.getId() + "/analise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Denúncia já foi analisada."));
    }

    @Test
    void deveFalharAoAnalisarComResultadoPendente() throws Exception {
        Denuncia d = Denuncia.builder()
                .tipo(TipoDenuncia.USUARIO)
                .referenciaId(11L)
                .descricao("Testar pendente")
                .status(StatusDenuncia.PENDENTE)
                .build();
        d = denunciaRepository.save(d);

        String payload = objectMapper.writeValueAsString(Map.of("resultado", "PENDENTE"));

        mockMvc.perform(post("/api/admin/denuncias/" + d.getId() + "/analise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Resultado inválido para análise."));
    }
}
