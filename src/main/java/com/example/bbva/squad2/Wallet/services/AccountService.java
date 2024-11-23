package com.example.bbva.squad2.Wallet.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.example.bbva.squad2.Wallet.dtos.*;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.AlkemyException;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.FixedTermDeposit;
import com.example.bbva.squad2.Wallet.models.Transaction;
import com.example.bbva.squad2.Wallet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class AccountService {
	
	@Autowired
	private AccountsRepository ar;
	
	@Autowired
	private UserRepository ur;

	//agregue por ful 30
	@Autowired
	private FixedTermDepositService fixedTermDepositService;

	@Autowired
	private TransactionService transactionService;


	public List<AccountDTO> getAccountsByUser(Long userId) {
		Optional<User> user = ur.findById(userId);

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


	public AccountDTO createAccount(Long userId, @RequestParam CurrencyTypeEnum currency) {
		Optional<User> userOptional = ur.findById(userId);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			Account newAccount = new Account();
			newAccount.setBalance(0.0);
			newAccount.setCbu(generaCBU());
			newAccount.setCurrency(currency);
			newAccount.setTransactionLimit(currency == CurrencyTypeEnum.USD ? 1000.0 : 300000.0);
			newAccount.setUser(user);
			Account savedAccount = ar.save(newAccount);
			return new AccountDTO().mapFromAccount(savedAccount);
		} else {
			throw new AlkemyException(HttpStatus.NOT_FOUND, "User not found");
		}
	}

	public String generaCBU() {
		Random random = new Random();

		// Construir un número aleatorio de 22 dígitos
		StringBuilder cbu = new StringBuilder();
		for (int i = 0; i < 22; i++) {
			cbu.append(random.nextInt(10));
		}
		System.out.println("CBU generado: " + cbu);

		return cbu.toString();
	}


	//agregue para la ful 34

	public AccountBalanceDTO getBalanceByUserId(Long userId) {
		Optional<User> userOptional = ur.findById(userId);
		if (userOptional.isEmpty()) {
			throw new RuntimeException("User not found");
		}

		User user = userOptional.get();
		Double balanceArs = 0.0;
		Double balanceUsd = 0.0;

		List<Account> accounts = user.getAccounts();
		AccountDTO accountArs = null;
		AccountDTO accountUsd = null;

		for (Account account : accounts) {
			if (account.getCurrency() == CurrencyTypeEnum.ARS) {
				accountArs = new AccountDTO().mapFromAccount(account);
				balanceArs += account.getBalance();
			} else if (account.getCurrency() == CurrencyTypeEnum.USD) {
				accountUsd = new AccountDTO().mapFromAccount(account);
				balanceUsd += account.getBalance();
			}
		}

		List<TransactionBalanceDTO> history = transactionService.getTransactionsByUserId(userId)
				.stream()
				.map(transaction -> new TransactionBalanceDTO().mapFromTransaction(transaction))
				.collect(Collectors.toList());

		List<FixedTermDTO> fixedTerms = fixedTermDepositService.getFixedTermDepositsByUserId(userId)
				.stream()
				.map(fixedTerm -> new FixedTermDTO().mapFromFixedTerm(fixedTerm))
				.collect(Collectors.toList());

		AccountBalanceDTO balanceDTO = new AccountBalanceDTO();
		balanceDTO.setAccountArs(accountArs != null ? new AccountBalanceDTO.AccountBalance(balanceArs, "ARS") : null);
		balanceDTO.setAccountUsd(accountUsd != null ? new AccountBalanceDTO.AccountBalance(balanceUsd, "USD") : null);
		balanceDTO.setHistory(history);
		balanceDTO.setFixedTerms(fixedTerms);

		return balanceDTO;
	}

}
