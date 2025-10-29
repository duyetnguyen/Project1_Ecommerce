import React, { createContext, useContext, useState } from "react";

interface CartItem {
  name: string;
  price: number;
  image?: string;
  quantity: number; // 👈 added
}

interface CartContextType {
  cart: CartItem[];
  addToCart: (item: Omit<CartItem, "quantity">) => void;
  removeFromCart: (name: string) => void;
  increaseQuantity: (name: string) => void;
  decreaseQuantity: (name: string) => void;
  clearCart: () => void;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [cart, setCart] = useState<CartItem[]>([]);

  // Function to clear all items from the cart
  const clearCart = () => {
    setCart([]);
  };

  // Add item or increase its quantity if already in cart
  const addToCart = (item: Omit<CartItem, "quantity">) => {
    setCart((prevCart) => {
      const existingItem = prevCart.find((p) => p.name === item.name);
      if (existingItem) {
        return prevCart.map((p) =>
          p.name === item.name ? { ...p, quantity: p.quantity + 1 } : p
        );
      }
      return [...prevCart, { ...item, quantity: 1 }];
    });
  };

  const removeFromCart = (name: string) => {
    setCart((prevCart) => prevCart.filter((item) => item.name !== name));
  };

  const increaseQuantity = (name: string) => {
    setCart((prevCart) =>
      prevCart.map((item) =>
        item.name === name ? { ...item, quantity: item.quantity + 1 } : item
      )
    );
  };

  const decreaseQuantity = (name: string) => {
    setCart((prevCart) =>
      prevCart
        .map((item) =>
          item.name === name && item.quantity > 1
            ? { ...item, quantity: item.quantity - 1 }
            : item
        )
        .filter((item) => item.quantity > 0)
    );
  };

  return (
    <CartContext.Provider
      value={{ cart, addToCart, removeFromCart, increaseQuantity, decreaseQuantity, clearCart }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) throw new Error("useCart must be used within CartProvider");
  return context;
};
