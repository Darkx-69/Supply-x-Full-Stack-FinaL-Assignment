/**
 * SupplySync Enterprise - dashboard.js
 * Analytics Dashboard with Dynamic Chart.js Visualizations & Live Stats
 */

let monthlyChartInstance = null;
let inventoryChartInstance = null;
let statusChartInstance = null;

async function loadDashboard() {
    const user = Auth.getUser();
    if (!user) return;

    if (user.role === 'ADMIN') {
        await loadAdminDashboard();
    } else {
        await loadVendorDashboard();
    }
}

async function loadAdminDashboard() {
    try {
        const res = await API.get('/dashboard/admin');
        if (!res || !res.success || !res.data) {
            throw new Error(res.message || 'Failed to fetch dashboard data');
        }

        const data = res.data;

        // 1. Update KPI Counters
        document.getElementById('stat-vendors').innerText = data.totalVendors || 0;
        document.getElementById('stat-products').innerText = data.totalProducts || 0;
        document.getElementById('stat-inventory').innerText = Number(data.totalInventoryItems || 0).toLocaleString();
        document.getElementById('stat-orders').innerText = data.totalPurchaseOrders || 0;
        document.getElementById('stat-pending').innerText = data.pendingOrders || 0;
        document.getElementById('stat-deliveries').innerText = data.completedDeliveries || 0;
        document.getElementById('stat-low-stock').innerText = data.lowStockProducts || 0;
        document.getElementById('stat-spend').innerText = Common.formatCurrency(data.totalProcurementValue || 0);

        // 2. Render Chart 1: Monthly Orders Chart
        renderMonthlyOrdersChart(data.monthlyOrdersChart);

        // 3. Render Chart 2: Inventory Distribution by Category
        renderInventoryDistributionChart(data.inventoryDistributionChart);

        // 4. Render Chart 3: Order Status Distribution
        renderOrderStatusChart(data.orderStatusChart);

        // 5. Render Recent Purchase Orders Table
        renderRecentOrdersTable(data.recentPurchaseOrders || []);

        // 6. Render Recent Stock Transactions Table
        renderRecentTransactionsTable(data.recentTransactions || []);

        // 7. Render Low Stock Alerts Widget
        renderLowStockAlerts(data.lowStockAlerts || []);

        // 8. Render Recent Vendors Table
        renderRecentVendorsTable(data.recentVendors || []);

    } catch (err) {
        console.error('Error loading admin dashboard:', err);
        Common.showToast('Failed to load dashboard data: ' + err.message, 'error');
    }
}

async function loadVendorDashboard() {
    try {
        const res = await API.get('/dashboard/vendor');
        if (!res || !res.success || !res.data) {
            throw new Error(res.message || 'Failed to fetch vendor dashboard data');
        }

        const data = res.data;

        // Hide admin specific KPI cards, update vendor cards
        document.getElementById('stat-orders').innerText = data.totalOrders || 0;
        document.getElementById('stat-pending').innerText = data.pendingOrders || 0;
        document.getElementById('stat-deliveries').innerText = data.deliveredOrders || 0;
        document.getElementById('stat-spend').innerText = Common.formatCurrency(data.totalValue || 0);

        // Render vendor charts
        renderMonthlyOrdersChart(data.monthlyOrdersChart);
        renderOrderStatusChart(data.orderStatusChart);

        // Render recent orders
        renderRecentOrdersTable(data.recentOrders || []);

    } catch (err) {
        console.error('Error loading vendor dashboard:', err);
        Common.showToast('Failed to load vendor dashboard: ' + err.message, 'error');
    }
}

function renderMonthlyOrdersChart(chartData) {
    const ctx = document.getElementById('monthlyOrdersChart');
    if (!ctx || !chartData) return;

    if (monthlyChartInstance) monthlyChartInstance.destroy();

    monthlyChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels: chartData.labels || [],
            datasets: [
                {
                    label: 'Order Count',
                    data: chartData.counts || [],
                    borderColor: '#2563eb',
                    backgroundColor: 'rgba(37, 99, 235, 0.08)',
                    fill: true,
                    tension: 0.35,
                    borderWidth: 2,
                    yAxisID: 'y'
                },
                {
                    label: 'Expenditure ($)',
                    data: chartData.amounts || [],
                    borderColor: '#10b981',
                    backgroundColor: 'transparent',
                    borderDash: [5, 5],
                    borderWidth: 2,
                    tension: 0.35,
                    yAxisID: 'y1'
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: 'top', labels: { font: { family: 'Inter', size: 12 } } },
                tooltip: { padding: 10 }
            },
            scales: {
                y: {
                    type: 'linear',
                    display: true,
                    position: 'left',
                    title: { display: true, text: 'Orders' },
                    grid: { color: '#f1f5f9' },
                    ticks: { precision: 0 }
                },
                y1: {
                    type: 'linear',
                    display: true,
                    position: 'right',
                    title: { display: true, text: 'Spend ($)' },
                    grid: { drawOnChartArea: false },
                    ticks: {
                        callback: val => '$' + Number(val).toLocaleString()
                    }
                },
                x: {
                    grid: { display: false }
                }
            }
        }
    });
}

function renderInventoryDistributionChart(chartData) {
    const ctx = document.getElementById('inventoryDistributionChart');
    if (!ctx || !chartData) return;

    if (inventoryChartInstance) inventoryChartInstance.destroy();

    inventoryChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: chartData.labels || [],
            datasets: [{
                label: 'Stock Quantity',
                data: chartData.quantities || [],
                backgroundColor: [
                    '#3b82f6', '#10b981', '#f59e0b', '#8b5cf6', '#06b6d4', '#ec4899'
                ],
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }
            },
            scales: {
                y: {
                    grid: { color: '#f1f5f9' },
                    ticks: { precision: 0 }
                },
                x: {
                    grid: { display: false }
                }
            }
        }
    });
}

function renderOrderStatusChart(chartData) {
    const ctx = document.getElementById('orderStatusChart');
    if (!ctx || !chartData) return;

    if (statusChartInstance) statusChartInstance.destroy();

    statusChartInstance = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: chartData.labels || [],
            datasets: [{
                data: chartData.counts || [],
                backgroundColor: [
                    '#f59e0b', // PENDING
                    '#10b981', // APPROVED
                    '#3b82f6', // PROCESSING
                    '#06b6d4', // SHIPPED
                    '#059669', // DELIVERED
                    '#ef4444'  // CANCELLED
                ],
                borderWidth: 2,
                borderColor: '#ffffff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: 'bottom', labels: { boxWidth: 12, font: { size: 11 } } }
            },
            cutout: '65%'
        }
    });
}

function renderRecentOrdersTable(orders) {
    const tbody = document.getElementById('recent-orders-tbody');
    if (!tbody) return;

    if (!orders || orders.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="table-empty"><i class="fa-solid fa-cart-shopping"></i>No purchase orders recorded yet.</td></tr>';
        return;
    }

    tbody.innerHTML = orders.map(po => `
        <tr>
            <td style="font-weight: 600;"><a href="purchase-orders.html">${po.orderNumber}</a></td>
            <td>${po.vendorCompanyName || '-'}</td>
            <td>${Common.formatDate(po.orderDate)}</td>
            <td>${Common.formatCurrency(po.totalAmount)}</td>
            <td>${Common.getStatusBadge(po.status)}</td>
        </tr>
    `).join('');
}

function renderRecentTransactionsTable(transactions) {
    const tbody = document.getElementById('recent-transactions-tbody');
    if (!tbody) return;

    if (!transactions || transactions.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="table-empty"><i class="fa-solid fa-clock-rotate-left"></i>No inventory transactions yet.</td></tr>';
        return;
    }

    tbody.innerHTML = transactions.map(t => {
        const isPositive = t.quantity > 0;
        const qtyFormatted = isPositive ? `+${t.quantity}` : `${t.quantity}`;
        const qtyColor = isPositive ? 'var(--success)' : 'var(--danger)';

        return `
            <tr>
                <td style="font-weight: 500;">${t.productName}</td>
                <td><span class="badge badge-secondary">${t.transactionType.replace(/_/g, ' ')}</span></td>
                <td style="font-weight: 700; color: ${qtyColor};">${qtyFormatted}</td>
                <td>${Common.formatDateTime(t.transactionDate)}</td>
            </tr>
        `;
    }).join('');
}

function renderLowStockAlerts(alerts) {
    const container = document.getElementById('low-stock-container');
    if (!container) return;

    if (!alerts || alerts.length === 0) {
        container.innerHTML = `
            <div style="text-align: center; padding: 2rem; color: var(--success);">
                <i class="fa-solid fa-circle-check" style="font-size: 2rem; margin-bottom: 0.5rem; display: block;"></i>
                <strong>All stock levels healthy!</strong>
                <p style="font-size: 0.8rem; color: var(--slate-500); margin-top: 0.25rem;">No items are currently below minimum thresholds.</p>
            </div>
        `;
        return;
    }

    container.innerHTML = alerts.map(item => `
        <div style="display: flex; align-items: center; justify-content: space-between; padding: 0.75rem 0; border-bottom: 1px solid var(--slate-100);">
            <div>
                <div style="font-weight: 600; font-size: 0.875rem;">${item.productName}</div>
                <div style="font-size: 0.75rem; color: var(--slate-400);">SKU: ${item.sku} | Min: ${item.minimumStockLevel}</div>
            </div>
            <div style="text-align: right;">
                <span class="badge badge-danger">${item.availableQuantity} left</span>
                <div style="margin-top: 0.25rem;">
                    <a href="inventory.html" class="btn btn-sm btn-secondary" style="font-size: 0.7rem; padding: 0.2rem 0.5rem;">Restock</a>
                </div>
            </div>
        </div>
    `).join('');
}

function renderRecentVendorsTable(vendors) {
    const tbody = document.getElementById('recent-vendors-tbody');
    if (!tbody) return;

    if (!vendors || vendors.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="table-empty"><i class="fa-solid fa-building"></i>No vendors registered.</td></tr>';
        return;
    }

    tbody.innerHTML = vendors.map(v => `
        <tr>
            <td style="font-weight: 600;"><a href="vendors.html">${v.companyName}</a></td>
            <td>${v.contactPerson}</td>
            <td>${v.businessCategory || '-'}</td>
            <td>${Common.getStatusBadge(v.vendorStatus)}</td>
        </tr>
    `).join('');
}
