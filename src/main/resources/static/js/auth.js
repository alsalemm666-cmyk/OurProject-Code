// auth.js

const API_BASE = ""; // نفس الدومين (Spring Boot) حالياً

async function handleLogin(event) {
  event.preventDefault();

  const email    = document.getElementById("loginEmail").value.trim();
  const password = document.getElementById("loginPassword").value.trim();
  const errorBox = document.getElementById("loginError");

  errorBox.textContent = "";

  try {
    const res = await fetch(`${API_BASE}/users/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password })
    });

    if (!res.ok) {
      const msg = await res.text();
      errorBox.textContent = msg || "بيانات الدخول غير صحيحة";
      return;
    }

    const user = await res.json(); // UserResponseDTO
    localStorage.setItem("user", JSON.stringify(user));
    window.location.href = "/home";
  } catch (err) {
    console.error(err);
    errorBox.textContent = "حدث خطأ في الاتصال بالخادم";
  }
}

async function handleRegister(event) {
  event.preventDefault();

  const name     = document.getElementById("regName").value.trim();
  const email    = document.getElementById("regEmail").value.trim();
  const password = document.getElementById("regPassword").value.trim();
  const errorBox = document.getElementById("registerError");

  errorBox.textContent = "";

  try {
    const res = await fetch(`${API_BASE}/users/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, email, password })
    });

    if (!res.ok) {
      const msg = await res.text();
      errorBox.textContent = msg || "تعذر إنشاء الحساب";
      return;
    }

    const user = await res.json(); // UserResponseDTO
    localStorage.setItem("user", JSON.stringify(user));
    window.location.href = "/home";
  } catch (err) {
    console.error(err);
    errorBox.textContent = "حدث خطأ في الاتصال بالخادم";
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const loginForm    = document.getElementById("loginForm");
  const registerForm = document.getElementById("registerForm");

  if (loginForm)    loginForm.addEventListener("submit", handleLogin);
  if (registerForm) registerForm.addEventListener("submit", handleRegister);
});
