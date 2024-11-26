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
    public ResponseEntity<TransactionListDTO> getTransactionById(
            @PathVariable Long id, HttpServletRequest request) {
        UsuarioSeguridad userSecurity = us.getInfoUserSecurity(request);

        Optional<TransactionListDTO> transaction = ts.getTransactionById(id, userSecurity.getId());

        return transaction
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }



    // empece a realizar la ful 38 (hugo)

    @GetMapping("/user/{userId}")
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
}

