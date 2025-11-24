package com.bikeunirio.bicicletario.externo.zmudancas.repository;

import com.bikeunirio.bicicletario.externo.zmudancas.entity.Cobranca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CobrancaLogsRepository extends JpaRepository<Cobranca, Long> {
}
