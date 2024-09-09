package br.com.fiap.ms_pagamento.controller;

import br.com.fiap.ms_pagamento.dto.PagamentoDTO;
import br.com.fiap.ms_pagamento.model.Pagamento;
import br.com.fiap.ms_pagamento.model.Status;
import br.com.fiap.ms_pagamento.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PagamentoControllerIT {
    @Autowired
    private MockMvc mockMvc;

    private Long existingId;
    private Long nonExistingId;
    private PagamentoDTO pagamentoDTO;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistingId  = 50L;
        pagamentoDTO = Factory.createPagamentoDTO();
    }

    @Test
    public void findAllShouldReturnListAllPagamentos() throws Exception {
        mockMvc.perform(get("/pagamentos")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].nome").isString())
                .andExpect(jsonPath("[0].nome").value("Nicodemus C Souza"))
                .andExpect(jsonPath("[5].status").value("CONFIRMADO"));

    }

    @Test
    public void findByIdShouldReturnPagamentoDTOWhenIdExists() throws Exception {
        mockMvc.perform(get("/pagamentos/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("nome").isString())
                .andExpect(jsonPath("nome").value("Nicodemus C Souza"))
                .andExpect(jsonPath("status").value("CRIADO"));

    }

    @Test
    public void findByIdShouldReturnResourceNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/pagamentos/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


    }

    @Test
    public void insertShouldReturnPagamentoDTO() throws Exception {
        Pagamento entity = Factory.createPagamento();
        entity.setId(null);
        String jsonBody = objectMapper.writeValueAsString(entity);

        mockMvc.perform(post("/pagamentos")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.valor").exists())
                .andExpect(jsonPath("$.nome").exists());
    }

    @Test
    public void insertShouldPersistPagamentoWithRequiredFields() throws Exception {
        Pagamento entity = new Pagamento(null, BigDecimal.valueOf(25.25), null, null, null,
                null, Status.CRIADO, 7L, 1L);

        String jsonBody = objectMapper.writeValueAsString(entity);

        mockMvc.perform(post("/pagamentos")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.valor").exists())
                .andExpect(jsonPath("$.nome").isEmpty());

    }


}
