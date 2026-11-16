/**
 * SupplySync Enterprise - procurement.js
 * Purchase Requisitions, Multi-Item Creator, Approval Workflow & Convert to PO
 */

let allRequests = [];
let vendorList = [];
let productList = [];

async function initProcurementPage() {
    await Promise.all([
        loadVendorsForDropdown(),
        loadProductsForDropdown()
    ]);
    await loadProcurementRequests();
}

async function loadVendorsForDropdown() {
    try {
        const res = await API.get('/vendors?status=ACTIVE');
        if (res && res.success) {
            vendorList = res.data || [];
            const select = document.getElementById('pr-vendor');
            if (select) {
                const user = Auth.getUser();
                if (user && user.role === 'VENDOR' && user.vendorId) {
                    const myVendor = vendorList.find(v => v.id === user.vendorId);
                    const name = myVendor ? myVendor.companyName : (user.vendorCompanyName || 'My Vendor Company');
                    select.innerHTML = `<option value="${user.vendorId}" selected>${name}</option>`;
                    select.disabled = true;
                } else {
                    select.disabled = false;
                    select.innerHTML = '<option value="">Select Vendor...</option>' +
                        vendorList.map(v => `<option value="${v.id}">${v.companyName}</option>`).join('');
                }
            }
        }
    } catch (err) {
        console.error('Failed to load vendors for PR:', err);
    }
}

async function loadProductsForDropdown() {
    try {
        const res = await API.get('/products?status=ACTIVE');
        if (res && res.success) {
            productList = res.data || [];
        }
    } catch (err) {
        console.error('Failed to load products for PR:', err);
    }
}

async function loadProcurementRequests() {
    const search = document.getElementById('search-input')?.value || '';
    const status = document.getElementById('status-filter')?.value || '';

    const params = new URLSearchParams();
    if (search) params.append('search', search);
    if (status) params.append('status', status);

    try {
        const res = await API.get(`/procurement-requests?${params.toString()}`);
        if (res && res.success) {
            allRequests = res.data || [];
            renderRequestsTable(allRequests);
        }
    } catch (err) {
        Common.showToast('Failed to load procurement requests: ' + err.message, 'error');
    }
}

function renderRequestsTable(requests) {
    const tbody = document.getElementById('procurement-tbody');
    if (!tbody) return;

    if (!requests || requests.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="table-empty"><i class="fa-solid fa-file-invoice-dollar"></i>No procurement requests found.</td></tr>';
        return;
    }

    tbody.innerHTML = requests.map(pr => {
        const canApprove = Auth.isAdmin() && pr.status === 'PENDING';
        const canConvert = Auth.isAdmin() && pr.status === 'APPROVED';

        return `
            <tr>
                <td style="font-weight: 600; color: var(--navy-900);">${pr.requestNumber}</td>
                <td>${pr.vendorCompanyName || '-'}</td>
                <td>${pr.requestedByName || '-'}</td>
                <td>${Common.formatDate(pr.createdAt)}</td>
                <td style="font-weight: 700; color: var(--navy-900);">${Common.formatCurrency(pr.totalEstimatedCost)}</td>
                <td>${Common.getStatusBadge(pr.status)}</td>
                <td>
                    <div style="display: flex; gap: 0.4rem; align-items: center;">
                        <button class="btn btn-sm btn-secondary" onclick="viewRequestDetails(${pr.id})" title="View Details">
                            <i class="fa-solid fa-eye"></i>
                        </button>
                        ${canApprove ? `
                            <button class="btn btn-sm btn-success" onclick="updatePrStatus(${pr.id}, 'APPROVED')" title="Approve Request">
                                <i class="fa-solid fa-check"></i> Approve
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="updatePrStatus(${pr.id}, 'REJECTED')" title="Reject Request">
                                <i class="fa-solid fa-xmark"></i> Reject
                            </button>
                        ` : ''}
                        ${canConvert ? `
                            <button class="btn btn-sm btn-primary" onclick="convertPrToPo(${pr.id})" title="Generate Purchase Order">
                                <i class="fa-solid fa-file-export"></i> Convert to PO
                            </button>
                        ` : ''}
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

function openCreatePrModal() {
    document.getElementById('pr-form').reset();
    const itemsContainer = document.getElementById('pr-items-tbody');
    itemsContainer.innerHTML = '';
    addPrItemRow(); // Add first item row
    recalcPrTotal();
    Common.openModal('create-pr-modal');
}

function addPrItemRow() {
    const tbody = document.getElementById('pr-items-tbody');
    const rowIndex = tbody.children.length;

    const row = document.createElement('tr');
    row.innerHTML = `
        <td>
            <select class="form-control pr-item-product" onchange="onPrProductSelect(this)" required>
                <option value="">Select Product...</option>
                ${productList.map(p => `<option value="${p.id}" data-price="${p.unitPrice}">${p.name} (${p.sku})</option>`).join('')}
            </select>
        </td>
        <td>
            <input type="number" class="form-control pr-item-qty" min="1" value="1" oninput="recalcPrTotal()" required>
        </td>
        <td>
            <input type="number" step="0.01" class="form-control pr-item-price" min="0.01" value="0.00" oninput="recalcPrTotal()" required>
        </td>
        <td style="font-weight: 600;" class="pr-item-subtotal">$0.00</td>
        <td>
            <button type="button" class="btn btn-sm btn-danger" onclick="removePrItemRow(this)" title="Remove item">
                <i class="fa-solid fa-trash"></i>
            </button>
        </td>
    `;
    tbody.appendChild(row);
}

function removePrItemRow(btn) {
    const tbody = document.getElementById('pr-items-tbody');
    if (tbody.children.length <= 1) {
        Common.showToast('Procurement request must have at least one item.', 'warning');
        return;
    }
    btn.closest('tr').remove();
    recalcPrTotal();
}

function onPrProductSelect(select) {
    const selectedOption = select.options[select.selectedIndex];
    const price = selectedOption.getAttribute('data-price') || '0.00';
    const row = select.closest('tr');
    row.querySelector('.pr-item-price').value = price;
    recalcPrTotal();
}

function recalcPrTotal() {
    let grandTotal = 0;
    const rows = document.querySelectorAll('#pr-items-tbody tr');

    rows.forEach(r => {
        const qty = parseFloat(r.querySelector('.pr-item-qty')?.value || 0);
        const price = parseFloat(r.querySelector('.pr-item-price')?.value || 0);
        const subtotal = qty * price;
        grandTotal += subtotal;
        const subtotalEl = r.querySelector('.pr-item-subtotal');
        if (subtotalEl) subtotalEl.innerText = Common.formatCurrency(subtotal);
    });

    const totalEl = document.getElementById('pr-grand-total');
    if (totalEl) totalEl.innerText = Common.formatCurrency(grandTotal);
}

async function handlePrFormSubmit(e) {
    e.preventDefault();

    const user = Auth.getUser();
    let vendorId = parseInt(document.getElementById('pr-vendor').value, 10);
    if ((isNaN(vendorId) || !vendorId) && user && user.vendorId) {
        vendorId = user.vendorId;
    }
    const notes = document.getElementById('pr-notes').value.trim();

    const rows = document.querySelectorAll('#pr-items-tbody tr');
    const items = [];

    for (const r of rows) {
        const productId = parseInt(r.querySelector('.pr-item-product').value, 10);
        const quantity = parseInt(r.querySelector('.pr-item-qty').value, 10);
        const estimatedUnitPrice = parseFloat(r.querySelector('.pr-item-price').value);

        if (!productId || isNaN(productId)) {
            Common.showToast('Please select a product for all rows.', 'warning');
            return;
        }

        items.push({ productId, quantity, estimatedUnitPrice });
    }

    if (items.length === 0) {
        Common.showToast('At least one item is required.', 'warning');
        return;
    }

    try {
        await API.post('/procurement-requests', {
            vendorId,
            notes,
            items
        });

        Common.showToast('Procurement request submitted successfully!', 'success');
        Common.closeModal('create-pr-modal');
        await loadProcurementRequests();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}

async function updatePrStatus(id, status) {
    if (!confirm(`Are you sure you want to mark this request as ${status}?`)) return;

    try {
        await API.patch(`/procurement-requests/${id}/status`, { status });
        Common.showToast(`Request marked as ${status}!`, 'success');
        await loadProcurementRequests();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}

async function convertPrToPo(id) {
    if (!confirm('Convert this approved procurement request to an active Purchase Order?')) return;

    try {
        const res = await API.post(`/procurement-requests/${id}/convert-to-po`, {});
        Common.showToast(`Converted! Purchase Order ${res.data.orderNumber} created.`, 'success');
        await loadProcurementRequests();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}

async function viewRequestDetails(id) {
    try {
        const res = await API.get(`/procurement-requests/${id}`);
        const pr = res.data;

        document.getElementById('view-pr-number').innerText = pr.requestNumber;
        document.getElementById('view-pr-vendor').innerText = pr.vendorCompanyName || '-';
        document.getElementById('view-pr-requester').innerText = pr.requestedByName || '-';
        document.getElementById('view-pr-date').innerText = Common.formatDate(pr.createdAt);
        document.getElementById('view-pr-status').innerHTML = Common.getStatusBadge(pr.status);
        document.getElementById('view-pr-notes').innerText = pr.notes || 'None';

        const tbody = document.getElementById('view-pr-items-tbody');
        tbody.innerHTML = (pr.items || []).map(i => `
            <tr>
                <td style="font-weight: 600;">${i.productName} (${i.productSku})</td>
                <td>${i.quantity}</td>
                <td>${Common.formatCurrency(i.estimatedUnitPrice)}</td>
                <td style="font-weight: 600;">${Common.formatCurrency(i.subtotal)}</td>
            </tr>
        `).join('');

        document.getElementById('view-pr-total').innerText = Common.formatCurrency(pr.totalEstimatedCost);

        Common.openModal('view-pr-modal');
    } catch (err) {
        Common.showToast('Failed to load request details: ' + err.message, 'error');
    }
}
