package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.*;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.enums.TransactionTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.WalletsException;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TransactionsControllerTest {




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

    @Test
    void testGetTransactionById() {
        Long transactionId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("username", "pepe.gimenez@yopmail.com");

        UsuarioSeguridad mockUsuarioSeguridad = new UsuarioSeguridad();
        mockUsuarioSeguridad.setUsername("pepe.gimenez@yopmail.com");

        when(usuarioLoggeadoService.getInfoUserSecurity(request)).thenReturn(mockUsuarioSeguridad);

        TransactionListDTO expectedTransaction = new TransactionListDTO(1L, "1234567891234567891234", "1234567891234567891234", 1000.00, TransactionTypeEnum.INGRESO, "Prueba", LocalDateTime.now());

        when(ts.getTransactionById(transactionId, mockUsuarioSeguridad.getId())).thenReturn(expectedTransaction);

        ResponseEntity<TransactionListDTO> result = tc.getTransactionById(transactionId, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedTransaction, result.getBody());
    }

    @Test
    void testGetTransactionByIdFail() {
        Long transactionId = 1L;

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("username", "pepe.gimenez@yopmail.com");

        UsuarioSeguridad mockUsuarioSeguridad = new UsuarioSeguridad();
        mockUsuarioSeguridad.setUsername("pepe.gimenez@yopmail.com");

        when(usuarioLoggeadoService.getInfoUserSecurity(request)).thenReturn(mockUsuarioSeguridad);

        when(ts.getTransactionById(transactionId, mockUsuarioSeguridad.getId()))
                .thenThrow(new WalletsException(HttpStatus.NOT_FOUND, "Transacción no encontrada"));

        WalletsException thrown = assertThrows(
                WalletsException.class,
                () -> tc.getTransactionById(transactionId, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
        assertEquals("Transacción no encontrada", thrown.getMessage());
    }

    @Test
    void testUpdateTransactionDescription() {
        Long transactionId = 1L;
        String newDescription = "Descripción actualizada";
        UpdateTransactionDTO updateTransactionDTO = new UpdateTransactionDTO(newDescription);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("username", "pepe.gimenez@yopmail.com");

        UsuarioSeguridad mockUsuarioSeguridad = new UsuarioSeguridad();
        mockUsuarioSeguridad.setUsername("pepe.gimenez@yopmail.com");

        // Mockea la llamada al servicio para devolver el usuario loggeado
        when(usuarioLoggeadoService.getInfoUserSecurity(request)).thenReturn(mockUsuarioSeguridad);

        // Mockea la llamada al servicio para actualizar la descripción de la transacción
        when(ts.updateTransactionDescription(transactionId, newDescription, mockUsuarioSeguridad.getId()))
                .thenReturn(updateTransactionDTO);

        // Llama al método del controlador
        ResponseEntity<UpdateTransactionDTO> result = tc.updateTransactionDescription(transactionId, updateTransactionDTO, request);

        // Verifica que el status de la respuesta sea OK y el mensaje sea el esperado
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(updateTransactionDTO, result.getBody());
    }

    @Test
    void testListUserTransactions() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("username", "pepe.gimenez@yopmail.com");

        UsuarioSeguridad mockUsuarioSeguridad = new UsuarioSeguridad();
        mockUsuarioSeguridad.setUsername("pepe.gimenez@yopmail.com");

        when(usuarioLoggeadoService.getInfoUserSecurity(request)).thenReturn(mockUsuarioSeguridad);

        List<TransactionListDTO> transactionList = List.of(
                new TransactionListDTO(1L, "1234567891234567891234", "1234567891234567891234", 1000.00, TransactionTypeEnum.INGRESO, "Prueba", LocalDateTime.now())
        );

        when(ts.getTransactionDtosByUserId(mockUsuarioSeguridad.getId())).thenReturn(transactionList);

        ResponseEntity<List<TransactionListDTO>> result = tc.listUserTransactions(mockUsuarioSeguridad.getId(), request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(transactionList, result.getBody());
    }

    @Test
    void testRealizarPago() {
        Long accountId = 1L;
        double amount = 1500.00;
        String description = "Pago de prueba";

        SendPaymentDTO sendPaymentDTO = new SendPaymentDTO("1234", 1000.0, CurrencyTypeEnum.ARS, "Prueba");

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

        DepositDTO expectedDeposit = new DepositDTO(transactionBalanceDTO, accountDTO);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("username", "pepe.gimenez@yopmail.com");

        UsuarioSeguridad mockUsuarioSeguridad = new UsuarioSeguridad();
        mockUsuarioSeguridad.setUsername("pepe.gimenez@yopmail.com");

        when(usuarioLoggeadoService.getInfoUserSecurity(request)).thenReturn(mockUsuarioSeguridad);

        // Aquí cambiamos doNothing() por when() porque payment probablemente devuelve un valor.
        when(ts.payment(sendPaymentDTO, mockUsuarioSeguridad.getId()))
                .thenReturn(expectedDeposit);

        // Llama al método del controlador
        ResponseEntity<DepositDTO> result = tc.realizarPago(sendPaymentDTO, request);

        // Verifica que el método de servicio haya sido invocado correctamente
        Mockito.verify(ts).payment(sendPaymentDTO, mockUsuarioSeguridad.getId());

        // Verifica que el estado de la respuesta sea OK y el mensaje sea el esperado
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedDeposit, result.getBody());
    }

    @Test
    void testEnviarDinero() {
        SendTransactionDTO sendTransactionDTO = SendTransactionDTO.builder()
                .destinationCbu("3948772355226879949513")
                .amount(2000.00)
                .currency(CurrencyTypeEnum.ARS)
                .description("Envio de dinero")
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("username", "pepe.gimenez@yopmail.com");

        UsuarioSeguridad mockUsuarioSeguridad = new UsuarioSeguridad();
        mockUsuarioSeguridad.setUsername("pepe.gimenez@yopmail.com");

        when(usuarioLoggeadoService.getInfoUserSecurity(request)).thenReturn(mockUsuarioSeguridad);

        Mockito.doNothing().when(ts).sendTransaction(sendTransactionDTO, mockUsuarioSeguridad.getUsername());

        ResponseEntity<String> result = tc.enviarDinero(sendTransactionDTO, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Transacción finalizada exitosamente.", result.getBody());
    }

}
