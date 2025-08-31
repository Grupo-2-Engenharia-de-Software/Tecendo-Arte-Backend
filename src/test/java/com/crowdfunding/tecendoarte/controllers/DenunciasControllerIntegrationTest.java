package com.crowdfunding.tecendoarte.controllers;

import com.crowdfunding.tecendoarte.models.Conta;
import com.crowdfunding.tecendoarte.models.Denuncia;
import com.crowdfunding.tecendoarte.models.enums.StatusDenuncia;
import com.crowdfunding.tecendoarte.models.enums.TipoDenuncia;
import com.crowdfunding.tecendoarte.models.enums.TipoConta;
import com.crowdfunding.tecendoarte.repositories.DenunciaRepository;
import com.crowdfunding.tecendoarte.repositories.ContaRepository;
import com.crowdfunding.tecendoarte.repositories.UsuarioRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DenunciasControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DenunciaRepository denunciaRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Conta contaTeste;

    @BeforeEach
    void setUp() {
        denunciaRepository.deleteAll();
        usuarioRepository.deleteAll();
        contaRepository.deleteAll();

        contaTeste = Conta.builder()
                .email("teste@teste.com")
                .senha("senha123")
                .nome("Usuário Teste")
                .tipoConta(TipoConta.USUARIO)
                .build();
        contaTeste = contaRepository.save(contaTeste);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void deveRetornar403SemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/admin/denuncias").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveRetornar403SemRoleAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/denuncias").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveListarDenunciasPendentes() throws Exception {
        Denuncia d1 = Denuncia.builder()
                .tipo(TipoDenuncia.PROJETO)
                .idAlvo(1L)
                .autor(contaTeste)
                .descricao("Conteúdo impróprio")
                .status(StatusDenuncia.PENDENTE)
                .build();
        Denuncia d2 = Denuncia.builder()
                .tipo(TipoDenuncia.USUARIO)
                .idAlvo(2L)
                .autor(contaTeste)
                .descricao("Usuário spammer")
                .status(StatusDenuncia.PENDENTE)
                .build();
        d1 = denunciaRepository.save(d1);
        d2 = denunciaRepository.save(d2);

        mockMvc.perform(get("/api/admin/denuncias").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(2)))
                // A ordem de retorno provavelmente é por ID, que é crescente, então a primeira denúncia (d1) deve vir primeiro.
                .andExpect(jsonPath("$[0].id").value(d1.getId()))
                .andExpect(jsonPath("$[0].tipo").value("PROJETO"))
                .andExpect(jsonPath("$[0].idAlvo").value(1))
                .andExpect(jsonPath("$[0].descricao").value("Conteúdo impróprio"))
                .andExpect(jsonPath("$[0].status").value("PENDENTE"))
                .andExpect(jsonPath("$[0].criadoEm").exists())
                .andExpect(jsonPath("$[0].atualizadoEm").exists())
                .andExpect(jsonPath("$[0].nomeAutor").value("Usuário Teste"))
                .andExpect(jsonPath("$[0].emailAutor").value("teste@teste.com"))
                // Verificar segunda denúncia (USUARIO)
                .andExpect(jsonPath("$[1].id").value(d2.getId()))
                .andExpect(jsonPath("$[1].tipo").value("USUARIO"))
                .andExpect(jsonPath("$[1].idAlvo").value(2))
                .andExpect(jsonPath("$[1].descricao").value("Usuário spammer"))
                .andExpect(jsonPath("$[1].status").value("PENDENTE"))
                .andExpect(jsonPath("$[1].criadoEm").exists())
                .andExpect(jsonPath("$[1].atualizadoEm").exists())
                .andExpect(jsonPath("$[1].nomeAutor").value("Usuário Teste"))
                .andExpect(jsonPath("$[1].emailAutor").value("teste@teste.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAnalisarDenunciaComoProcedente() throws Exception {
        Denuncia d = Denuncia.builder()
                .tipo(TipoDenuncia.ARTISTA)
                .idAlvo(7L)
                .autor(contaTeste)
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
    @WithMockUser(roles = "ADMIN")
    void deveAnalisarDenunciaComoImprocedente() throws Exception {
        Denuncia d = Denuncia.builder()
                .tipo(TipoDenuncia.PROJETO)
                .idAlvo(9L)
                .autor(contaTeste)
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
    @WithMockUser(roles = "ADMIN")
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
    @WithMockUser(roles = "ADMIN")
    void deveFalharAoAnalisarSemResultado() throws Exception {
        Denuncia d = Denuncia.builder()
                .tipo(TipoDenuncia.USUARIO)
                .idAlvo(10L)
                .autor(contaTeste)
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
    @WithMockUser(roles = "ADMIN")
    void deveFalharAoReanalisarDenuncia() throws Exception {
        Denuncia d = Denuncia.builder()
                .tipo(TipoDenuncia.PROJETO)
                .idAlvo(3L)
                .autor(contaTeste)
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
    @WithMockUser(roles = "ADMIN")
    void deveFalharAoAnalisarComResultadoPendente() throws Exception {
        Denuncia d = Denuncia.builder()
                .tipo(TipoDenuncia.USUARIO)
                .idAlvo(11L)
                .autor(contaTeste)
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
