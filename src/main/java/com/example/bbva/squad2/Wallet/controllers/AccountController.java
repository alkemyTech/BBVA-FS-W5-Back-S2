package com.example.bbva.squad2.Wallet.controllers;

import java.util.List;
import java.util.Objects;

import com.example.bbva.squad2.Wallet.config.JwtServices;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.AlkemyException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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


	@PostMapping("/{currency}")
	public ResponseEntity<AccountDTO> createAccount(HttpServletRequest request,
													@PathVariable CurrencyTypeEnum currency
													) {
		final String authHeader = request.getHeader("Authorization");
		final String token;
		if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ")) {
			throw new AlkemyException(HttpStatus.UNAUTHORIZED, "Invalid or missing Authorization header");
		}
		token = authHeader.substring(7);
		UsuarioSeguridad security = js.validateAndGetSecurity(token);
		Long userId = security.getId();

		AccountDTO accountDTO = as.createAccount(userId, currency);
		return ResponseEntity.ok(accountDTO);
	}




}
