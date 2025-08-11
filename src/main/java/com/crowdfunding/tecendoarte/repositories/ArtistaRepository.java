package com.crowdfunding.tecendoarte.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.crowdfunding.tecendoarte.models.Artista;

public interface ArtistaRepository extends JpaRepository<Artista, Long> {
    
    Optional<Artista> findByEmail(String email);
    Optional<Artista> findByNome(String nome);
    List<Artista> findAll();
    Boolean existsByEmail(String email);

}
