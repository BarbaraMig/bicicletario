package com.bikeunirio.bicicletario.externo.mapper;


import com.bikeunirio.bicicletario.externo.dto.EmailDto;
import com.bikeunirio.bicicletario.externo.entity.Email;
import org.springframework.stereotype.Component;

@Component
public class EmailMapper {
     public EmailDto emailParaDto(Email emailEntidade){
         if(emailEntidade == null)
             return null;
         EmailDto dto = new EmailDto();
         dto.setId(emailEntidade.getId());
         dto.setReceptor(emailEntidade.getReceptor());
         dto.setAssunto(emailEntidade.getAssunto());
         dto.setMensagem(emailEntidade.getMensagem());
         dto.setHoraEnvio(emailEntidade.getHoraEnvio());
         return dto;
     }

     public Email dtoParaEntidade(EmailDto dto){
         if(dto == null)
             return null;
         return new Email(dto.getId(),
                 dto.getReceptor(),
                 dto.getAssunto(),
                 dto.getMensagem(),
                 dto.getHoraEnvio());
     }

}
