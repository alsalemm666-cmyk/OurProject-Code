package laundry.com.online_laundry_service.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import laundry.com.online_laundry_service.DTO.PaymentRequestDTO;
import laundry.com.online_laundry_service.Entities.Order;
import laundry.com.online_laundry_service.Entities.Payment;
import laundry.com.online_laundry_service.Services.OrderService;
import laundry.com.online_laundry_service.Services.PaymentService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    @PostMapping("/pay")   // ✅ هذا هو السطر الذي يحل مشكلتك
    public ResponseEntity<?> pay(@RequestBody PaymentRequestDTO dto) {

        // جلب الطلب
        Order order = orderService.getOrderById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // إنشاء عملية الدفع
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(dto.getAmount());
        payment.setMethod(dto.getMethod());
        payment.setStatus("Paid");
        payment.setPaymentDate(LocalDate.now());

        Payment saved = paymentService.save(payment);

        return ResponseEntity.ok(saved);
    }
}
