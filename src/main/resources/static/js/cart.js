                let cart = JSON.parse(localStorage.getItem("cart")) || [];
        
                const cartItemsContainer = document.getElementById("cartItems");
                const cartTotal = document.getElementById("cartTotal");
                const checkoutBtn = document.getElementById("checkoutBtn");
        
                function renderCart() {
                    cartItemsContainer.innerHTML = "";
        
                    if (cart.length === 0) {
                        cartItemsContainer.innerHTML = `
                            <div class="empty-cart">
                                <i class="fa fa-cart-arrow-down"></i>
                                <h4 class="mt-3">السلة فارغة</h4>
                            </div>
                        `;
                        cartTotal.textContent = "";
                        checkoutBtn.style.display = "none";
                        return;
                    }
        
                    checkoutBtn.style.display = "block";
        
                    let total = 0;
        
                    cart.forEach((item, index) => {
                        total += item.price * item.quantity;
        
                        const element = `
                            <div class="cart-item">
                                <div>
                                    <strong>${item.name}</strong>
                                    <div class="text-muted">${item.price} ريال</div>
                                </div>
        
                                <div class="d-flex align-items-center">
        
                                    <button class="qty-btn" onclick="changeQty(${index}, 1)">+</button>
        
                                    <span class="px-3 fw-bold">${item.quantity}</span>
        
                                    <button class="qty-btn" onclick="changeQty(${index}, -1)">−</button>
        
                                    <button class="remove-btn ms-3" onclick="removeItem(${index})">
                                        <i class="fa fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        `;
        
                        cartItemsContainer.innerHTML += element;
                    });
        
                    cartTotal.textContent = "الإجمالي: " + total + " ريال";
                }
        
                function changeQty(index, amount) {
                    cart[index].quantity += amount;
                    if (cart[index].quantity <= 0) cart[index].quantity = 1;
        
                    localStorage.setItem("cart", JSON.stringify(cart));
                    renderCart();
                }
        
                function removeItem(index) {
                    cart.splice(index, 1);
                    localStorage.setItem("cart", JSON.stringify(cart));
                    renderCart();
                }
        
                checkoutBtn.addEventListener("click", () => {
                    alert("يتم الآن التوجيه لصفحة الدفع...");
                    window.location.href = "/checkout";
                });
        
                renderCart();
