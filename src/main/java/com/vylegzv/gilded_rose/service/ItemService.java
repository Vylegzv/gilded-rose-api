package com.vylegzv.gilded_rose.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Service;
import com.vylegzv.gilded_rose.client.ItemApi;
import com.vylegzv.gilded_rose.domain.Item;
import com.vylegzv.gilded_rose.exception.ItemOutOfStockException;
import com.vylegzv.gilded_rose.repo.ItemRepo;
import com.vylegzv.gilded_rose.resource.ItemResource;

/**
 * 
 * @author vella
 *
 */
@Service
public class ItemService implements ItemApi {

  @Autowired
  private ItemRepo itemRepo;

  @Override
  public Resources<ItemResource> getItems() {
    List<ItemResource> itemResources = itemRepo.findAll().stream()
        .map(ItemResource::new).collect(Collectors.toList());
    return new Resources<>(itemResources);
  }

  @Override
  @Transactional
  public ItemResource buyItem(long id) {
    Optional<Item> item = Optional.ofNullable(itemRepo.findOne(id));
    if (item.isPresent()) {
      itemRepo.delete(item.get());
      return new ItemResource(item.get());
    } else {
      throw new ItemOutOfStockException(id);
    }
  }
}
