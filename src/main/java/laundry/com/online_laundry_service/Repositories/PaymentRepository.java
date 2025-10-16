package laundry.com.online_laundry_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import laundry.com.online_laundry_service.Entities.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
}
