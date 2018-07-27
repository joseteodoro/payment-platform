package br.paymentservice.mockbankingintegration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.paymentservice.mockbankingintegration.controller.vo.CardAction;
import br.paymentservice.mockbankingintegration.exception.NotFoundException;
import br.paymentservice.mockbankingintegration.model.Movement;
import br.paymentservice.mockbankingintegration.model.Wallet;
import br.paymentservice.mockbankingintegration.repository.MovementDAO;
import br.paymentservice.mockbankingintegration.repository.WalletDAO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value="/balance")
public class BalanceController {

	
	@Autowired
	private WalletDAO walletDao;
	
	@Autowired
	private MovementDAO movementDAO;
	
	@ApiOperation(value = "Deposit the value to a wallet and created a new one if cannot find the wallet", response = Wallet.class)
	@PostMapping("/balance/deposit")
	@ResponseStatus(HttpStatus.CREATED)
	public Wallet deposit(@RequestBody CardAction deposit) {
		Wallet foundWallet = this.walletDao.findByTokenAndPassword(deposit.getToken(), deposit.getPassword());
		if (foundWallet == null) {
			makeMovement(deposit);
			Wallet createdWallet = new Wallet();
			createdWallet.setPassword(deposit.getPassword());
			createdWallet.setToken(deposit.getToken());
			createdWallet.setBalance(deposit.getValue());
			createdWallet = walletDao.save(createdWallet);
			return createdWallet;
		} else {
			makeMovement(deposit);
			foundWallet.setBalance(foundWallet.getBalance() + deposit.getValue());
			return walletDao.save(foundWallet);
		}
	}
	
	@ApiOperation(value = "Debit the value from a Wallet", response = Wallet.class)
	@PostMapping("/balance/debit")
	@ResponseStatus(HttpStatus.CREATED)
	public Wallet debit(@RequestBody CardAction debit) {
		Wallet foundWallet = this.walletDao.findByTokenAndPassword(debit.getToken(), debit.getPassword());
		if (foundWallet == null) {
			throw new NotFoundException("Wallet", String.format("token and password: [%s]", debit.getToken()));
		} else {
			if (foundWallet.getBalance() < debit.getValue()) {
				throw new NotFoundException("No balance found for", String.format("wallet token: [%s]", debit.getToken()));
			}
			
			makeMovement(debit);
			foundWallet.setBalance(foundWallet.getBalance() - debit.getValue());
			return walletDao.save(foundWallet);
		}
	}
	
	@ApiOperation(value = "Get the balance from a Wallet", response = Wallet.class)
	@PostMapping("/balance")
	public Wallet balance(@RequestBody CardAction action) {
		Wallet foundWallet = this.walletDao.findByTokenAndPassword(action.getToken(), action.getPassword());
		if (foundWallet == null) {
			throw new NotFoundException("Wallet", String.format("token and password: [%s]", action.getToken()));
		}  else {
			return foundWallet;
		}
	}
	
	@ApiOperation(value = "Get the history from a wallet", response = Wallet.class)
	@PostMapping("/balance/history")
	public List<Movement> history(@RequestBody CardAction action) {
		Wallet foundWallet = this.walletDao.findByTokenAndPassword(action.getToken(), action.getPassword());
		if (foundWallet == null) {
			throw new NotFoundException("Wallet", String.format("token and password: [%s]", action.getToken()));
		}  else {
			return this.movementDAO.findByToken(action.getToken());
		}
	}
	
	private Movement makeMovement(CardAction action) {
		Movement movement = new Movement();
		movement.setToken(action.getToken());
		movement.setDescription("Deposit: " + action.getDescription());
		movement.setValue(action.getValue());
		return movementDAO.save(movement);
	}
	
}
