package laundry.com.online_laundry_service.DTO;

public class PaymentRequestDTO {

    private Long orderId;
    private double amount;
    private String method;   // cash - card - online

    // Getters & Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
}
