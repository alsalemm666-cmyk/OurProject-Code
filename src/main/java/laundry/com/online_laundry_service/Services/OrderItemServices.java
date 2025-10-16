package laundry.com.online_laundry_service.Services;

import org.springframework.stereotype.Service;
import java.util.*;
import laundry.com.online_laundry_service.Entities.OrderItem;
import laundry.com.online_laundry_service.Repositories.OrderItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository repo;

    public List<OrderItem> getAll() { return repo.findAll(); }
    public Optional<OrderItem> getById(Long id) { return repo.findById(id); }
    public OrderItem save(OrderItem item) { return repo.save(item); }

    public OrderItem update(Long id, OrderItem updated) {
        return repo.findById(id).map(item -> {
            item.setItemType(updated.getItemType());
            item.setPrice(updated.getPrice());
            item.setQuantity(updated.getQuantity());
            return repo.save(item);
        }).orElse(null);
    }

    public boolean delete(Long id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }
}
