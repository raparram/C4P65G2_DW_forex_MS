/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bank.forexMS.controllers;

import com.bank.forexMS.models.Account;
import com.bank.forexMS.repositories.AccountRepository;
import com.bank.forexMS.exceptions.AccountNotFoundException;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/forex/accounts")
@RequiredArgsConstructor(onConstructor = @__(
        @Autowired))
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("{username}")
    Account getForexAccount(@PathVariable String username) {
        return accountRepository.findById(username).orElseThrow(() -> new AccountNotFoundException("El usuario " + username + " no cuenta con divisas extrangeras."));
    }
    
    @GetMapping("/all")
    List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @PostMapping("/new")
    Account addForexAccount(@RequestBody Account account) {
        if(accountRepository.existsById(account.getUsername())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese username. Creación de cuenta forex declinada.");
        }
        account.setUsdAmount(0);
        account.setLastChange(new Date());
        return accountRepository.save(account);
    }

}
