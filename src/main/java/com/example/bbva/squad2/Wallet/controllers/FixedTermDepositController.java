package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.config.JwtServices;
import com.example.bbva.squad2.Wallet.dtos.FixedTermDTO;
import com.example.bbva.squad2.Wallet.dtos.FixedTermSimulationDTO;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import com.example.bbva.squad2.Wallet.services.FixedTermDepositService;
import com.example.bbva.squad2.Wallet.services.UsuarioLoggeadoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fixed-term-deposits")
public class FixedTermDepositController {

    @Autowired
    private FixedTermDepositService fixedTermDepositService;

    @Autowired
    private JwtServices jwtServices;

    @Autowired
    private UsuarioLoggeadoService usuarioLoggeadoService;

    @GetMapping
    @Operation(summary = "Obtener los plazos fijos del usuario loggeado")
    public ResponseEntity<List<FixedTermDTO>> getAllFixedTermDeposits(HttpServletRequest request) {
        UsuarioSeguridad userDetails = usuarioLoggeadoService.getInfoUserSecurity(request);

        return ResponseEntity.ok(fixedTermDepositService.getFixedTermDepositsByUserId(userDetails.getId()));
    }

    @PostMapping("/fixedTerm")
    @Operation(summary = "Crear un plazo fijo para el usuario loggeado")
    public ResponseEntity<FixedTermSimulationDTO> createFixedTermDeposit(
            @RequestParam Double amount,
            @RequestParam Integer days,
            HttpServletRequest request) {

        // Obtener usuario autenticado desde el token
        UsuarioSeguridad userDetails = usuarioLoggeadoService.getInfoUserSecurity(request);
        FixedTermSimulationDTO fixedTermDeposit = fixedTermDepositService.createFixedTermDeposit(userDetails.getId(), amount, days, false);
        return ResponseEntity.status(HttpStatus.CREATED).body(fixedTermDeposit);
    }

    @PostMapping("/fixedTerm/simulate")
    @Operation(summary = "Simular un plazo fijo para el usuario loggeado")
    public ResponseEntity<FixedTermSimulationDTO> createFixedTermDepositSimulation(
            @RequestBody Double amount,
            @RequestBody Integer days,
            HttpServletRequest request) {

        // Obtener usuario autenticado desde el token
        UsuarioSeguridad userDetails = usuarioLoggeadoService.getInfoUserSecurity(request);
        FixedTermSimulationDTO fixedTermDeposit = fixedTermDepositService.createFixedTermDeposit(userDetails.getId(), amount, days, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(fixedTermDeposit);
    }

}
