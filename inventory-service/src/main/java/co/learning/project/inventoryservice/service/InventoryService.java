package co.learning.project.inventoryservice.service;

import co.learning.project.inventoryservice.dto.InventoryResponse;
import co.learning.project.inventoryservice.repo.InventoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepo inventoryRepo;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock( List<String> skuCode)
//            throws InterruptedException
    {
//        log.info("Wait Started");
//        Thread.sleep(5000);
//        log.info("Wait Ended");
        return inventoryRepo.findBySkuCodeIn(skuCode).stream()
                .map(inventory -> InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity()>0)
                        .build()).toList();
    }
}
