import React from "react";
import type { Category } from "../pages/Products";
import { FaBars } from "react-icons/fa";

interface SubNavbarProps {
  categories: Category[];
  selectedCategory: Category;
  onCategoryChange: (category: Category) => void;
}

const SubNavbar: React.FC<SubNavbarProps> = ({
  categories,
  selectedCategory,
  onCategoryChange,
}) => {
  return (
    <div
      style={{
        position: "sticky",
        top: 58, // 👈 match your Navbar height exactly
        zIndex: 999,
        width: "100%",
        backgroundColor: "#002028",
        margin: 0,
        padding: "10px 0",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        boxSizing: "border-box",
      }}
    >
      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: "40px",
          width: "100%",
          maxWidth: "1600px", // optional, keeps content centered nicely
          justifyContent: "space-evenly",
          padding: "0 20px",
          color: "white",
        }}
      >
        <FaBars size={30} />
        {categories.map((category) => (
          <button
            key={category}
            onClick={() => onCategoryChange(category)}
            style={{
              background: "transparent",
              color: "white",
              padding: "10px 16px",
              fontSize: "18px",
              fontWeight:
                selectedCategory === category ? "bold" : "normal",
              borderBottom:
                selectedCategory === category
                  ? "3px solid white"
                  : "3px solid transparent",
              border: "none",
              cursor: "pointer",
              transition: "0.3s ease",
            }}
          >
            {category}
          </button>
        ))}
      </div>
    </div>
  );
};

export default SubNavbar;
