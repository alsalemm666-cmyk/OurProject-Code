package laundry.com.online_laundry_service.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;
    private double price; // السعر النهائي = service.price * quantity

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private LaundryService service;

    public OrderItem() {}

    public OrderItem(int quantity, double price, LaundryService service, Order order) {
        this.quantity = quantity;
        this.price = price;
        this.service = service;
        this.order = order;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public LaundryService getService() { return service; }
    public void setService(LaundryService service) { this.service = service; }
}
