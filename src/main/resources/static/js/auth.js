/**
 * SupplySync Enterprise - auth.js
 * User Authentication, Session Storage, Role-Based Route Protection
 */

const Auth = {
    TOKEN_KEY: 'supplysync_token',
    USER_KEY: 'supplysync_user',

    /**
     * Authenticate user with credentials
     */
    async login(email, password) {
        try {
            const res = await API.post('/auth/login', { email, password });
            if (res && res.success && res.data) {
                const { token, ...userData } = res.data;
                localStorage.setItem(this.TOKEN_KEY, token);
                localStorage.setItem(this.USER_KEY, JSON.stringify(userData));
                return userData;
            } else {
                throw new Error(res.message || 'Login failed');
            }
        } catch (err) {
            throw err;
        }
    },

    /**
     * Clear auth tokens and session
     */
    logout() {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
        window.location.href = 'login.html';
    },

    /**
     * Get currently stored user
     */
    getUser() {
        const userStr = localStorage.getItem(this.USER_KEY);
        if (!userStr) return null;
        try {
            return JSON.parse(userStr);
        } catch {
            return null;
        }
    },

    /**
     * Check if user is logged in
     */
    isAuthenticated() {
        return !!localStorage.getItem(this.TOKEN_KEY);
    },

    /**
     * Check if user has ADMIN role
     */
    isAdmin() {
        const user = this.getUser();
        return user && user.role === 'ADMIN';
    },

    /**
     * Check if user has VENDOR role
     */
    isVendor() {
        const user = this.getUser();
        return user && user.role === 'VENDOR';
    },

    /**
     * Enforce authentication on protected pages
     */
    requireAuth(requiredRole = null) {
        if (!this.isAuthenticated()) {
            window.location.href = 'login.html';
            return false;
        }

        const user = this.getUser();
        if (requiredRole && user.role !== requiredRole) {
            // If vendor tries to access admin-only pages, redirect to dashboard or orders
            if (user.role === 'VENDOR' && requiredRole === 'ADMIN') {
                window.location.href = 'dashboard.html';
                return false;
            }
        }
        return true;
    }
};

window.Auth = Auth;
