package com.example.bbva.squad2.Wallet.controllers;

import java.util.List;

import com.example.bbva.squad2.Wallet.config.JwtServices;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.bbva.squad2.Wallet.dtos.AccountDTO;
import com.example.bbva.squad2.Wallet.services.AccountService;

@RestController
@RequestMapping("/accounts")
public class AccountController {

	@Autowired
	private AccountService as;

	@Autowired
	private JwtServices js;
	
	@GetMapping("/{id}")
	public ResponseEntity<List<AccountDTO>> getAccounts(@PathVariable Long id) throws Exception {
	    List<AccountDTO> accountsByUser = as.getAccountsByUser(id);
	  
	    return ResponseEntity.ok(accountsByUser);
	}

	@PostMapping("/accounts")
	public ResponseEntity<AccountDTO> createAccount(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		UsuarioSeguridad usuarioSeguridad = js.validateAndGetSecurity(token);

		return ResponseEntity.ok(as.createAccount(usuarioSeguridad.getId()));
	}

}
