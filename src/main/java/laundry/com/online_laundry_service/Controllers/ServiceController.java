package laundry.com.online_laundry_service.Controllers;

import laundry.com.online_laundry_service.Entities.LaundryService;
import laundry.com.online_laundry_service.Services.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @PostMapping
    public LaundryService createService(@RequestBody LaundryService service) {
        return serviceService.createService(service);
    }

    @GetMapping
    public List<LaundryService> getAllServices() {
        return serviceService.getAllServices();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LaundryService> getServiceById(@PathVariable Long id) {
        Optional<LaundryService> service = serviceService.getServiceById(id);
        return service.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LaundryService> updateService(@PathVariable Long id,
                                                        @RequestBody LaundryService serviceDetails) {
        LaundryService updatedService = serviceService.updateService(id, serviceDetails);
        if (updatedService != null) {
            return ResponseEntity.ok(updatedService);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
