package laundry.com.online_laundry_service.Repositories;

import laundry.com.online_laundry_service.Entities.LaundryService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<LaundryService, Long> {
}