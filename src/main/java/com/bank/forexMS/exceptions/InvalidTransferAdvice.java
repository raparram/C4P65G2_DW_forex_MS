/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bank.forexMS.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class InvalidTransferAdvice {
    
    @ResponseBody
    @ExceptionHandler(InvalidTransferException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String InsuffcientBalanceAdvice(InvalidTransferException ex) {
        return ex.getMessage();
    }    
}
