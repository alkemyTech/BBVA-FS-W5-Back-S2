package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.models.Account;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class AccountDTO {
	
	private Long id;
    private CurrencyTypeEnum currency;
    private Double transactionLimit;
    private Double balance;
    
    public AccountDTO mapFromAccount(final Account account) {
    	id = account.getId();
    	currency = account.getCurrency();
    	transactionLimit  = account.getTransactionLimit();
    	balance = account.getBalance();
    	return this;
    }
}
