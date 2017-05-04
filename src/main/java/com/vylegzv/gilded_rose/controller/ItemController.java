package com.vylegzv.gilded_rose.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.vylegzv.gilded_rose.client.ItemApi;
import com.vylegzv.gilded_rose.resource.ItemResource;
import com.vylegzv.gilded_rose.service.ItemService;

/**
 * 
 * @author vella
 *
 */
@RestController
public class ItemController implements ItemApi {

  @Autowired
  private ItemService itemService;

  @Override
  @RequestMapping(value = ItemApi.ITEMS_PATH, method = RequestMethod.GET,
      produces = {MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
  public @ResponseBody Resources<ItemResource> getItems() {
    return itemService.getItems();
  }

  @Override
  @RequestMapping(value = ItemApi.ITEM_BUY_PATH, method = RequestMethod.GET,
      produces = {MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
  public @ResponseBody ItemResource buyItem(@RequestParam(ID_PARAM) long id) {
    return itemService.buyItem(id);
  }
}

