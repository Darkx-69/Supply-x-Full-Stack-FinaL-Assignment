/**
 * SupplySync Enterprise - transactions.js
 * Comprehensive Inventory Audit Logs, Movement Timelines & Search
 */

let allTransactions = [];

async function loadTransactions() {
    const search = document.getElementById('search-input')?.value || '';
    const type = document.getElementById('type-filter')?.value || '';

    const params = new URLSearchParams();
    if (search) params.append('search', search);
    if (type) params.append('type', type);

    try {
        const res = await API.get(`/transactions?${params.toString()}`);
        if (res && res.success) {
            allTransactions = res.data || [];
            renderTransactionsTable(allTransactions);
        }
    } catch (err) {
        Common.showToast('Failed to load transaction history: ' + err.message, 'error');
    }
}

function renderTransactionsTable(transactions) {
    const tbody = document.getElementById('transactions-tbody');
    if (!tbody) return;

    if (!transactions || transactions.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="table-empty"><i class="fa-solid fa-clock-rotate-left"></i>No inventory transactions found.</td></tr>';
        return;
    }

    tbody.innerHTML = transactions.map(t => {
        const isPositive = t.quantity > 0;
        const qtyFormatted = isPositive ? `+${t.quantity}` : `${t.quantity}`;
        const qtyColor = isPositive ? 'var(--success)' : 'var(--danger)';

        let badgeClass = 'badge-secondary';
        if (t.transactionType === 'STOCK_ADDED') badgeClass = 'badge-success';
        if (t.transactionType === 'STOCK_REMOVED') badgeClass = 'badge-danger';
        if (t.transactionType === 'PURCHASE_RECEIVED') badgeClass = 'badge-primary';
        if (t.transactionType === 'MANUAL_ADJUSTMENT') badgeClass = 'badge-warning';

        return `
            <tr>
                <td style="font-weight: 600; color: var(--slate-500);">#${t.id}</td>
                <td>
                    <div style="font-weight: 600; color: var(--navy-900);">${t.productName}</div>
                    <div style="font-size: 0.75rem; color: var(--slate-400);">SKU: ${t.productSku}</div>
                </td>
                <td><span class="badge ${badgeClass}">${t.transactionType.replace(/_/g, ' ')}</span></td>
                <td style="font-weight: 700; font-size: 1rem; color: ${qtyColor};">${qtyFormatted}</td>
                <td>${t.previousQuantity != null ? t.previousQuantity : '-'} &rarr; <strong>${t.newQuantity != null ? t.newQuantity : '-'}</strong></td>
                <td><code>${t.referenceOrder || '-'}</code></td>
                <td>${Common.formatDateTime(t.transactionDate)}</td>
                <td style="font-size: 0.8rem; color: var(--slate-600);">${t.description || '-'}</td>
            </tr>
        `;
    }).join('');
}
