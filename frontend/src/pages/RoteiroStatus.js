import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import api from '../services/apiService';

const RoteiroStatus = () => {
    const [id, setId] = useState('');
    const [status, setStatus] = useState(null);
    const [error, setError] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await api.get(`/roteiros/status/${id}`);
            setStatus(response.data.status);
            setError(null);
        } catch (error) {
            console.error('Erro ao buscar status:', error);
            setError('Roteiro não encontrado ou erro ao buscar status.');
            setStatus(null);
        }
    };

    return (
        <div>
            <h2>Verificar status do roteiro</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    value={id}
                    onChange={(e) => setId(e.target.value)}
                    placeholder="Digite o ID do roteiro"
                    required
                />
                <button type="submit">Verificar Status</button>
            </form>
            {status && <p>O status do seu roteiro é: {status}</p>}
            {error && <p style={{ color: 'red' }}>{error}</p>}

        </div>
    );
};

export default RoteiroStatus;