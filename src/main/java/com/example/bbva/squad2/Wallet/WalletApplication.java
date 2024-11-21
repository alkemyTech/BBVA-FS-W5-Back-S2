package com.example.bbva.squad2.Wallet;

import com.example.bbva.squad2.Wallet.dtos.AccountDTO;
import com.example.bbva.squad2.Wallet.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WalletApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(WalletApplication.class, args);
	}

	@Autowired
	private AccountService as;

	@Override
	public void run(String... args) throws Exception {
		// Prueba del método createAccount
		try {
			// Usamos directamente 1 para el ID de prueba
			AccountDTO accountDTO = as.createAccount(1L); // El ID sigue siendo Long, pero es más claro
			System.out.println("Cuenta creada: " + accountDTO);
		} catch (Exception e) {
			System.err.println("Error al crear la cuenta: " + e.getMessage());
		}
	}
}
