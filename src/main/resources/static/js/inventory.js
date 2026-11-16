/**
 * SupplySync Enterprise - inventory.js
 * Stock Monitoring, Adjustments, Min/Max Thresholds & Alerts
 */

let allInventory = [];

async function initInventoryPage() {
    await loadCategoriesForFilter();
    await loadInventory();
}

async function loadCategoriesForFilter() {
    try {
        const res = await API.get('/categories');
        if (res && res.success) {
            const select = document.getElementById('category-filter');
            if (select) {
                select.innerHTML = '<option value="">All Categories</option>' +
                    (res.data || []).map(c => `<option value="${c.id}">${c.name}</option>`).join('');
            }
        }
    } catch (err) {
        console.error('Failed to load categories:', err);
    }
}

async function loadInventory() {
    const search = document.getElementById('search-input')?.value || '';
    const categoryId = document.getElementById('category-filter')?.value || '';
    const lowStockOnly = document.getElementById('low-stock-filter')?.checked || false;

    const params = new URLSearchParams();
    if (search) params.append('search', search);
    if (categoryId) params.append('categoryId', categoryId);
    if (lowStockOnly) params.append('lowStockOnly', 'true');

    try {
        const res = await API.get(`/inventory?${params.toString()}`);
        if (res && res.success) {
            allInventory = res.data || [];
            renderInventoryTable(allInventory);
            updateSummaryCounters(allInventory);
        }
    } catch (err) {
        Common.showToast('Failed to load inventory: ' + err.message, 'error');
    }
}

function updateSummaryCounters(items) {
    const totalUnits = items.reduce((acc, curr) => acc + (curr.availableQuantity || 0), 0);
    const lowCount = items.filter(i => i.lowStock).length;
    const totalVal = items.reduce((acc, curr) => acc + (curr.totalValue || 0), 0);

    const totalEl = document.getElementById('summary-total-units');
    const lowEl = document.getElementById('summary-low-stock');
    const valEl = document.getElementById('summary-total-val');

    if (totalEl) totalEl.innerText = totalUnits.toLocaleString();
    if (lowEl) lowEl.innerText = lowCount;
    if (valEl) valEl.innerText = Common.formatCurrency(totalVal);
}

function renderInventoryTable(items) {
    const tbody = document.getElementById('inventory-tbody');
    if (!tbody) return;

    if (!items || items.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="table-empty"><i class="fa-solid fa-warehouse"></i>No inventory records match filters.</td></tr>';
        return;
    }

    tbody.innerHTML = items.map(inv => {
        const stockStatusBadge = inv.lowStock
            ? '<span class="badge badge-danger"><i class="fa-solid fa-triangle-exclamation"></i> Low Stock</span>'
            : '<span class="badge badge-success"><i class="fa-solid fa-check"></i> Healthy</span>';

        return `
            <tr style="${inv.lowStock ? 'background-color: #fef2f2;' : ''}">
                <td>
                    <div style="font-weight: 600; color: var(--navy-900);">${inv.productName}</div>
                    <div style="font-size: 0.75rem; color: var(--slate-400);">SKU: ${inv.sku}</div>
                </td>
                <td><span class="badge badge-secondary">${inv.categoryName || '-'}</span></td>
                <td>
                    <span style="font-size: 1.1rem; font-weight: 700; color: ${inv.lowStock ? 'var(--danger)' : 'var(--navy-900)'};">
                        ${inv.availableQuantity}
                    </span>
                    <span style="font-size: 0.75rem; color: var(--slate-400);"> ${inv.unitOfMeasurement || 'units'}</span>
                </td>
                <td>Min: <strong>${inv.minimumStockLevel}</strong> | Max: ${inv.maximumStockLevel}</td>
                <td>${Common.formatCurrency(inv.unitPrice)}</td>
                <td style="font-weight: 600;">${Common.formatCurrency(inv.totalValue)}</td>
                <td>${stockStatusBadge}</td>
                <td>
                    ${Auth.isAdmin() ? `
                        <div style="display: flex; gap: 0.4rem;">
                            <button class="btn btn-sm btn-primary" onclick="openAdjustModal(${inv.productId}, '${inv.productName.replace(/'/g, "\\'")}', ${inv.availableQuantity})" title="Adjust Stock">
                                <i class="fa-solid fa-arrows-rotate"></i> Adjust
                            </button>
                            <button class="btn btn-sm btn-secondary" onclick="openThresholdModal(${inv.id}, '${inv.productName.replace(/'/g, "\\'")}', ${inv.minimumStockLevel}, ${inv.maximumStockLevel})" title="Edit Limits">
                                <i class="fa-solid fa-sliders"></i>
                            </button>
                        </div>
                    ` : '-'}
                </td>
            </tr>
        `;
    }).join('');
}

function openAdjustModal(productId, productName, currentStock) {
    document.getElementById('adjust-product-id').value = productId;
    document.getElementById('adjust-product-name').innerText = productName;
    document.getElementById('adjust-current-stock').innerText = currentStock;
    document.getElementById('adjust-form').reset();
    document.getElementById('adjust-product-id').value = productId; // Reset keeps product id
    Common.openModal('adjust-stock-modal');
}

async function handleAdjustSubmit(e) {
    e.preventDefault();

    const productId = parseInt(document.getElementById('adjust-product-id').value, 10);
    const transactionType = document.getElementById('adjust-type').value;
    const quantity = parseInt(document.getElementById('adjust-quantity').value, 10);
    const reason = document.getElementById('adjust-reason').value.trim();

    if (quantity <= 0) {
        Common.showToast('Quantity must be greater than zero', 'error');
        return;
    }

    try {
        await API.post('/inventory/adjust', {
            productId,
            transactionType,
            quantity,
            description: reason
        });

        Common.showToast('Stock adjusted successfully!', 'success');
        Common.closeModal('adjust-stock-modal');
        await loadInventory();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}

function openThresholdModal(inventoryId, productName, minLevel, maxLevel) {
    document.getElementById('threshold-inv-id').value = inventoryId;
    document.getElementById('threshold-product-name').innerText = productName;
    document.getElementById('threshold-min').value = minLevel;
    document.getElementById('threshold-max').value = maxLevel;
    Common.openModal('threshold-modal');
}

async function handleThresholdSubmit(e) {
    e.preventDefault();

    const id = document.getElementById('threshold-inv-id').value;
    const minLevel = parseInt(document.getElementById('threshold-min').value, 10);
    const maxLevel = parseInt(document.getElementById('threshold-max').value, 10);

    if (minLevel < 0 || maxLevel <= minLevel) {
        Common.showToast('Max level must be greater than Min level', 'error');
        return;
    }

    try {
        await API.patch(`/inventory/${id}/thresholds`, {
            minimumStockLevel: minLevel,
            maximumStockLevel: maxLevel
        });

        Common.showToast('Stock thresholds updated!', 'success');
        Common.closeModal('threshold-modal');
        await loadInventory();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}
