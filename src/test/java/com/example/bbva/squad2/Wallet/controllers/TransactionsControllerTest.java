package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.*;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.WalletsException;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;
import com.example.bbva.squad2.Wallet.services.TransactionService;
import com.example.bbva.squad2.Wallet.services.UsuarioLoggeadoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
public class TransactionsControllerTest {


    @Mock
    AccountsRepository accountsRepository;

    @Mock
    private TransactionService ts;

    @Mock
    private UsuarioLoggeadoService usuarioLoggeadoService;

    @InjectMocks
    private TransactionController tc;


    @Test
    void testSendTransactionSuccess() {
        SendTransactionDTO transactionDTO = SendTransactionDTO.builder()
                .destinationCbu("3948772355226879949513")
                .amount(1500.00)
                .currency(CurrencyTypeEnum.ARS)
                .description("Testeo")
                .build();


        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("username", "pepe.gimenez@yopmail.com");

        UsuarioSeguridad mockUsuarioSeguridad = new UsuarioSeguridad();
        mockUsuarioSeguridad.setUsername("pepe.gimenez@yopmail.com");
        when(usuarioLoggeadoService.getInfoUserSecurity(request))
                .thenReturn(mockUsuarioSeguridad);

        Mockito.doNothing().when(ts).sendTransaction(transactionDTO, "pepe.gimenez@yopmail.com");


        ResponseEntity<String> result = tc.sendTransaction(transactionDTO, request);

        Mockito.verify(ts).sendTransaction(transactionDTO, "pepe.gimenez@yopmail.com");

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertEquals("Transacción finalizada exitosamente.", result.getBody());
    }

    @Test
    void testSendTransactionToHimselfFail() {
        SendTransactionDTO transactionDTO = SendTransactionDTO.builder()
                .destinationCbu("2324267237237")
                .amount(1500.00)
                .currency(CurrencyTypeEnum.ARS)
                .description("Testeo")
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("username", "pepe.gimenez@yopmail.com");

        UsuarioSeguridad mockUsuarioSeguridad = new UsuarioSeguridad();
        mockUsuarioSeguridad.setUsername("pepe.gimenez@yopmail.com");

        when(usuarioLoggeadoService.getInfoUserSecurity(request))
                .thenReturn(mockUsuarioSeguridad);

        Mockito.doThrow(new WalletsException(HttpStatus.BAD_REQUEST, "No se puede realizar una transferencia a una cuenta propia."))
                .when(ts).sendTransaction(transactionDTO, mockUsuarioSeguridad.getUsername());

        WalletsException thrown = assertThrows(
                WalletsException.class,
                () -> tc.sendTransaction(transactionDTO, request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
        assertEquals("No se puede realizar una transferencia a una cuenta propia.", thrown.getMessage());
    }


    @Test
    void testDeposit() {
        SendDepositDTO sendDepositDTO = SendDepositDTO.builder()
                .amount(1500.00)
                .description("Testeo 2")
                .build();

        TransactionBalanceDTO transactionBalanceDTO = TransactionBalanceDTO.builder()
                .cbuDestino("2324267237237")
                .cbuOrigen("2424267237237")
                .amount(1500.00)
                .currency(CurrencyTypeEnum.ARS)
                .description("Test transaction")
                .build();

        AccountDTO accountDTO = AccountDTO.builder()
                .id(1L)
                .cbu("2324267237237")
                .currency(CurrencyTypeEnum.ARS)
                .transactionLimit(1000.00)
                .balance(5000.00)
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("username", "pepe.gimenez@yopmail.com");

        UsuarioSeguridad mockUsuarioSeguridad = new UsuarioSeguridad();
        mockUsuarioSeguridad.setUsername("pepe.gimenez@yopmail.com");

        Mockito.when(usuarioLoggeadoService.getInfoUserSecurity(request))
                .thenReturn(mockUsuarioSeguridad);

        DepositDTO expectedDeposit = new DepositDTO(transactionBalanceDTO, accountDTO);

        Mockito.when(ts.deposit(sendDepositDTO, "2428424248242442", mockUsuarioSeguridad.getUsername()))
                .thenReturn(expectedDeposit);

        ResponseEntity<DepositDTO> result = tc.deposit("2428424248242442", sendDepositDTO, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedDeposit, result.getBody());
    }

    @Test
    void testDepositFail() {
        SendDepositDTO sendDepositDTO = SendDepositDTO.builder()
                .amount(1500.00)
                .description("Testeo 3")
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("username", "pepe.gimenez@yopmail.com");

        UsuarioSeguridad mockUsuarioSeguridad = new UsuarioSeguridad();
        mockUsuarioSeguridad.setUsername("pepe.gimenez@yopmail.com");

        Mockito.when(usuarioLoggeadoService.getInfoUserSecurity(request))
                .thenReturn(mockUsuarioSeguridad);

        Mockito.doThrow(new WalletsException(HttpStatus.BAD_REQUEST, "El monto a depositar debe ser mayor a cero."))
                .when(ts).deposit(sendDepositDTO, "2428424248242442", mockUsuarioSeguridad.getUsername());

        WalletsException thrown = assertThrows(
                WalletsException.class,
                () -> tc.deposit("2428424248242442", sendDepositDTO, request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
        assertEquals("El monto a depositar debe ser mayor a cero.", thrown.getMessage());
    }



}
