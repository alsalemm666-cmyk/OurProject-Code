package laundry.com.online_laundry_service.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import laundry.com.online_laundry_service.Entities.User;

public interface Userrepository extends JpaRepository<User, Long> {
     User findByEmail(String email);
}
