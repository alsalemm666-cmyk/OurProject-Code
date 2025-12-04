package laundry.com.online_laundry_service.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import laundry.com.online_laundry_service.Entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
        List<Order> findByUserId(Long userId);

}
