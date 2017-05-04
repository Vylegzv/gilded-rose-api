package com.vylegzv.gilded_rose;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import com.vylegzv.gilded_rose.client.ItemApi;
import com.vylegzv.gilded_rose.domain.Item;
import com.vylegzv.gilded_rose.repo.ItemRepo;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GildedRoseApplication.class)
@WebAppConfiguration
public class GildedRoseApplicationTests {

  private MediaType contentType =
      new MediaType(MediaType.APPLICATION_JSON.getType(),
          MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

  private MockMvc mockMvc;
  private HttpMessageConverter mappingJackson2HttpMessageConverter;
  private List<Item> items = new ArrayList<>();

  @Autowired
  private ItemRepo itemRepo;
  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  void setConverters(HttpMessageConverter<?>[] converters) {
    this.mappingJackson2HttpMessageConverter =
        Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny().orElse(null);
    assertNotNull("JSON message converter cannot be null",
        this.mappingJackson2HttpMessageConverter);
  }

  @Before
  public void setup() throws Exception {
    this.mockMvc = webAppContextSetup(webApplicationContext).build();
    this.itemRepo.deleteAllInBatch();
    this.items.add(itemRepo.save(new Item("Item1", "Item1 Description", 10)));
    this.items.add(itemRepo.save(new Item("Item2", "Item2 Description", 11)));
    this.items.add(itemRepo.save(new Item("Item3", "Item3 Description", 12)));
    this.items.add(itemRepo.save(new Item("Item4", "Item4 Description", 13)));
  }

  @Test
  public void itemOutOfStock() throws Exception {
    mockMvc.perform(get(ItemApi.ITEM_BUY_PATH + "?" + ItemApi.ID_PARAM + "=5")
        .contentType(contentType)).andExpect(status().isGone());
  }

  @Test
  public void buyItem() throws Exception {
    mockMvc
        .perform(get(ItemApi.ITEM_BUY_PATH + "?" + ItemApi.ID_PARAM + "="
            + items.get(0).getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(contentType))
        .andExpect(jsonPath("$.item.name", is(items.get(0).getName())))
        .andExpect(
            jsonPath("$.item.description", is(items.get(0).getDescription())))
        .andExpect(jsonPath("$.item.price", is(items.get(0).getPrice())))
        .andExpect(jsonPath("$._links.items.href",
            is("http://localhost" + ItemApi.ITEMS_PATH)))
        .andExpect(jsonPath("$._links.self.href",
            is("http://localhost" + ItemApi.ITEM_BUY_PATH + "?"
                + ItemApi.ID_PARAM + "=" + items.get(0).getId())));
  }

  @Test
  public void getItems() throws Exception {
    mockMvc.perform(get(ItemApi.ITEMS_PATH)).andExpect(status().isOk())
        .andExpect(content().contentType(contentType))
        .andExpect(jsonPath("$._embedded.itemResources", hasSize(4)))
        .andExpect(jsonPath("$._embedded.itemResources[0].item.name",
            is(items.get(0).getName())))
        .andExpect(jsonPath("$._embedded.itemResources[0].item.description",
            is(items.get(0).getDescription())))
        .andExpect(jsonPath("$._embedded.itemResources[0].item.price",
            is(items.get(0).getPrice())))
        .andExpect(jsonPath("$._embedded.itemResources[1].item.name",
            is(items.get(1).getName())))
        .andExpect(jsonPath("$._embedded.itemResources[1].item.description",
            is(items.get(1).getDescription())))
        .andExpect(jsonPath("$._embedded.itemResources[1].item.price",
            is(items.get(1).getPrice())))
        .andExpect(jsonPath("$._embedded.itemResources[2].item.name",
            is(items.get(2).getName())))
        .andExpect(jsonPath("$._embedded.itemResources[2].item.description",
            is(items.get(2).getDescription())))
        .andExpect(jsonPath("$._embedded.itemResources[2].item.price",
            is(items.get(2).getPrice())))
        .andExpect(jsonPath("$._embedded.itemResources[3].item.name",
            is(items.get(3).getName())))
        .andExpect(jsonPath("$._embedded.itemResources[3].item.description",
            is(items.get(3).getDescription())))
        .andExpect(jsonPath("$._embedded.itemResources[3].item.price",
            is(items.get(3).getPrice())));
  }
}
