package com.example.bbva.squad2.Wallet.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bbva.squad2.Wallet.dtos.AccountDTO;
import com.example.bbva.squad2.Wallet.services.AccountService;

@RestController
@RequestMapping("/accounts")
public class AccountController {

	@Autowired
	private AccountService as;
	
	@GetMapping("/{id}")
	public ResponseEntity<List<AccountDTO>> getAccounts(@PathVariable Long id) throws Exception {
	    List<AccountDTO> accountsByUser = as.getAccountsByUser(id);
	  
	    return ResponseEntity.ok(accountsByUser);
	}
}
