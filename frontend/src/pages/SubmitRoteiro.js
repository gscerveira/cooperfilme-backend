import React, { useState } from 'react';
import api from '../services/apiService';

const SubmitRoteiro = () => {
    const [content, setContent] = useState('');
    const [clientName, setClientName] = useState('');
    const [clientEmail, setClientEmail] = useState('');
    const [clientPhone, setClientPhone] = useState('');
    const [submissionId, setSubmissionId] = useState(null);
    const [error, setError] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        try {
            const response = await api.post('/roteiros/submit', {
                content,
                clientName,
                clientEmail,
                clientPhone
            });
            if (response.data && response.data.id) {
                setSubmissionId(response.data.id);
                alert('Roteiro enviado com sucesso! Guarde o ID do seu roteiro: ' + response.data.id);
            } else {
                throw new Error('Resposta da API não contém um ID válido');
            }
        }
        catch (error) {
            console.error('Erro ao enviar roteiro:', error);
            alert('Erro ao enviar roteiro. Por favor, tente novamente.');
        }
    };

    return (
        <div>
            <h1>Enviar Roteiro</h1>
            <form onSubmit={handleSubmit}>
                <textarea
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    placeholder='Conteúdo do roteiro'
                    required
                />
                <input
                    type="text"
                    value={clientName}
                    onChange={(e) => setClientName(e.target.value)}
                    placeholder='Nome do cliente'
                    required
                />
                <input
                    type="email"
                    value={clientEmail}
                    onChange={(e) => setClientEmail(e.target.value)}
                    placeholder='Email do cliente'
                    required
                />
                <input
                    type="tel"
                    value={clientPhone}
                    onChange={(e) => setClientPhone(e.target.value)}
                    placeholder='Telefone do cliente'
                    required
                />
                <button type="submit">Enviar</button>
            </form>
            {submissionId && (
                <p>Seu roteiro foi enviado com sucesso! ID do roteiro: {submissionId}</p>
            )}
        </div>
    );
};

export default SubmitRoteiro;