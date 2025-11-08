package laundry.com.online_laundry_service.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemType;
    private double price;
    private int quantity;

    // العلاقة الصحيحة مع Order
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // 🔹 Constructors
    public OrderItem() {
    }

    public OrderItem(String itemType, double price, int quantity, Order order) {
        this.itemType = itemType;
        this.price = price;
        this.quantity = quantity;
        this.order = order;
    }

    // 🔹 Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
