package laundry.com.online_laundry_service.Services;

import org.springframework.stereotype.Service;
import java.util.*;
import laundry.com.online_laundry_service.Entities.OrderItem;
import laundry.com.online_laundry_service.Repositories.OrderItemrepository; // إذا غيرت الاسم لـ OrderItemRepository عدّله هنا
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemServices {

    private final OrderItemrepository repo;

    public List<OrderItem> getAll() {
        return repo.findAll();
    }

    public Optional<OrderItem> getById(Long id) {
        return repo.findById(id);
    }

    public OrderItem save(OrderItem item) {
        // هنا ممكن لاحقاً تحسب السعر بناءً على الخدمة والكمية
        return repo.save(item);
    }

    public OrderItem update(Long id, OrderItem updated) {
        return repo.findById(id).map(item -> {
            item.setQuantity(updated.getQuantity());
            item.setPrice(updated.getPrice());
            item.setService(updated.getService()); // 🔥 جديد بدل itemType
            return repo.save(item);
        }).orElse(null);
    }

    public boolean delete(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }
}
