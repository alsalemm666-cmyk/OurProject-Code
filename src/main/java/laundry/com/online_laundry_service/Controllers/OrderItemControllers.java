package laundry.com.online_laundry_service.Controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;
import lombok.RequiredArgsConstructor;

import laundry.com.online_laundry_service.Entities.OrderItem;
import laundry.com.online_laundry_service.Services.OrderItemServices;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemControllers {

    private final OrderItemServices service;

    @GetMapping public List<OrderItem> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getById(@PathVariable Long id) {
        return service.getById(id).map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping public OrderItem create(@RequestBody OrderItem item) { return service.save(item); }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> update(@PathVariable Long id, @RequestBody OrderItem item) {
        OrderItem updated = service.update(id, item);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? ResponseEntity.noContent().build()
                                  : ResponseEntity.notFound().build();
    }
}
