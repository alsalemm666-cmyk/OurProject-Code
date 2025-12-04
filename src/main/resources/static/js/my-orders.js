// ======================= my-orders.js =======================
// "My Orders" page logic
// - Only logged-in users can access
// - Loads orders from GET /orders/user/{userId}
// - Renders table with colored status badges

document.addEventListener("DOMContentLoaded", () => {
    const ordersTableBody = document.getElementById("ordersTableBody");
    const ordersAlert = document.getElementById("ordersAlert");
    const emptyState = document.getElementById("emptyState");

    /**
     * Get current user from localStorage or redirect to /login
     */
    function getCurrentUserOrRedirect() {
        const raw = localStorage.getItem("user");
        if (!raw) {
            window.location.href = "/login";
            return null;
        }

        try {
            const user = JSON.parse(raw);
            if (!user.id) {
                window.location.href = "/login";
                return null;
            }
            return user;
        } catch (e) {
            console.error("Invalid user JSON in localStorage", e);
            localStorage.removeItem("user");
            window.location.href = "/login";
            return null;
        }
    }

    /**
     * Show alert message
     * @param {string} message 
     * @param {"success"|"danger"|"info"} type 
     */
    function showAlert(message, type = "info") {
        if (!ordersAlert) return;
        ordersAlert.classList.remove("d-none");
        ordersAlert.classList.remove("alert-success", "alert-danger", "alert-info");
        ordersAlert.classList.add("alert", `alert-${type}`);
        ordersAlert.textContent = message;
    }

    function hideAlert() {
        if (!ordersAlert) return;
        ordersAlert.classList.add("d-none");
    }

    /**
     * Map status to CSS class
     */
    function getStatusClass(status) {
        switch ((status || "").toUpperCase()) {
            case "NEW":
                return "status-badge status-new";
            case "PROCESSING":
                return "status-badge status-processing";
            case "READY":
                return "status-badge status-ready";
            case "DELIVERED":
                return "status-badge status-delivered";
            default:
                return "status-badge";
        }
    }

    /**
     * Render orders into table body
     * @param {Array} orders 
     */
    function renderOrders(orders) {
        if (!ordersTableBody) return;

        ordersTableBody.innerHTML = "";

        if (!orders || orders.length === 0) {
            if (emptyState) emptyState.classList.remove("d-none");
            return;
        }

        if (emptyState) emptyState.classList.add("d-none");

        orders.forEach((order) => {
            const tr = document.createElement("tr");

            const serviceName = (order.orderItems && order.orderItems.length > 0)
                ? (order.orderItems[0].service ? order.orderItems[0].service.name : "خدمة غير معروفة")
                : "بدون عناصر";

            const createdAt = order.createdAt || "—";

            const statusClass = getStatusClass(order.status);

            tr.innerHTML = `
                <td>#${order.id}</td>
                <td>${createdAt}</td>
                <td>${serviceName}</td>
                <td>
                    <span class="${statusClass}">
                        ${order.status || "غير معروف"}
                    </span>
                </td>
            `;

            ordersTableBody.appendChild(tr);
        });
    }

    /**
     * Load orders from backend: GET /orders/user/{userId}
     */
    async function loadMyOrders(userId) {
        try {
            hideAlert();

           const response = await fetch(`/api/orders/user/${userId}`);
            if (!response.ok) {
                if (response.status === 401 || response.status === 403) {
                    showAlert("يجب تسجيل الدخول لعرض الطلبات.", "danger");
                    window.location.href = "/login";
                    return;
                }

                showAlert("فشل جلب الطلبات من الخادم.", "danger");
                return;
            }

            const data = await response.json();
            const orders = Array.isArray(data) ? data : [];

            if (orders.length === 0) {
                showAlert("لا توجد طلبات مسجلة لهذا المستخدم.", "info");
            }

            renderOrders(orders);

        } catch (error) {
            console.error("Error loading my orders:", error);
            showAlert("حدث خطأ أثناء الاتصال بالخادم.", "danger");
        }
    }

    // -------- Init --------
    const user = getCurrentUserOrRedirect();
    if (!user) return;

    loadMyOrders(user.id);

// ✅ إعادة التحديث تلقائيًا كل 10 ثواني
setInterval(() => {
    loadMyOrders(user.id);
}, 10000);

});
