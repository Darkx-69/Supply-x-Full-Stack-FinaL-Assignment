/**
 * SupplySync Enterprise - api.js
 * Centralized Fetch API Client with JWT Bearer Token Injection
 */

const API_BASE_URL = window.location.origin.includes(':8080')
    ? `${window.location.origin}/api`
    : (window.location.protocol === 'file:' ? 'http://localhost:8080/api' : `${window.location.origin}/api`);

const API = {
    /**
     * Generic fetch wrapper
     */
    async request(endpoint, options = {}) {
        const token = localStorage.getItem('supplysync_token');
        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            ...(options.headers || {})
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            ...options,
            headers
        };

        try {
            const url = endpoint.startsWith('http') ? endpoint : `${API_BASE_URL}${endpoint}`;
            const response = await fetch(url, config);

            if (response.status === 401) {
                // Token expired or invalid
                console.warn('Session expired or unauthorized. Redirecting to login.');
                localStorage.removeItem('supplysync_token');
                localStorage.removeItem('supplysync_user');
                if (!window.location.pathname.endsWith('login.html') && !window.location.pathname.endsWith('index.html')) {
                    window.location.href = 'login.html';
                }
                throw new Error('Unauthorized: Session expired');
            }

            if (response.status === 204) {
                return null;
            }

            const isJson = response.headers.get('content-type')?.includes('application/json');
            const data = isJson ? await response.json() : await response.text();

            if (!response.ok) {
                const message = (data && data.message) ? data.message : `Request failed with status ${response.status}`;
                throw new Error(message);
            }

            return data;
        } catch (error) {
            console.error(`API Error on ${endpoint}:`, error);
            throw error;
        }
    },

    get(endpoint, options = {}) {
        return this.request(endpoint, { method: 'GET', ...options });
    },

    post(endpoint, body, options = {}) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(body),
            ...options
        });
    },

    put(endpoint, body, options = {}) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(body),
            ...options
        });
    },

    patch(endpoint, body, options = {}) {
        return this.request(endpoint, {
            method: 'PATCH',
            body: JSON.stringify(body),
            ...options
        });
    },

    delete(endpoint, options = {}) {
        return this.request(endpoint, { method: 'DELETE', ...options });
    },

    getDownloadUrl(endpoint) {
        return `${API_BASE_URL}${endpoint}`;
    }
};

window.API = API;
window.API_BASE_URL = API_BASE_URL;
