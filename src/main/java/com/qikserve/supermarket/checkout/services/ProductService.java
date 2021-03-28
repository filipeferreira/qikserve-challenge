package com.qikserve.supermarket.checkout.services;

import com.qikserve.supermarket.checkout.excetions.ProductException;
import com.qikserve.supermarket.checkout.models.Product;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final RestTemplate restTemplate;
    private final String wiremockProductsUrl;

    public ProductService(@Qualifier("wiremock") RestTemplate restTemplate,
                          @Value("${integrations.wiremock.products}") String wiremockProductsUrl) {
        this.restTemplate = restTemplate;
        this.wiremockProductsUrl = wiremockProductsUrl;
    }

    public List<Product> getProducts() {
        return Arrays.asList(restTemplate.getForEntity(wiremockProductsUrl, Product[].class).getBody());
    }

    @SneakyThrows
    public Optional<Product> getProduct(String id) {
        try {
            return Optional.ofNullable(restTemplate.getForEntity(wiremockProductsUrl + "/" + id, Product.class).getBody());
        } catch (HttpClientErrorException e) {
            throw new ProductException("product.notfound", e, e.getStatusCode());
        }
    }
}
