import React from "react";
import { BrowserRouter as Router, Routes, Route, useLocation } from "react-router-dom";
import Navbar from "./components/Navbar";
import Products from "./pages/Products";
import Cart from "./pages/Cart";
import Profile from "./pages/Profile";
import { CartProvider } from "./context/CartContext";
import AuthPage from "./pages/AuthPage";


const AppContent: React.FC = () => {
  const location = useLocation();

  // Hide global Navbar on /products, since Products.tsx has its own
  const showNavbar = location.pathname !== "/products" && location.pathname !== "/";

  return (
    <>
      {showNavbar && <Navbar />}
      <div style={{ textAlign: "center" }}>
        <Routes>
          <Route path="/" element={<AuthPage />} />
          <Route path="/products" element={<Products />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/profile" element={<Profile />} />
        </Routes>
      </div>
    </>
  );
};

const App: React.FC = () => {
  return (
    <CartProvider>
      <Router>
        <AppContent />
      </Router>
    </CartProvider>
  );
};

export default App;
