package com.example.inventorymangamentsystem.service;

import com.example.inventorymangamentsystem.dto.ProductRequest;
import com.example.inventorymangamentsystem.entity.Product;
import com.example.inventorymangamentsystem.entity.User;
import com.example.inventorymangamentsystem.entity.UserDetailsPrinciple;
import com.example.inventorymangamentsystem.repository.ProductRepo;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


@Service
public class ProductService {

    private final ProductRepo productRepo;

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody ProductRequest request, Authentication authentication) {

        try {
            Product product = new Product();
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            User currentUser = userDetails.getUser();


            // Enter in product details in the request
            product.setProductName(request.getProductName());
            product.setProductDescription(request.getProductDescription());
            product.setProductPrice(request.getProductPrice());
            product.setProductQuantity(request.getProductQuantity());
            product.setProductCategory(request.getProductCategory());
            product.setUser(userDetails.getUser());

            productRepo.save(product);

            return ResponseEntity.status(200).body(Map.of(
                    "message", "Product created successfully",
                    "product", Map.of(
                            "productId", product.getProductId(),
                            "productName", product.getProductName(),
                            "ProductDescription", product.getProductDescription(),
                            "ProductCategory", product.getProductCategory(),
                            "ProductQuantity", product.getProductQuantity(),
                            "ProductPrice", product.getProductPrice(),
                            "UserId",product.getUser().getUserId()
                    )
            ));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of("Error message", e.getMessage()));
        }
    }

    public ResponseEntity<Map<String, Object>> getAllProducts(Authentication authentication) {
        try {
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            User currentUser = userDetails.getUser();
            List<Product> products;


            products = productRepo.findByUser(currentUser); // employees and viewrs can only see their products




            return ResponseEntity.status(200).body(Map.of(
                    "Products of user ", products
            ));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("Error message", e.getMessage()));
        }
    }


    public ResponseEntity<Map<String, Object>> updateProduct(@RequestBody ProductRequest request, int productId, Authentication authentication) {
        try {
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            User currentUser = userDetails.getUser();

            Product product = currentUser.getProducts().get(productId);

            product.setProductName(request.getProductName());
            product.setProductDescription(request.getProductDescription());
            product.setProductPrice(request.getProductPrice());
            product.setProductQuantity(request.getProductQuantity());
            product.setProductCategory(request.getProductCategory());

            productRepo.save(product);

            return ResponseEntity.status(200).body(Map.of(
                    "Message", "Updated Prodcut Successfully",
                    "product", product
            ));


        } catch (RuntimeException e){
            return ResponseEntity.status(400).body(Map.of("Error message", e.getMessage()));
        }
    }

    public ResponseEntity<Map<String, Object>> deleteProduct(int productId, Authentication authentication) {
        try {
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            User currentUser = userDetails.getUser();

                Product selectedProduct = currentUser.getProducts().get(productId);
                productRepo.delete(selectedProduct);

                return ResponseEntity.status(200).body(Map.of(
                        "Message", "Product deleted succesfully",
                        "product", selectedProduct
                ));
        } catch(RuntimeException e){
            return ResponseEntity.status(400).body(Map.of("Error message", e.getMessage()));
        }

    }


}
