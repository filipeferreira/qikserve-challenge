package com.qikserve.supermarket.checkout.controllers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.qikserve.supermarket.checkout.models.*;
import com.qikserve.supermarket.checkout.services.ProductService;
import com.qikserve.supermarket.checkout.services.PromotionService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasketControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PromotionService promotionService;

    @MockBean
    private ProductService productService;

    private Basket newBasket;
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String BASE_URL = "/baskets";

    @BeforeAll
    static void init() {

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
                .registerModule(new JavaTimeModule());

    }

    @BeforeEach
    void setUp() {

        newBasket = this.restTemplate.getForObject(BASE_URL + "/new",
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
        String url = BASE_URL + "/abcd";
        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$[0].userMessage").value("Invalid argument."));

        url = String.format("%s/%s", BASE_URL, UUID.randomUUID());
        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$[0].userMessage").value("Basket not found."));

        url = String.format("%s/%s", BASE_URL, newBasket.getId());
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

        String urlAddItem = String.format("%s/%s/add-item", BASE_URL, UUID.randomUUID());
        postRequestWithError(urlAddItem, new AddItemDTO("A", 1), HttpStatus.NOT_FOUND, "Basket not found.");

        urlAddItem = String.format("%s/%s/add-item", BASE_URL, newBasket.getId());
        Basket basket = postRequest(urlAddItem, new AddItemDTO("A", 1));
        assertThat(basket.getItems().size()).isEqualTo(1);
        assertThat(basket.getBasketTotals()).extracting("rawTotal", "totalPayable").allMatch(s -> s.equals("£10.00"));

        basket = postRequest(urlAddItem, new AddItemDTO("A", 1));
        assertThat(basket.getItems().size()).isEqualTo(1);
        assertThat(basket.getItems().stream().filter(i -> i.getProduct().getId().equals("A")).findFirst().get().getAmount()).isEqualTo(2);
        assertThat(basket.getBasketTotals()).extracting("rawTotal", "totalPayable").allMatch(s -> s.equals("£20.00"));

        basket = postRequest(urlAddItem, new AddItemDTO("B", 2));
        assertThat(basket.getItems().size()).isEqualTo(2);
        assertThat(basket.getBasketTotals().getRawTotal()).isEqualTo("£60.00");

        postRequestWithError(urlAddItem, new AddItemDTO(null, 1), HttpStatus.BAD_REQUEST, "Product Id is required.");

        postRequestWithError(urlAddItem, new AddItemDTO("A", -1), HttpStatus.BAD_REQUEST, "Amount must be greater than 1.");

        postRequestWithError(urlAddItem, null, HttpStatus.BAD_REQUEST, "Invalid request.");

    }

    private void postRequestWithError(String url, Object object, HttpStatus errorStatus, String errorMessage) throws Exception {
        this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(object)))
                .andDo(print())
                .andExpect(status().is(errorStatus.value()))
                .andExpect(jsonPath("$[0].userMessage").value(errorMessage));
    }

    private Basket postRequest(String url, Object object) throws Exception {
        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(object)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        return mapper.readValue(result.getResponse().getContentAsString(), Basket.class);
    }

    @Test
    @Order(4)
    void addPromotion() throws Exception {
        String url = String.format("%s/%s/add-promotion/%s", BASE_URL, UUID.randomUUID(), "WHATEVER");
        postRequestWithError(url, null, HttpStatus.NOT_FOUND, "Basket not found.");

        url = String.format("%s/%s/add-promotion/%s", BASE_URL, newBasket.getId(), "DOESNT_EXIST");
        postRequestWithError(url, null, HttpStatus.NOT_FOUND, "Promotion not found.");

        promotionService.save(new Promotion("SAVE_150", 150, LocalDate.now()));

        url = String.format("%s/%s/add-promotion/%s", BASE_URL, newBasket.getId(), "SAVE_150");
        postRequest(url, null);

        url = String.format("%s/%s/add-item", BASE_URL, newBasket.getId());
        AddItemDTO addItemDTO = new AddItemDTO("A", 6);
        Basket basket = postRequest(url, addItemDTO);

        assertThat(basket.getBasketTotals().getRawTotal()).isEqualTo("£60.00");
        assertThat(basket.getBasketTotals().getTotalPromos()).isEqualTo("£1.50");
        assertThat(basket.getBasketTotals().getTotalPayable()).isEqualTo("£58.50");

        url = String.format("%s/%s/add-promotion/%s", BASE_URL, newBasket.getId(), "SAVE_150");
        postRequestWithError(url, null, HttpStatus.CONFLICT, "Promotion already exists.");

    }

    @Test
    @Order(5)
    void checkout() throws Exception {
        String urlCheckout = String.format("%s/%s/checkout", BASE_URL, UUID.randomUUID());
        patchRequestWithError(urlCheckout, HttpStatus.NOT_FOUND, "Basket not found.");

        urlCheckout = String.format("%s/%s/checkout", BASE_URL, newBasket.getId());
        patchRequestWithError(urlCheckout, HttpStatus.FORBIDDEN, "Basket is empty.");

        String urlAdditem = String.format("%s/%s/add-item", BASE_URL, newBasket.getId());
        AddItemDTO addItemDTO = new AddItemDTO("A", 1);
        postRequest(urlAdditem, addItemDTO);

        Basket basket = patchRequest(urlCheckout);
        assertThat(basket.isCheckedOut()).isTrue();

        postRequestWithError(urlAdditem, addItemDTO, HttpStatus.FORBIDDEN, "Basket has already been ckeched out.");

    }

    private void patchRequestWithError(String url, HttpStatus errorStatus, String errorMessage) throws Exception {
        this.mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(null)))
                .andDo(print())
                .andExpect(status().is(errorStatus.value()))
                .andExpect(jsonPath("$[0].userMessage").value(errorMessage));
    }

    private Basket patchRequest(String url) throws Exception {
        MvcResult result = this.mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(null)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        return mapper.readValue(result.getResponse().getContentAsString(), Basket.class);
    }
}