/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bank.forexMS.models;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transaction {

    @Getter
    @Setter
    @Id
    private String id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private String currency;

    @Getter
    @Setter
    private double rate;

    @Getter
    @Setter
    private double originAmount;

    @Getter
    @Setter
    private double destinationAmount;

    @Getter
    @Setter
    private double commissionPercentage;

    @Getter
    @Setter
    private Date date;

}
