package com.example.bbva.squad2.Wallet.controllers;

import java.util.List;
import java.util.Objects;

import com.example.bbva.squad2.Wallet.config.JwtServices;
import com.example.bbva.squad2.Wallet.dtos.AccountBalanceDTO;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.AlkemyException;
import com.example.bbva.squad2.Wallet.services.UserService;
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
	private UserService userService;

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
		UsuarioSeguridad security = userService.getInfoUserSecurity(request);
		Long userId = security.getId();

		AccountDTO accountDTO = as.createAccount(userId, currency);
		return ResponseEntity.ok(accountDTO);
	}

	@GetMapping("/balance")
	public ResponseEntity<AccountBalanceDTO> getBalance(HttpServletRequest request) {
		UsuarioSeguridad security = userService.getInfoUserSecurity(request);
		Long userId = security.getId();

		AccountBalanceDTO balanceDTO = as.getBalanceByUserId(userId);

		return ResponseEntity.ok(balanceDTO);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<AccountDTO> updateTransactionLimit(
			@PathVariable Long id,
			@RequestParam Double newTransactionLimit,
			HttpServletRequest request) {

		UsuarioSeguridad security = userService.getInfoUserSecurity(request);
		Long userId = security.getId();

		// Actualizar el límite de transferencia
		AccountDTO updatedAccount = as.updateTransactionLimit(id, userId, newTransactionLimit);

		return ResponseEntity.ok(updatedAccount);
	}
}
