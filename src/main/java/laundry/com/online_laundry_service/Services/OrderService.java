package laundry.com.online_laundry_service.Services;

import org.springframework.stereotype.Service;

import laundry.com.online_laundry_service.Entities.Order;
import laundry.com.online_laundry_service.Repositories.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    // Constructor Injection (أفضل ممارسة)
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 🔹 إرجاع كل الطلبات
    public List<jakarta.persistence.criteria.Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 🔹 إرجاع طلب محدد بالـ ID
    public Optional<jakarta.persistence.criteria.Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // 🔹 إضافة طلب جديد
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    // 🔹 تحديث طلب موجود
    public jakarta.persistence.criteria.Order updateOrder(Long id, Order updatedOrder) {
        return orderRepository.findById(id)
                .map(order -> {
                    ((Order) order).setStatus(updatedOrder.getStatus());
                    ((Order) order).setPickupTime(updatedOrder.getPickupTime());
                    ((Order) order).setDeliveryTime(updatedOrder.getDeliveryTime());
                    ((Order) order).setUser(updatedOrder.getUser());
                    ((Order) order).setServices(updatedOrder.getServices());
                    ((Order) order).setOrderItems(updatedOrder.getOrderItems());
                    ((Order) order).setPayment(updatedOrder.getPayment());
                    return orderRepository.save(order);
                })
                .orElse(null);
    }

    // 🔹 حذف طلب
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
