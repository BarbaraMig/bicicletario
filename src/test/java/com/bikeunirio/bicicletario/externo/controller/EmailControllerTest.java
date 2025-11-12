package com.bikeunirio.bicicletario.externo.controller;

import com.bikeunirio.bicicletario.externo.service.EmailService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class EmailControllerTest {
    @Mock
    EmailService emailService;

    @InjectMocks
    EmailController emailController;

    @Test
    public void testEnviarEmail(){

    }
}
