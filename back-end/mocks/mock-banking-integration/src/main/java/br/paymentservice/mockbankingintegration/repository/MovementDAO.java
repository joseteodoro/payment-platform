package br.paymentservice.mockbankingintegration.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import br.paymentservice.mockbankingintegration.model.Movement;

public interface MovementDAO  extends CrudRepository<Movement, Long> {

	public List<Movement> findByToken(String token);
	
}
