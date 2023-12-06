package co.learning.project.inventoryservice.controller;

import co.learning.project.inventoryservice.dto.InventoryResponse;
import co.learning.project.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode)
//            throws InterruptedException
    {
return inventoryService.isInStock(skuCode);
    }
}
