package br.paymentservice.mockcreditcardintegration.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import br.paymentservice.mockcreditcardintegration.model.Card;
import br.paymentservice.mockcreditcardintegration.model.CardMovement;

public interface CardMovementDAO  extends CrudRepository<CardMovement, Long> {

	public List<CardMovement> findByCard(Card card);
	
}
