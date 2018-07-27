package br.paymentservice.mockcreditcardintegration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.paymentservice.mockcreditcardintegration.controller.vo.CardAction;
import br.paymentservice.mockcreditcardintegration.exception.NotFoundException;
import br.paymentservice.mockcreditcardintegration.model.Card;
import br.paymentservice.mockcreditcardintegration.model.CardMovement;
import br.paymentservice.mockcreditcardintegration.model.Wallet;
import br.paymentservice.mockcreditcardintegration.repository.CardDAO;
import br.paymentservice.mockcreditcardintegration.repository.CardMovementDAO;
import br.paymentservice.mockcreditcardintegration.repository.WalletDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value="/balance")
public class BalanceController {

	@Autowired
	private CardDAO cardDao;
	
	@Autowired
	private WalletDAO walletDao;
	
	@Autowired
	private CardMovementDAO cardMovementDAO;
	
	@ApiOperation(value = "Deposit the value on a Card and created a new one if cannot find the Card", response = Wallet.class)
	@PostMapping("/balance/deposit")
	@ResponseStatus(HttpStatus.CREATED)
	public Wallet deposit(@RequestBody CardAction deposit) {
		Card foundCard = this.cardDao.findByNumberAndCvv(deposit.getCardNumber(), deposit.getCvv());
		if (foundCard == null) {
			Card createdCard = new Card();
			createdCard.setCvv(deposit.getCvv());
			createdCard.setNumber(deposit.getCardNumber());
			createdCard.setOwner(deposit.getOwner());
			createdCard = cardDao.save(createdCard);
			
			makeMovement(deposit);
			
			Wallet createdWallet = new Wallet();
			createdWallet.setCard(createdCard);
			createdWallet.setBalance(deposit.getValue());
			createdWallet = walletDao.save(createdWallet);
			return createdWallet;
		} else {
			makeMovement(deposit);
			
			Wallet foundWallet = this.walletDao.findByCard(foundCard);
			foundWallet.setBalance(foundWallet.getBalance() + deposit.getValue());
			return walletDao.save(foundWallet);
		}
	}
	
	@ApiOperation(value = "Debit the value from a Card", response = Wallet.class)
	@PostMapping("/balance/debit")
	@ResponseStatus(HttpStatus.CREATED)
	public Wallet debit(@RequestBody CardAction debit) {
		Card foundCard = this.cardDao.findByNumberAndCvv(debit.getCardNumber(), debit.getCvv());
		if (foundCard == null) {
			throw new NotFoundException("Credit card", String.format("number: [%s]", debit.getCardNumber()));
		} else {
			makeMovement(debit);
			Wallet foundWallet = this.walletDao.findByCard(foundCard);
			foundWallet.setBalance(foundWallet.getBalance() - debit.getValue());
			return walletDao.save(foundWallet);
		}
	}
	
	@ApiOperation(value = "Get the balance from a Card", response = Wallet.class)
	@PostMapping("/balance")
	public Wallet balance(@RequestBody CardAction action) {
		Card foundCard = this.cardDao.findByNumberAndCvv(action.getCardNumber(), action.getCvv());
		if (foundCard == null) {
			throw new NotFoundException("Credit card", String.format("number: [%s]", action.getCardNumber()));
		} else {
			return this.walletDao.findByCard(foundCard);
		}
	}
	
	@ApiOperation(value = "Get the history from a Card", response = Wallet.class)
	@PostMapping("/balance/history")
	public List<CardMovement> history(@RequestBody CardAction action) {
		return this.cardMovementDAO.findByCardNumberAndCvv(action.getCardNumber(), action.getCvv());
	}
	
	private CardMovement makeMovement(CardAction action) {
		CardMovement cardMovement = new CardMovement();
		cardMovement.setCardNumber(action.getCardNumber());
		cardMovement.setDescription("Deposit: " + action.getDescription());
		cardMovement.setValue(action.getValue());
		cardMovement.setCvv(action.getCvv());
		return cardMovementDAO.save(cardMovement);
	}
	
}
