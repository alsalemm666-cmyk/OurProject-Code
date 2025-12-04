// ======================= login.js =======================
// Handle user login for Laundry System
// - Sends POST /users/login
// - Stores user in localStorage
// - Redirects based on user.role (ADMIN / WORKER / CUSTOMER)

// Run when DOM content is loaded
document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const loginBtn = document.getElementById("loginBtn");
    const loginBtnText = document.getElementById("loginBtnText");
    const loginBtnSpinner = document.getElementById("loginBtnSpinner");
    const alertBox = document.getElementById("alertBox");

    /**
     * Show message inside alert box
     * @param {string} message 
     * @param {"success"|"danger"|"info"} type 
     */
    function showAlert(message, type = "danger") {
        if (!alertBox) return;
        alertBox.classList.remove("d-none");
        alertBox.classList.remove("alert-danger", "alert-success", "alert-info");
        alertBox.classList.add("alert-" + type);
        alertBox.textContent = message;
    }

    /**
     * Toggle loading state on login button
     * @param {boolean} isLoading 
     */
    function setLoading(isLoading) {
        if (!loginBtn) return;
        loginBtn.disabled = isLoading;
        if (loginBtnSpinner && loginBtnText) {
            if (isLoading) {
                loginBtnSpinner.classList.remove("d-none");
                loginBtnText.textContent = "جاري تسجيل الدخول...";
            } else {
                loginBtnSpinner.classList.add("d-none");
                loginBtnText.textContent = "تسجيل الدخول";
            }
        }
    }

    if (!loginForm) {
        console.warn("loginForm not found in DOM.");
        return;
    }

    // Handle submit
    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        if (alertBox) alertBox.classList.add("d-none");

        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();

        if (!email || !password) {
            showAlert("الرجاء إدخال البريد الإلكتروني وكلمة المرور.", "danger");
            return;
        }

        setLoading(true);

        try {
            const response = await fetch("/users/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ email, password })
            });

            if (!response.ok) {
                // Try to read error as text
                let errorMessage = "فشل تسجيل الدخول. تأكد من البيانات.";
                try {
                    const text = await response.text();
                    if (text) errorMessage = text;
                } catch (_) {}
                showAlert(errorMessage, "danger");
                setLoading(false);
                return;
            }

            // Expecting UserResponseDTO: { id, name, email, role }
            const user = await response.json();

            // Save user in localStorage for later use
            localStorage.setItem("user", JSON.stringify(user));

            // Redirect based on role
            if (user.role === "ADMIN") {
                window.location.href = "/admin";
            } else if (user.role === "WORKER") {
                window.location.href = "/worker";
            } else {
                // CUSTOMER or any other role → home
                window.location.href = "/home";
            }

        } catch (error) {
            console.error("Login error:", error);
            showAlert("حدث خطأ أثناء الاتصال بالخادم.", "danger");
            setLoading(false);
        }
    });
});
