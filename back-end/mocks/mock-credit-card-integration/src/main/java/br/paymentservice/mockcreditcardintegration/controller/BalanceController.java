package br.paymentservice.mockcreditcardintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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

@RestController("/balance")
public class BalanceController {

	@Autowired
	private CardDAO cardDao;
	
	@Autowired
	private WalletDAO walletDao;
	
	@Autowired
	private CardMovementDAO cardMovementDAO;
	
	@PostMapping("/deposit")
	@ResponseStatus(HttpStatus.CREATED)
	public Wallet deposit(@RequestBody CardAction deposit) {
		Card foundCard = this.cardDao.findByCardNumber(deposit.getCardNumber());
		if (foundCard == null) {
			Card createdCard = new Card();
			createdCard.setCvv(deposit.getCvv());
			createdCard.setNumber(deposit.getCardNumber());
			createdCard.setOwner(deposit.getOwner());
			createdCard = cardDao.save(createdCard);
			
			makeMovement(createdCard, deposit);
			
			Wallet createdWallet = new Wallet();
			createdWallet.setCard(createdCard);
			createdWallet.setBalance(deposit.getValue());
			createdWallet = walletDao.save(createdWallet);
			return createdWallet;
		} else {
			Wallet foundWallet = this.walletDao.findByCard(foundCard);
			foundWallet.setBalance(foundWallet.getBalance() + deposit.getValue());
			return walletDao.save(foundWallet);
		}
	}
	
	@PostMapping("/debit")
	@ResponseStatus(HttpStatus.CREATED)
	public Wallet debit(@RequestBody CardAction debit) {
		Card foundCard = this.cardDao.findByCardNumber(debit.getCardNumber());
		if (foundCard == null) {
			throw new NotFoundException("Credit card", String.format("id: [%s]", debit.getCardNumber()));
		} else {
			makeMovement(foundCard, debit);
			
			Wallet foundWallet = this.walletDao.findByCard(foundCard);
			foundWallet.setBalance(foundWallet.getBalance() - debit.getValue());
			return walletDao.save(foundWallet);
		}
	}
	
	@GetMapping("/")
	public Wallet balance(@RequestBody CardAction action) {
		Card foundCard = this.cardDao.findByCardNumber(action.getCardNumber());
		if (foundCard == null) {
			throw new NotFoundException("Credit card", String.format("id: [%s]", action.getCardNumber()));
		} else {
			return this.walletDao.findByCard(foundCard);
		}
	}
	
	private CardMovement makeMovement(Card card, CardAction action) {
		CardMovement cardMovement = new CardMovement();
		cardMovement.setCard(card);
		cardMovement.setDescription("Deposit: " + action.getDescription());
		cardMovement.setValue(action.getValue());
		return cardMovementDAO.save(cardMovement);
	}
	
}
