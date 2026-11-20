package com.tecnm.merida.market_backend.web.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(DataIntegrityViolationException.class)
 @ResponseStatus(HttpStatus.CONFLICT)
 public ProblemDetail conflict(DataIntegrityViolationException exception) {
  return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,"La operacion viola una restriccion de datos. Comprueba referencias, duplicados y longitudes.");
 }
}