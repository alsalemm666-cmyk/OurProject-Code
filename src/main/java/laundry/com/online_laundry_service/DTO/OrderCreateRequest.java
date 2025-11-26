package laundry.com.online_laundry_service.DTO;

import java.util.List;

public class OrderCreateRequest {

    private Long userId;
    private List<CartItemDTO> items;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<CartItemDTO> getItems() { return items; }
    public void setItems(List<CartItemDTO> items) { this.items = items; }
}
