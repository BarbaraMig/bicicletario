package com.bikeunirio.bicicletario.externo.repository;

import com.bikeunirio.bicicletario.externo.entity.Cobranca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CobrancaRepository extends JpaRepository<Cobranca, Long> {
}
