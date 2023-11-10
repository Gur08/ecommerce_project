package co.learning.practice.orderservice.service;

import co.learning.practice.orderservice.dto.InventoryResponse;
import co.learning.practice.orderservice.dto.OrderLineItemDto;
import co.learning.practice.orderservice.dto.OrderRequest;
import co.learning.practice.orderservice.model.Order;
import co.learning.practice.orderservice.model.OrderLineItem;
import co.learning.practice.orderservice.repo.OrderRepo;
import org.hibernate.id.factory.internal.UUIDGenerationTypeStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    WebClient webClient;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> collect = orderRequest.getOrderLineItemDtos()
                .stream()
                .map(this::mapToOrderLineDtos)
                .collect(Collectors.toList());
        order.setOrderLineItems(collect);

        List<String> skuCodes = order.getOrderLineItems().stream().map(OrderLineItem::getSkucode).toList();


        InventoryResponse[] inventoryResponseArray = webClient.get()
                .uri("http://localhost:8082/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean productInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::getIsInStock);


        if (productInStock){
            orderRepo.save(order);
        }else {
            throw new IllegalArgumentException("Product not in stock Please try later");
        }


    }


    private OrderLineItem mapToOrderLineDtos(OrderLineItemDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setId(orderLineItemDto.getId());
        orderLineItem.setSkucode(orderLineItemDto.getSkucode());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        return orderLineItem;
    }
}
