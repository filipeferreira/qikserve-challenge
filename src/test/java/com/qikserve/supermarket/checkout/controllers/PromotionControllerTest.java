package com.qikserve.supermarket.checkout.controllers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.qikserve.supermarket.checkout.models.Promotion;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PromotionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String BASE_URL = "/promotions";

    @BeforeAll
    static void init() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
                .registerModule(new JavaTimeModule());
    }

    @Test
    void save() throws Exception {
        Promotion promotion = new Promotion("SAVE_150", 150, LocalDate.now());
        MvcResult result = this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(promotion)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        Promotion newPromotion = mapper.readValue(result.getResponse().getContentAsString(), Promotion.class);

        assertThat(promotion.getId()).isNotNull();
        assertThat(promotion.getCode()).isEqualTo(newPromotion.getCode());
        assertThat(promotion.getDiscount()).isEqualTo(newPromotion.getDiscount());
        assertThat(promotion.getExpiration()).isEqualTo(newPromotion.getExpiration());

        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newPromotion)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$[0].userMessage").value("Promotion already exists."));

        Promotion expiredPromotion = new Promotion("SAVE_1000", 1000, LocalDate.of(2020, 4, 3));

        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(expiredPromotion)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$[0].userMessage").value("Invalid date. Must be greater or equal to today's date."));

        Promotion promotionWithNullExpiration = new Promotion("SAVE_1000", 1000, null);
        Promotion promotionWithNullDiscount = new Promotion("SAVE_1000", null, LocalDate.now());
        Promotion promotionWithNullCode = new Promotion(null, 1000, LocalDate.now());
        Promotion promotionWithInvalidDiscount = new Promotion("SAVE_1000", -1, LocalDate.now());

        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(promotionWithNullExpiration)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$[0].userMessage").value("Expiration is required."));

        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(promotionWithNullDiscount)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$[0].userMessage").value("Discount is required."));

        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(promotionWithNullCode)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$[0].userMessage").value("Code is required."));

        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(promotionWithInvalidDiscount)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$[0].userMessage").value("Discount must be greater than 1."));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getPromotions() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Promotion[] promotions = mapper.readValue(mvcResult.getResponse().getContentAsString(), Promotion[].class);
        assertThat(promotions).isEmpty();

        Promotion save150 = new Promotion("SAVE_150", 150, LocalDate.now());
        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(save150)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        Promotion save200 = new Promotion("SAVE_200", 200, LocalDate.now());
        this.mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(save200)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        mvcResult = this.mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        promotions = mapper.readValue(mvcResult.getResponse().getContentAsString(), Promotion[].class);
        assertThat(promotions.length).isEqualTo(2);

    }
}