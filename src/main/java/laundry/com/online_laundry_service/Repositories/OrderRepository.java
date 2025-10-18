package laundry.com.online_laundry_service.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.criteria.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    laundry.com.online_laundry_service.Entities.Order save(laundry.com.online_laundry_service.Entities.Order order);
    
}
