package com.example.productmanagementservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Application no exists")
public class ApplicationNoExistsException extends RuntimeException {

}
