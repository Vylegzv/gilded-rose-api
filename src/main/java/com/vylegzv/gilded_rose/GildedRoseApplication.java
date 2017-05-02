package com.vylegzv.gilded_rose;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.vylegzv.gilded_rose.domain.Item;
import com.vylegzv.gilded_rose.repo.ItemRepo;

/**
 * 
 * @author vella
 *
 */
@SpringBootApplication
public class GildedRoseApplication {

  private static final Logger log =
      LoggerFactory.getLogger(GildedRoseApplication.class);

  @Autowired
  ItemRepo itemRepository;

  public static void main(String[] args) {
    SpringApplication.run(GildedRoseApplication.class, args);
  }

  @Bean
  public CommandLineRunner demo() {
    return (args) -> {
      itemRepository.save(new Item("Item1", "Item1 Description", 10));
      itemRepository.save(new Item("Item2", "Item2 Description", 11));
      itemRepository.save(new Item("Item3", "Item3 Description", 12));
      itemRepository.save(new Item("Item4", "Item4 Description", 13));

      log.info("Find all items:");
      for (Item item : itemRepository.findAll()) {
        log.info(item.toString());
      }

      log.info("");
      log.info("Find a item by id:");
      Item item = itemRepository.findOne(1L);
      log.info(item.toString());
      log.info("");

      log.info("Buy an item");
      itemRepository.delete(item);
      log.info("");

      log.info("Find all items after deleting item1:");
      for (Item item2 : itemRepository.findAll()) {
        log.info(item2.toString());
      }
      log.info("");
    };
  }
}
