/**
 * Dashboard Component for AttendEase
 * Handles dashboard data loading and visualization
 */

let attendanceChart = null;
let performanceChart = null;

/**
 * Initialize dashboard
 */
async function initializeDashboard() {
    // Check authentication
    if (!checkAuthentication()) return;
    
    // Update current date
    updateCurrentDate();
    
    // Apply role-based UI
    applyRoleBasedUI();
    
    // Load dashboard data based on role
    const userRole = getUserRole();
    
    if (userRole === 'STUDENT') {
        await loadStudentDashboard();
    } else if (userRole === 'TEACHER') {
        await loadTeacherDashboard();
    } else if (userRole === 'ADMIN') {
        await loadAdminDashboard();
    }
    
    // Load notifications
    await loadNotifications();
    
    // Setup event listeners
    setupEventListeners();
}

/**
 * Update current date display
 */
function updateCurrentDate() {
    const currentDateElement = document.getElementById('currentDate');
    if (currentDateElement) {
        const now = new Date();
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        currentDateElement.textContent = now.toLocaleDateString('en-US', options);
    }
}

/**
 * Load Student Dashboard
 */
async function loadStudentDashboard() {
    try {
        // Show loading
        showLoading('statsGrid');
        
        // Simulate API call - Replace with actual API endpoints
        const dashboardData = await fetchStudentDashboardData();
        
        // Render stats
        renderStudentStats(dashboardData.stats);
        
        // Render charts
        renderAttendanceChart(dashboardData.attendance);
        renderPerformanceChart(dashboardData.performance);
        
        // Render recent activities
        renderRecentAttendance(dashboardData.recentAttendance);
        renderRecentMarks(dashboardData.recentMarks);
        
        // Show upcoming assessments
        document.getElementById('upcomingSection').style.display = 'block';
        renderUpcomingAssessments(dashboardData.upcoming);
        
    } catch (error) {
        console.error('Error loading student dashboard:', error);
        showAlert('Failed to load dashboard data', 'error');
    }
}

/**
 * Load Teacher Dashboard
 */
async function loadTeacherDashboard() {
    try {
        // Show loading
        showLoading('statsGrid');
        
        // Simulate API call
        const dashboardData = await fetchTeacherDashboardData();
        
        // Render stats
        renderTeacherStats(dashboardData.stats);
        
        // Render charts
        renderAttendanceChart(dashboardData.attendance);
        renderPerformanceChart(dashboardData.performance);
        
        // Render recent activities
        renderRecentAttendance(dashboardData.recentAttendance);
        renderRecentMarks(dashboardData.recentMarks);
        
        // Show at-risk students
        document.getElementById('atRiskSection').style.display = 'block';
        renderAtRiskStudents(dashboardData.atRiskStudents);
        
    } catch (error) {
        console.error('Error loading teacher dashboard:', error);
        showAlert('Failed to load dashboard data', 'error');
    }
}

/**
 * Load Admin Dashboard
 */
async function loadAdminDashboard() {
    try {
        showLoading('statsGrid');
        
        const dashboardData = await fetchAdminDashboardData();
        
        renderAdminStats(dashboardData.stats);
        renderAttendanceChart(dashboardData.attendance);
        renderPerformanceChart(dashboardData.performance);
        
    } catch (error) {
        console.error('Error loading admin dashboard:', error);
        showAlert('Failed to load dashboard data', 'error');
    }
}

/**
 * Fetch student dashboard data (Mock - Replace with actual API)
 */
async function fetchStudentDashboardData() {
    // Simulate API delay
    await new Promise(resolve => setTimeout(resolve, 500));
    
    return {
        stats: {
            overallAttendance: 87.5,
            attendanceChange: 2.5,
            overallMarks: 78.3,
            marksChange: 5.2,
            totalSubjects: 6,
            upcomingTests: 3
        },
        attendance: {
            labels: ['Data Structures', 'DBMS', 'OS', 'Networks', 'Web Tech', 'Java'],
            data: [92, 88, 85, 90, 82, 87]
        },
        performance: {
            labels: ['Quiz', 'Assignment', 'Mid-term', 'Final'],
            data: [85, 78, 82, 75]
        },
        recentAttendance: [
            { date: '2025-10-22', subject: 'Data Structures', status: 'PRESENT' },
            { date: '2025-10-21', subject: 'DBMS', status: 'PRESENT' },
            { date: '2025-10-21', subject: 'Operating Systems', status: 'LATE' },
            { date: '2025-10-20', subject: 'Networks', status: 'PRESENT' },
            { date: '2025-10-20', subject: 'Web Technologies', status: 'PRESENT' }
        ],
        recentMarks: [
            { date: '2025-10-18', subject: 'Data Structures', assessment: 'Quiz 3', marks: 18, maxMarks: 20 },
            { date: '2025-10-15', subject: 'DBMS', assessment: 'Assignment 2', marks: 45, maxMarks: 50 },
            { date: '2025-10-12', subject: 'OS', assessment: 'Mid-term', marks: 82, maxMarks: 100 },
            { date: '2025-10-10', subject: 'Networks', assessment: 'Quiz 2', marks: 17, maxMarks: 20 }
        ],
        upcoming: [
            { date: '2025-10-25', subject: 'Data Structures', assessment: 'Mid-term Exam', time: '10:00 AM' },
            { date: '2025-10-28', subject: 'DBMS', assessment: 'Assignment 3', time: '11:59 PM' },
            { date: '2025-11-01', subject: 'Networks', assessment: 'Quiz 4', time: '2:00 PM' }
        ]
    };
}

/**
 * Fetch teacher dashboard data (Mock)
 */
async function fetchTeacherDashboardData() {
    await new Promise(resolve => setTimeout(resolve, 500));
    
    return {
        stats: {
            totalStudents: 120,
            avgAttendance: 82.5,
            attendanceChange: -1.5,
            avgMarks: 72.8,
            marksChange: 3.2,
            atRiskStudents: 8,
            totalClasses: 45
        },
        attendance: {
            labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
            data: [85, 88, 82, 90, 87, 84]
        },
        performance: {
            labels: ['A+', 'A', 'B+', 'B', 'C', 'F'],
            data: [15, 25, 30, 28, 18, 4]
        },
        recentAttendance: [
            { date: '2025-10-22', class: 'CSE101 - Section A', present: 58, total: 60 },
            { date: '2025-10-21', class: 'CSE102 - Section B', present: 55, total: 60 },
            { date: '2025-10-21', class: 'CSE101 - Section A', present: 57, total: 60 }
        ],
        recentMarks: [
            { date: '2025-10-18', class: 'CSE101 - Quiz 3', avgMarks: 16.5, maxMarks: 20 },
            { date: '2025-10-15', class: 'CSE102 - Assignment 2', avgMarks: 42, maxMarks: 50 }
        ],
        atRiskStudents: [
            { name: 'Rahul Kumar', rollNo: 'RA2111001010101', attendance: 68, marks: 42, riskLevel: 'HIGH' },
            { name: 'Priya Singh', rollNo: 'RA2111001010102', attendance: 72, marks: 48, riskLevel: 'MEDIUM' },
            { name: 'Amit Sharma', rollNo: 'RA2111001010103', attendance: 74, marks: 38, riskLevel: 'HIGH' }
        ]
    };
}

/**
 * Fetch admin dashboard data (Mock)
 */
async function fetchAdminDashboardData() {
    await new Promise(resolve => setTimeout(resolve, 500));
    
    return {
        stats: {
            totalStudents: 2500,
            totalTeachers: 120,
            totalSubjects: 85,
            avgAttendance: 81.3,
            attendanceChange: 0.8
        },
        attendance: {
            labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
            data: [80, 82, 81, 83]
        },
        performance: {
            labels: ['Excellent', 'Good', 'Average', 'Below Avg', 'Poor'],
            data: [450, 800, 750, 350, 150]
        }
    };
}

/**
 * Render student statistics
 */
function renderStudentStats(stats) {
    const statsGrid = document.getElementById('statsGrid');
    
    statsGrid.innerHTML = `
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-title">Overall Attendance</span>
                <div class="stat-icon" style="background: linear-gradient(135deg, #667eea, #764ba2);">
                    <i class="fas fa-user-check" style="color: white;"></i>
                </div>
            </div>
            <div class="stat-value">${formatPercentage(stats.overallAttendance)}</div>
            <div class="stat-change ${stats.attendanceChange >= 0 ? 'positive' : 'negative'}">
                <i class="fas fa-arrow-${stats.attendanceChange >= 0 ? 'up' : 'down'}"></i>
                ${Math.abs(stats.attendanceChange)}% from last month
            </div>
        </div>
        
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-title">Overall Marks</span>
                <div class="stat-icon" style="background: linear-gradient(135deg, #48bb78, #38a169);">
                    <i class="fas fa-chart-line" style="color: white;"></i>
                </div>
            </div>
            <div class="stat-value">${formatPercentage(stats.overallMarks)}</div>
            <div class="stat-change ${stats.marksChange >= 0 ? 'positive' : 'negative'}">
                <i class="fas fa-arrow-${stats.marksChange >= 0 ? 'up' : 'down'}"></i>
                ${Math.abs(stats.marksChange)}% from last semester
            </div>
        </div>
        
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-title">Total Subjects</span>
                <div class="stat-icon" style="background: linear-gradient(135deg, #4299e1, #3182ce);">
                    <i class="fas fa-book" style="color: white;"></i>
                </div>
            </div>
            <div class="stat-value">${stats.totalSubjects}</div>
            <div class="stat-change">
                Current Semester
            </div>
        </div>
        
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-title">Upcoming Tests</span>
                <div class="stat-icon" style="background: linear-gradient(135deg, #f6ad55, #ed8936);">
                    <i class="fas fa-calendar-alt" style="color: white;"></i>
                </div>
            </div>
            <div class="stat-value">${stats.upcomingTests}</div>
            <div class="stat-change">
                This Month
            </div>
        </div>
    `;
}

/**
 * Render teacher statistics
 */
function renderTeacherStats(stats) {
    const statsGrid = document.getElementById('statsGrid');
    
    statsGrid.innerHTML = `
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-title">Total Students</span>
                <div class="stat-icon" style="background: linear-gradient(135deg, #667eea, #764ba2);">
                    <i class="fas fa-users" style="color: white;"></i>
                </div>
            </div>
            <div class="stat-value">${stats.totalStudents}</div>
            <div class="stat-change">
                Across all classes
            </div>
        </div>
        
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-title">Average Attendance</span>
                <div class="stat-icon" style="background: linear-gradient(135deg, #48bb78, #38a169);">
                    <i class="fas fa-user-check" style="color: white;"></i>
                </div>
            </div>
            <div class="stat-value">${formatPercentage(stats.avgAttendance)}</div>
            <div class="stat-change ${stats.attendanceChange >= 0 ? 'positive' : 'negative'}">
                <i class="fas fa-arrow-${stats.attendanceChange >= 0 ? 'up' : 'down'}"></i>
                ${Math.abs(stats.attendanceChange)}% from last week
            </div>
        </div>
        
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-title">Average Marks</span>
                <div class="stat-icon" style="background: linear-gradient(135deg, #4299e1, #3182ce);">
                    <i class="fas fa-chart-bar" style="color: white;"></i>
                </div>
            </div>
            <div class="stat-value">${formatPercentage(stats.avgMarks)}</div>
            <div class="stat-change ${stats.marksChange >= 0 ? 'positive' : 'negative'}">
                <i class="fas fa-arrow-${stats.marksChange >= 0 ? 'up' : 'down'}"></i>
                ${Math.abs(stats.marksChange)}% improvement
            </div>
        </div>
        
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-title">At-Risk Students</span>
                <div class="stat-icon" style="background: linear-gradient(135deg, #f56565, #e53e3e);">
                    <i class="fas fa-exclamation-triangle" style="color: white;"></i>
                </div>
            </div>
            <div class="stat-value">${stats.atRiskStudents}</div>
            <div class="stat-change" style="color: var(--danger-color);">
                Require attention
            </div>
        </div>
    `;
}

/**
 * Render admin statistics
 */
function renderAdminStats(stats) {
    const statsGrid = document.getElementById('statsGrid');
    
    statsGrid.innerHTML = `
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-title">Total Students</span>
                <div class="stat-icon" style="background: linear-gradient(135deg, #667eea, #764ba2);">
                    <i class="fas fa-user-graduate" style="color: white;"></i>
                </div>
            </div>
            <div class="stat-value">${formatNumber(stats.totalStudents)}</div>
        </div>
        
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-title">Total Teachers</span>
                <div class="stat-icon" style="background: linear-gradient(135deg, #48bb78, #38a169);">
                    <i class="fas fa-chalkboard-teacher" style="color: white;"></i>
                </div>
            </div>
            <div class="stat-value">${stats.totalTeachers}</div>
        </div>
        
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-title">Total Subjects</span>
                <div class="stat-icon" style="background: linear-gradient(135deg, #4299e1, #3182ce);">
                    <i class="fas fa-book-open" style="color: white;"></i>
                </div>
            </div>
            <div class="stat-value">${stats.totalSubjects}</div>
        </div>
        
        <div class="stat-card">
            <div class="stat-header">
                <span class="stat-title">System Attendance</span>
                <div class="stat-icon" style="background: linear-gradient(135deg, #f6ad55, #ed8936);">
                    <i class="fas fa-chart-pie" style="color: white;"></i>
                </div>
            </div>
            <div class="stat-value">${formatPercentage(stats.avgAttendance)}</div>
        </div>
    `;
}

/**
 * Render attendance chart
 */
function renderAttendanceChart(data) {
    const ctx = document.getElementById('attendanceChart');
    if (!ctx) return;
    
    // Destroy existing chart
    if (attendanceChart) {
        attendanceChart.destroy();
    }
    
    attendanceChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: data.labels,
            datasets: [{
                label: 'Attendance %',
                data: data.data,
                backgroundColor: 'rgba(102, 126, 234, 0.8)',
                borderColor: 'rgba(102, 126, 234, 1)',
                borderWidth: 2,
                borderRadius: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    ticks: {
                        callback: function(value) {
                            return value + '%';
                        }
                    }
                }
            }
        }
    });
}

/**
 * Render performance chart
 */
function renderPerformanceChart(data) {
    const ctx = document.getElementById('performanceChart');
    if (!ctx) return;
    
    // Destroy existing chart
    if (performanceChart) {
        performanceChart.destroy();
    }
    
    performanceChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.labels,
            datasets: [{
                label: 'Performance',
                data: data.data,
                fill: true,
                backgroundColor: 'rgba(72, 187, 120, 0.2)',
                borderColor: 'rgba(72, 187, 120, 1)',
                borderWidth: 3,
                tension: 0.4,
                pointBackgroundColor: 'rgba(72, 187, 120, 1)',
                pointBorderColor: '#fff',
                pointBorderWidth: 2,
                pointRadius: 5
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100
                }
            }
        }
    });
}

/**
 * Render recent attendance
 */
function renderRecentAttendance(data) {
    const container = document.getElementById('recentAttendance');
    if (!container) return;
    
    if (!data || data.length === 0) {
        container.innerHTML = '<p class="no-data">No recent attendance records</p>';
        return;
    }
    
    container.innerHTML = data.map(item => `
        <div class="activity-item">
            <div class="activity-icon" style="background: ${getAttendanceColor(item.status)}20;">
                <i class="fas fa-${item.status === 'PRESENT' ? 'check' : item.status === 'ABSENT' ? 'times' : 'clock'}" 
                   style="color: ${getAttendanceColor(item.status)};"></i>
            </div>
            <div class="activity-details">
                <h4>${item.subject || item.class}</h4>
                <p>${formatDate(item.date)} • ${item.status}${item.present ? ` (${item.present}/${item.total})` : ''}</p>
            </div>
            <span class="status-badge" style="background: ${getAttendanceColor(item.status)};">
                ${item.status}
            </span>
        </div>
    `).join('');
}

/**
 * Render recent marks
 */
function renderRecentMarks(data) {
    const container = document.getElementById('recentMarks');
    if (!container) return;
    
    if (!data || data.length === 0) {
        container.innerHTML = '<p class="no-data">No recent marks records</p>';
        return;
    }
    
    container.innerHTML = data.map(item => {
        const percentage = item.marks ? (item.marks / item.maxMarks) * 100 : item.avgMarks ? (item.avgMarks / item.maxMarks) * 100 : 0;
        const grade = calculateGrade(percentage);
        
        return `
            <div class="activity-item">
                <div class="activity-icon" style="background: ${getGradeColor(grade)}20;">
                    <i class="fas fa-award" style="color: ${getGradeColor(grade)};"></i>
                </div>
                <div class="activity-details">
                    <h4>${item.subject || item.class}</h4>
                    <p>${item.assessment} • ${formatDate(item.date)}</p>
                </div>
                <div class="marks-display">
                    <span class="marks">${item.marks || item.avgMarks}/${item.maxMarks}</span>
                    <span class="grade-badge" style="background: ${getGradeColor(grade)};">${grade}</span>
                </div>
            </div>
        `;
    }).join('');
}

/**
 * Render upcoming assessments
 */
function renderUpcomingAssessments(data) {
    const container = document.getElementById('upcomingList');
    if (!container) return;
    
    if (!data || data.length === 0) {
        container.innerHTML = '<p class="no-data">No upcoming assessments</p>';
        return;
    }
    
    container.innerHTML = data.map(item => `
        <div class="upcoming-item">
            <div class="upcoming-date">
                <span class="day">${new Date(item.date).getDate()}</span>
                <span class="month">${MONTHS[new Date(item.date).getMonth()].substring(0, 3)}</span>
            </div>
            <div class="upcoming-details">
                <h4>${item.assessment}</h4>
                <p>${item.subject} • ${item.time}</p>
            </div>
            <i class="fas fa-chevron-right"></i>
        </div>
    `).join('');
}

/**
 * Render at-risk students
 */
function renderAtRiskStudents(data) {
    const container = document.getElementById('atRiskGrid');
    if (!container) return;
    
    if (!data || data.length === 0) {
        container.innerHTML = '<p class="no-data">No at-risk students</p>';
        return;
    }
    
    container.innerHTML = data.map(student => `
        <div class="risk-card">
            <div class="risk-header">
                <h4>${student.name}</h4>
                <span class="risk-badge ${student.riskLevel.toLowerCase()}">${student.riskLevel}</span>
            </div>
            <p class="roll-number">${student.rollNo}</p>
            <div class="risk-stats">
                <div class="risk-stat">
                    <span class="label">Attendance</span>
                    <span class="value" style="color: ${student.attendance < 75 ? 'var(--danger-color)' : 'var(--warning-color)'};">
                        ${formatPercentage(student.attendance)}
                    </span>
                </div>
                <div class="risk-stat">
                    <span class="label">Marks</span>
                    <span class="value" style="color: ${student.marks < 40 ? 'var(--danger-color)' : 'var(--warning-color)'};">
                        ${formatPercentage(student.marks)}
                    </span>
                </div>
            </div>
            <button class="btn-action" onclick="viewStudentDetails('${student.rollNo}')">
                View Details
            </button>
        </div>
    `).join('');
}

/**
 * Load notifications
 */
async function loadNotifications() {
    try {
        // Mock data - Replace with actual API call
        const notifications = [
            { id: 1, title: 'Low Attendance Alert', message: 'Your attendance in DBMS is below 75%', type: 'WARNING', isRead: false },
            { id: 2, title: 'New Assignment', message: 'New assignment posted in Data Structures', type: 'ANNOUNCEMENT', isRead: false }
        ];
        
        // Update notification badge
        const badge = document.getElementById('notificationBadge');
        const unreadCount = notifications.filter(n => !n.isRead).length;
        if (badge) {
            badge.textContent = unreadCount;
            badge.style.display = unreadCount > 0 ? 'block' : 'none';
        }
        
    } catch (error) {
        console.error('Error loading notifications:', error);
    }
}

/**
 * Setup event listeners
 */
function setupEventListeners() {
    // Notification icon
    const notificationIcon = document.getElementById('notificationIcon');
    const notificationDropdown = document.getElementById('notificationDropdown');
    
    if (notificationIcon && notificationDropdown) {
        notificationIcon.addEventListener('click', function(e) {
            e.stopPropagation();
            notificationDropdown.classList.toggle('show');
        });
    }
    
    // User profile
    const userProfile = document.getElementById('userProfile');
    const profileDropdown = document.getElementById('profileDropdown');
    
    if (userProfile && profileDropdown) {
        userProfile.addEventListener('click', function(e) {
            e.stopPropagation();
            profileDropdown.classList.toggle('show');
        });
    }
    
    // Close dropdowns when clicking outside
    document.addEventListener('click', function() {
        if (notificationDropdown) notificationDropdown.classList.remove('show');
        if (profileDropdown) profileDropdown.classList.remove('show');
    });
}

/**
 * View student details (for teachers)
 */
function viewStudentDetails(rollNo) {
    window.location.href = `reports.html?student=${rollNo}`;
}
