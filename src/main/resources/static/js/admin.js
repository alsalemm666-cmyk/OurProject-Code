// ======================= admin.js =======================
// Admin Panel logic for Laundry Management System
// - Handles Workers Management (front-end level)
// - Handles Services Management (connected to Spring Boot backend)
// - Switches between sections without reloading the page

// ----------- API base URLs (Spring Boot backend) -----------
const API_USERS_ALL = "/users/all";          // GET existing users (DTO) -> to show workers if role=WORKER
const API_USERS_UPDATE = "/users/update";    // PUT /users/update/{id}  (optional, best-effort)
const API_USERS_DELETE = "/users/delete";    // DELETE /users/delete/{id}
const API_ADMIN_CREATE_WORKER = "/api/admin/create-worker"; // POST create worker (User entity)

const API_SERVICES = "/api/services";        // GET, POST, PUT, DELETE LaundryService

// ----------- DOM elements references -----------
const btnWorkers = document.getElementById("btnWorkers");
const btnServices = document.getElementById("btnServices");

const workersSection = document.getElementById("workersSection");
const servicesSection = document.getElementById("servicesSection");

const globalAlert = document.getElementById("globalAlert");

// Top profile & auth controls (header)
const topProfileAvatar = document.getElementById("topProfileAvatar");
const topLoginBtn = document.getElementById("topLoginBtn");
const topLogoutBtn = document.getElementById("topLogoutBtn");

// Workers DOM
const workerForm = document.getElementById("workerForm");
const workerIdInput = document.getElementById("workerId");
const workerNameInput = document.getElementById("workerName");
const workerPhoneInput = document.getElementById("workerPhone");
const workerEmailInput = document.getElementById("workerEmail");    
const workerPasswordInput = document.getElementById("workerPassword");
const workerRoleInput = document.getElementById("workerRole");
const workerStatusInput = document.getElementById("workerStatus");
const workerFormBtnText = document.getElementById("workerFormBtnText");
const workersTableBody = document.getElementById("workersTableBody");

// Services DOM
const serviceForm = document.getElementById("serviceForm");
const serviceIdInput = document.getElementById("serviceId");
const serviceNameInput = document.getElementById("serviceName");
const serviceDescriptionInput = document.getElementById("serviceDescription");
const servicePriceInput = document.getElementById("servicePrice");
const serviceDurationInput = document.getElementById("serviceDuration");
const serviceFormBtnText = document.getElementById("serviceFormBtnText");
const servicesTableBody = document.getElementById("servicesTableBody");


// ----------- Helper: show small alert message on top -----------
/**
 * Show global alert message inside the admin content.
 * @param {string} message - Text to display
 * @param {"success"|"danger"|"info"} type - Bootstrap alert type
 */
function showGlobalAlert(message, type = "info") {
    globalAlert.className = "alert alert-" + type;
    globalAlert.textContent = message;
    globalAlert.classList.remove("d-none");

    // Auto hide after some seconds
    setTimeout(() => {
        globalAlert.classList.add("d-none");
    }, 3500);
}

// ----------- Auth / profile (using localStorage like other pages) -----------
/**
 * Initialize header profile & auth buttons based on localStorage "user".
 */
function initHeaderAuth() {
    const userJson = localStorage.getItem("user");
    if (!userJson) {
        // Not logged in
        topLoginBtn.classList.remove("d-none");
        topLogoutBtn.classList.add("d-none");
        topProfileAvatar.classList.add("d-none");
        return;
    }

    try {
        const user = JSON.parse(userJson);

        // Hide login, show logout + avatar
        topLoginBtn.classList.add("d-none");
        topLogoutBtn.classList.remove("d-none");
        topProfileAvatar.classList.remove("d-none");

        // If backend returned profileImage, use it
        if (user.profileImage) {
            topProfileAvatar.src = user.profileImage;
        }

        // Optional: if you want to restrict admin page to ROLE_ADMIN only
        // if (user.role && user.role !== "ADMIN") {
        //     showGlobalAlert("You are not authorized to access this page.", "danger");
        //     setTimeout(() => window.location.href = "/home", 1500);
        // }

        // Navigate to profile on avatar click
        topProfileAvatar.onclick = () => {
            window.location.href = "/profile";
        };

        // Logout button clears localStorage and returns to login
        topLogoutBtn.onclick = () => {
            localStorage.removeItem("user");
            window.location.href = "/login";
        };

    } catch (e) {
        console.warn("Invalid user JSON in localStorage");
    }
}

// ----------- Section switching (Workers / Services) -----------
/**
 * Switch displayed section between Workers and Services.
 * Uses CSS classes to show/hide sections and highlight the active sidebar button.
 */
function switchSection(target) {
    if (target === "workers") {
        workersSection.classList.remove("d-none");
        servicesSection.classList.add("d-none");
        btnWorkers.classList.add("active");
        btnServices.classList.remove("active");
    } else if (target === "services") {
        workersSection.classList.add("d-none");
        servicesSection.classList.remove("d-none");
        btnWorkers.classList.remove("active");
        btnServices.classList.add("active");
    }
}

// Attach click handlers to sidebar buttons
btnWorkers.addEventListener("click", () => switchSection("workers"));
btnServices.addEventListener("click", () => switchSection("services"));

// ----------- Workers: render table -----------
/**
 * Render all workers into the workers table body.
 */
function renderWorkersTable() {
    workersTableBody.innerHTML = "";

    if (workers.length === 0) {
        const row = document.createElement("tr");
        const cell = document.createElement("td");
        cell.colSpan = 6;
        cell.className = "text-center text-muted";
        cell.textContent = "لا يوجد عمال مسجلين حالياً.";
        row.appendChild(cell);
        workersTableBody.appendChild(row);
        return;
    }

    workers.forEach((worker) => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${worker.id}</td>
            <td>${worker.name}</td>
            <td>${worker.phone}</td>
            <td>${worker.roleLabel}</td>
            <td>${worker.status}</td>
            <td class="text-center">
                <button class="btn btn-sm btn-action btn-edit me-1" data-id="${worker.id}">
                    <i class="fa fa-edit"></i> Edit
                </button>
                <button class="btn btn-sm btn-action btn-delete" data-id="${worker.id}">
                    <i class="fa fa-trash"></i> Delete
                </button>
            </td>
        `;

        workersTableBody.appendChild(tr);
    });

    // Attach action buttons events after rendering
    workersTableBody.querySelectorAll(".btn-edit").forEach((btn) => {
        btn.addEventListener("click", onEditWorkerClick);
    });

    workersTableBody.querySelectorAll(".btn-delete").forEach((btn) => {
        btn.addEventListener("click", onDeleteWorkerClick);
    });
}

/**
 * Handle click on "Edit" button for worker row.
 * Prefills the form with existing worker data and switches form to edit mode.
 */
function onEditWorkerClick(event) {
    const id = Number(event.currentTarget.dataset.id);
    const worker = workers.find((w) => w.id === id);
    if (!worker) return;

    workerIdInput.value = worker.id;
    workerNameInput.value = worker.name;
    workerPhoneInput.value = worker.phone;
    workerEmailInput.value = worker.email || "";
    workerRoleInput.value = worker.roleLabel;
    workerStatusInput.value = worker.status;

    workerFormBtnText.textContent = "تعديل عامل";
}

/**
 * Handle click on "Delete" button for worker row.
 * Shows confirmation and removes worker from local array.
 * Tries to delete from backend if backendId is available.
 */
function onDeleteWorkerClick(event) {
    const id = Number(event.currentTarget.dataset.id);
    const worker = workers.find((w) => w.id === id);
    if (!worker) return;

    const confirmed = confirm(`هل أنت متأكد من حذف العامل: ${worker.name}؟`);
    if (!confirmed) return;

    // Remove from front-end array
    workers = workers.filter((w) => w.id !== id);
    renderWorkersTable();
    showGlobalAlert("Worker deleted locally.", "success");

    // Try to delete from backend if we know backend user id
    if (worker.backendId) {
        fetch(`${API_USERS_DELETE}/${worker.backendId}`, {
            method: "DELETE"
        }).catch(() => {
            console.warn("Failed to delete worker in backend (optional).");
        });
    }
}

// ----------- Workers: form submit (add / edit) -----------
workerForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const name = workerNameInput.value.trim();
    const phone = workerPhoneInput.value.trim();
    const email = workerEmailInput.value.trim();
    const password = workerPasswordInput.value.trim();
    const roleLabel = workerRoleInput.value;
    const status = workerStatusInput.value;
    const existingId = workerIdInput.value ? Number(workerIdInput.value) : null;

    // Simple front-end validation (required fields)
if (!name || !phone || !roleLabel || !status || !email || (!existingId && !password)) {
    showGlobalAlert("جميع الحقول مطلوبة (بما فيها كلمة المرور).", "danger");
    return;
}

    // Edit mode
    if (existingId) {
        const wIndex = workers.findIndex((w) => w.id === existingId);
        if (wIndex === -1) return;

        workers[wIndex].name = name;
        workers[wIndex].phone = phone;
        workers[wIndex].email = email;
        workers[wIndex].roleLabel = roleLabel;
        workers[wIndex].status = status;

        renderWorkersTable();
        showGlobalAlert("Worker updated locally.", "success");

        // Try to update backend user if backendId exists
        const backendId = workers[wIndex].backendId;
        if (backendId) {
            try {
                await fetch(`${API_USERS_UPDATE}/${backendId}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        name,
                        email
                        // Phone/status are frontend-only unless your User entity supports them
                    })
                });
            } catch (err) {
                console.warn("Failed to update worker in backend (optional).");
            }
        }

    } else {
        // Add mode (new worker)
        const newId = workers.length > 0 ? Math.max(...workers.map((w) => w.id)) + 1 : 1;
        const newWorker = {
            id: newId,
            name,
            phone,
            email,
            roleLabel,
            status,
            backendId: null
        };
        workers.push(newWorker);
        renderWorkersTable();
        showGlobalAlert("Worker added locally.", "success");

        // Try to also create worker on backend via Admin API (if configured)
        // NOTE: password is dummy; you can adapt this part to your real backend.
        try {
            const response = await fetch(API_ADMIN_CREATE_WORKER, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    name,
                    email,
                    password: password // default password for new worker (for demo)
                })
            });

            if (response.ok) {
                const savedWorker = await response.json();
                // Save backend id for future sync
                newWorker.backendId = savedWorker.id;
            }
        } catch (err) {
            console.warn("Failed to create worker in backend, but local data was updated.");
        }
    }

    // Reset form to add mode
    workerForm.reset();
    workerPasswordInput.value = "";
    workerIdInput.value = "";
    workerFormBtnText.textContent = "إضافة عامل";
});

// ----------- Services: render table -----------
/**
 * Render all services into the services table body.
 */
function renderServicesTable() {
    servicesTableBody.innerHTML = "";

    if (services.length === 0) {
        const row = document.createElement("tr");
        const cell = document.createElement("td");
        cell.colSpan = 6;
        cell.className = "text-center text-muted";
        cell.textContent = "لا توجد خدمات مسجلة حالياً.";
        row.appendChild(cell);
        servicesTableBody.appendChild(row);
        return;
    }

    services.forEach((service) => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${service.id}</td>
            <td>${service.name}</td>
            <td>${service.description}</td>
            <td>${service.price.toFixed(2)}</td>
            <td>${service.duration}</td>
            <td class="text-center">
                <button class="btn btn-sm btn-action btn-edit me-1" data-id="${service.id}">
                    <i class="fa fa-edit"></i> Edit
                </button>
                <button class="btn btn-sm btn-action btn-delete" data-id="${service.id}">
                    <i class="fa fa-trash"></i> Delete
                </button>
            </td>
        `;

        servicesTableBody.appendChild(tr);
    });

    // Attach events to buttons
    servicesTableBody.querySelectorAll(".btn-edit").forEach((btn) => {
        btn.addEventListener("click", onEditServiceClick);
    });

    servicesTableBody.querySelectorAll(".btn-delete").forEach((btn) => {
        btn.addEventListener("click", onDeleteServiceClick);
    });
}

/**
 * Handle click on "Edit" button for service row.
 * Prefills service form and switches to edit mode.
 */
function onEditServiceClick(event) {
    const id = Number(event.currentTarget.dataset.id);
    const service = services.find((s) => s.id === id);
    if (!service) return;

    serviceIdInput.value = service.id;
    serviceNameInput.value = service.name;
    serviceDescriptionInput.value = service.description;
    servicePriceInput.value = service.price;
    serviceDurationInput.value = service.duration;

    serviceFormBtnText.textContent = "تعديل خدمة";
}

/**
 * Handle click on "Delete" button for service row.
 * Removes service from local array and tries to delete from backend.
 */
function onDeleteServiceClick(event) {
    const id = Number(event.currentTarget.dataset.id);
    const service = services.find((s) => s.id === id);
    if (!service) return;

    const confirmed = confirm(`هل أنت متأكد من حذف الخدمة: ${service.name}؟`);
    if (!confirmed) return;

    // Remove from front-end
    services = services.filter((s) => s.id !== id);
    renderServicesTable();
    showGlobalAlert("Service deleted locally.", "success");

    // Also try to delete from backend if backend id is known
    if (service.backendId) {
        fetch(`${API_SERVICES}/${service.backendId}`, {
            method: "DELETE"
        }).catch(() => {
            console.warn("Failed to delete service from backend.");
        });
    }
}

// ----------- Services: form submit (add / edit) -----------
serviceForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const name = serviceNameInput.value.trim();
    const description = serviceDescriptionInput.value.trim();
    const price = Number(servicePriceInput.value);
    const duration = serviceDurationInput.value.trim();
    const existingId = serviceIdInput.value ? Number(serviceIdInput.value) : null;

    // Simple validation
    if (!name || !description || !duration || isNaN(price) || price < 0) {
        showGlobalAlert("Please fill all required service fields correctly.", "danger");
        return;
    }

    // Edit mode
    if (existingId) {
        const sIndex = services.findIndex((s) => s.id === existingId);
        if (sIndex === -1) return;

        services[sIndex].name = name;
        services[sIndex].description = description;
        services[sIndex].price = price;
        services[sIndex].duration = duration;

        renderServicesTable();
        showGlobalAlert("Service updated locally.", "success");

        // Try update in backend
        const backendId = services[sIndex].backendId;
        if (backendId) {
            try {
                await fetch(`${API_SERVICES}/${backendId}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        name,
                        description,
                        price
                        // Duration is front-end only, not part of LaundryService entity
                    })
                });
            } catch (err) {
                console.warn("Failed to update service in backend.");
            }
        }

    } else {
        // Add mode
        const newId = services.length > 0 ? Math.max(...services.map((s) => s.id)) + 1 : 1;
        const newService = {
            id: newId,
            name,
            description,
            price,
            duration,
            backendId: null
        };
        services.push(newService);
        renderServicesTable();
        showGlobalAlert("Service added locally.", "success");

        // Also try to create in backend
        try {
            const response = await fetch(API_SERVICES, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    name,
                    description,
                    price
                    // imageUrl and other fields can be added here if needed
                })
            });

            if (response.ok) {
                const savedService = await response.json();
                newService.backendId = savedService.id;
            }
        } catch (err) {
            console.warn("Failed to create service in backend, but local data was updated.");
        }
    }

    // Reset form to add mode
    serviceForm.reset();
    serviceIdInput.value = "";
    serviceFormBtnText.textContent = "إضافة خدمة";
});

// ----------- Backend sync: load initial data from backend (optional but recommended) -----------
/**
 * Try to load workers from backend /users/all (UserResponseDTO).
 * Filter by role=WORKER. If fails, keep dummy data.
 */
async function loadWorkersFromBackend() {
    try {
        const resp = await fetch(API_USERS_ALL);
        if (!resp.ok) return; // keep dummy data

        const data = await resp.json();

        // Map DTO to local workers structure
        workers = data
            .filter((u) => u.role === "WORKER")
            .map((u, index) => ({
                id: index + 1,               // local incremental id for table
                name: u.name || "Worker",
                phone: "—",                  // phone not provided by DTO (front-end only)
                email: u.email || "",
                roleLabel: "عامل مغسلة",
                status: "نشط",
                backendId: u.id
            }));

        renderWorkersTable();
    } catch (err) {
        console.warn("Failed to load workers from backend, using dummy data.");
        renderWorkersTable();
    }
}

/**
 * Try to load services from backend /api/services (LaundryService).
 * If fails, keep dummy data.
 */
async function loadServicesFromBackend() {
    try {
        const resp = await fetch(API_SERVICES);
        if (!resp.ok) {
            renderServicesTable();
            return;
        }

        const data = await resp.json();

        services = data.map((s, index) => ({
            id: index + 1,
            name: s.name,
            description: s.description || "",
            price: s.price || 0,
            duration: "غير محدد",  // front-end only
            backendId: s.id
        }));

        renderServicesTable();
    } catch (err) {
        console.warn("Failed to load services from backend, using dummy data.");
        renderServicesTable();
    }
}

// ----------- Init on page load -----------
/**
 * Initialize the admin page:
 * - Initialize header auth state
 * - Load initial workers/services from backend (or fallback to dummy)
 * - Render tables
 */
function initAdminPage() {
    initHeaderAuth();
    loadWorkersFromBackend();
    loadServicesFromBackend();
}

// Run when DOM is fully loaded
document.addEventListener("DOMContentLoaded", initAdminPage);
