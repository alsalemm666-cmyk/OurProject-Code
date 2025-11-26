package laundry.com.online_laundry_service.Controllers;

import lombok.RequiredArgsConstructor;
import laundry.com.online_laundry_service.DTO.CartItemDTO;
import laundry.com.online_laundry_service.DTO.OrderCreateRequest;
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

@PostMapping("/create")
public ResponseEntity<?> createOrder(@RequestBody OrderCreateRequest req) {

    // جلب المستخدم
    User user = userService.getUserById(req.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));

    Order order = new Order();
    order.setUser(user);
    order.setStatus("Pending");
    order.setPickupTime(LocalDateTime.now());
    order.setDeliveryTime(LocalDateTime.now().plusDays(1));

    List<OrderItem> items = new ArrayList<>();

    // معالجة سلة المشتريات
    for (CartItemDTO item : req.getItems()) {

        LaundryService service =
                serviceService.getServiceById(item.getId())
                        .orElseThrow(() -> new RuntimeException("Service not found: " + item.getId()));

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

}
