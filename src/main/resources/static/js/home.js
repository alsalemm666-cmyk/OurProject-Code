        const user = JSON.parse(localStorage.getItem("user"));

        const usernameDisplay = document.getElementById("usernameDisplay");
        const loginBtn = document.getElementById("loginBtn");
        const profileAvatar = document.getElementById("profileAvatar");
        const logoutBtn = document.getElementById("logoutBtn");
if (user) {
    usernameDisplay.textContent = user.name;

    if (loginBtn)   loginBtn.style.display   = "none";
    if (profileAvatar) profileAvatar.style.display = "inline-block";
    if (logoutBtn)  logoutBtn.style.display  = "inline-block";

    // ✅ حط الصورة اللي حفظتها في localStorage
    if (user.profileImage && profileAvatar) {
        profileAvatar.src = user.profileImage;
    }

    profileAvatar.onclick = () => {
        window.location.href = "/profile";
    };
} else {
    usernameDisplay.textContent = "الضيف";

    if (loginBtn)   loginBtn.style.display   = "inline-block";
    if (profileAvatar) profileAvatar.style.display = "none";
    if (logoutBtn)  logoutBtn.style.display  = "none";
}

        

        // تسجيل الخروج
        logoutBtn.onclick = function () {
            localStorage.removeItem("user");
            window.location.href = "/login";
        };