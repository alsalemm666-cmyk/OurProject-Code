// profile.js

const API_BASE = "";

async function loadProfile() {
  let user = null;
  try {
    user = JSON.parse(localStorage.getItem("user"));
  } catch (e) {}

  if (!user || !user.id) {
    // لو مافي مستخدم → رجّعه لتسجيل الدخول
    window.location.href = "/login";
    return;
  }

  // تعبئة الحقول
  document.getElementById("profileName").value  = user.name || "";
  document.getElementById("profileEmail").value = user.email || "";

  const preview = document.getElementById("profileImagePreview");
  if (user.profileImage) {
    preview.src = user.profileImage;
  }

  await loadUserOrders(user.id);
}

async function loadUserOrders(userId) {
  const list = document.getElementById("profileOrdersList");
  list.innerHTML = "<li class='list-group-item'>جاري التحميل...</li>";

  try {
    const res = await fetch(`${API_BASE}/api/orders`);
    if (!res.ok) {
      list.innerHTML = "<li class='list-group-item text-danger'>تعذر تحميل الطلبات</li>";
      return;
    }

    const orders = await res.json();

    // تصفية الطلبات الخاصة بالمستخدم الحالي
    const myOrders = orders.filter(o => o.user && o.user.id === userId);

    if (myOrders.length === 0) {
      list.innerHTML = "<li class='list-group-item'>لا توجد طلبات حتى الآن.</li>";
      return;
    }

    list.innerHTML = "";
    myOrders.forEach(order => {
      const li = document.createElement("li");
      li.className = "list-group-item d-flex justify-content-between align-items-center";
      li.innerHTML = `
        <span>طلب #${order.id} - الحالة: ${order.status || "غير محددة"}</span>
        <a href="/orders/${order.id}" class="btn btn-sm btn-outline-secondary">تفاصيل</a>
      `;
      list.appendChild(li);
    });
  } catch (err) {
    console.error(err);
    list.innerHTML = "<li class='list-group-item text-danger'>حدث خطأ أثناء تحميل الطلبات</li>";
  }
}

// حفظ الصورة في localStorage (Base64)
function setupProfileImageUpload() {
  const input = document.getElementById("profileImageInput");
  const preview = document.getElementById("profileImagePreview");

  if (!input) return;

  input.addEventListener("change", () => {
    const file = input.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = e => {
      const base64 = e.target.result;
      preview.src = base64;

      let user = JSON.parse(localStorage.getItem("user")) || {};
      user.profileImage = base64;
      localStorage.setItem("user", JSON.stringify(user));
    };
    reader.readAsDataURL(file);
  });
}

// تحديث الايميل/الباسورد في الباك-إند
async function setupProfileForm() {
  const form = document.getElementById("profileForm");
  const msg  = document.getElementById("profileMessage");

  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    msg.textContent = "";

    let user = JSON.parse(localStorage.getItem("user") || "{}");
    if (!user.id) {
      window.location.href = "/login";
      return;
    }

    const newEmail = document.getElementById("profileEmail").value.trim();
    const newPass  = document.getElementById("profilePassword").value.trim();

    const payload = {
      id: user.id,
      name: user.name,      // ما نعدله هنا
      email: newEmail,
      password: newPass || undefined,
      role: user.role
    };

    try {
      const res = await fetch(`${API_BASE}/users/update/${user.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      if (!res.ok) {
        msg.textContent = "تعذر حفظ التعديلات";
        return;
      }

      const updated = await res.json();
      // نحدث نسخة الـ user في localStorage (مع الإبقاء على profileImage)
      const stored   = JSON.parse(localStorage.getItem("user") || "{}");
      const merged   = { ...stored, ...updated };
      localStorage.setItem("user", JSON.stringify(merged));
      msg.classList.remove("text-danger");
      msg.classList.add("text-success");
      msg.textContent = "تم حفظ التعديلات بنجاح";
    } catch (err) {
      console.error(err);
      msg.textContent = "حدث خطأ أثناء الحفظ";
    }
  });
}

document.addEventListener("DOMContentLoaded", () => {
  loadProfile();
  setupProfileImageUpload();
  setupProfileForm();
});
