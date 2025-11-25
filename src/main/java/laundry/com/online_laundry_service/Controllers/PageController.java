package laundry.com.online_laundry_service.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Collections;

@Controller
public class PageController {

    // الصفحة الرئيسية
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("pageTitle", "الصفحة الرئيسية");
        // عشان الـ home.html ما يطيح في مشكلة لو استخدم ${user} و ${orders}
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
