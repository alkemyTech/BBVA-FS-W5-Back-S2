package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.config.JwtServices;
import com.example.bbva.squad2.Wallet.dtos.FixedTermDTO;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import com.example.bbva.squad2.Wallet.exceptions.WalletsException;
import com.example.bbva.squad2.Wallet.services.FixedTermDepositService;
import com.example.bbva.squad2.Wallet.services.UsuarioLoggeadoService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Autowired
    private UsuarioLoggeadoService usuarioLoggeadoService;

    public FixedTermDepositController(FixedTermDepositService fixedTermDepositService) {
        this.fixedTermDepositService = fixedTermDepositService;
    }

    @GetMapping
    @Operation(summary = "Obtener los plazos fijos del usuario loggeado")
    public ResponseEntity<List<FixedTermDTO>> getAllFixedTermDeposits(HttpServletRequest request) {
        UsuarioSeguridad userDetails = usuarioLoggeadoService.getInfoUserSecurity(request);

        List<FixedTermDTO> fixedTerms = fixedTermDepositService.getFixedTermDepositsByUserId(userDetails.getId())
                .stream()
                .map(fixedTerm -> new FixedTermDTO().mapFromFixedTerm(fixedTerm))
                .collect(Collectors.toList());

        return ResponseEntity.ok(fixedTerms);
    }

    @PostMapping("/fixedTerm")
    @Operation(summary = "Crear un plazo fijo para el usuario loggeado")
    public ResponseEntity<?> createFixedTermDeposit(
            @RequestParam Double amount,
            @RequestParam Integer days,
            HttpServletRequest request) {

        // Obtener usuario autenticado desde el token
        UsuarioSeguridad userDetails = usuarioLoggeadoService.getInfoUserSecurity(request);

        try {
            ResponseEntity<Object> fixedTermDeposit = fixedTermDepositService.createFixedTermDeposit(userDetails.getId(), amount, days, false);
            return ResponseEntity.status(HttpStatus.CREATED).body(fixedTermDeposit);
        } catch (WalletsException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        } catch (Exception e) {
            // Captura de otras excepciones genéricas
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/fixedTerm/simulate")
    @Operation(summary = "Simular un plazo fijo para el usuario loggeado")
    public ResponseEntity<?> createFixedTermDepositSimulation(
            @RequestBody Double amount,
            @RequestBody Integer days,
            HttpServletRequest request) {

        // Obtener usuario autenticado desde el token
        UsuarioSeguridad userDetails = usuarioLoggeadoService.getInfoUserSecurity(request);

        try {
            ResponseEntity<Object> fixedTermDeposit = fixedTermDepositService.createFixedTermDeposit(userDetails.getId(), amount, days, true);
            return ResponseEntity.status(HttpStatus.CREATED).body(fixedTermDeposit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
