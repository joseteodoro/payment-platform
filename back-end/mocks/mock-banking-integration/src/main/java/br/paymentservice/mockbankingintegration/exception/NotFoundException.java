package br.paymentservice.mockbankingintegration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = -7557004695560465726L;

	public NotFoundException(String entity, String query) {
		super(String.format("Cannot find [%s] with query: [%s]", entity, query));
	}
}
