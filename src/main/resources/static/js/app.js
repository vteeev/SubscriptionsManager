// API Configuration
const API_BASE = '/api';
let authToken = localStorage.getItem('authToken');
let currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    if (authToken) {
        showApp();
    } else {
        showAuth();
    }
    
    // Set default date to today
    const dateInput = document.getElementById('nextPaymentDate');
    if (dateInput) {
        dateInput.valueAsDate = new Date();
    }
});

// Auth Functions
function showTab(tab) {
    document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    
    document.getElementById(tab + 'Tab').classList.add('active');
    event.target.classList.add('active');
    clearAuthError();
}

function showAuth() {
    document.getElementById('authModal').classList.remove('hidden');
    document.getElementById('app').classList.add('hidden');
}

function showApp() {
    document.getElementById('authModal').classList.add('hidden');
    document.getElementById('app').classList.remove('hidden');
    document.getElementById('userEmail').textContent = currentUser.email || '';
    loadSubscriptions();
    loadMonthlyCost();
}

function clearAuthError() {
    const errorDiv = document.getElementById('authError');
    errorDiv.textContent = '';
    errorDiv.classList.remove('show');
}

function showAuthError(message) {
    const errorDiv = document.getElementById('authError');
    errorDiv.textContent = message;
    errorDiv.classList.add('show');
}

async function handleRegister(event) {
    event.preventDefault();
    clearAuthError();
    
    const email = document.getElementById('registerEmail').value;
    const password = document.getElementById('registerPassword').value;
    
    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });
        
        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Rejestracja nie powiodła się');
        }
        
        const data = await response.json();
        authToken = data.token;
        currentUser = { userId: data.userId, email: data.email };
        
        localStorage.setItem('authToken', authToken);
        localStorage.setItem('currentUser', JSON.stringify(currentUser));
        
        showApp();
    } catch (error) {
        showAuthError(error.message);
    }
}

async function handleLogin(event) {
    event.preventDefault();
    clearAuthError();
    
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;
    
    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });
        
        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Logowanie nie powiodło się');
        }
        
        const data = await response.json();
        authToken = data.token;
        currentUser = { userId: data.userId, email: data.email };
        
        localStorage.setItem('authToken', authToken);
        localStorage.setItem('currentUser', JSON.stringify(currentUser));
        
        showApp();
    } catch (error) {
        showAuthError(error.message);
    }
}

function logout() {
    authToken = null;
    currentUser = {};
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    showAuth();
}

// Subscription Functions
async function handleAddSubscription(event) {
    event.preventDefault();
    
    const subscription = {
        name: document.getElementById('name').value,
        price: parseFloat(document.getElementById('price').value),
        currency: document.getElementById('currency').value,
        billingCycle: document.getElementById('billingCycle').value,
        nextPaymentDate: document.getElementById('nextPaymentDate').value,
        autoRenewal: document.getElementById('autoRenewal').checked
    };
    
    try {
        const response = await fetch(`${API_BASE}/subscriptions`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify(subscription)
        });
        
        if (!response.ok) {
            throw new Error('Nie udało się dodać subskrypcji');
        }
        
        // Reset form
        event.target.reset();
        document.getElementById('nextPaymentDate').valueAsDate = new Date();
        
        // Reload subscriptions
        loadSubscriptions();
        loadMonthlyCost();
        
        showSuccess('Subskrypcja dodana pomyślnie!');
    } catch (error) {
        alert('Błąd: ' + error.message);
    }
}

async function loadSubscriptions() {
    const listDiv = document.getElementById('subscriptionsList');
    listDiv.innerHTML = '<div class="loading">Ładowanie...</div>';
    
    try {
        const response = await fetch(`${API_BASE}/subscriptions`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (!response.ok) throw new Error('Nie udało się załadować subskrypcji');
        
        const subscriptions = await response.json();
        displaySubscriptions(subscriptions);
    } catch (error) {
        listDiv.innerHTML = `<div class="error-message show">Błąd: ${error.message}</div>`;
    }
}

async function loadActiveSubscriptions() {
    const listDiv = document.getElementById('subscriptionsList');
    listDiv.innerHTML = '<div class="loading">Ładowanie...</div>';
    
    try {
        const response = await fetch(`${API_BASE}/subscriptions/active`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (!response.ok) throw new Error('Nie udało się załadować subskrypcji');
        
        const subscriptions = await response.json();
        displaySubscriptions(subscriptions);
    } catch (error) {
        listDiv.innerHTML = `<div class="error-message show">Błąd: ${error.message}</div>`;
    }
}

function displaySubscriptions(subscriptions) {
    const listDiv = document.getElementById('subscriptionsList');
    
    if (subscriptions.length === 0) {
        listDiv.innerHTML = '<div class="loading">Brak subskrypcji</div>';
        return;
    }
    
    listDiv.innerHTML = subscriptions.map(sub => `
        <div class="subscription-item ${sub.status === 'CANCELLED' ? 'cancelled' : ''}">
            <div class="subscription-info">
                <div class="subscription-name">${escapeHtml(sub.name)}</div>
                <div class="subscription-details">
                    <span>💰 ${sub.price.toFixed(2)} ${sub.currency}</span>
                    <span>📅 ${formatDate(sub.nextPaymentDate)}</span>
                    <span>🔄 ${sub.billingCycle}</span>
                    <span class="status-badge status-${sub.status.toLowerCase()}">${sub.status === 'ACTIVE' ? 'Aktywna' : 'Anulowana'}</span>
                </div>
            </div>
            <div class="subscription-actions">
                ${sub.status === 'ACTIVE' ? `<button onclick="cancelSubscription('${sub.subscriptionId}')" class="btn btn-danger">Anuluj</button>` : ''}
            </div>
        </div>
    `).join('');
}

async function cancelSubscription(subscriptionId) {
    if (!confirm('Czy na pewno chcesz anulować tę subskrypcję?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/subscriptions/${subscriptionId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (!response.ok) throw new Error('Nie udało się anulować subskrypcji');
        
        loadSubscriptions();
        loadMonthlyCost();
        showSuccess('Subskrypcja anulowana');
    } catch (error) {
        alert('Błąd: ' + error.message);
    }
}

async function loadMonthlyCost() {
    try {
        const response = await fetch(`${API_BASE}/subscriptions/cost/monthly`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (!response.ok) throw new Error('Nie udało się załadować kosztu');
        
        const data = await response.json();
        document.querySelector('.cost-amount').textContent = data.amount.toFixed(2);
        document.querySelector('.cost-currency').textContent = data.currency;
    } catch (error) {
        console.error('Error loading monthly cost:', error);
    }
}

// Utility Functions
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('pl-PL', { year: 'numeric', month: 'long', day: 'numeric' });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showSuccess(message) {
    // Simple success notification
    const notification = document.createElement('div');
    notification.className = 'success-message show';
    notification.textContent = message;
    notification.style.position = 'fixed';
    notification.style.top = '20px';
    notification.style.right = '20px';
    notification.style.zIndex = '1000';
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}
