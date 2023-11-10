package co.learning.practice.orderservice.controller;

import co.learning.practice.orderservice.dto.OrderRequest;
import co.learning.practice.orderservice.repo.OrderRepo;
import co.learning.practice.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    OrderService orderService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeorder(@RequestBody OrderRequest orderRequest){
        orderService.placeOrder(orderRequest);
        return "Order is succesfully placed";
    }
}
