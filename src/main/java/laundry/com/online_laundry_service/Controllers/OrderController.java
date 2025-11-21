package laundry.com.online_laundry_service.Controllers;

import lombok.RequiredArgsConstructor;
import laundry.com.online_laundry_service.Entities.LaundryService;
import laundry.com.online_laundry_service.Entities.Order;
import laundry.com.online_laundry_service.Entities.OrderItem;
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
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {

        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {

                // ربط الخدمة الصحيحة من قاعدة البيانات
                if (item.getService() != null && item.getService().getId() != null) {
                    LaundryService service = serviceService.getServiceById(item.getService().getId())
                            .orElseThrow(() -> new RuntimeException("Service not found: " + item.getService().getId()));

                    item.setService(service);

                    // لو الكمية <= 0 نخليها 1
                    if (item.getQuantity() <= 0) {
                        item.setQuantity(1);
                    }

                    // السعر = سعر الخدمة * الكمية
                    item.setPrice(service.getPrice() * item.getQuantity());
                }

                // ربط الـ OrderItem بالـ Order
                item.setOrder(order);
            }
        }

        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.ok(savedOrder);
    }

    // 🔹 تحديث طلب
    @PutMapping("/update/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order updatedOrder) {

        if (updatedOrder.getOrderItems() != null) {
            for (OrderItem item : updatedOrder.getOrderItems()) {
                if (item.getService() != null && item.getService().getId() != null) {
                    LaundryService service = serviceService.getServiceById(item.getService().getId())
                            .orElseThrow(() -> new RuntimeException("Service not found: " + item.getService().getId()));
                    item.setService(service);

                    if (item.getQuantity() <= 0) {
                        item.setQuantity(1);
                    }
                    item.setPrice(service.getPrice() * item.getQuantity());
                }
            }
        }

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
