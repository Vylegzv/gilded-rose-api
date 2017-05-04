package com.vylegzv.gilded_rose.service;

import java.util.Collection;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vylegzv.gilded_rose.domain.Item;
import com.vylegzv.gilded_rose.exception.ItemOutOfStockException;
import com.vylegzv.gilded_rose.repo.ItemRepo;

/**
 * 
 * @author vella
 *
 */
@Service
public class ItemService {

  @Autowired
  private ItemRepo itemRepo;

  public Collection<Item> getItems() {
    return Lists.newArrayList(itemRepo.findAll());
  }

  public Item buyItem(long id) {
    return Optional.ofNullable(itemRepo.findOne(id))
        .orElseThrow(() -> new ItemOutOfStockException(id));
  }
}
