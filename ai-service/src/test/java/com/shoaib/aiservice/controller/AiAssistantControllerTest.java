package com.shoaib.aiservice.controller;

import com.shoaib.aiservice.exception.GlobalExceptionHandler;
import com.shoaib.aiservice.service.AiAssistantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AiAssistantControllerTest {
    private MockMvc mockMvc;

    @BeforeEach void setUp() {
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        AiAssistantService unusedService = (userId, request) -> { throw new AssertionError("Invalid requests must not reach the service"); };
        mockMvc = MockMvcBuilders.standaloneSetup(new AiAssistantController(unusedService))
                .setControllerAdvice(new GlobalExceptionHandler()).setValidator(validator).build();
    }

    @Test void rejectsBlankQuestion() throws Exception {
        mockMvc.perform(post("/ai/ask").header("X-User-Id", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON).content("{\"question\":\"   \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test void rejectsOversizedQuestion() throws Exception {
        String body = "{\"question\":\"" + "a".repeat(2001) + "\"}";
        mockMvc.perform(post("/ai/ask").header("X-User-Id", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
