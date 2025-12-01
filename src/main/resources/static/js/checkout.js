// ===================== LOGIN / HEADER =====================
const user = JSON.parse(localStorage.getItem("user"));
const loginBtn = document.getElementById("loginBtn");
const profileAvatar = document.getElementById("profileAvatar");

if (user) {
    if (loginBtn) loginBtn.style.display = "none";
    if (profileAvatar) {
        profileAvatar.style.display = "block";
        if (user.profileImage) {
            profileAvatar.src = user.profileImage;
        }
    }
}

// ===================== CART DATA =====================
let cart = JSON.parse(localStorage.getItem("cart")) || [];

const cartContainer = document.getElementById("cartItemsContainer");
const cartTotalSpan = document.getElementById("cartTotal");

function renderCart() {
    if (!cartContainer) return;

    cartContainer.innerHTML = "";

    if (cart.length === 0) {
        cartContainer.innerHTML = `
            <div class="text-center text-white-50">
                <i class="fa fa-inbox fa-2x mb-2"></i>
                <p>السلة فارغة حالياً.</p>
            </div>
        `;
        cartTotalSpan.textContent = "0 ريال";
        return;
    }

    let total = 0;

    cart.forEach(item => {
        total += item.price * item.quantity;

        cartContainer.innerHTML += `
            <div class="cart-item-row">
                <div class="cart-item-main">
                    <span class="cart-item-name">${item.name}</span>
                    <span class="cart-item-qty">الكمية: ${item.quantity}</span>
                </div>
                <div class="cart-item-price">
                    ${(item.price * item.quantity).toFixed(2)} ريال
                </div>
            </div>
        `;
    });

    cartTotalSpan.textContent = total.toFixed(2) + " ريال";
}

document.addEventListener("DOMContentLoaded", renderCart);

// ===================== PAYMENT =====================
async function processPayment(orderId, totalPrice) {

    const methodInput = document.querySelector("input[name='payMethod']:checked");
    const method = methodInput ? methodInput.value : "CASH";

    const paymentData = {
        orderId: orderId,
        amount: totalPrice,
        method: method
    };

    // ✅✅ المسار الصحيح + إزالة (...) الخاطئة
    const response = await fetch("/api/payments/pay", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(paymentData)
    });

    if (!response.ok) {
        const err = await response.text();
        console.error("Payment Error:", err);
        throw new Error("فشل الدفع");
    }
}

// ===================== CONFIRM ORDER =====================
async function confirmOrder() {

    const fullName = document.getElementById("fullName").value.trim();
    const city = document.getElementById("city").value.trim();
    const address = document.getElementById("address").value.trim();

    if (!fullName || !city || !address) {
        alert("⚠ يرجى تعبئة جميع بيانات العميل.");
        return;
    }

    if (!user) {
        alert("⚠ يجب تسجيل الدخول أولاً.");
        window.location.href = "/login";
        return;
    }

    if (cart.length === 0) {
        alert("⚠ السلة فارغة.");
        return;
    }

    // ✅ حساب الإجمالي بشكل صحيح
    let total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

    // ✅ شكل البيانات المتوافق مع OrderCreateRequest
    const orderRequestBody = {
        userId: user.id,
        items: cart.map(item => ({
            id: item.id,          // serviceId
            quantity: item.quantity
        }))
    };

    try {
        // 1️⃣ إنشاء الطلب
        const orderResponse = await fetch("/api/orders/create", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(orderRequestBody)
        });

        if (!orderResponse.ok) {
            const errorText = await orderResponse.text();
            console.error("Order Error:", errorText);
            alert("❌ حدث خطأ أثناء إنشاء الطلب");
            return;
        }

        const createdOrder = await orderResponse.json();

        // 2️⃣ تنفيذ الدفع
        await processPayment(createdOrder.id, total);

        // 3️⃣ مسح السلة
        localStorage.removeItem("cart");

        // 4️⃣ الانتقال لصفحة النجاح
        window.location.href = "/success";

    } catch (err) {
        console.error(err);
        alert("❌ حدث خطأ أثناء معالجة الطلب أو الدفع");
    }
}
