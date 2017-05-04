package com.vylegzv.gilded_rose.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author vella
 *
 */
@ResponseStatus(HttpStatus.GONE)
public class ItemOutOfStockException extends RuntimeException {

  private static final long serialVersionUID = -512998333745323514L;

  public ItemOutOfStockException(long itemId) {
    super("Item " + itemId + " is out of stock");
  }
}
