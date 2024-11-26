package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.models.Account;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

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
    
    public AccountDTO mapFromAccount(Account account) {
    	this.id = account.getId();
    	this.currency = account.getCurrency();
    	this.transactionLimit  = account.getTransactionLimit();
    	this.balance = account.getBalance();
		this.cbu = account.getCbu();
    	return this;
    }





}
