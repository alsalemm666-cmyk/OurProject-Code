package laundry.com.online_laundry_service.Controllers;

import lombok.RequiredArgsConstructor;
import laundry.com.online_laundry_service.DTO.CartItemDTO;
import laundry.com.online_laundry_service.DTO.OrderCreateRequest;
import laundry.com.online_laundry_service.DTO.UpdateOrderStatusRequest;
import laundry.com.online_laundry_service.Entities.LaundryService;
import laundry.com.online_laundry_service.Entities.Order;
import laundry.com.online_laundry_service.Entities.OrderItem;
import laundry.com.online_laundry_service.Entities.User;
import laundry.com.online_laundry_service.Services.OrderService;
import laundry.com.online_laundry_service.Services.ServiceService;
import laundry.com.online_laundry_service.Services.Userservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final Userservice userService;
    private final ServiceService serviceService;

    // ========================= (1) إنشاء طلب جديد =========================
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderCreateRequest req) {

        User user = userService.getUserById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus("NEW");
        order.setPickupTime(LocalDateTime.now());
        order.setDeliveryTime(LocalDateTime.now().plusDays(1));

        List<OrderItem> items = new ArrayList<>();

        for (CartItemDTO item : req.getItems()) {
            LaundryService service =
                    serviceService.getServiceById(item.getId())
                            .orElseThrow(() -> new RuntimeException("Service not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setService(service);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(service.getPrice() * item.getQuantity());
            orderItem.setOrder(order);

            items.add(orderItem);
        }

        order.setOrderItems(items);

        Order saved = orderService.createOrder(order);
        return ResponseEntity.ok(saved);
    }

    // ========================= (2) جلب كل الطلبات لواجهة العامل =========================
    @GetMapping("/worker")
    public ResponseEntity<List<Order>> getAllOrdersForWorker() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // ========================= (3) جلب تفاصيل طلب واحد =========================
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderDetails(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ========================= (4) تحديث حالة الطلب من واجهة العامل =========================
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request) {

        Order updated = orderService.updateOrder(id, buildStatusOnlyOrder(request.getStatus()));

        if (updated == null) {
            return ResponseEntity.badRequest().body("Order not found");
        }

        return ResponseEntity.ok(updated);
    }

    // ========================= مساعد داخلي لتحديث الحالة فقط =========================
    private Order buildStatusOnlyOrder(String status) {
        Order o = new Order();
        o.setStatus(status);
        return o;
    }
    @GetMapping("/user/{userId}")
public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
    return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
}

}
