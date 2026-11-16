/**
 * SupplySync Enterprise - reports.js
 * Business Intelligence Reports, Dynamic Charts, CSV Data Export & Printable Views
 */

let activeReportTab = 'inventory';
let reportChartInstance = null;

async function initReportsPage() {
    await switchReportTab('inventory');
}

async function switchReportTab(tabName) {
    activeReportTab = tabName;

    // Update active tab buttons
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.toggle('active', btn.getAttribute('data-tab') === tabName);
    });

    const exportBtn = document.getElementById('btn-export-csv');
    if (exportBtn) {
        exportBtn.setAttribute('data-type', tabName);
    }

    if (tabName === 'inventory') {
        await loadInventoryReport();
    } else if (tabName === 'vendors') {
        await loadVendorReport();
    } else if (tabName === 'orders') {
        await loadPurchaseOrderReport();
    } else if (tabName === 'low-stock') {
        await loadLowStockReport();
    }
}

async function loadInventoryReport() {
    try {
        const res = await API.get('/reports/inventory');
        const data = res.data;

        document.getElementById('report-stat-1-label').innerText = 'Total Valuation';
        document.getElementById('report-stat-1-val').innerText = Common.formatCurrency(data.totalValuation);

        document.getElementById('report-stat-2-label').innerText = 'Stocked Products';
        document.getElementById('report-stat-2-val').innerText = data.totalProducts || 0;

        document.getElementById('report-stat-3-label').innerText = 'Total Units in Warehouse';
        document.getElementById('report-stat-3-val').innerText = Number(data.totalStockUnits || 0).toLocaleString();

        const thead = document.getElementById('report-table-thead');
        thead.innerHTML = `
            <tr>
                <th>Product Name</th>
                <th>SKU</th>
                <th>Category</th>
                <th>Unit Price</th>
                <th>Available Qty</th>
                <th>Total Valuation</th>
                <th>Status</th>
            </tr>
        `;

        const tbody = document.getElementById('report-table-tbody');
        tbody.innerHTML = (data.records || []).map(r => `
            <tr>
                <td style="font-weight: 600;">${r.productName}</td>
                <td><code>${r.sku}</code></td>
                <td>${r.category}</td>
                <td>${Common.formatCurrency(r.unitPrice)}</td>
                <td><strong>${r.availableQuantity}</strong></td>
                <td style="font-weight: 700;">${Common.formatCurrency(r.totalValue)}</td>
                <td>${r.isLowStock ? '<span class="badge badge-danger">Low Stock</span>' : '<span class="badge badge-success">Optimal</span>'}</td>
            </tr>
        `).join('');

        renderReportChart(
            'bar',
            (data.records || []).slice(0, 7).map(r => r.productName),
            (data.records || []).slice(0, 7).map(r => r.totalValue),
            'Top Stock Valuations ($)'
        );

    } catch (err) {
        Common.showToast('Failed to generate inventory report: ' + err.message, 'error');
    }
}

async function loadVendorReport() {
    try {
        const res = await API.get('/reports/vendors');
        const data = res.data;

        const totalSpend = (data.records || []).reduce((acc, curr) => acc + (curr.totalSpend || 0), 0);

        document.getElementById('report-stat-1-label').innerText = 'Total Vendors';
        document.getElementById('report-stat-1-val').innerText = data.totalVendors || 0;

        document.getElementById('report-stat-2-label').innerText = 'Total Spend Across Vendors';
        document.getElementById('report-stat-2-val').innerText = Common.formatCurrency(totalSpend);

        document.getElementById('report-stat-3-label').innerText = 'Active Vendors';
        document.getElementById('report-stat-3-val').innerText = (data.records || []).filter(v => v.status === 'ACTIVE').length;

        const thead = document.getElementById('report-table-thead');
        thead.innerHTML = `
            <tr>
                <th>Company Name</th>
                <th>Contact</th>
                <th>Email / Phone</th>
                <th>City</th>
                <th>Total Orders</th>
                <th>Total Spend</th>
                <th>Status</th>
            </tr>
        `;

        const tbody = document.getElementById('report-table-tbody');
        tbody.innerHTML = (data.records || []).map(r => `
            <tr>
                <td style="font-weight: 600;">${r.companyName}</td>
                <td>${r.contactPerson}</td>
                <td>${r.email}<br><small style="color: var(--slate-400);">${r.phone}</small></td>
                <td>${r.city || '-'}</td>
                <td><strong>${r.totalOrders}</strong> (${r.completedOrders} delivered)</td>
                <td style="font-weight: 700; color: var(--navy-900);">${Common.formatCurrency(r.totalSpend)}</td>
                <td>${Common.getStatusBadge(r.status)}</td>
            </tr>
        `).join('');

        renderReportChart(
            'pie',
            (data.records || []).map(r => r.companyName),
            (data.records || []).map(r => r.totalSpend),
            'Spend Breakdown by Vendor ($)'
        );

    } catch (err) {
        Common.showToast('Failed to generate vendor performance report: ' + err.message, 'error');
    }
}

async function loadPurchaseOrderReport() {
    const startDate = document.getElementById('report-start-date')?.value || '';
    const endDate = document.getElementById('report-end-date')?.value || '';

    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);

    try {
        const res = await API.get(`/reports/purchase-orders?${params.toString()}`);
        const data = res.data;

        document.getElementById('report-stat-1-label').innerText = 'Total Orders';
        document.getElementById('report-stat-1-val').innerText = data.totalOrders || 0;

        document.getElementById('report-stat-2-label').innerText = 'Period Spend';
        document.getElementById('report-stat-2-val').innerText = Common.formatCurrency(data.totalAmount);

        document.getElementById('report-stat-3-label').innerText = 'Average Order Value';
        const avg = data.totalOrders > 0 ? (data.totalAmount / data.totalOrders) : 0;
        document.getElementById('report-stat-3-val').innerText = Common.formatCurrency(avg);

        const thead = document.getElementById('report-table-thead');
        thead.innerHTML = `
            <tr>
                <th>Order Number</th>
                <th>Vendor</th>
                <th>Order Date</th>
                <th>Delivery Date</th>
                <th>Subtotal</th>
                <th>Tax</th>
                <th>Total Amount</th>
                <th>Status</th>
            </tr>
        `;

        const tbody = document.getElementById('report-table-tbody');
        tbody.innerHTML = (data.records || []).map(r => `
            <tr>
                <td style="font-weight: 600;">${r.orderNumber}</td>
                <td>${r.vendor}</td>
                <td>${Common.formatDate(r.orderDate)}</td>
                <td>${Common.formatDate(r.expectedDeliveryDate)}</td>
                <td>${Common.formatCurrency(r.subtotal)}</td>
                <td>${Common.formatCurrency(r.taxAmount)}</td>
                <td style="font-weight: 700;">${Common.formatCurrency(r.totalAmount)}</td>
                <td>${Common.getStatusBadge(r.status)}</td>
            </tr>
        `).join('');

        renderReportChart(
            'bar',
            (data.records || []).slice(0, 10).map(r => r.orderNumber),
            (data.records || []).slice(0, 10).map(r => r.totalAmount),
            'Recent Purchase Order Totals ($)'
        );

    } catch (err) {
        Common.showToast('Failed to generate orders report: ' + err.message, 'error');
    }
}

async function loadLowStockReport() {
    try {
        const res = await API.get('/reports/low-stock');
        const data = res.data;

        document.getElementById('report-stat-1-label').innerText = 'Deficit Products';
        document.getElementById('report-stat-1-val').innerText = data.count || 0;

        const totalDeficit = (data.records || []).reduce((acc, curr) => acc + (curr.deficit || 0), 0);
        document.getElementById('report-stat-2-label').innerText = 'Total Units Deficit';
        document.getElementById('report-stat-2-val').innerText = totalDeficit;

        document.getElementById('report-stat-3-label').innerText = 'Restock Severity';
        document.getElementById('report-stat-3-val').innerText = data.count > 0 ? 'Action Required' : 'Optimal';

        const thead = document.getElementById('report-table-thead');
        thead.innerHTML = `
            <tr>
                <th>Product Name</th>
                <th>SKU</th>
                <th>Category</th>
                <th>Current Available</th>
                <th>Minimum Required</th>
                <th>Replenishment Deficit</th>
                <th>Unit Cost</th>
            </tr>
        `;

        const tbody = document.getElementById('report-table-tbody');
        tbody.innerHTML = (data.records || []).map(r => `
            <tr style="background-color: #fef2f2;">
                <td style="font-weight: 600;">${r.productName}</td>
                <td><code>${r.sku}</code></td>
                <td>${r.category}</td>
                <td style="font-weight: 700; color: var(--danger);">${r.availableQuantity}</td>
                <td>${r.minimumStockLevel}</td>
                <td style="font-weight: 700; color: var(--danger);">-${r.deficit} units</td>
                <td>${Common.formatCurrency(r.unitPrice)}</td>
            </tr>
        `).join('');

        renderReportChart(
            'bar',
            (data.records || []).map(r => r.productName),
            (data.records || []).map(r => r.deficit),
            'Units Below Minimum Threshold'
        );

    } catch (err) {
        Common.showToast('Failed to generate low stock report: ' + err.message, 'error');
    }
}

function renderReportChart(type, labels, values, datasetLabel) {
    const ctx = document.getElementById('reportChart');
    if (!ctx) return;

    if (reportChartInstance) reportChartInstance.destroy();

    reportChartInstance = new Chart(ctx, {
        type: type,
        data: {
            labels: labels,
            datasets: [{
                label: datasetLabel,
                data: values,
                backgroundColor: [
                    '#2563eb', '#10b981', '#f59e0b', '#8b5cf6', '#06b6d4', '#ec4899', '#64748b'
                ],
                borderRadius: type === 'bar' ? 6 : 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: type === 'pie' ? 'right' : 'top' }
            }
        }
    });
}

async function exportCurrentReportCsv() {
    try {
        const token = localStorage.getItem('supplysync_token');
        const response = await fetch(`${API_BASE_URL}/reports/export/${activeReportTab}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error('CSV generation failed');

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `supplysync_${activeReportTab}_report_${new Date().toISOString().slice(0, 10)}.csv`;
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);

        Common.showToast('Report CSV exported successfully!', 'success');
    } catch (err) {
        Common.showToast('Failed to download CSV: ' + err.message, 'error');
    }
}
