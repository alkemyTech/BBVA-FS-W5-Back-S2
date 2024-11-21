package com.example.bbva.squad2.Wallet.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
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

	public AccountDTO createAccount(Long userId) {
		Optional<User> userOptional = us.findById(userId);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			Account newAccount = new Account();
			newAccount.setBalance(0.0);
			newAccount.setCbu(generaCBU());
			newAccount.setCurrency(CurrencyTypeEnum.ARS);
			newAccount.setTransactionLimit(300000.0);
			newAccount.setUser(user);
			Account savedAccount = ar.save(newAccount);
			return new AccountDTO().mapFromAccount(savedAccount);
		} else {
			throw new RuntimeException("User not found");
		}
	}

	public String generaCBU() {
		Random random = new Random();


		StringBuilder cbu = new StringBuilder();
		for (int i = 0; i < 22; i++) {
			cbu.append(random.nextInt(10));
		}
		System.out.println("CBU generado: " + cbu);

		return cbu.toString();
	}




}
