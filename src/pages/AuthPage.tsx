import React, { useState } from "react";
import { useNavigate } from "react-router-dom"; // ✅ inside component
import "../AuthPage.css";

const AuthPage: React.FC = () => {
  const [isRegister, setIsRegister] = useState(false);
  const navigate = useNavigate(); // ✅ inside component

  const handleSignIn = (e: React.FormEvent) => {
    e.preventDefault();
    // TODO: Add real authentication logic here
    navigate("/products"); // Redirect to products after login
  };

  const handleRegister = (e: React.FormEvent) => {
    e.preventDefault();
    // TODO: Add real registration logic here
    navigate("/products"); // Redirect to products after registration
  };

  return (
    <div className="auth-container">
      <div className="auth-left">
        <h1>E-Commerce Project</h1>
        <h2>Discover amazing products and deals every day!</h2>
        <p>~ Duyet Nguyen, and Nguyen Le</p>
      </div>

      <div className="auth-right">
        <div className="auth-tabs">
          <button
            className={!isRegister ? "active" : ""}
            onClick={() => setIsRegister(false)}
          >
            Sign In
          </button>
          <button
            className={isRegister ? "active" : ""}
            onClick={() => setIsRegister(true)}
          >
            Register
          </button>
        </div>

        {isRegister ? (
          <form className="auth-form" onSubmit={handleRegister}>
            <input type="text" placeholder="Full Name" required />
            <input type="email" placeholder="Email" required />
            <input type="password" placeholder="Password" required />
            <input type="password" placeholder="Confirm Password" required />
            <button type="submit">Register</button>
          </form>
        ) : (
          <form className="auth-form" onSubmit={handleSignIn}>
            <input type="email" placeholder="Email" required />
            <input type="password" placeholder="Password" required />
            <button type="submit">Sign In</button>
          </form>
        )}
      </div>
    </div>
  );
};

export default AuthPage;