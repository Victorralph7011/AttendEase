/**
 * Attendance Page Component for AttendEase
 * Handles attendance loading, marking, and export
 */

// Main initialization
function initializeAttendancePage() {
    applyRoleBasedUI();
    loadSubjects();
    initializeFilters();
    loadAttendanceRecords();
    setupAttendanceActions();
}

// Load subjects to the subject filter dropdown
function loadSubjects() {
    // TODO: Replace with actual API call for subjects
    const subjectFilter = document.getElementById('subjectFilter');
    if (!subjectFilter) return;
    subjectFilter.innerHTML += `
        <option value="CSE101">Data Structures</option>
        <option value="CSE102">DBMS</option>
        <option value="CSE103">OS</option>
    `;
}

// Initialize filter actions
function initializeFilters() {
    const filterBtn = document.getElementById('filterBtn');
    if (filterBtn) {
        filterBtn.addEventListener('click', loadAttendanceRecords);
    }
}

// Load attendance records based on filters
function loadAttendanceRecords() {
    const recordsTableBody = document.getElementById('recordsTableBody');
    if (!recordsTableBody) return;
    // Mock data, replace with fetch logic.
    recordsTableBody.innerHTML = `
        <tr>
            <td>2025-10-21</td>
            <td>Data Structures</td>
            <td>PRESENT</td>
            <td></td>
            <td class="no-print">
                <button class="btn btn-sm btn-danger">Delete</button>
            </td>
        </tr>
        <tr>
            <td>2025-10-20</td>
            <td>DBMS</td>
            <td>LATE</td>
            <td>Arrived late by 10 min</td>
            <td class="no-print">
                <button class="btn btn-sm btn-danger">Delete</button>
            </td>
        </tr>
    `;
}

// Set up event handlers for marking attendance and other teacher actions
function setupAttendanceActions() {
    const markAllPresent = document.getElementById('markAllPresent');
    const clearAll = document.getElementById('clearAll');
    if (markAllPresent) {
        markAllPresent.addEventListener('click', () => {
            // Mark all students as present in the attendance table
            // Implement logic for teacher view
            showAlert('Marked all as Present', 'success');
        });
    }
    if (clearAll) {
        clearAll.addEventListener('click', () => {
            // Clear all attendance selections
            showAlert('Cleared attendance selection', 'info');
        });
    }
    const saveAttendanceBtn = document.getElementById('saveAttendanceBtn');
    if (saveAttendanceBtn) {
        saveAttendanceBtn.addEventListener('click', () => {
            // Implement save attendance logic
            showAlert('Attendance saved successfully!', 'success');
        });
    }
    const exportBtn = document.getElementById('exportBtn');
    if (exportBtn) {
        exportBtn.addEventListener('click', () => {
            // Implement CSV export
            exportTableToCSV('recordsTable', 'Attendance_Records');
        });
    }
}

// Optionally: more event handlers for filtering, summary, edit, etc.
