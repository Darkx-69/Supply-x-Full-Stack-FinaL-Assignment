/**
 * SupplySync Enterprise - categories.js
 * Product Category CRUD, Product Counts, and Associated Products View
 */

let allCategories = [];

async function loadCategories() {
    try {
        const res = await API.get('/categories');
        if (res && res.success) {
            allCategories = res.data || [];
            renderCategoryTable(allCategories);
        }
    } catch (err) {
        Common.showToast('Failed to load categories: ' + err.message, 'error');
    }
}

function renderCategoryTable(categories) {
    const tbody = document.getElementById('categories-tbody');
    if (!tbody) return;

    if (!categories || categories.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="table-empty"><i class="fa-solid fa-tags"></i>No categories created yet.</td></tr>';
        return;
    }

    tbody.innerHTML = categories.map(c => `
        <tr>
            <td style="font-weight: 600; color: var(--navy-900);">${c.name}</td>
            <td>${c.description || '-'}</td>
            <td><span class="badge badge-primary">${c.productCount || 0} Products</span></td>
            <td>${Common.formatDate(c.createdAt)}</td>
            <td>
                <div style="display: flex; gap: 0.4rem;">
                    <button class="btn btn-sm btn-secondary" onclick="viewCategoryProducts(${c.id}, '${c.name.replace(/'/g, "\\'")}')" title="View Products">
                        <i class="fa-solid fa-boxes-stacked"></i> Products
                    </button>
                    ${Auth.isAdmin() ? `
                        <button class="btn btn-sm btn-secondary" onclick="openEditCategoryModal(${c.id})" title="Edit">
                            <i class="fa-solid fa-pen"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="deleteCategory(${c.id}, '${c.name.replace(/'/g, "\\'")}')" title="Delete">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    ` : ''}
                </div>
            </td>
        </tr>
    `).join('');
}

function openAddCategoryModal() {
    document.getElementById('category-modal-title').innerText = 'Add New Category';
    document.getElementById('category-form').reset();
    document.getElementById('category-id').value = '';
    Common.openModal('category-modal');
}

function openEditCategoryModal(id) {
    const c = allCategories.find(item => item.id === id);
    if (!c) return;

    document.getElementById('category-modal-title').innerText = 'Edit Category';
    document.getElementById('category-id').value = c.id;
    document.getElementById('category-name').value = c.name;
    document.getElementById('category-description').value = c.description || '';

    Common.openModal('category-modal');
}

async function handleCategoryFormSubmit(e) {
    e.preventDefault();

    const id = document.getElementById('category-id').value;
    const payload = {
        name: document.getElementById('category-name').value.trim(),
        description: document.getElementById('category-description').value.trim()
    };

    try {
        if (id) {
            await API.put(`/categories/${id}`, payload);
            Common.showToast('Category updated successfully!', 'success');
        } else {
            await API.post('/categories', payload);
            Common.showToast('Category created successfully!', 'success');
        }
        Common.closeModal('category-modal');
        await loadCategories();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}

async function deleteCategory(id, name) {
    if (!confirm(`Are you sure you want to delete category "${name}"?`)) return;

    try {
        await API.delete(`/categories/${id}`);
        Common.showToast(`Category "${name}" deleted.`, 'success');
        await loadCategories();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}

async function viewCategoryProducts(id, name) {
    try {
        const res = await API.get(`/categories/${id}/products`);
        const products = res.data || [];

        document.getElementById('category-products-modal-title').innerText = `Products in "${name}"`;
        const tbody = document.getElementById('category-products-tbody');

        if (products.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="table-empty">No products under this category yet.</td></tr>';
        } else {
            tbody.innerHTML = products.map(p => `
                <tr>
                    <td style="font-weight: 600;">${p.name}</td>
                    <td><code>${p.sku}</code></td>
                    <td>${Common.formatCurrency(p.unitPrice)} / ${p.unitOfMeasurement}</td>
                    <td>${Common.getStatusBadge(p.status)}</td>
                </tr>
            `).join('');
        }

        Common.openModal('category-products-modal');
    } catch (err) {
        Common.showToast('Failed to load category products: ' + err.message, 'error');
    }
}
