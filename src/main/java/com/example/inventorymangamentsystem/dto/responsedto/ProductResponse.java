package com.example.inventorymangamentsystem.dto.responsedto;

import lombok.Data;

@Data
public class ProductResponse {
    protected int productId;
    protected String productName;
    protected String productDescription;
    protected double productPrice;
    protected int productQuantity;
    protected String productImage;
    protected String productCategory;

    // Metadata
    private String createdBy;
    private String companyName;
}
