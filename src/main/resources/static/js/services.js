    /***********************
     *** login display ***
    ************************/
    const user = JSON.parse(localStorage.getItem("user"));
    const loginBtn = document.getElementById("loginBtn");
    const profileAvatar = document.getElementById("profileAvatar");

    if (user) {
      loginBtn.style.display = "none";
      profileAvatar.style.display = "block";

      if (user.profileImage) {
        profileAvatar.src = user.profileImage;
      }
    }

    /***********************
     *** Add to cart ***
    ************************/
function addToCart(id, name, price) {
  let cart = JSON.parse(localStorage.getItem("cart")) || [];

  const existing = cart.find(item => item.id === id);

  if (existing) {
    existing.quantity += 1;
  } else {
    cart.push({
      id: id,
      name: name,
      price: price,
      quantity: 1
    });
  }

  localStorage.setItem("cart", JSON.stringify(cart));
  alert("تمت إضافة الخدمة إلى السلة ✔");
}
document.querySelectorAll(".addToCartBtn").forEach(btn => {
  btn.addEventListener("click", () => {
    addToCart(
      Number(btn.dataset.id),
      btn.dataset.name,
      Number(btn.dataset.price)
    );
  });
});