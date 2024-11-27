package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.config.JwtServices;
import com.example.bbva.squad2.Wallet.dtos.*;
import com.example.bbva.squad2.Wallet.enums.TransactionTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.AlkemyException;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.Transaction;
import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.services.TransactionService;
import com.example.bbva.squad2.Wallet.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
public class TransactionController {


    @Autowired
    private JwtServices js;

    @Autowired
    private UserService us;

   @Autowired
   private TransactionService ts;

    @PostMapping("/sendTransaction")
    @Operation(summary = "Enviar una transacción a otra cuenta")
    public ResponseEntity<String> sendTransaction(
            @RequestBody SendTransactionDTO request,
            HttpServletRequest httpRequest
    ) {
        ts.sendTransaction(request, httpRequest);
        return ResponseEntity.ok("Transacción finalizada exitosamente.");
    }

    @PostMapping("/deposit/{cbu}")
    @Operation(summary = "Realizar un deposito a una cuenta del usuario loggeado")
    public ResponseEntity<DepositDTO> deposit(
            @PathVariable String cbu,
            @RequestBody SendDepositDTO request,
            HttpServletRequest httpRequest) {
        DepositDTO deposit = ts.deposit(request, httpRequest, cbu);
        return ResponseEntity.ok(deposit);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener la transacción del usuario loggeado por id")
    public ResponseEntity<TransactionListDTO> getTransactionById(
            @PathVariable Long id, HttpServletRequest request) {
        UsuarioSeguridad userSecurity = us.getInfoUserSecurity(request);

        TransactionListDTO transaction = ts.getTransactionById(id, userSecurity.getId());

        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener las transacciones de usuarios por id")
    public ResponseEntity<List<TransactionListDTO>> listUserTransactions(
            @PathVariable Long userId,
            HttpServletRequest request
    ) {
        // Obtener el usuario desde el token JWT
        UsuarioSeguridad userSecurity = us.getInfoUserSecurity(request);

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
    public ResponseEntity<?> realizarPago(
            @RequestBody SendTransactionDTO request,
            HttpServletRequest httpRequest
    ) {
        try {
            // Validar monto mayor a cero
            if (request.getAmount() <= 0) {
                return ResponseEntity.badRequest().body("El monto debe ser mayor a cero.");
            }

            // Obtener el usuario autenticado a través del token
            UsuarioSeguridad userSecurity = us.getInfoUserSecurity(httpRequest);
            Optional<User> userOpt = us.findById(userSecurity.getId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autorizado.");
            }
            User user = userOpt.get();

            // Validar que el usuario tenga una cuenta con la moneda indicada
            Optional<Account> cuentaOpt = user.getAccounts().stream()
                    .filter(cuenta -> cuenta.getCurrency().equals(request.getCurrency()))
                    .findFirst();

            if (cuentaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("No se encontró una cuenta con la moneda especificada.");
            }

            Account cuenta = cuentaOpt.get();

            // Validar saldo suficiente en la cuenta
            if (cuenta.getBalance() < request.getAmount()) {
                return ResponseEntity.badRequest().body("Saldo insuficiente en la cuenta.");
            }

            // Crear y registrar la transacción de pago
            Transaction transaccion = ts.createTransaction(
                    cuenta,
                    cuenta.getCbu(),
                    "Pago - Sin destino",
                    request.getAmount(),
                    TransactionTypeEnum.PAGO,
                    request.getDescription()
            );

            // Actualizar el balance de la cuenta
            cuenta.setBalance(cuenta.getBalance() - request.getAmount());
            ts.actualizarBalanceCuenta(cuenta);

            // Construir la respuesta con la transacción y la cuenta afectada
            return ResponseEntity.ok(
                    Map.of(
                            "transaccion", transaccion,
                            "cuenta", cuenta
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al realizar el pago: " + e.getMessage());
        }
    }

}


