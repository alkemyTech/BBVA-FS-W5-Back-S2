package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.config.JwtServices;
import com.example.bbva.squad2.Wallet.dtos.FixedTermDTO;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import com.example.bbva.squad2.Wallet.models.FixedTermDeposit;
import com.example.bbva.squad2.Wallet.services.FixedTermDepositService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/fixed-term-deposits")
public class FixedTermDepositController {

    @Autowired
    private final FixedTermDepositService fixedTermDepositService;

    @Autowired
    private JwtServices jwtServices;

    public FixedTermDepositController(FixedTermDepositService fixedTermDepositService) {
        this.fixedTermDepositService = fixedTermDepositService;
    }

    @GetMapping
    public ResponseEntity<List<FixedTermDTO>> getAllFixedTermDeposits() {
        List<FixedTermDTO> fixedTerms = fixedTermDepositService.getAllFixedTermDeposits()
                .stream()
                .map(fixedTerm -> new FixedTermDTO().mapFromFixedTerm(fixedTerm))
                .collect(Collectors.toList());

        return ResponseEntity.ok(fixedTerms);
    }

    @PostMapping("/fixedTerm")
    public ResponseEntity<?> createFixedTermDeposit(
            @RequestParam Double amount,
            @RequestParam Integer days,
            HttpServletRequest request) {

        // Obtener usuario autenticado desde el token
        final String authHeader = request.getHeader("Authorization");

        String token = authHeader.substring(7);
        UsuarioSeguridad userDetails = jwtServices.validateAndGetSecurity(token);

        try {
            ResponseEntity<FixedTermDTO> fixedTermDeposit = fixedTermDepositService.createFixedTermDeposit(userDetails.getId(), amount, days);
            return ResponseEntity.status(HttpStatus.CREATED).body(fixedTermDeposit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
