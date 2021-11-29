/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bank.forexMS.controllers;

import com.bank.forexMS.models.CurrencyExchangeTable;
import com.bank.forexMS.repositories.CurrencyExchangeTableRepository;
import com.bank.forexMS.exceptions.AccountNotFoundException;
import com.bank.forexMS.exceptions.InvalidCurrencyExchangeTableException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/forex/")
@RequiredArgsConstructor(onConstructor = @__(
        @Autowired))
public class CurrencyExchanceTableController {

    @Autowired
    private CurrencyExchangeTableRepository CETrepository;

    @GetMapping("/actual")
    CurrencyExchangeTable getActualCurrencyExchangeTable() {
        return CETrepository.findFirstByOrderByIdDesc();
    }

    @GetMapping("/all")
    List<CurrencyExchangeTable> getAllCurrencyExchangeTable() {
        return CETrepository.findAll();
    }

    @PostMapping("/")
    CurrencyExchangeTable addForexAccount(@RequestBody CurrencyExchangeTable table) {
        if (table.getCommissionPercentage() <= 0) {
            throw new InvalidCurrencyExchangeTableException("Comision no valida.");
        }

        if (table.getRates().isEmpty() || table.getRates() == null) {
            throw new InvalidCurrencyExchangeTableException("Tasas de cambio no validas.");
        }
        if (table.getUsernameCreator().isEmpty() || table.getUsernameCreator() == null) {
            throw new InvalidCurrencyExchangeTableException("Usuario no valido.");
        }
        table.setDateCreation(new Date());
        return CETrepository.save(table);
    }
}
