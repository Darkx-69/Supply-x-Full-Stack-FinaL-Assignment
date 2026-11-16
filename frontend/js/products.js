/**
 * SupplySync Enterprise - products.js
 * Product Catalog CRUD, Category Associations, Pricing & Stock Status
 */

let allProducts = [];
let categoryOptions = [];

async function initProductsPage() {
    await loadCategoryFilterOptions();
    await loadProducts();
}

async function loadCategoryFilterOptions() {
    try {
        const res = await API.get('/categories');
        if (res && res.success) {
            categoryOptions = res.data || [];
            const filterSelect = document.getElementById('category-filter');
            const modalSelect = document.getElementById('product-category');

            const optionsHtml = categoryOptions.map(c => `<option value="${c.id}">${c.name}</option>`).join('');

            if (filterSelect) {
                filterSelect.innerHTML = '<option value="">All Categories</option>' + optionsHtml;
            }
            if (modalSelect) {
                modalSelect.innerHTML = '<option value="">Select Category...</option>' + optionsHtml;
            }
        }
    } catch (err) {
        console.error('Failed to load categories for dropdown:', err);
    }
}

async function loadProducts() {
    const search = document.getElementById('search-input')?.value || '';
    const categoryId = document.getElementById('category-filter')?.value || '';
    const status = document.getElementById('status-filter')?.value || '';

    const params = new URLSearchParams();
    if (search) params.append('search', search);
    if (categoryId) params.append('categoryId', categoryId);
    if (status) params.append('status', status);

    try {
        const res = await API.get(`/products?${params.toString()}`);
        if (res && res.success) {
            allProducts = res.data || [];
            renderProductTable(allProducts);
        }
    } catch (err) {
        Common.showToast('Failed to load products: ' + err.message, 'error');
    }
}

function renderProductTable(products) {
    const tbody = document.getElementById('products-tbody');
    if (!tbody) return;

    if (!products || products.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="table-empty"><i class="fa-solid fa-box-open"></i>No products found.</td></tr>';
        return;
    }

    tbody.innerHTML = products.map(p => `
        <tr>
            <td>
                <div style="font-weight: 600; color: var(--navy-900);">${p.name}</div>
                <div style="font-size: 0.75rem; color: var(--slate-400);">${p.description ? p.description.substring(0, 45) + '...' : '-'}</div>
            </td>
            <td><code>${p.sku}</code></td>
            <td><span class="badge badge-secondary">${p.categoryName || '-'}</span></td>
            <td style="font-weight: 600;">${Common.formatCurrency(p.unitPrice)}</td>
            <td>${p.unitOfMeasurement || 'Units'}</td>
            <td>${Common.getStatusBadge(p.status)}</td>
            <td>
                ${Auth.isAdmin() ? `
                    <div style="display: flex; gap: 0.4rem;">
                        <button class="btn btn-sm btn-secondary" onclick="openEditProductModal(${p.id})" title="Edit">
                            <i class="fa-solid fa-pen"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="deleteProduct(${p.id}, '${p.name.replace(/'/g, "\\'")}')" title="Delete">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </div>
                ` : '-'}
            </td>
        </tr>
    `).join('');
}

function openAddProductModal() {
    document.getElementById('product-modal-title').innerText = 'Add New Product';
    document.getElementById('product-form').reset();
    document.getElementById('product-id').value = '';
    Common.openModal('product-modal');
}

function openEditProductModal(id) {
    const p = allProducts.find(item => item.id === id);
    if (!p) return;

    document.getElementById('product-modal-title').innerText = 'Edit Product';
    document.getElementById('product-id').value = p.id;
    document.getElementById('product-name').value = p.name;
    document.getElementById('product-sku').value = p.sku;
    document.getElementById('product-category').value = p.categoryId;
    document.getElementById('product-price').value = p.unitPrice;
    document.getElementById('product-uom').value = p.unitOfMeasurement;
    document.getElementById('product-status').value = p.status;
    document.getElementById('product-description').value = p.description || '';

    Common.openModal('product-modal');
}

async function handleProductFormSubmit(e) {
    e.preventDefault();

    const id = document.getElementById('product-id').value;
    const payload = {
        name: document.getElementById('product-name').value.trim(),
        sku: document.getElementById('product-sku').value.trim().toUpperCase(),
        categoryId: parseInt(document.getElementById('product-category').value, 10),
        unitPrice: parseFloat(document.getElementById('product-price').value),
        unitOfMeasurement: document.getElementById('product-uom').value.trim(),
        status: document.getElementById('product-status').value,
        description: document.getElementById('product-description').value.trim()
    };

    try {
        if (id) {
            await API.put(`/products/${id}`, payload);
            Common.showToast('Product updated successfully!', 'success');
        } else {
            await API.post('/products', payload);
            Common.showToast('Product created successfully and added to inventory!', 'success');
        }
        Common.closeModal('product-modal');
        await loadProducts();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}

async function deleteProduct(id, name) {
    if (!confirm(`Are you sure you want to delete product "${name}"? This will also remove inventory records.`)) return;

    try {
        await API.delete(`/products/${id}`);
        Common.showToast(`Product "${name}" deleted.`, 'success');
        await loadProducts();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}
