package com.vylegzv.gilded_rose.controller;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.vylegzv.gilded_rose.client.ItemApi;
import com.vylegzv.gilded_rose.domain.Item;
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
  @RequestMapping(value = ItemApi.ITEMS_PATH, method = RequestMethod.GET)
  public @ResponseBody Collection<Item> getItems() {
    return itemService.getItems();
  }

  @Override
  @RequestMapping(value = ItemApi.ITEM_BUY_PATH, method = RequestMethod.GET)
  public @ResponseBody Item buyItem(@RequestParam(ID_PARAM) long id) {
    return itemService.buyItem(id);
  }
}

