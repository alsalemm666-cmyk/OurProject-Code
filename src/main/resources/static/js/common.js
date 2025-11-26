// common.js

document.addEventListener("DOMContentLoaded", () => {
  let user = null;
  try {
    user = JSON.parse(localStorage.getItem("user"));
  } catch (e) {
    console.error("Error parsing user from localStorage", e);
  }

  const loginBtn      = document.getElementById("loginBtn");
  const registerBtn   = document.getElementById("registerBtn");
  const profileMenu   = document.getElementById("profileMenu");
  const headerUsername= document.getElementById("headerUsername");
  const profileAvatar = document.getElementById("profileAvatar");
  const logoutBtn     = document.getElementById("logoutBtn");

  if (user && user.id) {
    if (headerUsername) headerUsername.textContent = user.name || "مستخدم";
    if (profileAvatar && user.profileImage) {
      profileAvatar.src = user.profileImage; // base64 من localStorage
    }

    loginBtn    && loginBtn.classList.add("d-none");
    registerBtn && registerBtn.classList.add("d-none");
    profileMenu && profileMenu.classList.remove("d-none");
  }

  if (logoutBtn) {
    logoutBtn.addEventListener("click", () => {
      localStorage.removeItem("user");
      // تقدر تمسح cart هنا لو حاب:
      localStorage.removeItem("cart");
      window.location.href = "/login";
    });
  }
});
