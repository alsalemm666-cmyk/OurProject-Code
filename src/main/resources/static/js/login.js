    const form = document.getElementById("loginForm");
    const alertBox = document.getElementById("alertBox");
    const loginBtn = document.getElementById("loginBtn");
    const loginBtnText = document.getElementById("loginBtnText");
    const loginBtnSpinner = document.getElementById("loginBtnSpinner");

    function showAlert(message, type = "danger") {
        alertBox.className = "alert alert-" + type;
        alertBox.textContent = message;
        alertBox.classList.remove("d-none");
    }

    function clearAlert() {
        alertBox.classList.add("d-none");
        alertBox.textContent = "";
    }

    function setLoading(isLoading) {
        if (isLoading) {
            loginBtn.disabled = true;
            loginBtnSpinner.classList.remove("d-none");
            loginBtnText.textContent = "جاري التحقق...";
        } else {
            loginBtn.disabled = false;
            loginBtnSpinner.classList.add("d-none");
            loginBtnText.textContent = "تسجيل الدخول";
        }
    }

    form.addEventListener("submit", async function (e) {
        e.preventDefault();
        clearAlert();

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();

        if (!email || !password) {
            showAlert("الرجاء إدخال البريد الإلكتروني وكلمة المرور.");
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

            const contentType = response.headers.get("content-type");
            let data = null;

            if (contentType && contentType.includes("application/json")) {
                data = await response.json();
            } else {
                data = await response.text();
            }

            if (!response.ok) {
                const errorMessage = typeof data === "string" ? data : "فشل تسجيل الدخول، تأكد من البيانات.";
                showAlert(errorMessage, "danger");
                setLoading(false);
                return;
            }

            // في حالة النجاح: نحفظ بيانات المستخدم في localStorage
            if (typeof data === "object") {
                try {
                    localStorage.setItem("user", JSON.stringify(data));
                } catch (e) {
                    console.warn("لم يتمكن من حفظ المستخدم في localStorage:", e);
                }
            }

            // رسالة نجاح سريعة ثم تحويل
            showAlert("تم تسجيل الدخول بنجاح، سيتم تحويلك الآن...", "success");
            setTimeout(() => {
                window.location.href = "/home";
            }, 800);

        } catch (err) {
            console.error(err);
            showAlert("حدث خطأ أثناء الاتصال بالخادم، حاول مرة أخرى.", "danger");
            setLoading(false);
        }
    });