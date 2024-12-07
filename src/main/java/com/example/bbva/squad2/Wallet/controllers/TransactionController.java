package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.config.JwtServices;
import com.example.bbva.squad2.Wallet.dtos.*;
import com.example.bbva.squad2.Wallet.enums.Concept;
import com.example.bbva.squad2.Wallet.services.TransactionService;
import com.example.bbva.squad2.Wallet.services.UsuarioLoggeadoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/transactions")
public class TransactionController {


    @Autowired
    private JwtServices js;

    @Autowired
    private UsuarioLoggeadoService usuarioLoggeadoService;

   @Autowired
   private TransactionService ts;

    @PostMapping("/send")
    @Operation(summary = "Enviar una transacción a otra cuenta")
    public ResponseEntity<String> sendTransaction(
            @RequestBody SendTransactionDTO request,
            HttpServletRequest httpRequest
    ) {
        UsuarioSeguridad usuarioSeguridad = usuarioLoggeadoService.getInfoUserSecurity(httpRequest);
        ts.sendTransaction(request, usuarioSeguridad.getUsername());
        return ResponseEntity.ok("Transacción finalizada exitosamente.");
    }


    @PostMapping("/deposit/{cbu}")
    @Operation(summary = "Realizar un deposito a una cuenta del usuario loggeado")
    public ResponseEntity<DepositDTO> deposit(
            @PathVariable String cbu,
            @RequestBody SendDepositDTO request,
            HttpServletRequest httpRequest
    ) {
        UsuarioSeguridad usuarioSeguridad = usuarioLoggeadoService.getInfoUserSecurity(httpRequest);
        DepositDTO deposit = ts.deposit(request, cbu, usuarioSeguridad.getUsername());
        return ResponseEntity.ok(deposit);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Obtener la transacción del usuario loggeado por id")
    public ResponseEntity<TransactionListDTO> getTransactionById(
            @PathVariable Long id, HttpServletRequest request) {
        UsuarioSeguridad userSecurity = usuarioLoggeadoService.getInfoUserSecurity(request);

        TransactionListDTO transaction = ts.getTransactionById(id, userSecurity.getId());

        return ResponseEntity.ok(transaction);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Editar una transacción para modificar solo la descripción")
    public ResponseEntity<UpdateTransactionDTO> updateTransactionDescription(
            @PathVariable Long id,
            @RequestBody UpdateTransactionDTO updateRequest,
            HttpServletRequest request) {

        // Obtener el usuario autenticado desde el token JWT
        UsuarioSeguridad userSecurity = usuarioLoggeadoService.getInfoUserSecurity(request);

        // Llamar al servicio para actualizar la transacción
        UpdateTransactionDTO updatedTransaction = ts.updateTransactionDescription(id, updateRequest.getDescription(), userSecurity.getId());

        // Si la transacción no fue encontrada, devolver un error
        if (updatedTransaction == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Retornar la transacción actualizada
        return ResponseEntity.ok(updatedTransaction);
    }


    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener las transacciones de usuarios por id")
    public ResponseEntity<List<TransactionListDTO>> listUserTransactions(
            @PathVariable Long userId,
            HttpServletRequest request
    ) {
        // Obtener el usuario desde el token JWT
        UsuarioSeguridad userSecurity = usuarioLoggeadoService.getInfoUserSecurity(request);

        // Validar si el usuario tiene el rol ADMIN o es el dueño de las transacciones
        boolean isAdmin = "ADMIN".equals(userSecurity.getRole());
        boolean isOwner = Objects.equals(userSecurity.getId(), userId);

        if (!isAdmin && !isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Obtener las transacciones desde el servicio
        List<TransactionListDTO> transactions = ts.getTransactionDtosByUserId(userId);

        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/payment")
    @Operation(summary = "Realizar un pago por el usuario loggeado")
    public ResponseEntity<DepositDTO> realizarPago(
            @RequestBody SendPaymentDTO request,
            HttpServletRequest httpRequest
    ) {

        UsuarioSeguridad userSecurity = usuarioLoggeadoService.getInfoUserSecurity(httpRequest);
        DepositDTO payment = ts.payment(request, userSecurity.getId());
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/sendRecipient")
    @Operation(summary = "Enviar dinero a un beneficiario")
    public ResponseEntity<String> enviarDinero(
            @RequestBody SendTransactionDTO request,
            HttpServletRequest httpRequest) {
        UsuarioSeguridad userSecurity = usuarioLoggeadoService.getInfoUserSecurity(httpRequest);
        ts.sendTransactionToBeneficiario(request, userSecurity.getUsername());
        return ResponseEntity.ok("Transacción finalizada exitosamente.");
    }

    @GetMapping("/user/{userId}/paginated")
    @Operation(summary = "Obtener las transacciones paginadas de un usuario")
    public ResponseEntity<?> listUserTransactionsPaginated(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        UsuarioSeguridad userSecurity = usuarioLoggeadoService.getInfoUserSecurity(request);

        // Validar si el usuario tiene permisos
        boolean isAdmin = "ADMIN".equals(userSecurity.getRole());
        boolean isOwner = Objects.equals(userSecurity.getId(), userId);
        if (!isAdmin && !isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Falta URLS

        // Llamar al servicio para obtener las transacciones paginadas
        PageableResponseDTO<TransactionListDTO> paginatedTransactions = ts.getTransactionsByUserIdPaginated(userId, page, size);

        return ResponseEntity.ok(paginatedTransactions);
    }


}


