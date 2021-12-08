/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bank.forexMS.controllers;

import com.bank.forexMS.models.*;
import com.bank.forexMS.repositories.*;
import com.bank.forexMS.exceptions.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
        double originAmount = transaction.getOriginAmount();
        double USDrate = CETrepository.findFirstByOrderByIdDesc().getUsdRate();
        if (customer == null) {
            throw new AccountNotFoundException("El usuario " + transaction.getUsername() + " no cuenta con divisas extrangeras. Cree primero una cuenta tipo forex. Transacción declinada.");
        }
        if (!(type.equals("compra") || type.equals("venta"))) {
            throw new InvalidTransferException("Transacción tipo " + type + " no válida. Transacción declinada.");
        }
        if (originAmount < 0) {
            throw new InvalidTransferException("El monto origen debe ser mayor a cero. Transacción declinada.");
        }
        double commission = CETrepository.findFirstByOrderByIdDesc().getCommissionPercentage() / 100;
        double rate = CETrepository.findFirstByOrderByIdDesc().getUsdRate();
        double destinationAmount = 0;
        double cop2user = 0;
        double cop2bank = 0;
        double customerUsd = customer.getUsdAmount();
        if (type.equals("compra")) {
            destinationAmount = (originAmount * (1 - commission)) / rate;
            cop2user = 0;
            cop2bank = originAmount;
            customerUsd += destinationAmount;
        } else if (type.equals("venta")) {
            if (customerUsd < originAmount) {
                throw new InvalidTransferException("El usuario " + transaction.getUsername() + " no posee fondos necesarios en USD. Transacción declinada.");
            }
            destinationAmount = originAmount * rate;
            cop2user = destinationAmount * (1 - commission);
            cop2bank = destinationAmount * commission;
            customerUsd -= originAmount;
        }
        // Transfers to the bank as commissions, these are made in the api gate
        Date today = new Date();
        // Update Account
        customer.setUsdAmount(customerUsd);
        customer.setLastChange(today);
        accountRepository.save(customer);
        // Update Transaction
        transaction.setUsdRate(USDrate);
        transaction.setDestinationAmount(destinationAmount);
        transaction.setCommissionPercentage(CETrepository.findFirstByOrderByIdDesc().getCommissionPercentage()); //%
        transaction.setCop2user(cop2user);
        transaction.setCop2bank(cop2bank);
        transaction.setDate(today);
        return transactionRepository.save(transaction);
    }
    
    @GetMapping("/transactions/user/{username}")
    List<Transaction> costumerTransactionForex(@PathVariable String username) {
        Account customer = accountRepository.findById(username).orElse(null);
        if (customer == null) {
            throw new AccountNotFoundException("El usuario " + username + " no cuenta con divisas extrangeras. Cree primero una cuenta tipo forex. Transacción declinada.");
        }
        return transactionRepository.findByUsername(username);
    }
    
    @GetMapping("/transactions/id/{idTransaction}")
    Optional<Transaction> idTransactionForex(@PathVariable String idTransaction) {
        Transaction transaction = transactionRepository.findById(idTransaction).orElse(null);
        if (transaction == null) {
            throw new AccountNotFoundException("La transacción " + idTransaction + " no existe.");
        }
        return transactionRepository.findById(idTransaction);
    }
}
