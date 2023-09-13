package com.azercell.productservice.services.impl;

import com.azercell.productservice.entity.Product;
import com.azercell.productservice.services.ProductService;
import com.azercell.productservice.utils.ReadJson;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ReadJson readJson;

    public ProductServiceImpl(ReadJson readJson) {
        this.readJson = readJson;
    }

    @Override
    public List<Product> getAllProducts() {
       return readJson.fetchDataFromUrl();
    }
}
