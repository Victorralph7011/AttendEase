/**
 * Helper Functions for AttendEase
 * Utility functions used across the application
 */

// ============================================
// Date and Time Helpers
// ============================================

/**
 * Format date to readable string
 * @param {Date|string} date - Date to format
 * @param {string} format - Format type (default: 'DD MMM YYYY')
 * @returns {string} Formatted date string
 */
function formatDate(date, format = 'DD MMM YYYY') {
    if (!date) return '';
    
    const d = new Date(date);
    if (isNaN(d.getTime())) return '';
    
    const day = d.getDate();
    const month = d.getMonth();
    const year = d.getFullYear();
    const hours = d.getHours();
    const minutes = d.getMinutes();
    
    const pad = (n) => n.toString().padStart(2, '0');
    
    switch (format) {
        case 'DD MMM YYYY':
            return `${pad(day)} ${MONTHS[month].substring(0, 3)} ${year}`;
        case 'YYYY-MM-DD':
            return `${year}-${pad(month + 1)}-${pad(day)}`;
        case 'DD/MM/YYYY':
            return `${pad(day)}/${pad(month + 1)}/${year}`;
        case 'DD MMM YYYY, HH:mm':
            return `${pad(day)} ${MONTHS[month].substring(0, 3)} ${year}, ${pad(hours)}:${pad(minutes)}`;
        default:
            return d.toLocaleDateString();
    }
}

/**
 * Get current date in specified format
 * @param {string} format - Format type
 * @returns {string} Formatted current date
 */
function getCurrentDate(format = 'DD MMM YYYY') {
    return formatDate(new Date(), format);
}

/**
 * Calculate days between two dates
 * @param {Date} date1 - First date
 * @param {Date} date2 - Second date
 * @returns {number} Number of days
 */
function daysBetween(date1, date2) {
    const oneDay = 24 * 60 * 60 * 1000;
    return Math.round(Math.abs((date1 - date2) / oneDay));
}

/**
 * Get relative time (e.g., "2 hours ago")
 * @param {Date|string} date - Date to compare
 * @returns {string} Relative time string
 */
function getRelativeTime(date) {
    const now = new Date();
    const past = new Date(date);
    const diffMs = now - past;
    const diffSecs = Math.floor(diffMs / 1000);
    const diffMins = Math.floor(diffSecs / 60);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);
    
    if (diffSecs < 60) return 'Just now';
    if (diffMins < 60) return `${diffMins} minute${diffMins > 1 ? 's' : ''} ago`;
    if (diffHours < 24) return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
    if (diffDays < 7) return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;
    return formatDate(date);
}

// ============================================
// Number Formatting Helpers
// ============================================

/**
 * Format number to percentage
 * @param {number} value - Value to format
 * @param {number} decimals - Number of decimal places
 * @returns {string} Formatted percentage
 */
function formatPercentage(value, decimals = 2) {
    if (value === null || value === undefined || isNaN(value)) return '0.00%';
    return `${parseFloat(value).toFixed(decimals)}%`;
}

/**
 * Format number with commas
 * @param {number} value - Number to format
 * @returns {string} Formatted number
 */
function formatNumber(value) {
    if (value === null || value === undefined || isNaN(value)) return '0';
    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

/**
 * Round number to specified decimal places
 * @param {number} value - Number to round
 * @param {number} decimals - Decimal places
 * @returns {number} Rounded number
 */
function roundNumber(value, decimals = 2) {
    return Math.round(value * Math.pow(10, decimals)) / Math.pow(10, decimals);
}

// ============================================
// String Helpers
// ============================================

/**
 * Capitalize first letter of string
 * @param {string} str - String to capitalize
 * @returns {string} Capitalized string
 */
function capitalize(str) {
    if (!str) return '';
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

/**
 * Truncate string to specified length
 * @param {string} str - String to truncate
 * @param {number} length - Max length
 * @returns {string} Truncated string
 */
function truncate(str, length = 50) {
    if (!str || str.length <= length) return str;
    return str.substring(0, length) + '...';
}

/**
 * Convert string to title case
 * @param {string} str - String to convert
 * @returns {string} Title case string
 */
function toTitleCase(str) {
    if (!str) return '';
    return str.replace(/\w\S*/g, (txt) => {
        return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
    });
}

// ============================================
// Alert and Notification Helpers
// ============================================

/**
 * Show alert message
 * @param {string} message - Alert message
 * @param {string} type - Alert type (success, error, warning, info)
 * @param {number} duration - Duration in milliseconds
 */
function showAlert(message, type = 'info', duration = 5000) {
    // Create alert element
    const alert = document.createElement('div');
    alert.className = `alert alert-${type} alert-dismissible`;
    alert.innerHTML = `
        <div class="alert-content">
            <i class="fas fa-${getAlertIcon(type)}"></i>
            <span>${message}</span>
        </div>
        <button class="alert-close" onclick="this.parentElement.remove()">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    // Add to page
    let alertContainer = document.getElementById('alertContainer');
    if (!alertContainer) {
        alertContainer = document.createElement('div');
        alertContainer.id = 'alertContainer';
        alertContainer.style.cssText = 'position: fixed; top: 20px; right: 20px; z-index: 9999;';
        document.body.appendChild(alertContainer);
    }
    
    alertContainer.appendChild(alert);
    
    // Auto dismiss
    if (duration > 0) {
        setTimeout(() => {
            if (alert && alert.parentElement) {
                alert.style.animation = 'slideOut 0.3s ease-out';
                setTimeout(() => alert.remove(), 300);
            }
        }, duration);
    }
}

/**
 * Get alert icon based on type
 * @param {string} type - Alert type
 * @returns {string} Icon class
 */
function getAlertIcon(type) {
    const icons = {
        success: 'check-circle',
        error: 'exclamation-circle',
        warning: 'exclamation-triangle',
        info: 'info-circle'
    };
    return icons[type] || 'info-circle';
}

/**
 * Show confirmation dialog
 * @param {string} message - Confirmation message
 * @returns {Promise<boolean>} User's choice
 */
async function confirmDialog(message) {
    return new Promise((resolve) => {
        const confirmed = confirm(message);
        resolve(confirmed);
    });
}

// ============================================
// Loading Helpers
// ============================================

/**
 * Show loading spinner
 * @param {string} elementId - Element ID to show spinner in
 */
function showLoading(elementId) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    element.innerHTML = `
        <div class="loading-spinner">
            <div class="spinner"></div>
            <p>Loading...</p>
        </div>
    `;
}

/**
 * Hide loading spinner
 * @param {string} elementId - Element ID
 */
function hideLoading(elementId) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    const spinner = element.querySelector('.loading-spinner');
    if (spinner) {
        spinner.remove();
    }
}

// ============================================
// Grade and Performance Helpers
// ============================================

/**
 * Calculate grade from percentage
 * @param {number} percentage - Percentage value
 * @returns {string} Grade letter
 */
function calculateGrade(percentage) {
    if (percentage >= 90) return 'O';
    if (percentage >= 80) return 'A+';
    if (percentage >= 70) return 'A';
    if (percentage >= 60) return 'B+';
    if (percentage >= 50) return 'B';
    if (percentage >= 40) return 'C';
    return 'F';
}

/**
 * Get grade color
 * @param {string} grade - Grade letter
 * @returns {string} Color hex code
 */
function getGradeColor(grade) {
    const colors = {
        'O': '#28a745',
        'A+': '#5cb85c',
        'A': '#5bc0de',
        'B+': '#f0ad4e',
        'B': '#f0ad4e',
        'C': '#ff9800',
        'F': '#d9534f'
    };
    return colors[grade] || '#999';
}

/**
 * Get attendance status color
 * @param {string} status - Attendance status
 * @returns {string} Color hex code
 */
function getAttendanceColor(status) {
    const colors = {
        'PRESENT': '#28a745',
        'ABSENT': '#dc3545',
        'LATE': '#ffc107',
        'EXCUSED': '#17a2b8'
    };
    return colors[status] || '#999';
}

/**
 * Check if attendance is below threshold
 * @param {number} percentage - Attendance percentage
 * @returns {boolean} True if below threshold
 */
function isBelowAttendanceThreshold(percentage) {
    return percentage < ATTENDANCE_THRESHOLD.MINIMUM;
}

// ============================================
// Data Validation Helpers
// ============================================

/**
 * Validate email format
 * @param {string} email - Email to validate
 * @returns {boolean} True if valid
 */
function isValidEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

/**
 * Validate teacher email format
 * @param {string} email - Email to validate
 * @returns {boolean} True if valid
 */
function isValidTeacherEmail(email) {
    const regex = /^[a-z]{2}\d{4}@srmist\.edu\.in$/;
    return regex.test(email);
}

/**
 * Validate phone number
 * @param {string} phone - Phone number to validate
 * @returns {boolean} True if valid
 */
function isValidPhone(phone) {
    const regex = /^[6-9]\d{9}$/;
    return regex.test(phone);
}

/**
 * Validate marks (must be between 0 and max)
 * @param {number} marks - Marks obtained
 * @param {number} maxMarks - Maximum marks
 * @returns {boolean} True if valid
 */
function isValidMarks(marks, maxMarks) {
    return marks >= 0 && marks <= maxMarks;
}

// ============================================
// DOM Helpers
// ============================================

/**
 * Create element with attributes
 * @param {string} tag - HTML tag
 * @param {Object} attributes - Element attributes
 * @param {string} content - Inner content
 * @returns {HTMLElement} Created element
 */
function createElement(tag, attributes = {}, content = '') {
    const element = document.createElement(tag);
    
    Object.keys(attributes).forEach(key => {
        if (key === 'className') {
            element.className = attributes[key];
        } else if (key === 'innerHTML') {
            element.innerHTML = attributes[key];
        } else {
            element.setAttribute(key, attributes[key]);
        }
    });
    
    if (content) {
        element.textContent = content;
    }
    
    return element;
}

/**
 * Toggle element visibility
 * @param {string} elementId - Element ID
 * @param {boolean} show - Show or hide
 */
function toggleElement(elementId, show) {
    const element = document.getElementById(elementId);
    if (element) {
        element.style.display = show ? 'block' : 'none';
    }
}

// ============================================
// Storage Helpers
// ============================================

/**
 * Save to local storage
 * @param {string} key - Storage key
 * @param {*} value - Value to store
 */
function saveToStorage(key, value) {
    try {
        localStorage.setItem(key, JSON.stringify(value));
    } catch (error) {
        console.error('Error saving to storage:', error);
    }
}

/**
 * Get from local storage
 * @param {string} key - Storage key
 * @returns {*} Stored value
 */
function getFromStorage(key) {
    try {
        const item = localStorage.getItem(key);
        return item ? JSON.parse(item) : null;
    } catch (error) {
        console.error('Error getting from storage:', error);
        return null;
    }
}

/**
 * Remove from local storage
 * @param {string} key - Storage key
 */
function removeFromStorage(key) {
    try {
        localStorage.removeItem(key);
    } catch (error) {
        console.error('Error removing from storage:', error);
    }
}

// ============================================
// Array Helpers
// ============================================

/**
 * Sort array by property
 * @param {Array} array - Array to sort
 * @param {string} property - Property to sort by
 * @param {boolean} ascending - Sort direction
 * @returns {Array} Sorted array
 */
function sortByProperty(array, property, ascending = true) {
    return array.sort((a, b) => {
        const aVal = a[property];
        const bVal = b[property];
        
        if (aVal < bVal) return ascending ? -1 : 1;
        if (aVal > bVal) return ascending ? 1 : -1;
        return 0;
    });
}

/**
 * Group array by property
 * @param {Array} array - Array to group
 * @param {string} property - Property to group by
 * @returns {Object} Grouped object
 */
function groupByProperty(array, property) {
    return array.reduce((acc, item) => {
        const key = item[property];
        if (!acc[key]) {
            acc[key] = [];
        }
        acc[key].push(item);
        return acc;
    }, {});
}

// ============================================
// Export Helpers
// ============================================

/**
 * Download data as CSV
 * @param {Array} data - Data array
 * @param {string} filename - File name
 */
function downloadCSV(data, filename) {
    if (!data || data.length === 0) {
        showAlert('No data to export', 'warning');
        return;
    }
    
    // Convert to CSV
    const headers = Object.keys(data[0]);
    const csv = [
        headers.join(','),
        ...data.map(row => headers.map(header => {
            const value = row[header];
            return typeof value === 'string' && value.includes(',') 
                ? `"${value}"` 
                : value;
        }).join(','))
    ].join('\n');
    
    // Create download
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${filename}_${getCurrentDate('YYYY-MM-DD')}.csv`;
    link.click();
    window.URL.revokeObjectURL(url);
}

/**
 * Copy text to clipboard
 * @param {string} text - Text to copy
 */
async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        showAlert('Copied to clipboard!', 'success', 2000);
    } catch (error) {
        console.error('Copy failed:', error);
        showAlert('Failed to copy', 'error');
    }
}

// ============================================
// Debounce Helper
// ============================================

/**
 * Debounce function calls
 * @param {Function} func - Function to debounce
 * @param {number} wait - Wait time in ms
 * @returns {Function} Debounced function
 */
function debounce(func, wait = 300) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}
