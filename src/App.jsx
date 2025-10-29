import { useState } from 'react'

import './App.css'
import { BrowserRouter, Routes, Route, Link, Navigate } from "react-router-dom";
import AdminProductPage from "./pages/admin/AdminProductPage";
import Home from "./pages/front/Home.jsx";
function App() {
  const isEmployee = true;
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/admin/products" element={isEmployee ? <AdminProductPage /> : <Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}


export default App
