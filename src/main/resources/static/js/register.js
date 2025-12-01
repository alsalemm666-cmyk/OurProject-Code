    const form = document.getElementById("registerForm");
    const alertBox = document.getElementById("alertBox");
    const registerBtn = document.getElementById("registerBtn");
    const registerBtnText = document.getElementById("registerBtnText");
    const registerBtnSpinner = document.getElementById("registerBtnSpinner");

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
            registerBtn.disabled = true;
            registerBtnSpinner.classList.remove("d-none");
            registerBtnText.textContent = "جاري إنشاء الحساب...";
        } else {
            registerBtn.disabled = false;
            registerBtnSpinner.classList.add("d-none");
            registerBtnText.textContent = "إنشاء الحساب";
        }
    }

    form.addEventListener("submit", async function (e) {
        e.preventDefault();
        clearAlert();

        const name = document.getElementById("name").value.trim();
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();
        const confirmPassword = document.getElementById("confirmPassword").value.trim();

        // تحقق أولي من البيانات في الفرونت
        if (!name || !email || !password || !confirmPassword) {
            showAlert("الرجاء تعبئة جميع الحقول.", "danger");
            return;
        }

        if (name.length < 3) {
            showAlert("الاسم يجب أن يكون 3 حروف على الأقل.", "danger");
            return;
        }

        if (password.length < 6) {
            showAlert("كلمة المرور يجب أن تكون 6 أحرف على الأقل.", "danger");
            return;
        }

        if (password !== confirmPassword) {
            showAlert("كلمتا المرور غير متطابقتين.", "danger");
            return;
        }

        setLoading(true);

        try {
            const response = await fetch("/users/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ name, email, password })
            });

            const contentType = response.headers.get("content-type");
            let data = null;

            if (contentType && contentType.includes("application/json")) {
                data = await response.json();
            } else {
                data = await response.text();
            }

            if (!response.ok) {
                const errorMessage = typeof data === "string" ? data : "فشل إنشاء الحساب، تأكد من البيانات.";
                showAlert(errorMessage, "danger");
                setLoading(false);
                return;
            }

            // نجاح: حفظ المستخدم (اختياري) ثم تحويل
            if (typeof data === "object") {
                try {
                    localStorage.setItem("user", JSON.stringify(data));
                } catch (e) {
                    console.warn("تعذر حفظ بيانات المستخدم في localStorage:", e);
                }
            }

            showAlert("تم إنشاء الحساب بنجاح، سيتم تحويلك الآن...", "success");

            setTimeout(() => {
                window.location.href = "/home"; // أو "/login" إذا حاب
            }, 900);

        } catch (err) {
            console.error(err);
            showAlert("حدث خطأ أثناء الاتصال بالخادم، حاول مرة أخرى.", "danger");
            setLoading(false);
        }
    });

    
