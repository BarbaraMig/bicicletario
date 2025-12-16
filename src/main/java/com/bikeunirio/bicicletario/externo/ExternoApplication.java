package com.bikeunirio.bicicletario.externo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;

@SpringBootApplication(exclude = MailSenderAutoConfiguration.class)

public class ExternoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExternoApplication.class, args);
	}

}
