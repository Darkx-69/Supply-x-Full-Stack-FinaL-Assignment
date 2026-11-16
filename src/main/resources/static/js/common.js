/**
 * SupplySync Enterprise - common.js
 * Layout Setup, Sidebar Generator, Toast System, Modal Helpers, Formatters
 */

const Common = {
    /**
     * Initialize shared page layout (Sidebar, Topbar, Role Restrictions)
     */
    initLayout(activePageKey, pageTitle, breadcrumbCategory = 'Management') {
        const user = Auth.getUser();
        if (!user) return;

        // 1. Render Sidebar
        const sidebarContainer = document.getElementById('sidebar-container');
        if (sidebarContainer) {
            sidebarContainer.innerHTML = this.getSidebarHtml(activePageKey, user);
        }

        // 2. Render Topbar
        const topbarContainer = document.getElementById('topbar-container');
        if (topbarContainer) {
            topbarContainer.innerHTML = this.getTopbarHtml(pageTitle, breadcrumbCategory, user);
        }

        // 3. Bind mobile sidebar toggle
        const toggleBtn = document.getElementById('sidebar-toggle-btn');
        const sidebar = document.querySelector('.sidebar');
        const overlay = document.getElementById('sidebar-overlay');

        if (toggleBtn && sidebar && overlay) {
            toggleBtn.addEventListener('click', () => {
                sidebar.classList.toggle('open');
                overlay.classList.toggle('active');
            });

            overlay.addEventListener('click', () => {
                sidebar.classList.remove('open');
                overlay.classList.remove('active');
            });
        }
    },

    /**
     * Generate Enterprise Sidebar HTML based on Role
     */
    getSidebarHtml(activeKey, user) {
        const isAdmin = user.role === 'ADMIN';

        const adminMenu = `
            <div class="menu-category">Main</div>
            <li class="nav-item">
                <a href="dashboard.html" class="nav-link ${activeKey === 'dashboard' ? 'active' : ''}">
                    <i class="fa-solid fa-chart-pie"></i>
                    <span>Dashboard</span>
                </a>
            </li>

            <div class="menu-category">Supply Chain</div>
            <li class="nav-item">
                <a href="vendors.html" class="nav-link ${activeKey === 'vendors' ? 'active' : ''}">
                    <i class="fa-solid fa-building"></i>
                    <span>Vendors</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="categories.html" class="nav-link ${activeKey === 'categories' ? 'active' : ''}">
                    <i class="fa-solid fa-tags"></i>
                    <span>Categories</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="products.html" class="nav-link ${activeKey === 'products' ? 'active' : ''}">
                    <i class="fa-solid fa-box-archive"></i>
                    <span>Products</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="inventory.html" class="nav-link ${activeKey === 'inventory' ? 'active' : ''}">
                    <i class="fa-solid fa-warehouse"></i>
                    <span>Inventory</span>
                </a>
            </li>

            <div class="menu-category">Operations</div>
            <li class="nav-item">
                <a href="procurement.html" class="nav-link ${activeKey === 'procurement' ? 'active' : ''}">
                    <i class="fa-solid fa-file-invoice-dollar"></i>
                    <span>Procurement</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="purchase-orders.html" class="nav-link ${activeKey === 'orders' ? 'active' : ''}">
                    <i class="fa-solid fa-cart-shopping"></i>
                    <span>Purchase Orders</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="deliveries.html" class="nav-link ${activeKey === 'deliveries' ? 'active' : ''}">
                    <i class="fa-solid fa-truck-fast"></i>
                    <span>Deliveries</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="transactions.html" class="nav-link ${activeKey === 'transactions' ? 'active' : ''}">
                    <i class="fa-solid fa-clock-rotate-left"></i>
                    <span>Stock Transactions</span>
                </a>
            </li>

            <div class="menu-category">Intelligence</div>
            <li class="nav-item">
                <a href="reports.html" class="nav-link ${activeKey === 'reports' ? 'active' : ''}">
                    <i class="fa-solid fa-chart-line"></i>
                    <span>Reports & Analytics</span>
                </a>
            </li>
        `;

        const vendorMenu = `
            <div class="menu-category">Vendor Portal</div>
            <li class="nav-item">
                <a href="dashboard.html" class="nav-link ${activeKey === 'dashboard' ? 'active' : ''}">
                    <i class="fa-solid fa-chart-pie"></i>
                    <span>My Dashboard</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="purchase-orders.html" class="nav-link ${activeKey === 'orders' ? 'active' : ''}">
                    <i class="fa-solid fa-cart-shopping"></i>
                    <span>My Purchase Orders</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="deliveries.html" class="nav-link ${activeKey === 'deliveries' ? 'active' : ''}">
                    <i class="fa-solid fa-truck-fast"></i>
                    <span>Shipments & Tracking</span>
                </a>
            </li>
        `;

        const initials = user.fullName ? user.fullName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() : 'U';

        return `
            <aside class="sidebar">
                <div class="sidebar-header">
                    <div class="brand-icon">
                        <i class="fa-solid fa-cubes-stacked"></i>
                    </div>
                    <div class="brand-info">
                        <span class="brand-title">SupplySync</span>
                        <span class="brand-subtitle">Enterprise SCM</span>
                    </div>
                </div>

                <ul class="sidebar-menu">
                    ${isAdmin ? adminMenu : vendorMenu}

                    <div class="menu-category">Account</div>
                    <li class="nav-item">
                        <a href="profile.html" class="nav-link ${activeKey === 'profile' ? 'active' : ''}">
                            <i class="fa-solid fa-user-shield"></i>
                            <span>Profile</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="settings.html" class="nav-link ${activeKey === 'settings' ? 'active' : ''}">
                            <i class="fa-solid fa-sliders"></i>
                            <span>Settings</span>
                        </a>
                    </li>
                </ul>

                <div class="sidebar-footer">
                    <div class="user-avatar">${initials}</div>
                    <div class="user-info">
                        <div class="user-name" title="${user.fullName}">${user.fullName}</div>
                        <span class="user-role-badge">${user.role}</span>
                    </div>
                    <button class="btn-logout" title="Sign Out" onclick="Auth.logout()">
                        <i class="fa-solid fa-arrow-right-from-bracket"></i>
                    </button>
                </div>
            </aside>
            <div class="sidebar-overlay" id="sidebar-overlay"></div>
        `;
    },

    /**
     * Generate Topbar HTML
     */
    getTopbarHtml(pageTitle, category, user) {
        const isAdmin = user.role === 'ADMIN';
        const roleLabel = isAdmin ? 'Admin' : 'Vendor (Apex Logistics)';
        const switchBtn = isAdmin
            ? `<button class="btn btn-sm btn-secondary" onclick="Common.quickSwitchRole('VENDOR')" title="Switch to Vendor view"><i class="fa-solid fa-repeat"></i> Switch to Vendor</button>`
            : `<button class="btn btn-sm btn-secondary" onclick="Common.quickSwitchRole('ADMIN')" title="Switch to Admin view"><i class="fa-solid fa-repeat"></i> Switch to Admin</button>`;

        return `
            <header class="topbar">
                <div class="topbar-left">
                    <button class="sidebar-toggle-btn" id="sidebar-toggle-btn">
                        <i class="fa-solid fa-bars"></i>
                    </button>
                    <nav class="page-breadcrumb">
                        <span>SupplySync</span>
                        <i class="fa-solid fa-chevron-right" style="font-size: 0.65rem;"></i>
                        <span>${category}</span>
                        <i class="fa-solid fa-chevron-right" style="font-size: 0.65rem;"></i>
                        <span class="breadcrumb-active">${pageTitle}</span>
                    </nav>
                </div>
                <div class="topbar-right">
                    <div style="display: flex; align-items: center; gap: 0.5rem; font-size: 0.78rem;">
                        <span class="badge ${isAdmin ? 'badge-primary' : 'badge-success'}" style="font-size: 0.7rem;">${roleLabel}</span>
                        ${switchBtn}
                    </div>
                    <div class="system-status-indicator">
                        <span class="status-dot"></span>
                        <span>Operational</span>
                    </div>
                </div>
            </header>
        `;
    },

    /**
     * Instantly switch between Demo Admin and Demo Vendor accounts
     */
    async quickSwitchRole(targetRole) {
        try {
            const credentials = targetRole === 'ADMIN'
                ? { email: 'admin@supplysync.com', password: 'admin123' }
                : { email: 'vendor@apexsupplies.com', password: 'vendor123' };

            const user = await Auth.login(credentials.email, credentials.password);
            this.showToast(`Switched account to ${user.fullName} (${user.role})!`, 'success');
            setTimeout(() => {
                window.location.reload();
            }, 300);
        } catch (err) {
            this.showToast('Failed to switch role: ' + err.message, 'error');
        }
    },

    /**
     * Display a modern floating toast notification
     */
    showToast(message, type = 'success', title = null) {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            container.className = 'toast-container';
            document.body.appendChild(container);
        }

        const icons = {
            success: 'fa-circle-check',
            error: 'fa-circle-xmark',
            warning: 'fa-triangle-exclamation',
            info: 'fa-circle-info'
        };

        const defaultTitles = {
            success: 'Success',
            error: 'Error',
            warning: 'Warning',
            info: 'Information'
        };

        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `
            <i class="fa-solid ${icons[type] || 'fa-bell'} toast-icon"></i>
            <div class="toast-content">
                <div class="toast-title">${title || defaultTitles[type] || 'Notification'}</div>
                <div class="toast-message">${message}</div>
            </div>
        `;

        container.appendChild(toast);

        // Animate entrance
        setTimeout(() => toast.classList.add('show'), 10);

        // Auto dismiss after 4 seconds
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    },

    /**
     * Generic Modal Controller
     */
    openModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    },

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('active');
            document.body.style.overflow = '';
        }
    },

    /**
     * Format currency amount
     */
    formatCurrency(amount) {
        if (amount == null || isNaN(amount)) return '$0.00';
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
            minimumFractionDigits: 2
        }).format(amount);
    },

    /**
     * Format date
     */
    formatDate(dateStr) {
        if (!dateStr) return '-';
        const d = new Date(dateStr);
        if (isNaN(d.getTime())) return dateStr;
        return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    },

    formatDateTime(dtStr) {
        if (!dtStr) return '-';
        const d = new Date(dtStr);
        if (isNaN(d.getTime())) return dtStr;
        return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    },

    /**
     * Get styled status badge HTML
     */
    getStatusBadge(status) {
        if (!status) return '<span class="badge badge-secondary">-</span>';

        const s = status.toUpperCase();
        switch (s) {
            case 'ACTIVE':
            case 'DELIVERED':
            case 'APPROVED':
                return `<span class="badge badge-success"><i class="fa-solid fa-check"></i> ${s}</span>`;
            case 'PENDING':
            case 'PREPARING':
            case 'DRAFT':
                return `<span class="badge badge-warning"><i class="fa-solid fa-hourglass-half"></i> ${s}</span>`;
            case 'IN_TRANSIT':
            case 'DISPATCHED':
            case 'OUT_FOR_DELIVERY':
            case 'PROCESSING':
            case 'CONVERTED_TO_ORDER':
                return `<span class="badge badge-primary"><i class="fa-solid fa-truck-fast"></i> ${s.replace(/_/g, ' ')}</span>`;
            case 'CANCELLED':
            case 'REJECTED':
            case 'INACTIVE':
            case 'OUT_OF_STOCK':
            case 'DELAYED':
                return `<span class="badge badge-danger"><i class="fa-solid fa-circle-exclamation"></i> ${s.replace(/_/g, ' ')}</span>`;
            default:
                return `<span class="badge badge-secondary">${s}</span>`;
        }
    }
};

window.Common = Common;
