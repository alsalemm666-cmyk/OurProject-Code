// services.js

const API_BASE = "";

async function loadServices() {
  const container = document.getElementById("servicesContainer");
  container.innerHTML = "جاري التحميل...";

  const res = await fetch(`${API_BASE}/api/services`);
  const services = await res.json();

  container.innerHTML = "";
  services.forEach(s => {
    const card = document.createElement("div");
    card.className = "col-md-4 mb-3";
    card.innerHTML = `
      <div class="card h-100 shadow-sm">
        <div class="card-body text-end">
          <h5 class="card-title">${s.name}</h5>
          <p class="card-text small text-muted">${s.description || ""}</p>
          <p class="fw-bold mb-3">السعر: ${s.price} ريال</p>
          <button class="btn btn-primary w-100" data-id="${s.id}">إضافة للسلة</button>
        </div>
      </div>
    `;
    container.appendChild(card);
  });

  container.querySelectorAll("button").forEach(btn => {
    btn.addEventListener("click", () => {
      addToCart(parseInt(btn.getAttribute("data-id")));
    });
  });
}

function addToCart(serviceId) {
  let cart = JSON.parse(localStorage.getItem("cart") || "[]");
  const existing = cart.find(item => item.serviceId === serviceId);
  if (existing) {
    existing.quantity += 1;
  } else {
    cart.push({ serviceId, quantity: 1 });
  }
  localStorage.setItem("cart", JSON.stringify(cart));
  alert("تمت إضافة الخدمة إلى السلة");
}

document.addEventListener("DOMContentLoaded", loadServices);
