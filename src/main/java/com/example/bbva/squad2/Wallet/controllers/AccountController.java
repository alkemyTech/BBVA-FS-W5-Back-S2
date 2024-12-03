package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.config.JwtServices;
import com.example.bbva.squad2.Wallet.dtos.AccountBalanceDTO;
import com.example.bbva.squad2.Wallet.dtos.AccountDTO;
import com.example.bbva.squad2.Wallet.dtos.PageableResponseDTO;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.WalletsException;
import com.example.bbva.squad2.Wallet.services.AccountService;
import com.example.bbva.squad2.Wallet.services.UsuarioLoggeadoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

	@Autowired
	private AccountService as;

	@Autowired
	private UsuarioLoggeadoService usuarioLoggeadoService;

	@Autowired
	private JwtServices js;
	
	@GetMapping("/{id}")
	@Operation(summary = "Obtener cuentas de un usuario específico")
	public ResponseEntity<List<AccountDTO>> getAccounts(@PathVariable Long id) throws Exception {
	    List<AccountDTO> accountsByUser = as.getAccountsByUser(id);
	  
	    return ResponseEntity.ok(accountsByUser);
	}

	@PostMapping("/{currency}")
	@Operation(summary = "Crear una cuenta para el usuario loggeado")
	public ResponseEntity<AccountDTO> createAccount(HttpServletRequest request,
													@PathVariable CurrencyTypeEnum currency
													) {
		UsuarioSeguridad security = usuarioLoggeadoService.getInfoUserSecurity(request);
		Long userId = security.getId();

		AccountDTO accountDTO = as.createAccount(userId, currency);
		return ResponseEntity.ok(accountDTO);
	}

	@GetMapping("/balance")
	@Operation(summary = "Obtener balance de cuentas del usuario loggeado")
	public ResponseEntity<AccountBalanceDTO> getBalance(HttpServletRequest request) {
		UsuarioSeguridad security = usuarioLoggeadoService.getInfoUserSecurity(request);
		Long userId = security.getId();

		AccountBalanceDTO balanceDTO = as.getBalanceByUserId(userId);

		return ResponseEntity.ok(balanceDTO);
	}

	@PatchMapping("/{id}")
	@Operation(summary = "Editar el limite de transacción de la cuenta del usuario loggeado")
	public ResponseEntity<Object> updateTransactionLimit(
			@PathVariable Long id,
			@RequestParam Double newTransactionLimit,
			HttpServletRequest request) {

		UsuarioSeguridad security = usuarioLoggeadoService.getInfoUserSecurity(request);
		Long userId = security.getId();

		try {
			// Actualizar el límite de transferencia
			AccountDTO updatedAccount = as.updateTransactionLimit(id, userId, newTransactionLimit);
			return ResponseEntity.status(HttpStatus.CREATED).body(updatedAccount);
		} catch (WalletsException e) {
			return ResponseEntity.status(e.getStatus()).body(e.getMessage());
		} catch (Exception e) {
			// Captura de otras excepciones genéricas
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("/paginated")
	@Operation(summary = "Obtener cuentas paginados", description = "Devuelve una lista paginada " +
			"de cuentas no eliminados.")
	public ResponseEntity<?> getAllAccounts(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		try {
			// Validar valores de entrada
			if (page < 0 || size <= 0) {
				return ResponseEntity.badRequest().body("Los valores de página y tamaño deben " +
						"ser positivos.");
			}

			// Llama al servicio para obtener los usuarios paginados
			PageableResponseDTO<AccountDTO> paginatedAccounts = as.getAllAccountsPaginated(page, size);
			return ResponseEntity.ok(paginatedAccounts);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al obtener cuentas paginados.");
		}
	}

}
