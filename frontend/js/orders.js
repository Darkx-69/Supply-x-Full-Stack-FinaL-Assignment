/**
 * SupplySync Enterprise - orders.js
 * Purchase Order Lifecycle, Printable Documents, Tax Calculation & Status Management
 */

let allOrders = [];
let vendorList = [];
let productList = [];

async function initOrdersPage() {
    await Promise.all([
        loadVendorsForPo(),
        loadProductsForPo()
    ]);
    await loadPurchaseOrders();
}

async function loadVendorsForPo() {
    try {
        const res = await API.get('/vendors?status=ACTIVE');
        if (res && res.success) {
            vendorList = res.data || [];
            const select = document.getElementById('po-vendor');
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
        console.error('Failed to load vendors for PO:', err);
    }
}

async function loadProductsForPo() {
    try {
        const res = await API.get('/products?status=ACTIVE');
        if (res && res.success) {
            productList = res.data || [];
        }
    } catch (err) {
        console.error('Failed to load products for PO:', err);
    }
}

async function loadPurchaseOrders() {
    const search = document.getElementById('search-input')?.value || '';
    const status = document.getElementById('status-filter')?.value || '';
    const startDate = document.getElementById('start-date-filter')?.value || '';
    const endDate = document.getElementById('end-date-filter')?.value || '';

    const params = new URLSearchParams();
    if (search) params.append('search', search);
    if (status) params.append('status', status);
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);

    try {
        const res = await API.get(`/purchase-orders?${params.toString()}`);
        if (res && res.success) {
            allOrders = res.data || [];
            renderOrdersTable(allOrders);
        }
    } catch (err) {
        Common.showToast('Failed to load purchase orders: ' + err.message, 'error');
    }
}

function renderOrdersTable(orders) {
    const tbody = document.getElementById('orders-tbody');
    if (!tbody) return;

    if (!orders || orders.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="table-empty"><i class="fa-solid fa-cart-shopping"></i>No purchase orders found.</td></tr>';
        return;
    }

    tbody.innerHTML = orders.map(po => {
        const canEditStatus = Auth.isAdmin() || (Auth.isVendor() && po.status !== 'DELIVERED');

        return `
            <tr>
                <td style="font-weight: 700; color: var(--navy-900);">
                    <a href="javascript:void(0)" onclick="openPrintPoModal(${po.id})">${po.orderNumber}</a>
                </td>
                <td>${po.vendorCompanyName || '-'}</td>
                <td>${Common.formatDate(po.orderDate)}</td>
                <td>${Common.formatDate(po.expectedDeliveryDate)}</td>
                <td>${po.items ? po.items.length : 0} items</td>
                <td style="font-weight: 700; color: var(--navy-900);">${Common.formatCurrency(po.totalAmount)}</td>
                <td>${Common.getStatusBadge(po.status)}</td>
                <td>
                    <div style="display: flex; gap: 0.4rem; align-items: center;">
                        <button class="btn btn-sm btn-secondary" onclick="openPrintPoModal(${po.id})" title="View / Print PO">
                            <i class="fa-solid fa-print"></i>
                        </button>
                        ${canEditStatus ? `
                            <button class="btn btn-sm btn-primary" onclick="openUpdateStatusModal(${po.id}, '${po.status}')" title="Update Status">
                                <i class="fa-solid fa-arrow-progress"></i> Status
                            </button>
                        ` : ''}
                        ${Auth.isAdmin() && !po.inventoryReceived ? `
                            <button class="btn btn-sm btn-danger" onclick="deletePurchaseOrder(${po.id}, '${po.orderNumber}')" title="Delete">
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        ` : ''}
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

function openCreatePoModal() {
    document.getElementById('po-form').reset();
    document.getElementById('po-date').valueAsDate = new Date();
    const d = new Date();
    d.setDate(d.getDate() + 7);
    document.getElementById('po-expected-date').valueAsDate = d;

    const tbody = document.getElementById('po-items-tbody');
    tbody.innerHTML = '';
    addPoItemRow();
    recalcPoTotals();
    Common.openModal('create-po-modal');
}

function addPoItemRow() {
    const tbody = document.getElementById('po-items-tbody');
    const row = document.createElement('tr');
    row.innerHTML = `
        <td>
            <select class="form-control po-item-product" onchange="onPoProductSelect(this)" required>
                <option value="">Select Product...</option>
                ${productList.map(p => `<option value="${p.id}" data-price="${p.unitPrice}" data-uom="${p.unitOfMeasurement}">${p.name} (${p.sku})</option>`).join('')}
            </select>
        </td>
        <td>
            <input type="number" class="form-control po-item-qty" min="1" value="1" oninput="recalcPoTotals()" required>
        </td>
        <td>
            <input type="number" step="0.01" class="form-control po-item-price" min="0.01" value="0.00" oninput="recalcPoTotals()" required>
        </td>
        <td style="font-weight: 600;" class="po-item-subtotal">$0.00</td>
        <td>
            <button type="button" class="btn btn-sm btn-danger" onclick="removePoItemRow(this)" title="Remove item">
                <i class="fa-solid fa-trash"></i>
            </button>
        </td>
    `;
    tbody.appendChild(row);
}

function removePoItemRow(btn) {
    const tbody = document.getElementById('po-items-tbody');
    if (tbody.children.length <= 1) {
        Common.showToast('Purchase order must have at least one item.', 'warning');
        return;
    }
    btn.closest('tr').remove();
    recalcPoTotals();
}

function onPoProductSelect(select) {
    const selected = select.options[select.selectedIndex];
    const price = selected.getAttribute('data-price') || '0.00';
    const row = select.closest('tr');
    row.querySelector('.po-item-price').value = price;
    recalcPoTotals();
}

function recalcPoTotals() {
    let subtotal = 0;
    const rows = document.querySelectorAll('#po-items-tbody tr');

    rows.forEach(r => {
        const qty = parseFloat(r.querySelector('.po-item-qty')?.value || 0);
        const price = parseFloat(r.querySelector('.po-item-price')?.value || 0);
        const rowSub = qty * price;
        subtotal += rowSub;
        const subtotalEl = r.querySelector('.po-item-subtotal');
        if (subtotalEl) subtotalEl.innerText = Common.formatCurrency(rowSub);
    });

    const taxRate = 5.0; // 5%
    const taxAmount = (subtotal * taxRate) / 100.0;
    const grandTotal = subtotal + taxAmount;

    document.getElementById('po-summary-subtotal').innerText = Common.formatCurrency(subtotal);
    document.getElementById('po-summary-tax').innerText = Common.formatCurrency(taxAmount);
    document.getElementById('po-summary-total').innerText = Common.formatCurrency(grandTotal);
}

async function handlePoFormSubmit(e) {
    e.preventDefault();

    const user = Auth.getUser();
    let vendorId = parseInt(document.getElementById('po-vendor').value, 10);
    if ((isNaN(vendorId) || !vendorId) && user && user.vendorId) {
        vendorId = user.vendorId;
    }
    const orderDate = document.getElementById('po-date').value;
    const expectedDeliveryDate = document.getElementById('po-expected-date').value;
    const notes = document.getElementById('po-notes').value.trim();

    const rows = document.querySelectorAll('#po-items-tbody tr');
    const items = [];

    for (const r of rows) {
        const productId = parseInt(r.querySelector('.po-item-product').value, 10);
        const quantity = parseInt(r.querySelector('.po-item-qty').value, 10);
        const unitPrice = parseFloat(r.querySelector('.po-item-price').value);

        if (!productId || isNaN(productId)) {
            Common.showToast('Please select a valid product for every row.', 'warning');
            return;
        }

        items.push({ productId, quantity, unitPrice });
    }

    if (items.length === 0) {
        Common.showToast('At least one item is required.', 'warning');
        return;
    }

    try {
        const res = await API.post('/purchase-orders', {
            vendorId,
            orderDate,
            expectedDeliveryDate,
            notes,
            items,
            taxRate: 5.0
        });

        Common.showToast(`Purchase order ${res.data.orderNumber} created successfully!`, 'success');
        Common.closeModal('create-po-modal');
        await loadPurchaseOrders();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}

function openUpdateStatusModal(id, currentStatus) {
    document.getElementById('update-po-id').value = id;
    document.getElementById('update-po-status').value = currentStatus;
    Common.openModal('update-status-modal');
}

async function handleStatusUpdateSubmit(e) {
    e.preventDefault();

    const id = document.getElementById('update-po-id').value;
    const newStatus = document.getElementById('update-po-status').value;

    try {
        const res = await API.patch(`/purchase-orders/${id}/status`, { status: newStatus });
        if (newStatus === 'DELIVERED') {
            Common.showToast(`Order marked DELIVERED! Goods have been automatically received into inventory.`, 'success');
        } else {
            Common.showToast(`Purchase Order status updated to ${newStatus}`, 'success');
        }
        Common.closeModal('update-status-modal');
        await loadPurchaseOrders();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}

async function deletePurchaseOrder(id, orderNumber) {
    if (!confirm(`Are you sure you want to delete Purchase Order "${orderNumber}"?`)) return;

    try {
        await API.delete(`/purchase-orders/${id}`);
        Common.showToast(`Order "${orderNumber}" deleted.`, 'success');
        await loadPurchaseOrders();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}

async function openPrintPoModal(id) {
    try {
        const res = await API.get(`/purchase-orders/${id}`);
        const po = res.data;

        document.getElementById('print-po-number').innerText = po.orderNumber;
        document.getElementById('print-po-date').innerText = Common.formatDate(po.orderDate);
        document.getElementById('print-po-expected').innerText = Common.formatDate(po.expectedDeliveryDate);
        document.getElementById('print-po-status').innerHTML = Common.getStatusBadge(po.status);

        document.getElementById('print-vendor-company').innerText = po.vendorCompanyName || 'Vendor';
        document.getElementById('print-vendor-contact').innerText = po.vendorEmail ? `Email: ${po.vendorEmail}` : '';
        document.getElementById('print-vendor-phone').innerText = po.vendorPhone ? `Phone: ${po.vendorPhone}` : '';
        document.getElementById('print-vendor-address').innerText = po.vendorAddress || 'Address on file';

        const tbody = document.getElementById('print-po-items-tbody');
        tbody.innerHTML = (po.items || []).map((item, idx) => `
            <tr>
                <td>${idx + 1}</td>
                <td>
                    <div style="font-weight: 600;">${item.productName}</div>
                    <div style="font-size: 0.75rem; color: var(--slate-400);">SKU: ${item.productSku}</div>
                </td>
                <td>${item.quantity} ${item.unitOfMeasurement || 'units'}</td>
                <td>${Common.formatCurrency(item.unitPrice)}</td>
                <td style="font-weight: 600;">${Common.formatCurrency(item.totalPrice)}</td>
            </tr>
        `).join('');

        document.getElementById('print-po-subtotal').innerText = Common.formatCurrency(po.subtotal);
        document.getElementById('print-po-tax').innerText = Common.formatCurrency(po.taxAmount);
        document.getElementById('print-po-grand-total').innerText = Common.formatCurrency(po.totalAmount);
        document.getElementById('print-po-notes').innerText = po.notes || 'None';

        Common.openModal('print-po-modal');
    } catch (err) {
        Common.showToast('Failed to load purchase order document: ' + err.message, 'error');
    }
}
