package com.example.inventorymangamentsystem.dto.responsedto.mappers;

import com.example.inventorymangamentsystem.dto.responsedto.ProductResponse;
import com.example.inventorymangamentsystem.entity.Product;
import lombok.Data;

@Data
public class ProductMapper {
    public ProductResponse toResponse(Product product) {
        ProductMapper productMapper = new ProductMapper();

        ProductResponse dto = new ProductResponse();

        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setProductDescription(product.getProductDescription());
        dto.setProductPrice(product.getProductPrice());
        dto.setProductQuantity(product.getProductQuantity());
        dto.setProductCategory(product.getProductCategory());
        dto.setProductImage(product.getProductImageUrl());

        //only get the names (not full objects)
        dto.setCreatedBy(product.getCreatedBy().getFirstName());
        dto.setCompanyName(product.getCompany().getCompanyName());

        return dto;
    }
}