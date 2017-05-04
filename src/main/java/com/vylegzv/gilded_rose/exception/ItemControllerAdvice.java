package com.vylegzv.gilded_rose.exception;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author vella
 *
 */
@ControllerAdvice
public class ItemControllerAdvice {

  @ResponseBody
  @ExceptionHandler(ItemOutOfStockException.class)
  @ResponseStatus(HttpStatus.GONE)
  VndErrors itemOutOfStockExceptionHandles(ItemOutOfStockException ex) {
    return new VndErrors("error", ex.getMessage());
  }
}
