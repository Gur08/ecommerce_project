package com.programmingtechie.productservice.service;

import com.programmingtechie.productservice.dto.ProductRequest;
import com.programmingtechie.productservice.dto.ProductResponse;
import com.programmingtechie.productservice.model.Product;
import com.programmingtechie.productservice.repo.Productrepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final Productrepo productrepo;
    public void createProduct(ProductRequest productrequest){
Product product = Product.builder().name(productrequest.getName())
        .description(productrequest.getDescription())
        .price(productrequest.getPrice())
        .build();
productrepo.save(product);
log.info("Product {} is saved",product.getId());
    }

    public List<ProductResponse> getAllProducts(){
        List<Product> products = productrepo.findAll();
        List<ProductResponse> productResponses = products.stream().map((this::mapToProductResponse)).collect(Collectors.toList());
        log.info("Product list is found");
        return productResponses;
    }

    private ProductResponse mapToProductResponse(Product product) {
      return   ProductResponse.builder().id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();

    }

}

