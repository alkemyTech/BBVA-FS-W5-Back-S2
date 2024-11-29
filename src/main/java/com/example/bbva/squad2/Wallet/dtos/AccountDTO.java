package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.models.Account;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class AccountDTO {
	
	private Long id;
	private String cbu;
    private CurrencyTypeEnum currency;
    private Double transactionLimit;
    private Double balance;

	public static AccountDTO mapFromAccount(Account account) {
		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setId(account.getId());
		accountDTO.setCurrency(account.getCurrency());
		accountDTO.setTransactionLimit(account.getTransactionLimit());
		accountDTO.setBalance(account.getBalance());
		accountDTO.setCbu(account.getCbu());
		return accountDTO;
	}





}
