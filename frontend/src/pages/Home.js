import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => {
  return (
    <div>
      <h1>Bem-vindo à COOPERFILME</h1>
      <div>
        <h2>Para Clientes</h2>
        <Link to="/submit">
          <button>Enviar um Roteiro</button>
        </Link>
        <Link to="/status">
          <button>Verificar Status de um Roteiro</button>
        </Link>
      </div>
      <div>
        <h2>Para Funcionários</h2>
        <Link to="/login">
          <button>Login</button>
        </Link>
      </div>
    </div>
  );
};

export default Home;