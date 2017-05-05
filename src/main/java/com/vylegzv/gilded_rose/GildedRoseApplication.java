package com.vylegzv.gilded_rose;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.vylegzv.gilded_rose.controller.ItemController;
import com.vylegzv.gilded_rose.domain.Item;
import com.vylegzv.gilded_rose.exception.ItemControllerAdvice;
import com.vylegzv.gilded_rose.repo.ItemRepo;
import com.vylegzv.gilded_rose.security.AuthorizationServerConfig;
import com.vylegzv.gilded_rose.security.OAuth2Config;
import com.vylegzv.gilded_rose.security.ResourceServerConfig;
import com.vylegzv.gilded_rose.service.ItemService;

/**
 * 
 * @author vella
 *
 */
@EnableAutoConfiguration
@EnableWebMvc
@EnableJpaRepositories(basePackageClasses = ItemRepo.class)
@ComponentScan(basePackageClasses = {ItemControllerAdvice.class,
    ItemController.class, ItemService.class})
@EntityScan(basePackageClasses = Item.class)
@Configuration
@SpringBootApplication
@Import({OAuth2Config.class, ResourceServerConfig.class,
    AuthorizationServerConfig.class})
public class GildedRoseApplication extends SpringBootServletInitializer {

  private static final Logger log =
      LoggerFactory.getLogger(GildedRoseApplication.class);

  @Autowired
  ItemRepo itemRepository;

  public static void main(String[] args) {
    SpringApplication.run(GildedRoseApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(
      SpringApplicationBuilder application) {
    return application.sources(GildedRoseApplication.class);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
      }
    };
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
    };
  }
}
