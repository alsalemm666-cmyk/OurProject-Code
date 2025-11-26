package laundry.com.online_laundry_service.Controllers;

import laundry.com.online_laundry_service.Services.ServiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;

@Controller
public class PageController {

    private final ServiceService serviceService;

    public PageController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // الصفحة الرئيسية
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("pageTitle", "الصفحة الرئيسية");
        model.addAttribute("user", null);
        model.addAttribute("orders", Collections.emptyList());
        return "home";
    }

    // صفحة تسجيل الدخول
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "تسجيل الدخول");
        return "login";
    }

    // صفحة التسجيل
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("pageTitle", "إنشاء حساب");
        return "register";
    }

    // صفحة الخدمات (التعديل هنا 🔥)
    @GetMapping("/services")
    public String servicesPage(Model model) {
        model.addAttribute("pageTitle", "الخدمات");

        // أهم سطر ▼▼
        model.addAttribute("services", serviceService.getAllServices());

        return "services";
    }

    // صفحة إنشاء الطلب
    @GetMapping("/orders/create")
    public String orderCreatePage(Model model) {
        model.addAttribute("pageTitle", "إنشاء طلب");

        // إضافة الخدمات لاختيارها داخل إنشاء الطلب
        model.addAttribute("services", serviceService.getAllServices());

        return "order_create";
    }

    @GetMapping("/profile")
    public String profilePage(Model model) {
        model.addAttribute("pageTitle", "الملف الشخصي");
        return "profile";
    }
    @GetMapping("/cart")
public String cartPage() {
    return "cart";
}

    // ⬇⬇⬇ أضِف هذا الميثود الجديد للـ checkout ⬇⬇⬇
    @GetMapping("/checkout")
    public String checkoutPage(Model model) {
        model.addAttribute("pageTitle", "إتمام الدفع");
        return "checkout";   // يفتح الملف checkout.html من مجلد templates
    }

    // (اختياري ولكن أنصح فيه) صفحة نجاح الطلب
    @GetMapping("/success")
    public String successPage(Model model) {
        model.addAttribute("pageTitle", "تم تأكيد الطلب");
        return "success";    // success.html في templates
    }
}
