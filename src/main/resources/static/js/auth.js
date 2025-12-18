/**
 * Authentication utilities for FBK Balkan
 */

// Check if user is authenticated
function isAuthenticated() {
    const token = localStorage.getItem('token');
    return token !== null && token !== undefined && token !== '';
}

// Get current user data
function getCurrentUser() {
    const userStr = localStorage.getItem('user');
    if (userStr) {
        try {
            return JSON.parse(userStr);
        } catch (e) {
            return null;
        }
    }
    return null;
}

// Update navbar based on authentication state
function updateNavbar() {
    const isAuth = isAuthenticated();
    const user = getCurrentUser();
    const loginLink = document.getElementById('login-link');
    const profileDropdown = document.getElementById('profile-dropdown');
    const mobileLogout = document.getElementById('mobile-logout');
    const userName = document.getElementById('user-name');

    if (isAuth && user) {
        // Hide login link
        if (loginLink) {
            loginLink.classList.add('hidden');
        }
        
        // Show profile dropdown
        if (profileDropdown) {
            profileDropdown.classList.remove('hidden');
        }
        
        // Show mobile logout
        if (mobileLogout) {
            mobileLogout.classList.remove('hidden');
        }
        
        // Update user name
        if (userName && user.fullName) {
            userName.textContent = user.fullName;
        }
    } else {
        // Show login link
        if (loginLink) {
            loginLink.classList.remove('hidden');
        }
        
        // Hide profile dropdown
        if (profileDropdown) {
            profileDropdown.classList.add('hidden');
        }
        
        // Hide mobile logout
        if (mobileLogout) {
            mobileLogout.classList.add('hidden');
        }
    }
}

// Logout function with confirmation
async function logout(showConfirmation = true) {
    return new Promise((resolve, reject) => {
        if (showConfirmation) {
            // Show confirmation dialog
            const confirmed = confirm('Är du säker på att du vill logga ut?');
            if (!confirmed) {
                resolve(false);
                return;
            }
        }

        // Show loading state
        const loadingToast = showLoadingToast('Loggar ut...');

        // Call logout endpoint
        const token = localStorage.getItem('token');
        
        fetch('/api/auth/logout', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            // Clear local storage regardless of response
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            
            // Clear any session storage
            sessionStorage.clear();
            
            // Hide loading toast
            if (loadingToast) {
                loadingToast.remove();
            }
            
            // Show success message
            showSuccessToast('Du har loggats ut framgångsrikt');
            
            // Prevent back-button access by replacing history
            window.history.replaceState(null, null, '/login');
            
            // Redirect to login page
            setTimeout(() => {
                window.location.href = '/login';
            }, 1000);
            
            resolve(true);
        })
        .catch(error => {
            console.error('Logout error:', error);
            
            // Even if API call fails, clear local storage
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            sessionStorage.clear();
            
            // Hide loading toast
            if (loadingToast) {
                loadingToast.remove();
            }
            
            // Show error message but still redirect
            showErrorToast('Ett fel uppstod vid utloggning, men du har loggats ut lokalt');
            
            // Prevent back-button access
            window.history.replaceState(null, null, '/login');
            
            setTimeout(() => {
                window.location.href = '/login';
            }, 1500);
            
            resolve(true);
        });
    });
}

// Show loading toast
function showLoadingToast(message) {
    const toast = document.createElement('div');
    toast.className = 'toast toast-top toast-center z-50';
    toast.innerHTML = `
        <div class="alert alert-info">
            <span class="loading loading-spinner loading-sm"></span>
            <span>${message}</span>
        </div>
    `;
    document.body.appendChild(toast);
    return toast;
}

// Show success toast
function showSuccessToast(message) {
    const toast = document.createElement('div');
    toast.className = 'toast toast-top toast-center z-50';
    toast.innerHTML = `
        <div class="alert alert-success">
            <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>${message}</span>
        </div>
    `;
    document.body.appendChild(toast);
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        toast.remove();
    }, 3000);
    
    return toast;
}

// Show error toast
function showErrorToast(message) {
    const toast = document.createElement('div');
    toast.className = 'toast toast-top toast-center z-50';
    toast.innerHTML = `
        <div class="alert alert-error">
            <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>${message}</span>
        </div>
    `;
    document.body.appendChild(toast);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        toast.remove();
    }, 5000);
    
    return toast;
}

// Initialize auth on page load
document.addEventListener('DOMContentLoaded', function() {
    updateNavbar();
    
    // Add logout handlers to all logout links
    const logoutLinks = document.querySelectorAll('a[href="/logout"], #mobile-logout a, #profile-dropdown a[href="/logout"]');
    logoutLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            logout(true);
        });
    });
});

