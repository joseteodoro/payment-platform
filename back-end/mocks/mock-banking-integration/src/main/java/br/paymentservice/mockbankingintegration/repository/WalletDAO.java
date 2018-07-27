package br.paymentservice.mockbankingintegration.repository;

import org.springframework.data.repository.CrudRepository;

import br.paymentservice.mockbankingintegration.model.Wallet;

public interface WalletDAO extends CrudRepository<Wallet, Long> {

	public Wallet findByTokenAndPassword(String token, String password);
	
}

