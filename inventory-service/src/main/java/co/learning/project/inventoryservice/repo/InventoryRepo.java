package co.learning.project.inventoryservice.repo;

import co.learning.project.inventoryservice.module.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory,Long> {

    Optional<Inventory> findBySkuCodeIn(List<String> skuCode);

}
