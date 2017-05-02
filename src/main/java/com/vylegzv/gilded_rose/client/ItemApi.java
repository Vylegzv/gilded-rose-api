package com.vylegzv.gilded_rose.client;

import java.util.Collection;

import com.vylegzv.gilded_rose.domain.Item;

import retrofit.http.GET;

/**
 * 
 * @author vella
 *
 */
public interface ItemApi {

  public static final String ITEMS_PATH = "/items";
  
  @GET(ITEMS_PATH)
  public Collection<Item> getItems();
}
