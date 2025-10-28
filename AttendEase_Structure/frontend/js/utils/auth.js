/**
 * Constants for AttendEase
 * Application-wide constant values
 */

// API Base URL
const API_BASE_URL = '/AttendEase';

// API Endpoints
const API_ENDPOINTS = {
    LOGIN: `${API_BASE_URL}/login`,
    LOGOUT: `${API_BASE_URL}/logout`,
    ATTENDANCE: `${API_BASE_URL}/attendance`,
    MARKS: `${API_BASE_URL}/marks`,
    REPORTS: `${API_BASE_URL}/reports`,
    USERS: `${API_BASE_URL}/users`,
    SUBJECTS: `${API_BASE_URL}/subjects`,
    NOTIFICATIONS: `${API_BASE_URL}/notifications`
};

// User Roles
const USER_ROLES = {
    TEACHER: 'TEACHER',
    STUDENT: 'STUDENT',
    ADMIN: 'ADMIN'
};

// Attendance Status
const ATTENDANCE_STATUS = {
    PRESENT: { value: 'PRESENT', label: 'Present', color: '#28a745', icon: '✓' },
    ABSENT: { value: 'ABSENT', label: 'Absent', color: '#dc3545', icon: '✗' },
    LATE: { value: 'LATE', label: 'Late', color: '#ffc107', icon: '⏰' },
    EXCUSED: { value: 'EXCUSED', label: 'Excused', color: '#17a2b8', icon: '📝' }
};

// Grade System
const GRADE_SYSTEM = {
    'O': { min: 90, max: 100, label: 'Outstanding', color: '#28a745' },
    'A+': { min: 80, max: 89, label: 'Excellent', color: '#5cb85c' },
    'A': { min: 70, max: 79, label: 'Very Good', color: '#5bc0de' },
    'B+': { min: 60, max: 69, label: 'Good', color: '#f0ad4e' },
    'B': { min: 50, max: 59, label: 'Above Average', color: '#f0ad4e' },
    'C': { min: 40, max: 49, label: 'Average', color: '#ff9800' },
    'F': { min: 0, max: 39, label: 'Fail', color: '#d9534f' }
};

// Assessment Types
const ASSESSMENT_TYPES = {
    QUIZ: { id: 1, name: 'Quiz', weightage: 10 },
    ASSIGNMENT: { id: 2, name: 'Assignment', weightage: 15 },
    MIDTERM: { id: 3, name: 'Mid-term', weightage: 25 },
    FINAL: { id: 4, name: 'Final Exam', weightage: 50 }
};

// Notification Types
const NOTIFICATION_TYPES = {
    ATTENDANCE: 'ATTENDANCE',
    MARKS: 'MARKS',
    ANNOUNCEMENT: 'ANNOUNCEMENT',
    WARNING: 'WARNING'
};

// Alert Types
const ALERT_TYPES = {
    SUCCESS: 'success',
    ERROR: 'error',
    WARNING: 'warning',
    INFO: 'info'
};

// Date Formats
const DATE_FORMATS = {
    DISPLAY: 'DD MMM YYYY',
    INPUT: 'YYYY-MM-DD',
    DATETIME: 'DD MMM YYYY, HH:mm',
    TIME: 'HH:mm'
};

// Pagination
const PAGINATION = {
    DEFAULT_PAGE_SIZE: 10,
    PAGE_SIZE_OPTIONS: [10, 25, 50, 100]
};

// Chart Colors
const CHART_COLORS = {
    PRIMARY: '#667eea',
    SECONDARY: '#764ba2',
    SUCCESS: '#48bb78',
    WARNING: '#f6ad55',
    DANGER: '#f56565',
    INFO: '#4299e1',
    LIGHT: '#e2e8f0',
    DARK: '#2d3748'
};

// Session Configuration
const SESSION_CONFIG = {
    TIMEOUT: 30 * 60 * 1000, // 30 minutes in milliseconds
    WARNING_TIME: 25 * 60 * 1000, // 25 minutes
    CHECK_INTERVAL: 60 * 1000 // Check every minute
};

// File Upload Limits
const FILE_UPLOAD = {
    MAX_SIZE: 5 * 1024 * 1024, // 5MB
    ALLOWED_TYPES: ['image/jpeg', 'image/png', 'application/pdf', 'text/csv'],
    ALLOWED_EXTENSIONS: ['.jpg', '.jpeg', '.png', '.pdf', '.csv']
};

// Attendance Threshold
const ATTENDANCE_THRESHOLD = {
    MINIMUM: 75.0,
    WARNING: 80.0,
    GOOD: 85.0,
    EXCELLENT: 95.0
};

// Performance Levels
const PERFORMANCE_LEVELS = {
    OUTSTANDING: { min: 90, label: 'Outstanding', color: '#28a745' },
    EXCELLENT: { min: 80, label: 'Excellent', color: '#5cb85c' },
    VERY_GOOD: { min: 70, label: 'Very Good', color: '#5bc0de' },
    GOOD: { min: 60, label: 'Good', color: '#f0ad4e' },
    AVERAGE: { min: 50, label: 'Average', color: '#ff9800' },
    BELOW_AVERAGE: { min: 40, label: 'Below Average', color: '#ff5722' },
    POOR: { min: 0, label: 'Poor', color: '#d9534f' }
};

// Risk Levels
const RISK_LEVELS = {
    NONE: { value: 'NONE', label: 'No Risk', color: '#28a745', icon: '✓' },
    LOW: { value: 'LOW', label: 'Low Risk', color: '#ffc107', icon: '⚠️' },
    MEDIUM: { value: 'MEDIUM', label: 'Medium Risk', color: '#ff9800', icon: '⚠️' },
    HIGH: { value: 'HIGH', label: 'High Risk', color: '#dc3545', icon: '⚠️' }
};

// Export Report Formats
const EXPORT_FORMATS = {
    CSV: 'csv',
    PDF: 'pdf',
    EXCEL: 'xlsx'
};

// Academic Year
const CURRENT_ACADEMIC_YEAR = '2024-25';

// Semesters
const SEMESTERS = [1, 2, 3, 4, 5, 6, 7, 8];

// Days of Week
const DAYS_OF_WEEK = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];

// Months
const MONTHS = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
];

// Loading Messages
const LOADING_MESSAGES = [
    'Loading data...',
    'Please wait...',
    'Fetching information...',
    'Processing...'
];

// Error Messages
const ERROR_MESSAGES = {
    NETWORK_ERROR: 'Network error. Please check your connection.',
    SERVER_ERROR: 'Server error. Please try again later.',
    UNAUTHORIZED: 'You are not authorized to perform this action.',
    SESSION_EXPIRED: 'Your session has expired. Please login again.',
    VALIDATION_ERROR: 'Please check your input and try again.',
    UNKNOWN_ERROR: 'An unexpected error occurred.'
};

// Success Messages
const SUCCESS_MESSAGES = {
    SAVE_SUCCESS: 'Data saved successfully!',
    UPDATE_SUCCESS: 'Data updated successfully!',
    DELETE_SUCCESS: 'Data deleted successfully!',
    UPLOAD_SUCCESS: 'File uploaded successfully!'
};

// Freeze constants
Object.freeze(API_ENDPOINTS);
Object.freeze(USER_ROLES);
Object.freeze(ATTENDANCE_STATUS);
Object.freeze(GRADE_SYSTEM);
Object.freeze(ASSESSMENT_TYPES);
Object.freeze(NOTIFICATION_TYPES);
Object.freeze(ALERT_TYPES);
