import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const login = async (email, password) => {
    const response = await axios.post(`${API_URL}/auth/login`, { email, password });
    if (response.data && response.data.token) {
        const decodedToken = jwtDecode(response.data.token);
        const user = {
            token: response.data.token,
            email: decodedToken.sub,
            role: decodedToken.role
        };
        localStorage.setItem('user', JSON.stringify(user));
        return user;
    }
    return null;
};

export const logout = () => {
    localStorage.removeItem('user');
};

export const getCurrentUser = () => {
    const userStr = localStorage.getItem('user');
    if (userStr) return JSON.parse(userStr);
    return null;
};