/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bank.forexMS.controllers;

import com.bank.forexMS.models.Account;
import com.bank.forexMS.repositories.AccountRepository;
import com.bank.forexMS.exceptions.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/new")
    Account addForexAccount(@RequestBody Account account) {
        return accountRepository.save(account);
    }

}
