package com.example.productmanagementservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Does not match current user")
public class NotMatchUserException extends RuntimeException{
}
