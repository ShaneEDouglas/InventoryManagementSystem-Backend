package com.example.inventorymangamentsystem.service;

import com.example.inventorymangamentsystem.ResponseObject.ResponseHandler;
import com.example.inventorymangamentsystem.dto.ProductRequest;
import com.example.inventorymangamentsystem.dto.responsedto.CompanyResponse;
import com.example.inventorymangamentsystem.dto.responsedto.ProductResponse;
import com.example.inventorymangamentsystem.dto.responsedto.mappers.ProductMapper;
import com.example.inventorymangamentsystem.dto.responsedto.mappers.UsersInCompany;
import com.example.inventorymangamentsystem.entity.*;
import com.example.inventorymangamentsystem.repository.CompanyRepo;
import com.example.inventorymangamentsystem.repository.ProductRepo;
import com.example.inventorymangamentsystem.repository.UserRepo;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Transactional
public class ProductService {

    private final ProductRepo productRepo;
    private final CompanyRepo companyRepo;
    private final UserRepo userRepo;

    public ProductService(ProductRepo productRepo, CompanyRepo companyRepo, UserRepo userRepo) {
        this.productRepo = productRepo;
        this.companyRepo = companyRepo;
        this.userRepo = userRepo;
    }

    ProductMapper productMapper = new ProductMapper();



    public ResponseEntity<ResponseHandler<ProductResponse>> createProduct(@RequestBody ProductRequest request, Authentication authentication) {

        try {
            Product product = new Product();
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            //Use the user enity from the database not the pincipal
            int userId  = userDetails.getUserId();

            User currentUser = userRepo.findByUserId(userId).orElseThrow( () -> new RuntimeException("User Not Found"));

            if ( ! (currentUser.getRoles().contains(Role.ADMIN)
                    || currentUser.getRoles().contains(Role.MANAGER)
                    || currentUser.getRoles().contains(Role.EMPLOYEE))
            ) {
                return ResponseHandler.responseBuilder("Only Admin, Managers, and Employees can create new products", HttpStatus.BAD_REQUEST,null);
            }

            product.setProductName(request.getProductName());
            product.setProductDescription(request.getProductDescription());
            product.setProductPrice(request.getProductPrice());
            product.setProductCategory(request.getProductCategory());
            product.setProductQuantity(request.getProductQuantity());
            product.setProductImageUrl(request.getProductImageUrl());

            product.setCompany(currentUser.getCompany());
            product.setCreatedBy(currentUser);

            productRepo.save(product);



            return ResponseHandler.responseBuilder("New Product Created", HttpStatus.CREATED, productMapper.toResponse(product));



        }
        catch (Exception e) {
            e.printStackTrace();
            return  ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ResponseEntity<ResponseHandler<List<ProductResponse>>> getAllProducts(Authentication authentication) {
        try {

            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            int userId  = userDetails.getUserId();
            User currentUser = userRepo.findByUserId(userId).orElseThrow( () -> new RuntimeException("User Not Found"));
            List<Product> products;
            ProductResponse productResponse = new ProductResponse();
            List<ProductResponse> productResponses;

            // If admin or manager, retrive all products
            if (currentUser.getRoles().contains(Role.ADMIN) || currentUser.getRoles().contains(Role.MANAGER)) {
                products = productRepo.findByCompany(currentUser.getCompany());

                List<ProductResponse> productsInCompany = products
                        .stream()
                        .map(productMapper::toResponse)
                        .toList();



                        /*
                        product -> map to  -> list of products

                        List<Product> products = productRepo.findByCompany(currentUser.getCompany());
                        product

                        List<Productreposnes> products in company =

                        */

                return ResponseHandler.responseBuilder("All Prodcuts in the Company", HttpStatus.OK, productsInCompany);

            }

            // If employee, retrieve products you created

            if (currentUser.getRoles().contains(Role.EMPLOYEE)) {

                products = productRepo.findByCreatedBy(currentUser);
                List<ProductResponse> productsInCompany = products
                        .stream()
                        .map(productMapper::toResponse)
                        .toList();

                return ResponseHandler.responseBuilder("Products created by "
                        + currentUser.getFirstName()
                        + currentUser.getLastName(),
                        HttpStatus.OK,
                        productsInCompany);
            }

            return ResponseHandler.responseBuilder("Not authorized", HttpStatus.FORBIDDEN, null);



        } catch (Exception e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }


    /*
    This need to updated the selcted prudct you have
**/
    public ResponseEntity<ResponseHandler<ProductResponse>> updateProduct(@RequestBody ProductRequest request, int productId, Authentication authentication) {
        try {
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            int userId  = userDetails.getUserId();
            User currentUser = userRepo.findByUserId(userId).orElseThrow( () -> new RuntimeException("User Not Found"));
            Company userCompany = currentUser.getCompany();

            Product product = productRepo.findByCompanyAndProductId(userCompany, productId).orElseThrow(() -> new RuntimeException("Product not found"));

            // Need to update only the prducts you have

            // Check if the company has the produc


            product.setProductName(request.getProductName());
            product.setProductDescription(request.getProductDescription());
            product.setProductPrice(request.getProductPrice());
            product.setProductQuantity(request.getProductQuantity());
            product.setProductCategory(request.getProductCategory());

            productRepo.save(product);


            return ResponseHandler.responseBuilder("Product Updated", HttpStatus.OK, productMapper.toResponse(product));

        } catch (RuntimeException e){
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ResponseEntity<ResponseHandler<ProductResponse>> deleteProduct(int productId, Authentication authentication) {
        try {
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            int userId  = userDetails.getUserId();

            User currentUser = userRepo.findByUserId(userId).orElseThrow( () -> new RuntimeException("User Not Found"));
            Company userCompany = currentUser.getCompany();

            boolean isManager = currentUser.getRoles().contains(Role.MANAGER);
            boolean isAdmin = currentUser.getRoles().contains(Role.ADMIN);
            boolean isemployee = currentUser.getRoles().contains(Role.EMPLOYEE);


            if (! (isAdmin || isManager)) {
                return ResponseHandler.responseBuilder("Only Admins and managers can delete products", HttpStatus.FORBIDDEN, null);
            }
                Product selectedProduct = productRepo.findByCompanyAndProductId(userCompany, productId).orElseThrow( () -> new RuntimeException("Product not found"));
                productRepo.delete(selectedProduct);

                return ResponseHandler.responseBuilder("Product Deleted", HttpStatus.OK, productMapper.toResponse(selectedProduct));
        } catch(RuntimeException e){
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }

    }


    // For updating indvidual pieces of data( adjusting stock of a product

    public ResponseEntity<ResponseHandler<ProductResponse>> adjustStock(int productId, int quantity, Authentication authentication) {
        try {
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            int userId = userDetails.getUser().getUserId();
            User currentUser = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User Not Found"));
            Company userCompany = currentUser.getCompany();

            Product product = productRepo.findByCompanyAndProductId(userCompany, productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            int newQuantity = product.getProductQuantity() + quantity;

            if (newQuantity < 0) {
                return ResponseHandler.responseBuilder("Insufficient stock", HttpStatus.BAD_REQUEST, null);
            }

            product.setProductQuantity(newQuantity);
            productRepo.save(product);

            return ResponseHandler.responseBuilder("Stock Adjusted", HttpStatus.OK, productMapper.toResponse(product));

        } catch (RuntimeException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }


}
