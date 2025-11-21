package laundry.com.online_laundry_service.Services;

import laundry.com.online_laundry_service.Entities.LaundryService;
import laundry.com.online_laundry_service.Repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public LaundryService createService(LaundryService service) {
        // ممكن تضبط صورة افتراضية لو imageUrl == null
        // if (service.getImageUrl() == null) service.setImageUrl("/img/default-service.jpg");
        return serviceRepository.save(service);
    }

    public List<LaundryService> getAllServices() {
        return serviceRepository.findAll();
    }

    public Optional<LaundryService> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    public LaundryService updateService(Long id, LaundryService serviceDetails) {
        return serviceRepository.findById(id)
                .map(service -> {
                    service.setName(serviceDetails.getName());
                    service.setDescription(serviceDetails.getDescription());
                    service.setPrice(serviceDetails.getPrice());
                    service.setImageUrl(serviceDetails.getImageUrl()); // 🔥 مهم
                    return serviceRepository.save(service);
                }).orElse(null);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
}
