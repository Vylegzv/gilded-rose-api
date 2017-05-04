package com.vylegzv.gilded_rose.exception;

/**
 * 
 * @author vella
 *
 */
public class ItemOutOfStockException extends RuntimeException {

  private static final long serialVersionUID = -512998333745323514L;

  public ItemOutOfStockException(long itemId) {
    super("Item " + itemId + " is out of stock");
  }
}
