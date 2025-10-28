/**
 * Reports Page Component for AttendEase
 * Handles loading, generating, and exporting reports
 */

function initializeReportsPage() {
    applyRoleBasedUI();
    loadReportSubjects();
    initializeReportFilters();
    setupReportActions();
}

// Load subjects for report filter dropdown
function loadReportSubjects() {
    // TODO: Replace with API call
    const reportSubjectFilter = document.getElementById('reportSubjectFilter');
    if (!reportSubjectFilter) return;
    reportSubjectFilter.innerHTML += `
        <option value="CSE101">Data Structures</option>
        <option value="CSE102">DBMS</option>
        <option value="CSE103">OS</option>
    `;
}

// Initialize filter actions
function initializeReportFilters() {
    const reportFilterBtn = document.getElementById('reportFilterBtn');
    if (reportFilterBtn) {
        reportFilterBtn.addEventListener('click', generateReport);
    }
}

// Generate report based on selected type and filters
function generateReport() {
    const reportType = document.getElementById('reportTypeFilter').value;
    const subject = document.getElementById('reportSubjectFilter').value;
    const reportsTableBody = document.getElementById('reportsTableBody');
    if (!reportsTableBody) return;

    // Mock data for demonstration (replace with AJAX/fetch call to backend API)
    if (reportType === 'performance' || reportType === 'attendance') {
        reportsTableBody.innerHTML = `
            <tr>
                <td>RA2111001010101</td>
                <td>Rahul Kumar</td>
                <td>${subject || 'Data Structures'}</td>
                <td>92%</td>
                <td>89%</td>
                <td>A+</td>
                <td>None</td>
            </tr>
            <tr>
                <td>RA2111001010102</td>
                <td>Priya Singh</td>
                <td>${subject || 'Data Structures'}</td>
                <td>74%</td>
                <td>62%</td>
                <td>B+</td>
                <td>Low</td>
            </tr>
            <tr>
                <td>RA2111001010103</td>
                <td>Amit Sharma</td>
                <td>${subject || 'Data Structures'}</td>
                <td>68%</td>
                <td>35%</td>
                <td>F</td>
                <td>High</td>
            </tr>
        `;
    } else if (reportType === 'risk') {
        reportsTableBody.innerHTML = `
            <tr>
                <td>RA2111001010103</td>
                <td>Amit Sharma</td>
                <td>${subject || 'Data Structures'}</td>
                <td>68%</td>
                <td>35%</td>
                <td>F</td>
                <td>HIGH</td>
            </tr>
        `;
    }
}

// Setup actions for export and more
function setupReportActions() {
    const reportsExportBtn = document.getElementById('reportsExportBtn');
    if (reportsExportBtn) {
        reportsExportBtn.addEventListener('click', () => {
            exportTableToCSV('reportsTable', 'Reports');
        });
    }
}
