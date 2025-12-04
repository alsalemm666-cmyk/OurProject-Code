package laundry.com.online_laundry_service.Services;

import org.springframework.stereotype.Service;
import laundry.com.online_laundry_service.Entities.Order;
import laundry.com.online_laundry_service.Entities.OrderItem;
import laundry.com.online_laundry_service.Repositories.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    // 🧩 Constructor Injection
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 🔹 إرجاع كل الطلبات
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 🔹 إرجاع طلب محدد بالـ ID
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // 🔹 إضافة طلب جديد
    public Order createOrder(Order order) {

        // ربط كل OrderItem بالـ Order
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                item.setOrder(order);
            }

            // حساب إجمالي السعر
            double total = order.getOrderItems()
                    .stream()
                    .mapToDouble(OrderItem::getPrice)
                    .sum();
            order.setTotalAmount(total);
        } else {
            order.setTotalAmount(0.0);
        }

        return orderRepository.save(order);
    }

    // 🔹 تحديث طلب موجود
    public Order updateOrder(Long id, Order updatedOrder) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(updatedOrder.getStatus());
                    order.setPickupTime(updatedOrder.getPickupTime());
                    order.setDeliveryTime(updatedOrder.getDeliveryTime());
                    order.setUser(updatedOrder.getUser());

                    // تحديث العناصر
                    if (updatedOrder.getOrderItems() != null) {
                        // نفصل القديمة ونربط الجديدة
                        order.getOrderItems().clear();
                        for (OrderItem item : updatedOrder.getOrderItems()) {
                            item.setOrder(order);
                            order.getOrderItems().add(item);
                        }

                        double total = order.getOrderItems()
                                .stream()
                                .mapToDouble(OrderItem::getPrice)
                                .sum();
                        order.setTotalAmount(total);
                    }

                    // الدفع (لو حاب تحدثه)
                    order.setPayment(updatedOrder.getPayment());

                    return orderRepository.save(order);
                })
                .orElse(null);
    }

    // 🔹 حذف طلب
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    // (اختياري) لجلب طلبات مستخدم معيّن إذا احتجتها للـ Profile أو My Orders
public List<Order> getOrdersByUserId(Long userId) {
    return orderRepository.findByUserId(userId);
}

}
