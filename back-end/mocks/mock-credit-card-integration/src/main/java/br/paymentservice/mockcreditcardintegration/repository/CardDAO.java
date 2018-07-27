package br.paymentservice.mockcreditcardintegration.repository;

import org.springframework.data.repository.CrudRepository;

import br.paymentservice.mockcreditcardintegration.model.Card;

public interface CardDAO extends CrudRepository<Card, Long> {

	public Card findByNumber(String number);
	
}
