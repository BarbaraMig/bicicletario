package com.bikeunirio.bicicletario.externo.service;

import com.bikeunirio.bicicletario.externo.entity.Cobranca;
import com.bikeunirio.bicicletario.externo.repository.CobrancaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CobrancaService {

    private final CobrancaRepository cobrancaRepository;
    //Explicitando dependências
    public CobrancaService(CobrancaRepository cobrancaRepository){
        this.cobrancaRepository = cobrancaRepository;
    }

    public Cobranca realizarCobranca(){

        return null;
    }

    public Optional<Cobranca> obterCobranca(long id){
        //Optional não permite retornar null mas pode retornar um objeto vazio
        return cobrancaRepository.findById(id);
    }

    public void adicionarFilaCobranca(){
        //entrega 2
    }

    public void processaCobrancasEmFila(){
        //entrega 2
    }

    public void validarCartaoCredito(){
        //validação é feita com uma cobrança de 1 centavo
    }
}
