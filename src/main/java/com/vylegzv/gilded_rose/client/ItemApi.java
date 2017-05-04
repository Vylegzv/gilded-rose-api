package com.vylegzv.gilded_rose.client;

import java.util.Collection;
import com.vylegzv.gilded_rose.domain.Item;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * 
 * @author vella
 *
 */
public interface ItemApi {

  public static final String ITEMS_PATH = "/items";
  public static final String ID_PARAM = "id";
  public static final String ITEM_BUY_PATH = ITEMS_PATH + "/buy";

  @GET(ITEMS_PATH)
  public Collection<Item> getItems();

  @GET(ITEM_BUY_PATH)
  public Item buyItem(@Query(ID_PARAM) long id);
}
