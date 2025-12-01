package laundry.com.online_laundry_service.Services;

import laundry.com.online_laundry_service.Entities.Order;
import laundry.com.online_laundry_service.Entities.Payment;
import laundry.com.online_laundry_service.Repositories.OrderRepository;
import laundry.com.online_laundry_service.Repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Payment createPayment(Payment payment) {

        if (payment.getOrder() == null || payment.getOrder().getId() == null) {
            throw new RuntimeException("Order ID is required");
        }

        // ربط الطلب
        Order order = orderRepository.findById(payment.getOrder().getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        payment.setOrder(order);
        payment.setPaymentDate(LocalDate.now());

        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }
}
