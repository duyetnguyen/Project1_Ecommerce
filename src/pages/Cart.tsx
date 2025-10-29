import React, { useState } from "react";
import { useCart } from "../context/CartContext";
import { FaTrash, FaPlus, FaMinus } from "react-icons/fa";

const Cart: React.FC = () => {
  const { cart, removeFromCart, increaseQuantity, decreaseQuantity, clearCart } = useCart();
  //Add these new states for checkout and order confirmation
  const [showCheckout, setShowCheckout] = useState(false);
  const [paymentType, setPaymentType] = useState("card");
  const [orderPlaced, setOrderPlaced] = useState(false);

  const subtotal = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  // Function to show the checkout popup
  const handleCheckout = () => {
    setShowCheckout(true);
  };

  // Function to confirm the order and clear cart
  const confirmOrder = () => {
    setOrderPlaced(true);
    clearCart(); // empties the shopping cart
    setShowCheckout(false);
    setTimeout(() => setOrderPlaced(false), 3000); // hides success message after 3s
  };

  return (
    <div style={{ maxWidth: "900px", margin: "40px auto", padding: "20px" }}>
      <h1 style={{ fontSize: "28px", fontWeight: "bold", marginBottom: "30px" }}>
        SHOPPING CART
      </h1>

      {cart.length === 0 ? (
        <p style={{ textAlign: "center" }}>No items in your cart yet!</p>
      ) : (
        <>
          {cart.map((item, index) => (
            <div
              key={index}
              style={{
                display: "grid",
                gridTemplateColumns: "100px 1fr 100px",
                alignItems: "center",
                gap: "20px",
                borderBottom: "1px solid #ddd",
                padding: "20px 0",
              }}
            >
              <div>
                <img
                  src={item.image || "https://via.placeholder.com/100"}
                  alt={item.name}
                  style={{
                    width: "100px",
                    height: "100px",
                    objectFit: "contain",
                    borderRadius: "8px",
                    border: "1px solid #eee",
                  }}
                />
              </div>

              <div style={{ textAlign: "left" }}>
                <h3 style={{ margin: 0 }}>{item.name}</h3>

                {/* Quantity Controls */}
                <div
                  style={{
                    display: "flex",
                    alignItems: "center",
                    gap: "10px",
                    marginTop: "10px",
                  }}
                >
                  <button
                    onClick={() => removeFromCart(item.name)}
                    style={{
                      backgroundColor: "transparent",
                      border: "none",
                      cursor: "pointer",
                      color: "#e63946",
                    }}
                    title="Remove item"
                  >
                    <FaTrash size={20} />
                  </button>

                  {/* Negative sign (only shown if quantity > 1) */}
                  {item.quantity > 1 && (
                    <button
                      onClick={() => decreaseQuantity(item.name)}
                      style={{
                      backgroundColor: "transparent",
                      color: "black",
                        border: "none",
                        borderRadius: "5px",
                        padding: "3px 10px",
                        cursor: "pointer",
                      }}
                    >
                      <FaMinus size={20} />
                    </button>
                  )}

                  <span
                    style={{
                      border: "1px solid #ccc",
                      padding: "3px 10px",
                      borderRadius: "5px",
                      minWidth: "40px",
                      textAlign: "center",
                    }}
                  >
                    {item.quantity}
                  </span>

                  <button
                    onClick={() => increaseQuantity(item.name)}
                    style={{
                      backgroundColor: "transparent",
                      color: "black",
                      border: "none",
                      borderRadius: "5px",
                      padding: "3px 10px",
                      cursor: "pointer",
                    }}
                  >
                    <FaPlus size={20} />
                  </button>
                </div>
              </div>

              <div style={{ textAlign: "right" }}>
                <p style={{ fontWeight: "bold", fontSize: "16px" }}>
                  ${(item.price * item.quantity).toFixed(2)}
                </p>
              </div>
            </div>
          ))}

          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              marginTop: "30px",
              paddingTop: "20px",
              borderTop: "2px solid #ddd",
            }}
          >
            <h3>
              Subtotal ({cart.reduce((sum, i) => sum + i.quantity, 0)} items):{" "}
              <span style={{ color: "#007bff" }}>${subtotal.toFixed(2)}</span>
            </h3>

            {/* Proceed to Checkout button */}
            <button
              onClick={handleCheckout} // open checkout modal
              style={{
                backgroundColor: "#fffe4c",
                color: "black",
                border: "3px solid",
                borderColor: "black",
                borderRadius: "20px",
                padding: "20px 20px",
                cursor: "pointer",
                fontWeight: "bold",
                fontSize: "16px",
              }}
            >
              Proceed to Checkout ({cart.reduce((sum, i) => sum + i.quantity, 0)} items){" "}
            </button>
          </div>
        </>
      )}
      {/* Checkout Modal */}
        {showCheckout && (
          <div
            style={{
              position: "fixed",
              top: 0,
              left: 0,
              width: "100%",
              height: "100%",
              backgroundColor: "rgba(0,0,0,0.5)",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            <div
              style={{
                backgroundColor: "white",
                padding: "30px",
                borderRadius: "10px",
                width: "350px",
                textAlign: "center",
              }}
            >
              <h2>Confirm Your Order</h2>
              <p>
                You have <b>{cart.reduce((sum, i) => sum + i.quantity, 0)}</b> items totaling{" "}
                <b>${subtotal.toFixed(2)}</b>.
              </p>
              <label style={{ fontWeight: "bold" }}>Payment Type:</label>
              <select
                value={paymentType}
                onChange={(e) => setPaymentType(e.target.value)}
                style={{
                  marginLeft: "10px",
                  padding: "5px",
                  borderRadius: "5px",
                  border: "1px solid #ccc",
                }}
              >
                <option value="card">Credit / Debit Card</option>
                <option value="paypal">PayPal</option>
                <option value="cash">Cash on Delivery</option>
              </select>

              <div style={{ marginTop: "20px" }}>
                <button
                  onClick={confirmOrder}
                  style={{
                    backgroundColor: "#28a745",
                    color: "white",
                    border: "none",
                    borderRadius: "5px",
                    padding: "8px 15px",
                    marginRight: "10px",
                    cursor: "pointer",
                  }}
                >
                  Confirm
                </button>
                <button
                  onClick={() => setShowCheckout(false)}
                  style={{
                    backgroundColor: "#ccc",
                    border: "none",
                    borderRadius: "5px",
                    padding: "8px 15px",
                    cursor: "pointer",
                  }}
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Order Placed Message */}
        {orderPlaced && (
          <div
            style={{
              position: "fixed",
              bottom: "30px",
              left: "50%",
              transform: "translateX(-50%)",
              backgroundColor: "#28a745",
              color: "white",
              padding: "15px 30px",
              borderRadius: "10px",
              boxShadow: "0 2px 10px rgba(0,0,0,0.3)",
            }}
          >
            Order Placed Successfully!
          </div>
        )}
      </div>
    );
  };

export default Cart;