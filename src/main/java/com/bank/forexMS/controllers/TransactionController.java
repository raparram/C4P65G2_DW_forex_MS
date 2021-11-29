/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bank.forexMS.controllers;

import com.bank.forexMS.models.*;
import com.bank.forexMS.repositories.*;
import com.bank.forexMS.exceptions.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forex")
@RequiredArgsConstructor(onConstructor = @__(
        @Autowired))
public class TransactionController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CurrencyExchangeTableRepository CETrepository;

    @PostMapping("/transactions")
    Transaction newForexTransaction(@RequestBody Transaction transaction) {
        Account customer = accountRepository.findById(transaction.getUsername()).orElse(null);
        String type = transaction.getType();
        String currency = transaction.getCurrency();
        double originAmount = transaction.getOriginAmount();
        Map<String, Double> rates = CETrepository.findFirstByOrderByIdDesc().getRates();
        if (customer == null) {
            throw new AccountNotFoundException("El usuario " + transaction.getUsername() + " no cuenta con divisas extrangeras. Cree primero una cuenta tipo forex. Transacción declinada.");
        }
        if (!(type.equals("compra") || type.equals("venta"))) {
            throw new InvalidTransferException("Transacción tipo " + type + " no válido. Transacción declinada.");
        }
        if (!(rates.containsKey(currency))) {
            throw new InvalidTransferException("Divisa " + currency + " no válida. Transacción declinada.");
        }
        if (originAmount < 0) {
            throw new InvalidTransferException("El monto origen debe ser mayor a cero.");
        }
        Map<String, Double> customerWallet = customer.getCurrencyWallet();
        if(customerWallet == null){
            customerWallet = new HashMap<String, Double>() {};
        }
        double commission = CETrepository.findFirstByOrderByIdDesc().getCommissionPercentage() / 100;
        double rate = rates.get(currency);
        double destinationAmount = 0;        
        if (type.equals("compra")) {
            destinationAmount = (originAmount * (1 - commission)) / rate;
            if (customerWallet.containsKey(currency)) {
                double currentAmount = customerWallet.get(currency);
                customerWallet.put(currency, destinationAmount + currentAmount);
            } else {
                customerWallet.put(currency, destinationAmount);
            }
        } else if (type.equals("venta")) {
            if (customerWallet.containsKey(currency)) {
                throw new InvalidTransferException("El usuario" + transaction.getUsername() + "no posee fondos en " + currency + ". Transacción declinada.");
            }
            if (customerWallet.get(currency) < originAmount * (1 + commission)) {
                throw new InvalidTransferException("El usuario" + transaction.getUsername() + " no posee fondos suficientes en " + currency + ". Transacción declinada.");
            }
            destinationAmount = (originAmount * (1 - commission)) * rate;
            double currentAmount = customerWallet.get(currency);
            customerWallet.put(currency, currentAmount - destinationAmount);
        }
        // Transfers to the bank as commissions, these are made in the api gate
        Date today = new Date();
        // Update Account
        customer.setCurrencyWallet(customerWallet);
        customer.setLastChange(today);
        accountRepository.save(customer);
        // Update Transaction
        transaction.setRate(rate);
        transaction.setDestinationAmount(destinationAmount);
        transaction.setCommissionPercentage(commission * 100); //%
        transaction.setDate(today);
        return transactionRepository.save(transaction);
    }

    @GetMapping("/transactions/{username}")
    List<Transaction> costumerTransactionForex(@PathVariable String username) {
        Account customer = accountRepository.findById(username).orElse(null);
        if (customer == null) {
            throw new AccountNotFoundException("El usuario " + username + " no cuenta con divisas extrangeras. Cree primero una cuenta tipo forex. Transacción declinada.");
        }
        return transactionRepository.findByUsername(username);
    }
}
