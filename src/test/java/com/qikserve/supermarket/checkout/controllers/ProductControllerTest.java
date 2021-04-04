package com.qikserve.supermarket.checkout.controllers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.qikserve.supermarket.checkout.models.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8088)
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String BASE_URL = "/products";

    @BeforeAll
    static void init() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
                .registerModule(new JavaTimeModule());
    }

    @Test
    void getProducts() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Product[] products = mapper.readValue(mvcResult.getResponse().getContentAsString(), Product[].class);
        assertThat(products.length).isEqualTo(4);
    }

    @Test
    void getProduct() throws Exception {
        String url = String.format("%s/%s", BASE_URL, "Dwt5F7KAhi");
        MvcResult mvcResult = this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Product product = mapper.readValue(mvcResult.getResponse().getContentAsString(), Product.class);
        assertThat(product.getId()).isEqualTo("Dwt5F7KAhi");
        assertThat(product.getName()).isEqualTo("Amazing Pizza!");
        assertThat(product.getPrice()).isEqualTo(1099);

        url = String.format("%s/%s", BASE_URL, "DOESNT_EXIST");

        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$[0].userMessage").value("Product not found."));

    }
}