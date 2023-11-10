package com.programmingtechie.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmingtechie.productservice.dto.ProductRequest;
import com.programmingtechie.productservice.dto.ProductResponse;
import com.programmingtechie.productservice.model.Product;
import com.programmingtechie.productservice.repo.Productrepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

@Autowired
    Productrepo productrepo;

// @InjectMocks
//    ProductController productController;

//@Before
// public void setup(){
//     MockitoAnnotations.initMocks(this);
//     this.mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
// }

@Container
   static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10");

@DynamicPropertySource
static  void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
    dynamicPropertyRegistry.add("spring.data.mongodb.uri",mongoDBContainer::getReplicaSetUrl);
}
    @Test
    void shouldCreateObject() throws Exception {
        ProductRequest productRequest = getProductRequest();
        String valueAsString = objectMapper.writeValueAsString(productRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString))
                .andExpect(status().isCreated());
        Assertions.assertEquals(1, productrepo.findAll().size());
    }
@Test
void shouldGetProducts() throws Exception {

//    Mockito.when(productrepo.findAll()).thenReturn(getProducts());
productrepo.saveAll(getProducts());
productrepo.findAll();
    mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$",hasSize(4)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[2].name", is("Phone")));
//            .andExpect(jsonPath("$[2].name", Matchers.is("Screen")));
}
    private ProductRequest getProductRequest(){
   return ProductRequest.builder()
            .name("Iphone13")
           .description("Iphone13")
           .price(BigDecimal.valueOf(1200))
           .build();
    }
    private List<Product> getProducts(){
        ProductResponse product_1 = new ProductResponse("45ty","Laptop","Lenovo Legion",BigDecimal.valueOf(2400));
        ProductResponse product_2 = new ProductResponse("84gy","Phone","Iphone 15",BigDecimal.valueOf(1400));
        ProductResponse product_3 = new ProductResponse("79ut","Screen","Lenovo 14 inch",BigDecimal.valueOf(400));
       List<ProductResponse> productResponses = new ArrayList<>(Arrays.asList(product_1,product_2,product_3));
       return productResponses.stream().map(this::mapToProduct).collect(Collectors.toList());
    }
    private Product mapToProduct(ProductResponse productResponse) {
        return   Product.builder().id(productResponse.getId())
                .name(productResponse.getName())
                .description(productResponse.getDescription())
                .price(productResponse.getPrice())
                .build();

    }
}
