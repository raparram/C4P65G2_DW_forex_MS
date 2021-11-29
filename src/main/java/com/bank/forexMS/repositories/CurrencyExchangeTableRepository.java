/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.bank.forexMS.repositories;

import com.bank.forexMS.models.CurrencyExchangeTable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyExchangeTableRepository extends MongoRepository<CurrencyExchangeTable, String> {

    CurrencyExchangeTable findFirstByOrderByIdDesc();

}
