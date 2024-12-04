package com.example.bbva.squad2.Wallet.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Visualización de usuarios por parte de un administrador
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldReturnUserListForAdmin() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists());
    }


    // Visualización de cuentas de un usuario por parte de un administrador
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldReturnAccountsForSpecificUser() throws Exception {
        Long userId = 1L; // ID del usuario
        mockMvc.perform(get("/accounts/{userId}", userId))
                .andDo(print()) // Muestra la respuesta del endpoint
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists()); // Ajustar si el campo es "id"
    }

    // Envío de pesos a otro usuario
    @Test
    //@WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void shouldSendTransactionSuccessfully() throws Exception {
        String transactionJson = """
    {
        "currency": "ARS",
        "destinationCbu": "1234567890123456789012",
        "amount": 1000,
        "description": "Transferencia de prueba"
    }
    """;
        mockMvc.perform(post("/transactions/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson))
                .andExpect(status().isOk()) // Verifica que el estado sea 200 (OK)
                .andExpect(content().string("Transacción finalizada exitosamente.")); // Verifica el mensaje de éxito
    }



}
