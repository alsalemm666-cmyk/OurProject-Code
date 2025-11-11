package laundry.com.online_laundry_service.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // الصفحة الرئيسية
    @GetMapping("/home")
    public String home(Model model) {
        // ممكن تمرر بيانات لاحقاً مثل اسم المستخدم أو الطلبات
        model.addAttribute("pageTitle", "الصفحة الرئيسية");
        return "home"; // يفتح home.html من templates
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

    // صفحة الخدمات
    @GetMapping("/services")
    public String servicesPage(Model model) {
        model.addAttribute("pageTitle", "الخدمات");
        return "services";
    }

    // صفحة إنشاء الطلب
    @GetMapping("/orders/create")
    public String orderCreatePage(Model model) {
        model.addAttribute("pageTitle", "إنشاء طلب");
        return "order_create";
    }
}