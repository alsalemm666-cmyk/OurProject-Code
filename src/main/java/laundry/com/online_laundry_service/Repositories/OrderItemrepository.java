package laundry.com.online_laundry_service.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import laundry.com.online_laundry_service.Entities.OrderItem;

public interface OrderItemrepository extends JpaRepository<OrderItem, Long> {
    
}