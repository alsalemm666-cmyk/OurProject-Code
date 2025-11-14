package laundry.com.online_laundry_service.Controllers;

import lombok.RequiredArgsConstructor;
import laundry.com.online_laundry_service.Entities.LaundryService;
import laundry.com.online_laundry_service.Entities.Order;
import laundry.com.online_laundry_service.Services.OrderService;
import laundry.com.online_laundry_service.Services.ServiceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ServiceService serviceService;

    // 🔹 إرجاع كل الطلبات
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // 🔹 جلب طلب محدد
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 إنشاء طلب جديد
@PostMapping("/create")
public ResponseEntity<?> createOrder(@RequestBody Order order) {

    List<Long> serviceIds = order.getServices()
                                 .stream()
                                 .map(LaundryService::getId)
                                 .toList();

    List<LaundryService> services = serviceIds.stream()
            .map(id -> serviceService.getServiceById(id)
            .orElseThrow(() -> new RuntimeException("Service not found: " + id)))
            .toList();

    order.setServices(services);

    Order savedOrder = orderService.createOrder(order);
    return ResponseEntity.ok(savedOrder);
}


    // 🔹 تحديث طلب
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody Order updatedOrder) {
        Order order = orderService.updateOrder(id, updatedOrder);
        if (order != null) return ResponseEntity.ok(order);
        return ResponseEntity.notFound().build();
    }

    // 🔹 حذف طلب
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully");
    }
}
