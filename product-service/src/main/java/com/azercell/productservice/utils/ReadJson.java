package com.azercell.productservice.utils;

import com.azercell.productservice.dto.ProductListWrapper;
import com.azercell.productservice.entity.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
@Service
public class ReadJson {
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    private final RestTemplate restTemplate;
    private static final String url = "http://www.mocky.io/v2/5e307edf3200005d00858b49";

    public ReadJson(ObjectMapper objectMapper, ModelMapper modelMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.restTemplate = restTemplate;
    }
    @SneakyThrows
    public List<Product> fetchDataFromUrl() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        ProductListWrapper productListWrapper = objectMapper.readValue(response.getBody(), ProductListWrapper.class);
        List<Product> products = productListWrapper.getProducts();
        return products;

    }
}
