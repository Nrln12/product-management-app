package com.azercell.productservice.services.impl;

import com.azercell.productservice.dto.ProductListWrapper;
import com.azercell.productservice.entity.Product;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RedisServiceImpl {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;


    public RedisServiceImpl(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }


    @SneakyThrows
    public List<Product> getProductFromCache(String url, Double minPrice, Double maxPrice, List<String> sizes, List<String> highlights) {
        List<Product> products;
        String cachedData = getCachedData(url);
        if (cachedData != null) {
            ProductListWrapper productListWrapper = objectMapper.readValue(cachedData, ProductListWrapper.class);
            products = productListWrapper.getProducts();
        } else {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            putDataInCache(url, response.getBody());
            products = objectMapper.readValue(response.getBody(), ProductListWrapper.class).getProducts();
        }
        return products;
    }

    public String getCachedData(String url) {
        return stringRedisTemplate.opsForValue().get(url);
    }

    public void putDataInCache(String url, String jsonData) {
        stringRedisTemplate.opsForValue().set(url, jsonData, Duration.ofMinutes(10));
    }
}
