package laundry.com.online_laundry_service.DTO;

public class CartItemDTO {
    private Long id;      // serviceId
    private int quantity; // عدد القطع

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
