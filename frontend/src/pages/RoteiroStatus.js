import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import api from '../services/apiService';

const RoteiroStatus = () => {
    const [status, setStatus] = useState(null);
    const { id } = useParams();

    useEffect(() => {
        const fetchStatus = async () => {
            try {
                const response = await api.get(`/roteiros/status/${id}`);
                setStatus(response.data.status); 
            } catch (error) {
                console.error('Erro ao buscar status:', error);
            }
        };

        fetchStatus();
    } [id]);

    return (
        <div>
            <h2>Status do Roteiro</h2>
            {status ? (
                <p>O status do seu roteiro é: {status}</p>
            ) : (
                <p>Carregando...</p>
            )}
        </div>
    );
};

export default RoteiroStatus;