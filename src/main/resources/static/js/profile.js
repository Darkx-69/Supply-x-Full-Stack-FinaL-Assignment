/**
 * SupplySync Enterprise - profile.js
 * User Profile Details, Contact Updates & Secure Password Change
 */

async function loadUserProfile() {
    try {
        const res = await API.get('/users/me');
        if (res && res.success) {
            const u = res.data;

            document.getElementById('profile-email').value = u.email;
            document.getElementById('profile-fullname').value = u.fullName;
            document.getElementById('profile-phone').value = u.phone || '';
            document.getElementById('profile-role').innerText = u.role;
            document.getElementById('profile-joined').innerText = Common.formatDate(u.createdAt);

            const vendorContainer = document.getElementById('profile-vendor-info');
            if (u.role === 'VENDOR' && u.vendorCompanyName) {
                if (vendorContainer) {
                    vendorContainer.style.display = 'block';
                    document.getElementById('profile-vendor-name').innerText = u.vendorCompanyName;
                }
            }

            const avatar = document.getElementById('profile-avatar');
            if (avatar && u.fullName) {
                avatar.innerText = u.fullName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
            }
        }
    } catch (err) {
        Common.showToast('Failed to load profile: ' + err.message, 'error');
    }
}

async function handleProfileUpdate(e) {
    e.preventDefault();

    const fullName = document.getElementById('profile-fullname').value.trim();
    const phone = document.getElementById('profile-phone').value.trim();

    try {
        const res = await API.put('/users/me', { fullName, phone });
        Common.showToast('Profile updated successfully!', 'success');

        // Update stored user
        const curr = Auth.getUser();
        if (curr) {
            curr.fullName = fullName;
            localStorage.setItem('supplysync_user', JSON.stringify(curr));
        }

        // Re-initialize sidebar to reflect new name
        Common.initLayout('profile', 'User Profile', 'Account');
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}

async function handlePasswordChange(e) {
    e.preventDefault();

    const currentPassword = document.getElementById('pwd-current').value;
    const newPassword = document.getElementById('pwd-new').value;
    const confirmPassword = document.getElementById('pwd-confirm').value;

    if (newPassword.length < 6) {
        Common.showToast('New password must be at least 6 characters long.', 'warning');
        return;
    }

    if (newPassword !== confirmPassword) {
        Common.showToast('New passwords do not match.', 'error');
        return;
    }

    try {
        await API.post('/users/me/change-password', { currentPassword, newPassword });
        Common.showToast('Password changed successfully!', 'success');
        document.getElementById('password-form').reset();
    } catch (err) {
        Common.showToast(err.message, 'error');
    }
}
