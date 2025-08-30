package com.crowdfunding.tecendoarte.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.crowdfunding.tecendoarte.models.Conta;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    boolean existsByEmail(String email);
    Optional<Conta> findByEmail(String email);
}