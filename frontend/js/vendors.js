/**
 * SupplySync Enterprise - vendors.js
 * Vendor Directory, CRUD, Search, Filter & Order History Modal
 */

let allVendors = [];

async function loadVendors() {
    const search = document.getElementById('search-input')?.value || '';
    const status = document.getElementById('status-filter')?.value || '';
    const category = document.getElementById('category-filter')?.value || '';

    const params = new URLSearchParams();
    if (search) params.append('search', search);
    if (status) params.append('status', status);
    if (category) params.append('category', category);

    try {
        const res = await API.get(`/vendors?${params.toString()}`);
        if (res && res.success) {
            allVendors = res.data || [];
            renderVendorTable(allVendors);
        }
    } catch (err) {
        Common.showToast('Failed to load vendors: ' + err.message, 'error');
    }
}

function renderVendorTable(vendors) {
    const tbody = document.getElementById('vendors-tbody');
    if (!tbody) return;

    if (!vendors || vendors.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="table-empty"><i class="fa-solid fa-building"></i>No vendors found matching your criteria.</td></tr>';
        return;
    }

    tbody.innerHTML = vendors.map(v => `
        <tr>
            <td style="font-weight: 600; color: var(--navy-900);">${v.companyName}</td>
            <td>${v.contactPerson}</td>
            <td>
                <div><i class="fa-regular fa-envelope" style="color: var(--slate-400); margin-right: 0.35rem;"></i>${v.email}</div>
                <div style="font-size: 0.75rem; color: var(--slate-400);"><i class="fa-solid fa-phone" style="margin-right: 0.35rem;"></i>${v.phone}</div>
            </td>
            <td>${v.city || '-'}</td>
            <td><span class="badge badge-secondary">${v.businessCategory || 'General'}</span></td>
            <td>${Common.getStatusBadge(v.vendorStatus)}</td>
            <td>
                <div style="display: flex; gap: 0.4rem;">
                    <button class="btn btn-sm btn-secondary" onclick="viewVendorDetails(${v.id})" title="View Profile & Orders">
                        <i class="fa-solid fa-eye"></i>
                    </button>
                    ${Auth.isAdmin() ? `
                        <button class="btn btn-sm btn-secondary" onclick="openEditVendorModal(${v.id})" title="Edit Vendor">
                            <i class="fa-solid fa-pen"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="deleteVendor(${v.id}, '${v.companyName.replace(/'/g, "\\'")}')" title="Delete Vendor">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    ` : ''}
                </div>
            </td>
        </tr>
    `).join('');
}

function openAddVendorModal() {
    document.getElementById('vendor-modal-title').innerText = 'Add New Vendor';
    document.getElementById('vendor-form').reset();
    document.getElementById('vendor-id').value = '';
    Common.openModal('vendor-modal');
}

function openEditVendorModal(id) {
    const v = allVendors.find(item => item.id === id);
    if (!v) return;

    document.getElementById('vendor-modal-title').innerText = 'Edit Vendor';
    document.getElementById('vendor-id').value = v.id;
    document.getElementById('company-name').value = v.companyName;
    document.getElementById('contact-person').value = v.contactPerson;
    document.getElementById('email').value = v.email;
    document.getElementById('phone').value = v.phone;
    document.getElementById('address').value = v.address || '';
    document.getElementById('city').value = v.city || '';
    document.getElementById('business-category').value = v.businessCategory || '';
    document.getElementById('vendor-status').value = v.vendorStatus;

    Common.openModal('vendor-modal');
}

async function handleVendorFormSubmit(e) {
    e.preventDefault();

    const id = document.getElementById('vendor-id').value;
    const payload = {
        companyName: document.getElementById('company-name').value.trim(),
        contactPerson: document.getElementById('contact-person').value.trim(),
        email: document.getElementById('email').value.trim(),
        phone: document.getElementById('phone').value.trim(),
        address: document.getElementById('address').value.trim(),
        city: document.getElementById('city').value.trim(),
        businessCategory: document.getElementById('business-category').value.trim(),
        vendorStatus: document.getElementById('vendor-status').value
    };

    try {
        if (id) {
            await API.put(`/vendors/${id}`, payload);
            Common.showToast('Vendor updated successfully!', 'success');
        } else {
            await API.post('/vendors', payload);
            Common.showToast('Vendor registered successfully!', 'success');
        }
        Common.closeModal('vendor-modal');
        await loadVendors();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}

async function deleteVendor(id, name) {
    if (!confirm(`Are you sure you want to delete vendor "${name}"? This action cannot be undone.`)) {
        return;
    }

    try {
        await API.delete(`/vendors/${id}`);
        Common.showToast(`Vendor "${name}" deleted.`, 'success');
        await loadVendors();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}

async function viewVendorDetails(id) {
    try {
        const [vendorRes, ordersRes] = await Promise.all([
            API.get(`/vendors/${id}`),
            API.get(`/vendors/${id}/orders`)
        ]);

        const v = vendorRes.data;
        const orders = ordersRes.data || [];

        document.getElementById('view-company').innerText = v.companyName;
        document.getElementById('view-contact').innerText = v.contactPerson;
        document.getElementById('view-email').innerText = v.email;
        document.getElementById('view-phone').innerText = v.phone;
        document.getElementById('view-address').innerText = `${v.address || ''}, ${v.city || ''}`;
        document.getElementById('view-category').innerText = v.businessCategory || '-';
        document.getElementById('view-status').innerHTML = Common.getStatusBadge(v.vendorStatus);

        const ordersTbody = document.getElementById('vendor-orders-tbody');
        if (orders.length === 0) {
            ordersTbody.innerHTML = '<tr><td colspan="5" class="table-empty">No purchase orders found for this vendor.</td></tr>';
        } else {
            ordersTbody.innerHTML = orders.map(o => `
                <tr>
                    <td style="font-weight: 600;">${o.orderNumber}</td>
                    <td>${Common.formatDate(o.orderDate)}</td>
                    <td>${Common.formatCurrency(o.totalAmount)}</td>
                    <td>${Common.getStatusBadge(o.status)}</td>
                </tr>
            `).join('');
        }

        Common.openModal('vendor-details-modal');
    } catch (err) {
        Common.showToast('Failed to load vendor details: ' + err.message, 'error');
    }
}
