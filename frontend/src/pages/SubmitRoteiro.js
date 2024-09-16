import React, { useState } from 'react';
import api from '../services/apiService';

const SubmitRoteiro = () => {
    const [content, setContent] = useState('');
    const [clientName, setClientName] = useState('');
    const [clientEmail, setClientEmail] = useState('');
    const [clientPhone, setClientPhone] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await api.post('/roteiros/submit', {
                content,
                clientName,
                clientEmail,
                clientPhone
            });
            alert('Roteiro enviado com sucesso!');
            } catch (error) {
                console.error('Erro ao enviar roteiro:', error);
            }
        };

    return (
        <form onSubmit={handleSubmit}>
            <textArea
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
    );
};

export default SubmitRoteiro;