package com.crowdfunding.tecendoarte.controllers;

import com.crowdfunding.tecendoarte.models.Conta;
import com.crowdfunding.tecendoarte.models.Artista;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.crowdfunding.tecendoarte.models.enums.TipoConta;
import com.crowdfunding.tecendoarte.repositories.ArtistaRepository;
import com.crowdfunding.tecendoarte.repositories.ContaRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private ContaRepository contaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private Conta conta;

    @BeforeEach
    void setUp() {
        artistaRepository.deleteAll();
        contaRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        conta = Conta.builder()
                .nome("Conta Teste")
                .email("conta@example.com")
                .senha(passwordEncoder.encode("123456"))
                .tipoConta(TipoConta.ARTISTA)
                .build();
        contaRepository.save(conta);
    }

    private String buildCadastroRequestJson(Long contaId, String descricao, List<String> categorias) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "contaId", contaId,
                "descricao", descricao,
                "categorias", categorias
        ));
    }

    private String buildLoginRequestJson(String email, String senha) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "email", email,
                "senha", senha
        ));
    }

    // ---------- TESTES DE CADASTRO ----------

    @Test
    void deveCadastrarArtistaComSucesso() throws Exception {
        String payload = buildCadastroRequestJson(conta.getIdConta(), "Artista de teste", List.of("PINTURA", "ESCULTURA"));

        mockMvc.perform(post("/api/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome").value("Conta Teste"))
                .andExpect(jsonPath("$.descricao").value("Artista de teste"))
                .andExpect(jsonPath("$.categorias").isArray());
    }

    @Test
    void naoDeveCadastrarArtistaContaInexistente() throws Exception {
        String payload = buildCadastroRequestJson(999L, "Descricao", List.of("PINTURA"));

        mockMvc.perform(post("/api/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Conta nao encontrada")));
    }

    @Test
    void naoDeveCadastrarArtistaComTipoArteInvalido() throws Exception {
        String payload = buildCadastroRequestJson(conta.getIdConta(), "Descricao", List.of("TIPO_INVALIDO"));

        mockMvc.perform(post("/api/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Tipo de arte inválido")));
    }

    @Test
    void naoDeveCadastrarArtistaSemCategorias() throws Exception {
        String payload = buildCadastroRequestJson(conta.getIdConta(), "Artista sem categorias", List.of());
        
        mockMvc.perform(post("/api/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Falha de validacao nos campos.")));
    }

    // ---------- TESTES DE LOGIN ----------

    @Test
    void deveLogarComSucesso() throws Exception {
        Artista artista = Artista.builder()
                .conta(conta)
                .descricao("Artista plástico")
                .categorias(List.of(TipoArte.PINTURA))
                .build();
        artistaRepository.save(artista);

        String payload = buildLoginRequestJson("conta@example.com", "123456");

        mockMvc.perform(post("/api/artistas/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Conta Teste"))
                .andExpect(jsonPath("$.email").value("conta@example.com"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void deveFalharLoginEmailNaoEncontrado() throws Exception {
        String payload = buildLoginRequestJson("naoexiste@example.com", "123456");

        mockMvc.perform(post("/api/artistas/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Artista não encontrado")));
    }

    @Test
    void deveFalharLoginSenhaInvalida() throws Exception {
        Artista artista = Artista.builder()
                .conta(conta)
                .descricao("Artista teste")
                .categorias(List.of(TipoArte.ESCULTURA))
                .build();
        artistaRepository.save(artista);

        String payload = buildLoginRequestJson("conta@example.com", "errada");

        mockMvc.perform(post("/api/artistas/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Senha inválida")));
    }

    @Test
    void deveFalharLoginCamposVazios() throws Exception {
        String payload = buildLoginRequestJson("", "");

        mockMvc.perform(post("/api/artistas/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Falha de validacao nos campos.")));
    }

    // ---------- TESTES DE BUSCA / LISTAGEM ----------

    @Test
    void deveBuscarArtistaPorNomeComSucesso() throws Exception {
        Artista artista = Artista.builder()
                .conta(conta)
                .descricao("Artista de teste busca")
                .categorias(List.of(TipoArte.ESCULTURA))
                .build();
        artistaRepository.save(artista);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/artistas/buscar")
                        .param("nome", "Conta Teste")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Conta Teste"))
                .andExpect(jsonPath("$.descricao").value("Artista de teste busca"));
    }

    @Test
    void deveRetornarNotFoundQuandoNaoEncontrarArtista() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/artistas/buscar")
                        .param("nome", "Inexistente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Artista não encontrado")));
    }

    @Test
    void deveListarArtistasComSucesso() throws Exception {
        Artista artista = Artista.builder()
                .conta(conta)
                .descricao("Artista de Teste listagem")
                .categorias(List.of(TipoArte.PINTURA))
                .build();
        artistaRepository.save(artista);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/artistas/listar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Conta Teste"));
    }

    @Test
    void deveRetornarNotFoundQuandoNaoExistiremArtistas() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/artistas/listar")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Nenhum artista encontrado")));
    }

    @Test
    void deveFalharBuscarComNomeVazio() throws Exception {
        mockMvc.perform(get("/api/artistas/buscar")
                        .param("nome", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Nome para busca obrigatório")));
    }

    // ---------- TESTES DE ATUALIZAR / DELETAR ----------

    @Test
    void deveAtualizarArtistaComSucesso() throws Exception {
        Artista artista = Artista.builder()
                .conta(conta)
                .descricao("Artista de teste update")
                .categorias(List.of(TipoArte.PINTURA))
                .build();
        artistaRepository.save(artista);

        String payload = buildCadastroRequestJson(conta.getIdConta(), "Descricao nova", List.of("ESCULTURA"));

        mockMvc.perform(put("/api/artistas/atualizar")
                        .param("nome", "Conta Teste")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Descricao nova"))
                .andExpect(jsonPath("$.categorias[0]").value("ESCULTURA"));
    }

    @Test
    void deveDeletarArtistaComSucesso() throws Exception {
        Artista artista = Artista.builder()
                .conta(conta)
                .descricao("Artista de Teste Apagar")
                .categorias(List.of(TipoArte.PINTURA))
                .build();
        artistaRepository.save(artista);

        mockMvc.perform(delete("/api/artistas/deletar")
                        .param("nome", "Conta Teste"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveFalharDeletarArtistaNaoExistente() throws Exception {
        mockMvc.perform(delete("/api/artistas/deletar")
                        .param("nome", "NaoExiste"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Artista não encontrado")));
    }

    @Test
    void deveFalharDeletarComNomeVazio() throws Exception {
        mockMvc.perform(delete("/api/artistas/deletar")
                        .param("nome", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Nome obrigatório")));
    }
}
