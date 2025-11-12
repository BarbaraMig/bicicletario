package com.bikeunirio.bicicletario.externo.mapper;

import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import com.bikeunirio.bicicletario.externo.entity.Email;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

//informa que será usado em conjunto com o Mockito
@ExtendWith(MockitoExtension.class)
public class EmailMapperTest {
    //não precisa de @Mock pois nenhuma classe será simulada

    @InjectMocks
    private EmailMapper emailMapper;

    @BeforeEach
    void setup(){
        emailMapper = new EmailMapper();
    }
    @Test
    public void testEmailParaDto(){
        //criação do email que será convertido
        Email email = new Email("emailexternoes2@gmail.com","Teste Assunto","Teste Mensagem");
        //conversão
        EmailDto dto = emailMapper.emailParaDto(email);
        assertEquals("emailexternoes2@gmail.com",dto.getReceptor());
        assertEquals("Teste Assunto",dto.getAssunto());
        assertEquals("Teste Mensagem", dto.getMensagem());


    }
    @Test
    public void testDtoParaEmail(){

    }

    @Test
    public void testRetornaNull(){
        assertNull(emailMapper.emailParaDto(null));
        assertNull(emailMapper.dtoParaEntidade(null));
    }

}
