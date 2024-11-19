package com.example.bbva.squad2.Wallet.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bbva.squad2.Wallet.dtos.AccountDTO;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;

@Service
public class AccountService {
	
	@Autowired
	private AccountsRepository ar;
	
	public List<AccountDTO> getAccountsByUser(Long userId){
        List<Account> accounts = ar.findByUserId(userId);
        return accounts.stream()
        		.map(account -> new AccountDTO().mapFromAccount(account))
        		.collect(Collectors.toList());
	}

}
