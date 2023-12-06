package co.learning.project.inventoryservice;

import co.learning.project.inventoryservice.module.Inventory;
import co.learning.project.inventoryservice.repo.InventoryRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class  InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(InventoryRepo inventoryRepo){
        return args -> {
            Inventory inventory=new Inventory();
            inventory.setSkuCode("iphone_15");
            inventory.setQuantity(15);

            Inventory inventory1=new Inventory();
            inventory1.setSkuCode("iphone_15 - Black");
            inventory1.setQuantity(15);

            inventoryRepo.save(inventory);
            inventoryRepo.save(inventory1);

        };
    }
}
