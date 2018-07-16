package com.example.productmanagementservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Incorrect value")
public class IncorrectValueException extends RuntimeException{
}
