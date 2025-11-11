package com.bikeunirio.bicicletario.externo.repository;

import com.bikeunirio.bicicletario.externo.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    //Ta certo isso??


}
