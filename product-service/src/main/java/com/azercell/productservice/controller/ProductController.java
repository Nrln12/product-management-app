package com.azercell.productservice.controller;

import com.azercell.productservice.services.ProductService;
import com.azercell.productservice.services.impl.RedisServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final RedisServiceImpl redisService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate; // Inject the Redis template

    @Autowired
    private ObjectMapper objectMapper; // Jackson ObjectMapper for JSON conversion
    private String url ="http://www.mocky.io/v2/5e307edf3200005d00858b49";

    @GetMapping("filters")
    public ResponseEntity<?> getAllProducts(@RequestParam(value = "minPrice", required = false) Double minPrice,
                                            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                                            @RequestParam(value = "size", required = false) List<String> sizes,
                                            @RequestParam(value = "highlight",required = false) List<String> highlights) {
        return ResponseEntity.ok(redisService.getProductFromCache(url, minPrice, maxPrice,sizes, highlights));
    }
    @GetMapping("/fetch-data")
    public ResponseEntity<?> fetchData() {
        return ResponseEntity.ok(productService.getAllProducts());
    }


    @GetMapping("/fetch-json")
    public ResponseEntity<String> fetchJsonFromUrl() {
        String cachedData = getCachedData(url);
        if (cachedData != null) {
            return ResponseEntity.ok(cachedData);
        }

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            putDataInCache(url, response.getBody());

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Cache methods
    public String getCachedData(String url) {
        return stringRedisTemplate.opsForValue().get(url);
    }
    public void putDataInCache(String url, String jsonData) {
        stringRedisTemplate.opsForValue().set(url, jsonData, Duration.ofMinutes(10));
    }
}
