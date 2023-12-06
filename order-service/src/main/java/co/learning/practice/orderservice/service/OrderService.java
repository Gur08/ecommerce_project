package co.learning.practice.orderservice.service;

import co.learning.practice.orderservice.dto.InventoryResponse;
import co.learning.practice.orderservice.dto.OrderLineItemDto;
import co.learning.practice.orderservice.dto.OrderRequest;
import co.learning.practice.orderservice.event.OrderPlaceEvent;
import co.learning.practice.orderservice.model.Order;
import co.learning.practice.orderservice.model.OrderLineItem;
import co.learning.practice.orderservice.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    WebClient.Builder webClientBuilder;

    private final Tracer tracer;
    private final KafkaTemplate<String, OrderPlaceEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> collect = orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::mapToOrderLineDtos)
                .collect(Collectors.toList());
        order.setOrderLineItems(collect);

        List<String> skuCodes = order.getOrderLineItems().stream().map(OrderLineItem::getSkuCode).toList();

        log.info("Calling Inventory Service");

        Span name = tracer.nextSpan().name("Inventory-Service Lookup");

        try (Tracer.SpanInScope inScope=tracer.withSpan(name.start())){
            InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            boolean productInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::getIsInStock);


            if (productInStock){
                orderRepo.save(order);
                kafkaTemplate.send("NotificationTopic",new OrderPlaceEvent(order.getOrderNumber()));
                return "Order is successfully placed";
            }else {
                throw new IllegalArgumentException("Product not in stock Please try later");
            }

        }finally {
            name.end();
        }

    }

    private OrderLineItem mapToOrderLineDtos(OrderLineItemDto orderLineItemDtos) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setId(orderLineItemDtos.getId());
        orderLineItem.setSkuCode(orderLineItemDtos.getSkuCode());
        orderLineItem.setQuantity(orderLineItemDtos.getQuantity());
        orderLineItem.setPrice(orderLineItemDtos.getPrice());
        return orderLineItem;
    }
}
