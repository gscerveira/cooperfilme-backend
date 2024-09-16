import React, { useState, useEffect } from 'react';
import api from '../services/apiService';
import { getCurrentUser } from '../services/authService';

const Dashboard = () => {
    const [roteiros, setRoteiros] = useState([]);
    const [filters, setFilters] = useState({
        status: '',
        startDate: '',
        endDate: '',
        clientEmail: ''
    });
    const [error, setError] = useState(null);
    const currentUser = getCurrentUser();

    const statusOptions = [
        'AGUARDANDO_ANALISE',
        'EM_ANALISE',
        'AGUARDANDO_REVISAO',
        'EM_REVISAO',
        'AGUARDANDO_APROVACAO',
        'EM_APROVACAO',
        'APROVADO',
        'RECUSADO'
    ];

    const fetchRoteiros = async () => {
        try {
            const response = await api.get('/roteiros', { params: filters });
            setRoteiros(response.data);
            setError(null);
        } catch (error) {
            console.error('Erro ao buscar roteiros:', error);
            setError('Erro ao buscar roteiros. Por favor, tente novamente.');
            setRoteiros([]);
        }
    };

    const handleFilterChange = (e) => {
        setFilters({ ...filters, [e.target.name]: e.target.value });
    };

    const handleSearch = (e) => {
        e.preventDefault();
        fetchRoteiros();
    };

    const handleStatusUpdate = async (id, newStatus) => {
        try {
            await api.put(`/roteiros/${id}/status`, { status: newStatus });
            fetchRoteiros();
        } catch (error) {
            console.error('Erro ao atualizar status:', error);
            setError('Erro ao atualizar status. Verifique se você tem permissão para esta ação.');
        }
    };

    const handleVote = async (id, approved) => {
        try {
            await api.post(`/roteiros/${id}/vote`, { approved });
            fetchRoteiros();
        } catch (error) {
            console.error('Erro ao votar:', error);
            setError('Erro ao votar. Verifique se você tem permissão para esta ação.');
        }
    };

    const renderActionButtons = (roteiro) => {
        switch (currentUser.role) {
            case 'ANALISTA':
                if (roteiro.status === 'AGUARDANDO_ANALISE') {
                    return (
                        <button onClick={() => handleStatusUpdate(roteiro.id, 'EM_ANALISE')}>
                            Iniciar Análise
                        </button>
                    );
                } else if (roteiro.status === 'EM_ANALISE') {
                    return (
                        <>
                            <button onClick={() => handleStatusUpdate(roteiro.id, 'AGUARDANDO_REVISAO')}>
                                Enviar para Revisão
                            </button>
                            <button onClick={() => handleStatusUpdate(roteiro.id, 'RECUSADO')}>
                                Recusar
                            </button>
                        </>
                    );
                }
                break;
            case 'REVISOR':
                if (roteiro.status === 'AGUARDANDO_REVISAO') {
                    return (
                        <button onClick={() => handleStatusUpdate(roteiro.id, 'EM_REVISAO')}>
                            Iniciar Revisão
                        </button>
                    );
                } else if (roteiro.status === 'EM_REVISAO') {
                    return (
                        <button onClick={() => handleStatusUpdate(roteiro.id, 'AGUARDANDO_APROVACAO')}>
                            Enviar para Aprovação
                        </button>
                    );
                }
                break;
            case 'APROVADOR':
                if (roteiro.status === 'AGUARDANDO_APROVACAO' || roteiro.status === 'EM_APROVACAO') {
                    return (
                        <>
                            <button onClick={() => handleVote(roteiro.id, true)}>Aprovar</button>
                            <button onClick={() => handleVote(roteiro.id, false)}>Recusar</button>
                        </>
                    );
                }
                break;
            default:
                return null;
        }
    };

    return (
        <div>
            <h1>Dashboard</h1>
            <form onSubmit={handleSearch}>
                <select
                    name="status"
                    value={filters.status}
                    onChange={handleFilterChange}
                >
                    <option value="">Todos os status</option>
                    {statusOptions.map(status => (
                        <option key={status} value={status}>{status}</option>
                    ))}
                </select>
                <input
                    type="date"
                    name="startDate"
                    value={filters.startDate}
                    onChange={handleFilterChange}
                />
                <input
                    type="date"
                    name="endDate"
                    value={filters.endDate}
                    onChange={handleFilterChange}
                />
                <input
                    type="email"
                    name="clientEmail"
                    placeholder="Email do cliente"
                    value={filters.clientEmail}
                    onChange={handleFilterChange}
                />
                <button type="submit">Buscar</button>
            </form>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {roteiros.length === 0 ? (
                <p>Nenhum roteiro encontrado com os filtros atuais.</p>
            ) : (
                <ul>
                    {roteiros.map((roteiro) => (
                        <li key={roteiro.id} style={{ marginBottom: '20px', borderBottom: '1px solid #ccc', paddingBottom: '10px' }}>
                            <h3>Roteiro ID: {roteiro.id}</h3>
                            <p><strong>Título:</strong> {roteiro.title}</p>
                            <p><strong>Status:</strong> {roteiro.status}</p>
                            <p><strong>Conteúdo:</strong> {roteiro.content}</p>
                            <p><strong>Cliente:</strong> {roteiro.clientName}</p>
                            <p><strong>Email do Cliente:</strong> {roteiro.clientEmail}</p>
                            <p><strong>Telefone do Cliente:</strong> {roteiro.clientPhone}</p>
                            <p><strong>Data de Criação:</strong> {new Date(roteiro.createdAt).toLocaleString()}</p>
                            <p><strong>Atribuído a:</strong> {roteiro.assignedTo ? roteiro.assignedTo.name : 'Não atribuído'}</p>
                            <p><strong>Justificativa:</strong> {roteiro.justification || 'Nenhuma justificativa fornecida'}</p>
                            {renderActionButtons(roteiro)}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default Dashboard;