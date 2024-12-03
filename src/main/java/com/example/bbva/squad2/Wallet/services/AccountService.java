package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.dtos.*;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.WalletsException;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.AccountStatic;
import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;
import com.example.bbva.squad2.Wallet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AccountService {
	
	@Autowired
	private AccountsRepository ar;
	
	@Autowired
	private UserRepository ur;

	@Autowired
	private FixedTermDepositService fixedTermDepositService;

	@Autowired
	private TransactionService transactionService;

	public List<AccountDTO> getAccountsByUser(Long userId) {
		Optional<User> user = ur.findById(userId);

		if (user.isPresent()) {
			List<Account> accounts = user.get().getAccounts();
			return accounts.stream()
					.map(account -> new AccountDTO().mapFromAccount(account))
					.collect(Collectors.toList());
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
			throw new WalletsException(HttpStatus.NOT_FOUND, "User not found");
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
		balanceDTO.setAccountArs(accountArs != null ? new AccountStatic.AccountBalance(balanceArs, "ARS") : null);
		balanceDTO.setAccountUsd(accountUsd != null ? new AccountStatic.AccountBalance(balanceUsd, "USD") : null);
		balanceDTO.setHistory(history);
		balanceDTO.setFixedTerms(fixedTerms);

		return balanceDTO;
	}

	public AccountDTO updateTransactionLimit(Long accountId, Long userId, Double newTransactionLimit) throws WalletsException{
		// Validar que el límite no sea nulo ni negativo
		if (newTransactionLimit == null || newTransactionLimit <= 0) {
			throw new WalletsException(HttpStatus.BAD_REQUEST, "El limite de transacción no puede ser nulo.");
		}

		// Buscar la cuenta por ID
		Account account = ar.findById(accountId)
				.orElseThrow(() -> new WalletsException(HttpStatus.NOT_FOUND, "Cuenta no encontrada."));

		// Verificar que la cuenta pertenezca al usuario loggeado
		if (!account.getUser().getId().equals(userId)) {
			throw new WalletsException(HttpStatus.FORBIDDEN, "No esta autorizado para modificar esta cuenta.");
		}

		// Actualizar el límite de transferencia
		account.setTransactionLimit(newTransactionLimit);
		ar.save(account);

		// Devolver el DTO actualizado
		return new AccountDTO().mapFromAccount(account);
	}

	public PageableResponseDTO<AccountDTO> getAllAccountsPaginated(int page, int size) {
		// Crea el objeto Pageable con la página y tamaño proporcionados
		Pageable pageable = PageRequest.of(page, size);

		// Obtén las cuentas paginadas desde el repositorio
		Page<Account> accountPage = ar.findAll(pageable); // Cambia ur por accountRepository

		// Filtra las cuentas que no están eliminadas (soft delete)
		List<AccountDTO> accountDTOs = accountPage.getContent().stream()
				.filter(account -> account.getUser().getSoftDelete() == null) // Ajusta la lógica si `softDelete` es diferente en Account
				.map(AccountDTO::mapFromAccount) // Asegúrate de que `mapFromAccount` esté implementado correctamente
				.collect(Collectors.toList());

		// Devuelve la respuesta paginada con datos relevantes
		return new PageableResponseDTO<>(
				accountDTOs,
				accountPage.getNumber(),
				accountPage.getTotalPages(),
				accountPage.hasPrevious() ? "/accounts?page=" + (page - 1) : null, // Cambia las rutas a "/accounts"
				accountPage.hasNext() ? "/accounts?page=" + (page + 1) : null
		);
	}

}



