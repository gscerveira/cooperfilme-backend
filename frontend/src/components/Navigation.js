import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { getCurrentUser, logout } from '../services/authService';

const Navigation = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const currentUser = getCurrentUser();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav style={{ padding: '10px', backgroundColor: '#f0f0f0' }}>
      <Link to="/" style={{ marginRight: '10px' }}>Home</Link>
      
      {!currentUser && location.pathname !== '/submit' && (
        <Link to="/submit" style={{ marginRight: '10px' }}>Enviar Roteiro</Link>
      )}
      
      {!currentUser && location.pathname !== '/status' && (
        <Link to="/status" style={{ marginRight: '10px' }}>Verificar Status</Link>
      )}
      
      {!currentUser && location.pathname !== '/login' && (
        <Link to="/login" style={{ marginRight: '10px' }}>Login</Link>
      )}
      
      {currentUser && (
        <>
          <Link to="/dashboard" style={{ marginRight: '10px' }}>Dashboard</Link>
          <button onClick={handleLogout}>Logout</button>
        </>
      )}
    </nav>
  );
};

export default Navigation;