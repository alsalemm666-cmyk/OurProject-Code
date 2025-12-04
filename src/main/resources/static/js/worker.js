// ======================= worker.js =======================
// Worker Page logic
// - Protects page: only logged-in WORKER can stay
// - Loads worker's orders using /orders/user/{userId}
// - Filters and displays orders, allows status update (example)

// ---------- Auth Guard: only WORKER can access this page ----------
/**
 * Get current logged in user from localStorage.
 * Redirect to /login if not found.
 * Redirect ADMIN to /admin (so he doesn't stay here).
 */
function getCurrentUserOrRedirectForWorker() {
    const raw = localStorage.getItem("user");
    if (!raw) {
        // No user → go to login
        window.location.href = "/login";
        return null;
    }

    try {
        const user = JSON.parse(raw);

        if (!user.role) {
            window.location.href = "/login";
            return null;
        }

        if (user.role === "ADMIN") {
            // Admin shouldn't be here
            window.location.href = "/admin";
            return null;
        }

        if (user.role !== "WORKER") {
            // Any other role → send to home
            window.location.href = "/home";
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

// ---------- Global state ----------
let currentUser = null;       // { id, name, email, role }
let allOrders = [];           // Orders fetched from backend
let filteredOrders = [];      // Orders after filter/search
let selectedOrder = null;     // Order currently shown in details

// ---------- DOM references ----------
let searchInput;
let statusFilter;
let ordersList;
let orderDetails;
let workerAlert;
let statusSelect;
let updateStatusBtn;

// Map status → badge class (CSS)
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
            return "status-badge status-default";
    }
}

/**
 * Show a small alert message on worker page
 * @param {string} message 
 * @param {"success"|"danger"|"info"} type 
 */
function showWorkerAlert(message, type = "info") {
    if (!workerAlert) return;
    workerAlert.className = "";
    workerAlert.classList.add("worker-alert", "alert", `alert-${type}`);
    workerAlert.textContent = message;
    workerAlert.classList.remove("d-none");

    setTimeout(() => {
        workerAlert.classList.add("d-none");
    }, 3000);
}

/**
 * Render orders list on left side
 */
function renderOrdersList() {
    if (!ordersList) return;
    ordersList.innerHTML = "";

    if (!filteredOrders || filteredOrders.length === 0) {
        const emptyDiv = document.createElement("div");
        emptyDiv.className = "empty-state";
        emptyDiv.textContent = "لا توجد طلبات لعرضها.";
        ordersList.appendChild(emptyDiv);
        return;
    }

    filteredOrders.forEach((order) => {
        const card = document.createElement("div");
        card.className = "order-card";
        card.dataset.id = order.id;

        const statusClass = getStatusClass(order.status);

        const firstItem = (order.orderItems && order.orderItems.length > 0)
            ? (order.orderItems[0].service ? order.orderItems[0].service.name : "خدمة غير معروفة")
            : "بدون عناصر";

        card.innerHTML = `
            <div class="order-card-header">
                <span class="order-id">طلب #${order.id}</span>
                <span class="${statusClass}">${order.status || "غير معروف"}</span>
            </div>
            <div class="order-card-body">
                <div class="order-line"><strong>العميل:</strong> ${order.user?.name || "غير متوفر"}</div>
                <div class="order-line"><strong>الخدمة:</strong> ${firstItem}</div>
                <div class="order-line"><strong>التاريخ:</strong> ${order.createdAt || "—"}</div>
            </div>
        `;

        card.addEventListener("click", () => {
            selectOrder(order.id);
        });

        ordersList.appendChild(card);
    });
}

/**
 * Select order by ID and show its details on right side
 * @param {number} orderId 
 */
function selectOrder(orderId) {
    selectedOrder = allOrders.find((o) => o.id === orderId);
    if (!selectedOrder) return;
    renderOrderDetails();
}

/**
 * Render selected order details
 */
function renderOrderDetails() {
    if (!orderDetails || !selectedOrder) {
        if (orderDetails) {
            orderDetails.innerHTML = "<p class='text-muted'>اختر طلب من القائمة لعرض التفاصيل.</p>";
        }
        return;
    }

    const order = selectedOrder;
    const firstItem = (order.orderItems && order.orderItems.length > 0)
        ? (order.orderItems[0].service ? order.orderItems[0].service.name : "خدمة غير معروفة")
        : "بدون عناصر";

    const statusClass = getStatusClass(order.status);

    orderDetails.innerHTML = `
        <h3>تفاصيل الطلب #${order.id}</h3>
        <div class="detail-line"><strong>العميل:</strong> ${order.user?.name || "غير متوفر"}</div>
        <div class="detail-line"><strong>الجوال:</strong> ${order.user?.phone || "غير متوفر"}</div>
        <div class="detail-line"><strong>الخدمة الرئيسية:</strong> ${firstItem}</div>
        <div class="detail-line"><strong>المبلغ الكلي:</strong> ${order.totalAmount ?? "غير محدد"} ريال</div>
        <div class="detail-line"><strong>الحالة الحالية:</strong> 
            <span class="${statusClass}">${order.status || "غير معروف"}</span>
        </div>

        <div class="detail-line"><strong>تحديث الحالة:</strong></div>
        <div class="status-update-group">
            <select id="statusSelect" class="form-select">
                <option value="NEW">جديد</option>
                <option value="PROCESSING">قيد المعالجة</option>
                <option value="READY">جاهز للاستلام</option>
                <option value="DELIVERED">تم التسليم</option>
            </select>
            <button id="updateStatusBtn" class="btn btn-primary">
                تحديث الحالة
            </button>
        </div>
    `;

    // Attach references
    statusSelect = document.getElementById("statusSelect");
    updateStatusBtn = document.getElementById("updateStatusBtn");

    if (statusSelect && selectedOrder.status) {
        statusSelect.value = selectedOrder.status.toUpperCase();
    }

    if (updateStatusBtn) {
        updateStatusBtn.addEventListener("click", onUpdateStatusClick);
    }
}

/**
 * Handle click on "Update Status" button
 * Sends PUT /orders/{id}/status
 */
async function onUpdateStatusClick() {
    if (!selectedOrder || !statusSelect) return;
    const newStatus = statusSelect.value;

    if (!newStatus) {
        showWorkerAlert("الرجاء اختيار حالة جديدة.", "danger");
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/orders/${selectedOrder.id}/status`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ status: newStatus })
        });

        if (!response.ok) {
            showWorkerAlert("فشل تحديث حالة الطلب.", "danger");
            return;
        }

        // Update in local array
        selectedOrder.status = newStatus;
        const idx = allOrders.findIndex((o) => o.id === selectedOrder.id);
        if (idx !== -1) {
            allOrders[idx].status = newStatus;
        }

        showWorkerAlert("تم تحديث حالة الطلب بنجاح.", "success");
        renderOrderDetails();
        applyFilterAndSearch(); // re-render list with updated badge

    } catch (err) {
        console.error("Status update error:", err);
        showWorkerAlert("حدث خطأ أثناء الاتصال بالخادم.", "danger");
    }
}

/**
 * Apply filters based on search text + status select
 */
function applyFilterAndSearch() {
    const searchText = (searchInput?.value || "").toLowerCase().trim();
    const statusValue = statusFilter?.value || "ALL";

    filteredOrders = allOrders.filter((order) => {
        const matchesStatus =
            statusValue === "ALL" ||
            (order.status && order.status.toUpperCase() === statusValue.toUpperCase());

        const text = [
            order.id,
            order.user?.name,
            order.user?.email
        ].join(" ").toLowerCase();

        const matchesText = !searchText || text.includes(searchText);

        return matchesStatus && matchesText;
    });

    renderOrdersList();
}

/**
 * Load orders for current worker from backend
 * Uses endpoint: GET /orders/user/{userId}
 */
async function loadWorkerOrders() {
    if (!currentUser || !currentUser.id) {
        showWorkerAlert("المستخدم غير معروف.", "danger");
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/orders/worker`);
        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                showWorkerAlert("غير مصرح لك بعرض هذه الطلبات.", "danger");
                window.location.href = "/login";
                return;
            }
            showWorkerAlert("فشل جلب الطلبات من الخادم.", "danger");
            return;
        }

        const data = await response.json();
        allOrders = Array.isArray(data) ? data : [];
        filteredOrders = allOrders.slice();

        if (allOrders.length === 0) {
            showWorkerAlert("لا توجد طلبات مرتبطة بك حاليًا.", "info");
        }

        renderOrdersList();
        renderOrderDetails();
    } catch (err) {
        console.error("Load worker orders error:", err);
        showWorkerAlert("حدث خطأ أثناء الاتصال بالخادم.", "danger");
    }
}

/**
 * Initialize worker page
 */
function initWorkerPage() {
    currentUser = getCurrentUserOrRedirectForWorker();
    if (!currentUser) return; // Redirected already

    // Bind DOM
    searchInput = document.getElementById("searchInput");
    statusFilter = document.getElementById("statusFilter");
    ordersList = document.getElementById("ordersList");
    orderDetails = document.getElementById("orderDetails");
    workerAlert = document.getElementById("workerAlert");

    if (searchInput) {
        searchInput.addEventListener("input", applyFilterAndSearch);
    }

    if (statusFilter) {
        statusFilter.addEventListener("change", applyFilterAndSearch);
    }

    loadWorkerOrders();
}

// Run on DOM ready
document.addEventListener("DOMContentLoaded", initWorkerPage);
        logoutBtn.onclick = function () {
            localStorage.removeItem("user");
            window.location.href = "/login";
        };
const adminLink = document.getElementById("adminLink");
if (user && user.role === "ADMIN") {
    adminLink.style.display = "inline-block";
}