import React, { useState, useEffect } from 'react';
import api from '../services/apiService';

const Dashboard = () => {
    const [roteiros, setRoteiros] = useState([]);

    useEffect(() => {
        const fetchRoteiros = async () => {
            try {
                const response = await api.get('/roteiros');
                setRoteiros(response.data);
            } catch (error) {
                console.error('Erro ao buscar roteiros:', error);
            }
        };

        fetchRoteiros();
    }, []);

    return (
        <div>
            <h1>Dashboard</h1>
            <ul>
                {roteiros.map((roteiro) => (
                    <li key={roteiro.id}>{roteiro.title} - Status: {roteiro.status}</li>
                ))}
            </ul>
        </div>
    );
};

export default Dashboard;