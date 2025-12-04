document.addEventListener("DOMContentLoaded", function () {

    const user = JSON.parse(localStorage.getItem("user"));

    const usernameDisplay = document.getElementById("usernameDisplay");
    const loginBtn = document.getElementById("loginBtn");
    const profileAvatar = document.getElementById("profileAvatar");
    const logoutBtn = document.getElementById("logoutBtn");
    const adminLink = document.getElementById("adminLink");
    const myOrdersLink = document.getElementById("myOrdersLink"); // زر طلباتي

    // =========================
    // ✅ في حال المستخدم مسجل
    // =========================
    if (user) {
        usernameDisplay.textContent = user.name;

        if (loginBtn) loginBtn.style.display = "none";
        if (profileAvatar) profileAvatar.style.display = "inline-block";
        if (logoutBtn) logoutBtn.style.display = "inline-block";
        if (myOrdersLink) myOrdersLink.style.display = "inline-block";

        // ✅ صورة البروفايل
        if (user.profileImage && profileAvatar) {
            profileAvatar.src = user.profileImage;
        }

        profileAvatar.onclick = () => {
            window.location.href = "/profile";
        };

        // ✅ صلاحية الأدمن فقط
        if (user.role === "ADMIN" && adminLink) {
            adminLink.style.display = "inline-block";
        }

    } 
    // =========================
    // ❌ في حال الزائر (غير مسجل)
    // =========================
    else {
        usernameDisplay.textContent = "الضيف";

        if (loginBtn) loginBtn.style.display = "inline-block";
        if (profileAvatar) profileAvatar.style.display = "none";
        if (logoutBtn) logoutBtn.style.display = "none";
        if (adminLink) adminLink.style.display = "none";
        if (myOrdersLink) myOrdersLink.style.display = "none";
    }

    // =========================
    // ✅ تسجيل الخروج
    // =========================
    if (logoutBtn) {
        logoutBtn.onclick = function () {
            localStorage.removeItem("user");
            window.location.href = "/login";
        };
    }

});
