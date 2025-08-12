package com.crowdfunding.tecendoarte.config;

import com.crowdfunding.tecendoarte.models.Administrador;
import com.crowdfunding.tecendoarte.models.enums.PermissaoAdministrador;
import com.crowdfunding.tecendoarte.repositories.AdministradorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Set;

@Configuration
@Profile("dev")
public class DevDataSeederConfig {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeederConfig.class);

    @Value("${seed.admin.enabled:true}")
    private boolean seedEnabled;

    @Value("${seed.admin.nome:Admin}")
    private String adminNome;

    @Value("${seed.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${seed.admin.password-hash:}")
    private String adminPasswordHash;

    @Bean
    CommandLineRunner seedAdmin(AdministradorRepository administradorRepository) {
        return args -> {
            if (!seedEnabled) {
                return;
            }
            if (administradorRepository.existsByEmail(adminEmail)) {
                log.info("Administrador ja existe: {}", adminEmail);
                return;
            }
            if (adminPasswordHash == null || adminPasswordHash.isBlank()) {
                log.warn("Seed de admin habilitado, mas 'seed.admin.password-hash' não foi definido. Não criando administrador.");
                return;
            }
            Administrador admin = Administrador.builder()
                    .nome(adminNome)
                    .email(adminEmail)
                    .senha(adminPasswordHash)
                    .permissoes(Set.of(PermissaoAdministrador.GERENCIAR_USUARIOS))
                    .build();
            administradorRepository.save(admin);
            log.info("Administrador criado via seed: {}", adminEmail);
        };
    }
}
