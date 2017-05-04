package com.vylegzv.gilded_rose.resource;

import org.springframework.hateoas.ResourceSupport;

import com.vylegzv.gilded_rose.client.ItemApi;
import com.vylegzv.gilded_rose.controller.ItemController;
import com.vylegzv.gilded_rose.domain.Item;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * 
 * @author vella
 *
 */
public class ItemResource extends ResourceSupport {

  private final Item item;

  public ItemResource(Item item) {
    this.item = item;
    this.add(linkTo(ItemController.class).slash(ItemApi.ITEMS_PATH)
        .withRel("items"));
    this.add(linkTo(methodOn(ItemController.class).buyItem(item.getId()))
        .withSelfRel());
  }

  public Item getItem() {
    return item;
  }
}
