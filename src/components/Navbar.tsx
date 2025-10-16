import React, { useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { FaHome, FaShoppingCart, FaUser } from "react-icons/fa";
import { FaSearch } from "react-icons/fa";

const Navbar: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const navigate = useNavigate();
  const location = useLocation(); // 👈 to detect current route

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    // Pass search term to product listing page via query param
    navigate(`/products?search=${searchTerm}`);
  };

  const showSearchBar = location.pathname === "/products";

  return (
    <nav
      style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        backgroundColor: "#005C64",
        padding: "10px 20px",
        color: "white",
        position: "sticky",
        top: 0,
        zIndex: 1000,
      }}
    >
      {/* Logo / Brand */}
    <Link to="/products" style={{ display: "flex", alignItems: "center", textDecoration: "none", color: "white" }}>
      <FaHome size={30} />
      <h2 style={{ marginLeft: "10px" }}>E-Commerce Project</h2>
    </Link>


      {/* Conditionally render the Search Bar only on /products */}
      {showSearchBar && (
        <form
          onSubmit={handleSearch}
          style={{ position: "relative", display: "flex", alignItems: "center", width: "960px" }}
        >
          <input
            type="text"
            placeholder="Search products..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{
              padding: "6px 35px 6px 10px", // extra space on right for the clear button
              borderRadius: "6px",
              border: "1px solid #ccc",
              outline: "none",
              width: "100%",
              height: "35px",
            }}
          />

          {/* Clear Button inside the input */}
          {searchTerm && (
            <button
              type="button"
              onClick={() => setSearchTerm("")}
              style={{
                position: "absolute",
                right: "60px", // distance from the right edge of the form
                top: "50%",
                transform: "translateY(-50%)",
                background: "transparent",
                border: "none",
                cursor: "pointer",
                fontSize: "16px",
                color: "#0e0e0eff",
                fontWeight: "bold",
              }}
            >
              ✕
            </button>
          )}

          {/* Search Button */}
          <button
            type="submit"
            style={{
              backgroundColor: "black",
              border: "none",
              borderRadius: "0 6px 6px 0",
              padding: "6px 10px",
              cursor: "pointer",
              width: "50px",
              height: "49px",
              position: "absolute",
              right: 0,
              top: 0,
            }}
          >
            <FaSearch color="white" />
          </button>
        </form>
      )}


      {/* Icons */}
      <div style={{ display: "flex", gap: "30px" }}>
        <Link to="/cart" style={{ color: "white" }}>
          <FaShoppingCart size={30} />
        </Link>
        <Link to="/profile" style={{ color: "white" }}>
          <FaUser size={30} />
        </Link>
      </div>
    </nav>
  );
};

export default Navbar;
