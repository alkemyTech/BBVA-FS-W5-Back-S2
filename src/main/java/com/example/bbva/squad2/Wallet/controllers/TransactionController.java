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
    public ResponseEntity<String> sendTransaction(
            @RequestBody SendTransactionDTO request,
            HttpServletRequest httpRequest
    ) {
        ts.sendTransaction(request, httpRequest);
        return ResponseEntity.ok("Transacción finalizada exitosamente.");
    }


    @PostMapping("/deposit/{cbu}")
    public ResponseEntity<DepositDTO> deposit(
            @PathVariable String cbu,
            @RequestBody SendDepositDTO request,
            HttpServletRequest httpRequest) {
        DepositDTO deposit = ts.deposit(request, httpRequest, cbu);
        return ResponseEntity.ok(deposit);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionBalanceDTO> obtenerTransactionBalanceDTO(@PathVariable Long id, HttpServletRequest request) {
        UsuarioSeguridad userSecurity = us.getInfoUserSecurity(request);
        Optional<User> user = us.findById(userSecurity.getId());
        if (user.isPresent()) {
            boolean isOwner = ts.isTransactionOwnedByUser(id, user.get().getId());
            if (isOwner) {
                Optional<TransactionBalanceDTO> transaction = ts.getTransactionById(id);
                return transaction.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
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
            accountsRepository.save(account);

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


