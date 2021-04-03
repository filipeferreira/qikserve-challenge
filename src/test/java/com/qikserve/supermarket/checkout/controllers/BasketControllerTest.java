package com.qikserve.supermarket.checkout.controllers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.qikserve.supermarket.checkout.models.AddItemDTO;
import com.qikserve.supermarket.checkout.models.Basket;
import com.qikserve.supermarket.checkout.models.BasketTotals;
import com.qikserve.supermarket.checkout.models.Product;
import com.qikserve.supermarket.checkout.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BasketControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    private String baseUrl;

    private Basket newBasket = new Basket();

    private ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));

        baseUrl = String.format("http://localhost:%d/baskets", port);

        newBasket = this.restTemplate.getForObject(baseUrl + "/new",
                Basket.class);

        when(productService.getProduct("A")).thenReturn(
                new Product()
                        .id("A")
                        .name("Product A")
                        .price(1000));

        when(productService.getProduct("B")).thenReturn(
                new Product()
                        .id("B")
                        .name("Product B")
                        .price(2000));

        when(productService.getProduct("C")).thenReturn(
                new Product()
                        .id("C")
                        .name("Product C")
                        .price(3500));


    }

    @Test
    @Order(1)
    void create() {

        assertThat(newBasket).isNotNull();
        assertThat(newBasket).extracting("id", "checkedOut").isNotEmpty();
        assertThat(newBasket).extracting("items", "promotions").allMatch(arr -> arr.equals(Collections.emptyList()));
        assertThat(newBasket.getId()).isNotNull();
        assertThat(newBasket.isCheckedOut()).isFalse();

        assertThat(newBasket.getBasketTotals())
                .extracting("rawTotal", "totalPromos", "totalPayable")
                .allMatch(s -> s.equals("£0.00"));
    }

    @Test
    @Order(2)
    void getBasket() throws Exception {
        String url = baseUrl + "/abcd";
        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$[0].userMessage").value("Invalid argument."));

        url = String.format("%s/%s", baseUrl, UUID.randomUUID());
        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$[0].userMessage").value("Basket not found."));

        url = String.format("%s/%s", baseUrl, newBasket.getId());
        MvcResult result = this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Basket basket = mapper.readValue(result.getResponse().getContentAsString(), Basket.class);

        testIfAllValuesAreEqualsToNewBasket(basket);

    }

    private void testIfAllValuesAreEqualsToNewBasket(Basket basket) {
        assertThat(basket.getId()).isEqualTo(newBasket.getId());
        assertThat(basket.isCheckedOut()).isEqualTo(newBasket.isCheckedOut());
        assertThat(basket.getPromotions()).isEmpty();
        assertThat(basket.getItems()).isEmpty();

        BasketTotals totals = basket.getBasketTotals();
        assertThat(totals.getRawTotal()).isEqualTo(newBasket.getBasketTotals().getRawTotal());
        assertThat(totals.getTotalPayable()).isEqualTo(newBasket.getBasketTotals().getTotalPayable());
        assertThat(totals.getTotalPromos()).isEqualTo(newBasket.getBasketTotals().getTotalPromos());
    }

    @Test
    @Order(3)
    void addItem() throws Exception {

        String url = String.format("%s/%s/add-item", baseUrl, UUID.randomUUID());
        this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new AddItemDTO("A", 1))))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$[0].userMessage").value("Basket not found."));

        MvcResult result = addProduct("A", 1);

        Basket basket = mapper.readValue(result.getResponse().getContentAsString(), Basket.class);
        assertThat(basket.getItems().size()).isEqualTo(1);
        assertThat(basket.getBasketTotals()).extracting("rawTotal", "totalPayable").allMatch(s -> s.equals("£10.00"));

        result = addProduct("A", 1);

        basket = mapper.readValue(result.getResponse().getContentAsString(), Basket.class);
        assertThat(basket.getItems().size()).isEqualTo(1);
        assertThat(basket.getItems().stream().filter(i -> i.getProduct().getId().equals("A")).findFirst().get().getAmount()).isEqualTo(2);
        assertThat(basket.getBasketTotals()).extracting("rawTotal", "totalPayable").allMatch(s -> s.equals("£20.00"));

        result = addProduct("B", 1);

        basket = mapper.readValue(result.getResponse().getContentAsString(), Basket.class);
        assertThat(basket.getItems().size()).isEqualTo(2);
        assertThat(basket.getBasketTotals().getRawTotal()).isEqualTo("£40.00");

    }

    private MvcResult addProduct(String idProduct, Integer amount) throws Exception {
        String url = String.format("%s/%s/add-item", baseUrl, newBasket.getId());
        AddItemDTO addItemDTO = new AddItemDTO(idProduct, amount);
        return this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(addItemDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
//
//    @Test
//    void addPromotion() {
//    }
//
//    @Test
//    void checkout() {
//    }
}