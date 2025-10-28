
/**
 * Marks Page Component for AttendEase
 * Handles marks loading, entry, and export
 */

function initializeMarksPage() {
    applyRoleBasedUI();
    loadMarksSubjects();
    loadAssessmentTypes();
    initializeMarksFilters();
    loadMarksRecords();
    setupMarksActions();
}

// Load subjects to the subject filter dropdown
function loadMarksSubjects() {
    // TODO: Replace with API call for subjects
    const marksSubjectFilter = document.getElementById('marksSubjectFilter');
    if (!marksSubjectFilter) return;
    marksSubjectFilter.innerHTML += `
        <option value="CSE101">Data Structures</option>
        <option value="CSE102">DBMS</option>
        <option value="CSE103">OS</option>
    `;
}

// Load assessment types
function loadAssessmentTypes() {
    // TODO: Replace with API call for types
    const assessmentTypeFilter = document.getElementById('assessmentTypeFilter');
    if (!assessmentTypeFilter) return;
    assessmentTypeFilter.innerHTML += `
        <option value="quiz">Quiz</option>
        <option value="assignment">Assignment</option>
        <option value="midterm">Mid-term</option>
        <option value="final">Final Exam</option>
    `;
}

// Initialize filter actions
function initializeMarksFilters() {
    const marksFilterBtn = document.getElementById('marksFilterBtn');
    if (marksFilterBtn) {
        marksFilterBtn.addEventListener('click', loadMarksRecords);
    }
}

// Load marks records based on filters
function loadMarksRecords() {
    const marksRecordsTableBody = document.getElementById('marksRecordsTableBody');
    if (!marksRecordsTableBody) return;
    // Mock data, replace with fetch logic
    marksRecordsTableBody.innerHTML = `
        <tr>
            <td>2025-10-18</td>
            <td>Data Structures</td>
            <td>Quiz</td>
            <td>18</td>
            <td>20</td>
            <td>90%</td>
            <td>O</td>
            <td class="no-print">
                <button class="btn btn-sm btn-danger">Delete</button>
            </td>
        </tr>
        <tr>
            <td>2025-10-15</td>
            <td>DBMS</td>
            <td>Assignment</td>
            <td>45</td>
            <td>50</td>
            <td>90%</td>
            <td>O</td>
            <td class="no-print">
                <button class="btn btn-sm btn-danger">Delete</button>
            </td>
        </tr>
    `;
}

// Set up event handlers for adding marks and exporting
function setupMarksActions() {
    const saveMarksBtn = document.getElementById('saveMarksBtn');
    if (saveMarksBtn) {
        saveMarksBtn.addEventListener('click', () => {
            // Implement save marks logic
            showAlert('Marks saved successfully!', 'success');
        });
    }
    const marksExportBtn = document.getElementById('marksExportBtn');
    if (marksExportBtn) {
        marksExportBtn.addEventListener('click', () => {
            // Implement CSV export
            exportTableToCSV('marksRecordsTable', 'Marks_Records');
        });
    }
}
