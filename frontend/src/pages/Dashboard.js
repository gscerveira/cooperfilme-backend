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
    const [justification, setJustification] = useState('');
    const [showJustificationModal, setShowJustificationModal] = useState(false);
    const [pendingAction, setPendingAction] = useState(null);
    const [reviewerComments, setReviewerComments] = useState('');
    const [showReviewerCommentsModal, setShowReviewerCommentsModal] = useState(false);

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

    useEffect(() => {
        fetchRoteiros();
    }, []);

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
        if (currentUser.role === 'ANALISTA' && newStatus !== 'EM_ANALISE') {
            setShowJustificationModal(true);
            setPendingAction({ id, newStatus });
        } else if (currentUser.role === 'REVISOR' && newStatus === 'AGUARDANDO_APROVACAO') {
            setShowReviewerCommentsModal(true);
            setPendingAction({ id, newStatus });
        }
        else {
            try {
                await api.put(`/roteiros/${id}/status`, newStatus);
                fetchRoteiros();
            } catch (error) {
                console.error('Erro ao atualizar status:', error);
                setError('Erro ao atualizar status. Verifique se você tem permissão para esta ação.');
            }
        }
    };

    const handleJustificationSubmit = async () => {
        if (!justification.trim()) {
            setError('A justificativa é obrigatória.');
            return;
        }
        try {
            await api.put(`/roteiros/${pendingAction.id}/status`, pendingAction.newStatus, {
                params: { justification }
            });
            setShowJustificationModal(false);
            setJustification('');
            setPendingAction(null);
            fetchRoteiros();
        } catch (error) {
            console.error('Erro ao atualizar status:', error);
            setError('Erro ao atualizar status. Verifique se você tem permissão para esta ação.');
        }
    };

    const handleVote = async (id, approved) => {
        try {
            await api.post(`/roteiros/${id}/vote?approved=${approved}`);
            fetchRoteiros();
        } catch (error) {
            console.error('Erro ao votar:', error);
            setError('Erro ao votar. ' + (error.response?.data || 'Verifique se você tem permissão para esta ação.'));
        }
    };

    const handleReviewerCommentsSubmit = async () => {
        if (!reviewerComments.trim()) {
            setError('Os comentários do revisor são obrigatórios.');
            return;
        }
        try {
            await api.put(`/roteiros/${pendingAction.id}/status`, pendingAction.newStatus, {
                params: { reviewerComments }
            });
            setShowReviewerCommentsModal(false);
            setReviewerComments('');
            setPendingAction(null);
            fetchRoteiros();
        } catch (error) {
            console.error('Erro ao atualizar status:', error);
            setError('Erro ao atualizar status. Verifique se você tem permissão para esta ação.');
        }
    };

    const renderActionButtons = (roteiro) => {
        const userRole = currentUser?.role;
        const status = roteiro.status;

        console.log('User Role:', userRole);
        console.log('Roteiro Status:', status);

        if (userRole === 'ANALISTA' && status === 'AGUARDANDO_ANALISE') {
            return (
                <button onClick={() => handleStatusUpdate(roteiro.id, 'EM_ANALISE')}>
                    Iniciar Análise
                </button>
            );
        }

        if (userRole === 'ANALISTA' && status === 'EM_ANALISE') {
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

        if (userRole === 'REVISOR' && status === 'AGUARDANDO_REVISAO') {
            return (
                <button onClick={() => handleStatusUpdate(roteiro.id, 'EM_REVISAO')}>
                    Iniciar Revisão
                </button>
            );
        }

        if (userRole === 'REVISOR' && status === 'EM_REVISAO') {
            return (
                <button onClick={() => handleStatusUpdate(roteiro.id, 'AGUARDANDO_APROVACAO')}>
                    Enviar para Aprovação
                </button>
            );
        }

        if (userRole === 'APROVADOR' && (status === 'AGUARDANDO_APROVACAO' || status === 'EM_APROVACAO')) {
            const hasVoted = roteiro.votes?.some(vote => vote.user.id === currentUser.id);
            if (!hasVoted) {
                return (
                    <>
                        <button onClick={() => handleVote(roteiro.id, true)}>Aprovar</button>
                        <button onClick={() => handleVote(roteiro.id, false)}>Recusar</button>
                    </>
                );
            }
        }


        return null;
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
                            {roteiro.votes && (
                                <p><strong>Votos:</strong> {roteiro.votes.length} / 3</p>
                            )}
                            {renderActionButtons(roteiro)}
                        </li>
                    ))}
                </ul>
            )}

            {showReviewerCommentsModal && (
                <div className="modal">
                    <h2>Comentários do Revisor</h2>
                    <textarea
                        value={reviewerComments}
                        onChange={(e) => setReviewerComments(e.target.value)}
                        placeholder="Digite os comentários, erros encontrados ou novas ideias"
                        required
                    />
                    <button onClick={handleReviewerCommentsSubmit}>Enviar</button>
                    <button onClick={() => {
                        setShowReviewerCommentsModal(false);
                        setReviewerComments('');
                        setPendingAction(null);
                    }}>Cancelar</button>
                </div>
            )}

            {showJustificationModal && (
                <div className="modal" style={{
                    position: 'fixed',
                    top: '50%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)',
                    backgroundColor: 'white',
                    padding: '20px',
                    boxShadow: '0 0 10px rgba(0,0,0,0.1)',
                    zIndex: 1000
                }}>
                    <h2>Justificativa</h2>
                    <textarea
                        value={justification}
                        onChange={(e) => setJustification(e.target.value)}
                        placeholder="Digite a justificativa"
                        required
                        style={{ width: '100%', minHeight: '100px' }}
                    />
                    <button onClick={handleJustificationSubmit}>Enviar</button>
                    <button onClick={() => {
                        setShowJustificationModal(false);
                        setJustification('');
                        setPendingAction(null);
                    }}>Cancelar</button>
                </div>
            )}
        </div>
    );
};

export default Dashboard;