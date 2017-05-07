package com.vylegzv.gilded_rose;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.common.util.JacksonJsonParser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

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
  private FilterChainProxy springSecurityFilterChain;

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
    this.mockMvc = webAppContextSetup(webApplicationContext)
        .addFilter(springSecurityFilterChain).build();
    this.itemRepo.deleteAllInBatch();
    this.items.add(itemRepo.save(new Item("Item1", "Item1 Description", 10)));
    this.items.add(itemRepo.save(new Item("Item2", "Item2 Description", 11)));
    this.items.add(itemRepo.save(new Item("Item3", "Item3 Description", 12)));
    this.items.add(itemRepo.save(new Item("Item4", "Item4 Description", 13)));
  }

  @Test
  public void itemOutOfStockUnauthorized() throws Exception {
    mockMvc
        .perform(get(ItemApi.ITEM_BUY_PATH + "?" + ItemApi.ID_PARAM + "=100")
            .contentType(contentType))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error", is("unauthorized")));
  }

  @Test
  public void buyItemUnauthorized() throws Exception {
    mockMvc
        .perform(get(ItemApi.ITEM_BUY_PATH + "?" + ItemApi.ID_PARAM + "="
            + items.get(0).getId()).secure(true))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error", is("unauthorized")));
  }

  @Test
  public void getItems() throws Exception {
    mockMvc.perform(get(ItemApi.ITEMS_PATH).secure(true))
        .andExpect(status().isOk())
        .andExpect(content().contentType(contentType))
        .andExpect(jsonPath("$._embedded.itemResources", hasSize(4)))
        .andExpect(jsonPath("$._embedded.itemResources[0].item.name",
            is(items.get(0).getName())))
        .andExpect(jsonPath("$._embedded.itemResources[0].item.description",
            is(items.get(0).getDescription())))
        .andExpect(jsonPath("$._embedded.itemResources[0].item.price",
            is(items.get(0).getPrice())))

        .andExpect(jsonPath("$._embedded.itemResources[0]._links.items.href",
            is("http://localhost" + ItemApi.ITEMS_PATH)))
        .andExpect(jsonPath("$._embedded.itemResources[0]._links.self.href",
            is("http://localhost" + ItemApi.ITEM_BUY_PATH + "?"
                + ItemApi.ID_PARAM + "=" + items.get(0).getId())))

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

  @Test
  public void buyItemAuthorized() throws Exception {
    String accessToken = obtainAccessToken("john", "pwd123");
    mockMvc
        .perform(get(ItemApi.ITEM_BUY_PATH + "?" + ItemApi.ID_PARAM + "="
            + items.get(0).getId())
                .header("Authorization", "Bearer " + accessToken).secure(true))
        .andExpect(status().isOk());
  }

  @Test
  public void wrongCredentials() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "password");
    params.add("client_id", "trustedclient");
    params.add("username", "wronguser");
    params.add("password", "wrongpass");

    mockMvc
        .perform(post("/oauth/token").params(params)
            .with(httpBasic("trustedclient", "trustedclient123"))
            .accept(contentType))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("invalid_grant")))
        .andExpect(jsonPath("$.error_description", is("Bad credentials")));
  }

  private String obtainAccessToken(String username, String password)
      throws Exception {

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "password");
    params.add("client_id", "trustedclient");
    params.add("username", username);
    params.add("password", password);

    ResultActions result = mockMvc
        .perform(post("/oauth/token").params(params)
            .with(httpBasic("trustedclient", "trustedclient123"))
            .accept(contentType))
        .andExpect(status().isOk())
        .andExpect(content().contentType(contentType))
        .andExpect(jsonPath("$.access_token", is(notNullValue())))
        .andExpect(jsonPath("$.token_type", is(equalTo("bearer"))))
        .andExpect(jsonPath("$.refresh_token", is(notNullValue())))
        .andExpect(jsonPath("$.expires_in", is(lessThan(200))))
        .andExpect(jsonPath("$.scope", is(equalTo("buy"))));

    String resultString = result.andReturn().getResponse().getContentAsString();

    JacksonJsonParser jsonParser = new JacksonJsonParser();
    return jsonParser.parseMap(resultString).get("access_token").toString();
  }
}
