package br.paymentservice.mockbankingintegration.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Movement {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String description;
	
	@Column(nullable = false)
	private Long value;
	
	@Column(nullable = false)
	private String token;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}
	
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
