/**
 * SupplySync Enterprise - deliveries.js
 * Shipment Tracking, Carrier Logistics, Status Progress Stepper & Delivery Completion
 */

let allDeliveries = [];
let pendingOrdersForDelivery = [];

async function initDeliveriesPage() {
    await loadOrdersForDeliveryModal();
    await loadDeliveries();
}

async function loadOrdersForDeliveryModal() {
    try {
        const res = await API.get('/purchase-orders');
        if (res && res.success) {
            // Find orders that don't have a completed delivery
            pendingOrdersForDelivery = (res.data || []).filter(o => o.status !== 'CANCELLED');
            const select = document.getElementById('del-po-select');
            if (select) {
                select.innerHTML = '<option value="">Select Purchase Order...</option>' +
                    pendingOrdersForDelivery.map(o => `<option value="${o.id}">${o.orderNumber} - ${o.vendorCompanyName} (${Common.formatCurrency(o.totalAmount)})</option>`).join('');
            }
        }
    } catch (err) {
        console.error('Failed to load POs for delivery modal:', err);
    }
}

async function loadDeliveries() {
    const search = document.getElementById('search-input')?.value || '';
    const status = document.getElementById('status-filter')?.value || '';

    const params = new URLSearchParams();
    if (search) params.append('search', search);
    if (status) params.append('status', status);

    try {
        const res = await API.get(`/deliveries?${params.toString()}`);
        if (res && res.success) {
            allDeliveries = res.data || [];
            renderDeliveriesTable(allDeliveries);
        }
    } catch (err) {
        Common.showToast('Failed to load deliveries: ' + err.message, 'error');
    }
}

function renderDeliveriesTable(deliveries) {
    const tbody = document.getElementById('deliveries-tbody');
    if (!tbody) return;

    if (!deliveries || deliveries.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="table-empty"><i class="fa-solid fa-truck-ramp-box"></i>No shipment records found.</td></tr>';
        return;
    }

    tbody.innerHTML = deliveries.map(d => `
        <tr>
            <td style="font-weight: 700; color: var(--navy-900);">
                <a href="javascript:void(0)" onclick="viewDeliveryDetails(${d.id})">${d.deliveryNumber}</a>
            </td>
            <td><strong>${d.purchaseOrderNumber || '-'}</strong></td>
            <td>${d.vendorCompanyName || '-'}</td>
            <td>
                <div><i class="fa-solid fa-truck" style="color: var(--slate-400); margin-right: 0.35rem;"></i>${d.carrierName || 'Standard Freight'}</div>
                <div style="font-size: 0.75rem; color: var(--slate-400);">Tracking: <code>${d.trackingNumber || 'Pending'}</code></div>
            </td>
            <td>${Common.formatDate(d.expectedDeliveryDate)}</td>
            <td>${Common.getStatusBadge(d.shipmentStatus)}</td>
            <td>
                <div style="display: flex; gap: 0.4rem;">
                    <button class="btn btn-sm btn-secondary" onclick="viewDeliveryDetails(${d.id})" title="Track Shipment">
                        <i class="fa-solid fa-location-dot"></i> Track
                    </button>
                    <button class="btn btn-sm btn-primary" onclick="openUpdateDeliveryStatusModal(${d.id}, '${d.shipmentStatus}')" title="Update Status">
                        <i class="fa-solid fa-pen-to-square"></i>
                    </button>
                    ${Auth.isAdmin() ? `
                        <button class="btn btn-sm btn-danger" onclick="deleteDelivery(${d.id}, '${d.deliveryNumber}')" title="Delete">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    ` : ''}
                </div>
            </td>
        </tr>
    `).join('');
}

function openCreateDeliveryModal() {
    document.getElementById('delivery-form').reset();
    document.getElementById('del-expected-date').valueAsDate = new Date();
    Common.openModal('create-delivery-modal');
}

async function handleDeliveryFormSubmit(e) {
    e.preventDefault();

    const purchaseOrderId = parseInt(document.getElementById('del-po-select').value, 10);
    const carrierName = document.getElementById('del-carrier').value.trim();
    const trackingNumber = document.getElementById('del-tracking').value.trim();
    const expectedDeliveryDate = document.getElementById('del-expected-date').value;
    const shipmentStatus = document.getElementById('del-status').value;
    const deliveryNotes = document.getElementById('del-notes').value.trim();

    try {
        const res = await API.post('/deliveries', {
            purchaseOrderId,
            carrierName,
            trackingNumber,
            expectedDeliveryDate,
            shipmentStatus,
            deliveryNotes
        });

        Common.showToast(`Delivery ${res.data.deliveryNumber} initialized!`, 'success');
        Common.closeModal('create-delivery-modal');
        await loadDeliveries();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}

function openUpdateDeliveryStatusModal(id, currentStatus) {
    document.getElementById('update-del-id').value = id;
    document.getElementById('update-del-status').value = currentStatus;
    Common.openModal('update-delivery-status-modal');
}

async function handleDeliveryStatusSubmit(e) {
    e.preventDefault();

    const id = document.getElementById('update-del-id').value;
    const newStatus = document.getElementById('update-del-status').value;

    try {
        await API.patch(`/deliveries/${id}/status`, { status: newStatus });
        if (newStatus === 'DELIVERED') {
            Common.showToast('Shipment marked DELIVERED! Purchase order & inventory updated.', 'success');
        } else {
            Common.showToast(`Shipment status updated to ${newStatus}`, 'success');
        }
        Common.closeModal('update-delivery-status-modal');
        await loadDeliveries();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}

async function viewDeliveryDetails(id) {
    try {
        const res = await API.get(`/deliveries/${id}`);
        const d = res.data;

        document.getElementById('track-delivery-number').innerText = d.deliveryNumber;
        document.getElementById('track-po-number').innerText = d.purchaseOrderNumber;
        document.getElementById('track-vendor').innerText = d.vendorCompanyName || '-';
        document.getElementById('track-carrier').innerText = d.carrierName || '-';
        document.getElementById('track-number').innerText = d.trackingNumber || '-';
        document.getElementById('track-expected').innerText = Common.formatDate(d.expectedDeliveryDate);
        document.getElementById('track-status-badge').innerHTML = Common.getStatusBadge(d.shipmentStatus);
        document.getElementById('track-recipient').innerText = d.recipientName || 'Warehouse Receiving';
        document.getElementById('track-notes').innerText = d.deliveryNotes || 'None';

        // Render Stepper Active States
        renderDeliveryStepper(d.shipmentStatus);

        Common.openModal('track-delivery-modal');
    } catch (err) {
        Common.showToast('Failed to load shipment details: ' + err.message, 'error');
    }
}

function renderDeliveryStepper(currentStatus) {
    const steps = ['PREPARING', 'DISPATCHED', 'IN_TRANSIT', 'OUT_FOR_DELIVERY', 'DELIVERED'];
    const currentIdx = steps.indexOf(currentStatus);

    steps.forEach((step, idx) => {
        const stepEl = document.getElementById(`step-${step.toLowerCase()}`);
        if (!stepEl) return;

        stepEl.classList.remove('completed', 'active');
        if (currentStatus === 'DELAYED') {
            if (idx === 2) stepEl.classList.add('active');
        } else if (idx < currentIdx) {
            stepEl.classList.add('completed');
        } else if (idx === currentIdx) {
            stepEl.classList.add('active');
        }
    });
}

async function deleteDelivery(id, delNumber) {
    if (!confirm(`Are you sure you want to delete delivery tracking "${delNumber}"?`)) return;

    try {
        await API.delete(`/deliveries/${id}`);
        Common.showToast(`Delivery "${delNumber}" deleted.`, 'success');
        await loadDeliveries();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}
