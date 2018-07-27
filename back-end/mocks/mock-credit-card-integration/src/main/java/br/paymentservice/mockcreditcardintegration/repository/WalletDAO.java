package br.paymentservice.mockcreditcardintegration.repository;

import org.springframework.data.repository.CrudRepository;

import br.paymentservice.mockcreditcardintegration.model.Card;
import br.paymentservice.mockcreditcardintegration.model.Wallet;

public interface WalletDAO extends CrudRepository<Wallet, Long> {

	public Wallet findByCard(Card card);
	
}

