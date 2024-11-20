package com.example.bbva.squad2.Wallet.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.bbva.squad2.Wallet.models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bbva.squad2.Wallet.dtos.AccountDTO;
import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;

@Service
public class AccountService {
	
	@Autowired
	private AccountsRepository ar;
	
	@Autowired
	private UserService us;

	public List<AccountDTO> getAccountsByUser(Long userId) {
		Optional<User> user = us.findById(userId);

		if (user.isPresent()) {
			List<Account> accounts = user.get().getAccounts();
			List<AccountDTO> accountDTOs = accounts.stream()
					.map(account -> new AccountDTO().mapFromAccount(account))
					.collect(Collectors.toList());

			return accountDTOs;
		} else {
			return new ArrayList<>();
		}
	}


}
