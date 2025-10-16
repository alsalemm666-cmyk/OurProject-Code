package laundry.com.online_laundry_service.Services;

import laundry.com.online_laundry_service.Entities.Service;
import laundry.com.online_laundry_service.Repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public Service createService(Service service) {
        return serviceRepository.save(service);
    }

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public Optional<Service> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    public Service updateService(Long id, Service serviceDetails) {
        return serviceRepository.findById(id)
                .map(service -> {
                    service.setName(serviceDetails.getName());
                    service.setDescription(serviceDetails.getDescription());
                    service.setPrice(serviceDetails.getPrice());
                    return serviceRepository.save(service);
                }).orElse(null);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
}