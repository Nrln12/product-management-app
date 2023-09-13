package com.azercell.productservice.dto;

import com.azercell.productservice.entity.Product;
import lombok.Data;

import java.util.List;

@Data
public class ProductListWrapper {
    private List<Product> products;

}
