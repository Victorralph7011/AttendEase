/**
 * Main JavaScript File for AttendEase
 * Global initialization and common functionality
 */

// Global variables
let currentUser = null;
let notificationInterval = null;

// Initialize application on DOM load
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

/**
 * Initialize application
 */
function initializeApp() {
    // Get current user
    currentUser = getUser();
    
    // Setup global event listeners
    setupGlobalEventListeners();
    
    // Initialize tooltips
    initializeTooltips();
    
    // Check for updates periodically
    startNotificationPolling();
    
    // Setup scroll animations
    setupScrollAnimations();
    
    // Apply role-based UI
    applyRoleBasedUI();
    
    console.log('AttendEase initialized successfully');
}

/**
 * Setup global event listeners
 */
function setupGlobalEventListeners() {
    // Mobile menu toggle
    const mobileMenuToggle = document.getElementById('mobileMenuToggle');
    const navMenu = document.getElementById('navMenu');
    
    if (mobileMenuToggle && navMenu) {
        mobileMenuToggle.addEventListener('click', function() {
            navMenu.classList.toggle('show');
            
            // Change icon
            const icon = this.querySelector('i');
            if (icon) {
                icon.classList.toggle('fa-bars');
                icon.classList.toggle('fa-times');
            }
        });
    }
    
    // Close mobile menu when clicking outside
    document.addEventListener('click', function(event) {
        if (navMenu && !event.target.closest('.nav-container')) {
            navMenu.classList.remove('show');
            const icon = mobileMenuToggle?.querySelector('i');
            if (icon) {
                icon.classList.remove('fa-times');
                icon.classList.add('fa-bars');
            }
        }
    });
    
    // Handle all external links
    document.querySelectorAll('a[href^="http"]').forEach(link => {
        link.setAttribute('target', '_blank');
        link.setAttribute('rel', 'noopener noreferrer');
    });
    
    // Form validation
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!validateForm(this)) {
                e.preventDefault();
            }
        });
    });
}

/**
 * Initialize tooltips
 */
function initializeTooltips() {
    document.querySelectorAll('[data-tooltip]').forEach(element => {
        element.addEventListener('mouseenter', function() {
            showTooltip(this, this.getAttribute('data-tooltip'));
        });
        
        element.addEventListener('mouseleave', function() {
            hideTooltip();
        });
    });
}

/**
 * Show tooltip
 */
function showTooltip(element, text) {
    const tooltip = document.createElement('div');
    tooltip.className = 'tooltip-popup';
    tooltip.textContent = text;
    tooltip.id = 'active-tooltip';
    
    document.body.appendChild(tooltip);
    
    const rect = element.getBoundingClientRect();
    tooltip.style.position = 'absolute';
    tooltip.style.left = rect.left + (rect.width / 2) - (tooltip.offsetWidth / 2) + 'px';
    tooltip.style.top = rect.top - tooltip.offsetHeight - 10 + 'px';
}

/**
 * Hide tooltip
 */
function hideTooltip() {
    const tooltip = document.getElementById('active-tooltip');
    if (tooltip) {
        tooltip.remove();
    }
}

/**
 * Start notification polling
 */
function startNotificationPolling() {
    // Poll for notifications every 30 seconds
    notificationInterval = setInterval(async () => {
        if (currentUser) {
            await checkForNewNotifications();
        }
    }, 30000);
}

/**
 * Check for new notifications
 */
async function checkForNewNotifications() {
    try {
        // Replace with actual API call
        // const response = await authenticatedFetch(API_ENDPOINTS.NOTIFICATIONS);
        // const data = await response.json();
        
        // Update notification badge
        // updateNotificationBadge(data.unreadCount);
        
    } catch (error) {
        console.error('Error checking notifications:', error);
    }
}

/**
 * Update notification badge
 */
function updateNotificationBadge(count) {
    const badge = document.getElementById('notificationBadge');
    if (badge) {
        badge.textContent = count;
        badge.style.display = count > 0 ? 'block' : 'none';
    }
}

/**
 * Setup scroll animations
 */
function setupScrollAnimations() {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
            }
        });
    }, {
        threshold: 0.1
    });
    
    document.querySelectorAll('.scroll-fade-in').forEach(element => {
        observer.observe(element);
    });
}

/**
 * Validate form
 */
function validateForm(form) {
    let isValid = true;
    
    // Clear previous errors
    form.querySelectorAll('.form-error').forEach(error => error.remove());
    
    // Check required fields
    form.querySelectorAll('[required]').forEach(field => {
        if (!field.value.trim()) {
            showFieldError(field, 'This field is required');
            isValid = false;
        }
    });
    
    // Validate email fields
    form.querySelectorAll('input[type="email"]').forEach(field => {
        if (field.value && !isValidEmail(field.value)) {
            showFieldError(field, 'Please enter a valid email address');
            isValid = false;
        }
    });
    
    // Validate phone fields
    form.querySelectorAll('input[type="tel"]').forEach(field => {
        if (field.value && !isValidPhone(field.value)) {
            showFieldError(field, 'Please enter a valid phone number');
            isValid = false;
        }
    });
    
    // Validate number fields
    form.querySelectorAll('input[type="number"]').forEach(field => {
        const min = field.getAttribute('min');
        const max = field.getAttribute('max');
        const value = parseFloat(field.value);
        
        if (min && value < parseFloat(min)) {
            showFieldError(field, `Value must be at least ${min}`);
            isValid = false;
        }
        
        if (max && value > parseFloat(max)) {
            showFieldError(field, `Value must not exceed ${max}`);
            isValid = false;
        }
    });
    
    return isValid;
}

/**
 * Show field error
 */
function showFieldError(field, message) {
    const error = document.createElement('div');
    error.className = 'form-error';
    error.textContent = message;
    
    field.classList.add('is-invalid');
    field.parentElement.appendChild(error);
}

/**
 * Clear form errors
 */
function clearFormErrors(form) {
    form.querySelectorAll('.form-error').forEach(error => error.remove());
    form.querySelectorAll('.is-invalid').forEach(field => field.classList.remove('is-invalid'));
}

/**
 * Show confirmation dialog
 */
function showConfirmDialog(message, onConfirm, onCancel) {
    const modal = document.createElement('div');
    modal.className = 'modal show';
    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">Confirm Action</h3>
                <button class="modal-close" onclick="this.closest('.modal').remove()">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <div class="modal-body">
                <p>${message}</p>
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" onclick="this.closest('.modal').remove()">
                    Cancel
                </button>
                <button class="btn btn-primary" id="confirmBtn">
                    Confirm
                </button>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    document.getElementById('confirmBtn').addEventListener('click', function() {
        if (onConfirm) onConfirm();
        modal.remove();
    });
    
    modal.querySelector('.btn-secondary').addEventListener('click', function() {
        if (onCancel) onCancel();
    });
}

/**
 * Show loading overlay
 */
function showLoadingOverlay(message = 'Loading...') {
    const overlay = document.createElement('div');
    overlay.className = 'loading-overlay';
    overlay.id = 'global-loading';
    overlay.innerHTML = `
        <div class="loading-spinner">
            <div class="spinner"></div>
            <p>${message}</p>
        </div>
    `;
    
    document.body.appendChild(overlay);
}

/**
 * Hide loading overlay
 */
function hideLoadingOverlay() {
    const overlay = document.getElementById('global-loading');
    if (overlay) {
        overlay.remove();
    }
}

/**
 * Format currency
 */
function formatCurrency(amount, currency = 'INR') {
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: currency
    }).format(amount);
}

/**
 * Generate unique ID
 */
function generateUniqueId() {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
}

/**
 * Debounced search
 */
function setupSearch(inputId, callback, delay = 300) {
    const input = document.getElementById(inputId);
    if (!input) return;
    
    const debouncedSearch = debounce(callback, delay);
    
    input.addEventListener('input', function() {
        debouncedSearch(this.value);
    });
}

/**
 * Export table to CSV
 */
function exportTableToCSV(tableId, filename) {
    const table = document.getElementById(tableId);
    if (!table) {
        showAlert('Table not found', 'error');
        return;
    }
    
    const rows = Array.from(table.querySelectorAll('tr'));
    const csv = rows.map(row => {
        const cells = Array.from(row.querySelectorAll('th, td'));
        return cells.map(cell => {
            const text = cell.textContent.trim();
            return text.includes(',') ? `"${text}"` : text;
        }).join(',');
    }).join('\n');
    
    // Create download
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${filename}_${getCurrentDate('YYYY-MM-DD')}.csv`;
    link.click();
    window.URL.revokeObjectURL(url);
    
    showAlert('Data exported successfully!', 'success');
}

/**
 * Print page section
 */
function printSection(sectionId) {
    const section = document.getElementById(sectionId);
    if (!section) {
        showAlert('Section not found', 'error');
        return;
    }
    
    const printWindow = window.open('', '_blank');
    printWindow.document.write(`
        <!DOCTYPE html>
        <html>
        <head>
            <title>Print - AttendEase</title>
            <link rel="stylesheet" href="../css/main.css">
            <link rel="stylesheet" href="../css/components.css">
            <style>
                @media print {
                    body { padding: 20px; }
                    .no-print { display: none !important; }
                }
            </style>
        </head>
        <body>
            ${section.innerHTML}
            <script>
                window.onload = function() {
                    window.print();
                    window.onafterprint = function() {
                        window.close();
                    };
                };
            </script>
        </body>
        </html>
    `);
    printWindow.document.close();
}

/**
 * Handle API errors
 */
function handleAPIError(error) {
    console.error('API Error:', error);
    
    if (error.response) {
        switch (error.response.status) {
            case 401:
                showAlert('Session expired. Please login again.', 'error');
                setTimeout(() => logout(), 2000);
                break;
            case 403:
                showAlert('You do not have permission to perform this action.', 'error');
                break;
            case 404:
                showAlert('The requested resource was not found.', 'error');
                break;
            case 500:
                showAlert('Server error. Please try again later.', 'error');
                break;
            default:
                showAlert('An error occurred. Please try again.', 'error');
        }
    } else if (error.request) {
        showAlert('Network error. Please check your connection.', 'error');
    } else {
        showAlert('An unexpected error occurred.', 'error');
    }
}

/**
 * Initialize data tables
 */
function initializeDataTable(tableId, options = {}) {
    const table = document.getElementById(tableId);
    if (!table) return;
    
    const defaultOptions = {
        searchable: true,
        sortable: true,
        pagination: true,
        pageSize: 10
    };
    
    const settings = { ...defaultOptions, ...options };
    
    // Add search box
    if (settings.searchable) {
        addTableSearch(table);
    }
    
    // Add sorting
    if (settings.sortable) {
        addTableSorting(table);
    }
    
    // Add pagination
    if (settings.pagination) {
        addTablePagination(table, settings.pageSize);
    }
}

/**
 * Add table search functionality
 */
function addTableSearch(table) {
    const searchBox = document.createElement('input');
    searchBox.type = 'text';
    searchBox.className = 'form-control table-search';
    searchBox.placeholder = 'Search...';
    
    table.parentElement.insertBefore(searchBox, table);
    
    searchBox.addEventListener('input', debounce(function() {
        const searchTerm = this.value.toLowerCase();
        const rows = table.querySelectorAll('tbody tr');
        
        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(searchTerm) ? '' : 'none';
        });
    }, 300));
}

/**
 * Add table sorting functionality
 */
function addTableSorting(table) {
    const headers = table.querySelectorAll('thead th');
    
    headers.forEach((header, index) => {
        header.style.cursor = 'pointer';
        header.innerHTML += ' <i class="fas fa-sort"></i>';
        
        header.addEventListener('click', function() {
            sortTable(table, index);
        });
    });
}

/**
 * Sort table by column
 */
function sortTable(table, columnIndex) {
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    const isAscending = table.getAttribute('data-sort-order') === 'asc';
    
    rows.sort((a, b) => {
        const aValue = a.querySelectorAll('td')[columnIndex].textContent.trim();
        const bValue = b.querySelectorAll('td')[columnIndex].textContent.trim();
        
        // Try to parse as numbers
        const aNum = parseFloat(aValue);
        const bNum = parseFloat(bValue);
        
        if (!isNaN(aNum) && !isNaN(bNum)) {
            return isAscending ? aNum - bNum : bNum - aNum;
        }
        
        // Sort as strings
        return isAscending 
            ? aValue.localeCompare(bValue)
            : bValue.localeCompare(aValue);
    });
    
    tbody.innerHTML = '';
    rows.forEach(row => tbody.appendChild(row));
    
    table.setAttribute('data-sort-order', isAscending ? 'desc' : 'asc');
}

/**
 * Add table pagination
 */
function addTablePagination(table, pageSize) {
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    const pageCount = Math.ceil(rows.length / pageSize);
    
    if (pageCount <= 1) return;
    
    // Create pagination container
    const pagination = document.createElement('div');
    pagination.className = 'pagination';
    
    for (let i = 1; i <= pageCount; i++) {
        const pageBtn = document.createElement('button');
        pageBtn.className = 'pagination-item';
        pageBtn.textContent = i;
        
        if (i === 1) pageBtn.classList.add('active');
        
        pageBtn.addEventListener('click', function() {
            showPage(table, i, pageSize);
            
            // Update active state
            pagination.querySelectorAll('.pagination-item').forEach(btn => {
                btn.classList.remove('active');
            });
            this.classList.add('active');
        });
        
        pagination.appendChild(pageBtn);
    }
    
    table.parentElement.appendChild(pagination);
    showPage(table, 1, pageSize);
}

/**
 * Show specific page in table
 */
function showPage(table, pageNumber, pageSize) {
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    
    rows.forEach((row, index) => {
        const start = (pageNumber - 1) * pageSize;
        const end = start + pageSize;
        
        row.style.display = (index >= start && index < end) ? '' : 'none';
    });
}

/**
 * Cleanup on page unload
 */
window.addEventListener('beforeunload', function() {
    if (notificationInterval) {
        clearInterval(notificationInterval);
    }
});

// Export functions for global use
window.AttendEase = {
    showAlert,
    showConfirmDialog,
    showLoadingOverlay,
    hideLoadingOverlay,
    exportTableToCSV,
    printSection,
    initializeDataTable,
    handleAPIError
};
