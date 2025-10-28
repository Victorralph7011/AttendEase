package dao;

import config.DBConnection;
import model.Report;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Report operations
 * Handles all database operations related to generating reports
 */
public class ReportDAO {
    
    private AttendanceDAO attendanceDAO;
    private MarksDAO marksDAO;
    
    public ReportDAO() {
        this.attendanceDAO = new AttendanceDAO();
        this.marksDAO = new MarksDAO();
    }
    
    /**
     * Generate comprehensive report for a student in a subject
     * @param studentId Student ID
     * @param subjectId Subject ID
     * @param academicYear Academic year
     * @return Report object with all statistics
     */
    public Report generateStudentReport(int studentId, int subjectId, String academicYear) {
        Report report = new Report(studentId, subjectId, academicYear, "COMPREHENSIVE");
        
        try (Connection conn = DBConnection.getConnection()) {
            
            // Get student information
            String studentSql = "SELECT u.full_name, s.roll_number, u.email, s.semester " +
                              "FROM students s " +
                              "JOIN users u ON s.user_id = u.user_id " +
                              "WHERE s.student_id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(studentSql)) {
                pstmt.setInt(1, studentId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    report.setStudentName(rs.getString("full_name"));
                    report.setRollNumber(rs.getString("roll_number"));
                    report.setEmail(rs.getString("email"));
                    report.setSemester(rs.getInt("semester"));
                }
            }
            
            // Get subject information
            String subjectSql = "SELECT subject_name, subject_code, credits " +
                               "FROM subjects WHERE subject_id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(subjectSql)) {
                pstmt.setInt(1, subjectId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    report.setSubjectName(rs.getString("subject_name"));
                    report.setSubjectCode(rs.getString("subject_code"));
                    report.setCredits(rs.getInt("credits"));
                }
            }
            
            // Get attendance statistics
            var attendanceStats = attendanceDAO.getAttendanceStatistics(studentId, subjectId, academicYear);
            if (!attendanceStats.isEmpty()) {
                report.setTotalClasses((Integer) attendanceStats.get("totalClasses"));
                report.setClassesAttended((Integer) attendanceStats.get("attended"));
                report.setClassesAbsent((Integer) attendanceStats.get("absent"));
                report.setClassesLate((Integer) attendanceStats.get("late"));
                report.setClassesExcused((Integer) attendanceStats.get("excused"));
                report.setAttendancePercentage((Double) attendanceStats.get("percentage"));
            }
            
            // Get marks statistics
            var marksStats = marksDAO.getMarksStatistics(studentId, subjectId, academicYear);
            if (!marksStats.isEmpty()) {
                report.setTotalMarksObtained((Double) marksStats.get("totalObtained"));
                report.setTotalMaxMarks((Double) marksStats.get("totalMax"));
                report.setOverallPercentage((Double) marksStats.get("percentage"));
                report.setOverallGrade((String) marksStats.get("grade"));
            }
            
            // Get assessment-wise marks
            String assessmentSql = "SELECT at.type_name, m.marks_obtained, m.max_marks, at.weightage " +
                                  "FROM marks m " +
                                  "JOIN enrollments e ON m.enrollment_id = e.enrollment_id " +
                                  "JOIN assessment_types at ON m.assessment_type_id = at.type_id " +
                                  "WHERE e.student_id = ? AND e.subject_id = ? AND e.academic_year = ? " +
                                  "ORDER BY m.assessment_date";
            
            try (PreparedStatement pstmt = conn.prepareStatement(assessmentSql)) {
                pstmt.setInt(1, studentId);
                pstmt.setInt(2, subjectId);
                pstmt.setString(3, academicYear);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Report.AssessmentMark assessmentMark = new Report.AssessmentMark(
                        rs.getString("type_name"),
                        rs.getDouble("marks_obtained"),
                        rs.getDouble("max_marks"),
                        rs.getDouble("weightage")
                    );
                    report.addAssessmentMark(assessmentMark);
                }
            }
            
            // Calculate performance level
            if (report.getOverallPercentage() >= 80) {
                report.setPerformanceLevel("Excellent");
            } else if (report.getOverallPercentage() >= 60) {
                report.setPerformanceLevel("Good");
            } else if (report.getOverallPercentage() >= 40) {
                report.setPerformanceLevel("Average");
            } else {
                report.setPerformanceLevel("Poor");
            }
            
            // Analyze risk and generate insights
            report.analyzeRiskLevel();
            report.generateInsights();
            
        } catch (SQLException e) {
            System.err.println("Error generating student report: " + e.getMessage());
            e.printStackTrace();
        }
        
        return report;
    }
    
    /**
     * Generate attendance report for all students in a subject
     * @param subjectId Subject ID
     * @param academicYear Academic year
     * @return List of attendance reports
     */
    public List<Report> generateSubjectAttendanceReport(int subjectId, String academicYear) {
        List<Report> reports = new ArrayList<>();
        
        String sql = "SELECT DISTINCT s.student_id, u.full_name, s.roll_number, u.email " +
                    "FROM enrollments e " +
                    "JOIN students s ON e.student_id = s.student_id " +
                    "JOIN users u ON s.user_id = u.user_id " +
                    "WHERE e.subject_id = ? AND e.academic_year = ? " +
                    "ORDER BY s.roll_number";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subjectId);
            pstmt.setString(2, academicYear);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                Report report = new Report(studentId, subjectId, academicYear, "ATTENDANCE");
                
                report.setStudentName(rs.getString("full_name"));
                report.setRollNumber(rs.getString("roll_number"));
                report.setEmail(rs.getString("email"));
                
                // Get attendance statistics
                var attendanceStats = attendanceDAO.getAttendanceStatistics(studentId, subjectId, academicYear);
                if (!attendanceStats.isEmpty()) {
                    report.setTotalClasses((Integer) attendanceStats.get("totalClasses"));
                    report.setClassesAttended((Integer) attendanceStats.get("attended"));
                    report.setClassesAbsent((Integer) attendanceStats.get("absent"));
                    report.setAttendancePercentage((Double) attendanceStats.get("percentage"));
                }
                
                reports.add(report);
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating subject attendance report: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reports;
    }
    
    /**
     * Generate marks report for all students in a subject
     * @param subjectId Subject ID
     * @param academicYear Academic year
     * @return List of marks reports
     */
    public List<Report> generateSubjectMarksReport(int subjectId, String academicYear) {
        List<Report> reports = new ArrayList<>();
        
        String sql = "SELECT DISTINCT s.student_id, u.full_name, s.roll_number, u.email " +
                    "FROM enrollments e " +
                    "JOIN students s ON e.student_id = s.student_id " +
                    "JOIN users u ON s.user_id = u.user_id " +
                    "WHERE e.subject_id = ? AND e.academic_year = ? " +
                    "ORDER BY s.roll_number";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subjectId);
            pstmt.setString(2, academicYear);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                Report report = new Report(studentId, subjectId, academicYear, "MARKS");
                
                report.setStudentName(rs.getString("full_name"));
                report.setRollNumber(rs.getString("roll_number"));
                report.setEmail(rs.getString("email"));
                
                // Get marks statistics
                var marksStats = marksDAO.getMarksStatistics(studentId, subjectId, academicYear);
                if (!marksStats.isEmpty()) {
                    report.setTotalMarksObtained((Double) marksStats.get("totalObtained"));
                    report.setTotalMaxMarks((Double) marksStats.get("totalMax"));
                    report.setOverallPercentage((Double) marksStats.get("percentage"));
                    report.setOverallGrade((String) marksStats.get("grade"));
                }
                
                reports.add(report);
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating subject marks report: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reports;
    }
    
    /**
     * Get all subjects for a student
     * @param studentId Student ID
     * @param academicYear Academic year
     * @return List of reports for all subjects
     */
    public List<Report> generateStudentAllSubjectsReport(int studentId, String academicYear) {
        List<Report> reports = new ArrayList<>();
        
        String sql = "SELECT DISTINCT sub.subject_id, sub.subject_name, sub.subject_code, sub.credits " +
                    "FROM enrollments e " +
                    "JOIN subjects sub ON e.subject_id = sub.subject_id " +
                    "WHERE e.student_id = ? AND e.academic_year = ? " +
                    "ORDER BY sub.subject_code";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setString(2, academicYear);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int subjectId = rs.getInt("subject_id");
                Report report = generateStudentReport(studentId, subjectId, academicYear);
                reports.add(report);
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating student all subjects report: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reports;
    }
    
    /**
     * Get at-risk students (low attendance or poor performance)
     * @param academicYear Academic year
     * @return List of at-risk student reports
     */
    public List<Report> getAtRiskStudents(String academicYear) {
        List<Report> atRiskReports = new ArrayList<>();
        
        // Get students with low attendance
        var lowAttendanceStudents = attendanceDAO.getStudentsWithLowAttendance(75.0, academicYear);
        
        for (var studentData : lowAttendanceStudents) {
            int studentId = (Integer) studentData.get("studentId");
            int subjectId = (Integer) studentData.get("subjectId");
            
            Report report = new Report(studentId, subjectId, academicYear, "AT_RISK");
            report.setStudentName((String) studentData.get("fullName"));
            report.setRollNumber((String) studentData.get("rollNumber"));
            report.setSubjectName((String) studentData.get("subjectName"));
            report.setSubjectCode((String) studentData.get("subjectCode"));
            report.setAttendancePercentage((Double) studentData.get("percentage"));
            report.setAtRisk(true);
            report.setRiskLevel("HIGH");
            
            atRiskReports.add(report);
        }
        
        // Get students with failing grades
        String sql = "SELECT DISTINCT sub.subject_id FROM subjects WHERE is_active = TRUE";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int subjectId = rs.getInt("subject_id");
                var failingStudents = marksDAO.getFailingStudents(subjectId, academicYear);
                
                for (var studentData : failingStudents) {
                    int studentId = (Integer) studentData.get("studentId");
                    
                    Report report = new Report(studentId, subjectId, academicYear, "AT_RISK");
                    report.setStudentName((String) studentData.get("fullName"));
                    report.setRollNumber((String) studentData.get("rollNumber"));
                    report.setSubjectName((String) studentData.get("subjectName"));
                    report.setSubjectCode((String) studentData.get("subjectCode"));
                    report.setOverallPercentage((Double) studentData.get("percentage"));
                    report.setAtRisk(true);
                    report.setRiskLevel("HIGH");
                    
                    atRiskReports.add(report);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting at-risk students: " + e.getMessage());
            e.printStackTrace();
        }
        
        return atRiskReports;
    }
    
    /**
     * Get subject-wise performance summary
     * @param subjectId Subject ID
     * @param academicYear Academic year
     * @return Summary statistics map
     */
    public java.util.Map<String, Object> getSubjectPerformanceSummary(int subjectId, String academicYear) {
        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        
        String sql = "SELECT " +
                    "COUNT(DISTINCT e.student_id) as total_students, " +
                    "AVG(attendance_percentage) as avg_attendance, " +
                    "AVG(marks_percentage) as avg_marks, " +
                    "SUM(CASE WHEN marks_percentage >= 40 THEN 1 ELSE 0 END) as passed, " +
                    "SUM(CASE WHEN marks_percentage < 40 THEN 1 ELSE 0 END) as failed " +
                    "FROM ( " +
                    "  SELECT e.student_id, " +
                    "  (SELECT ROUND((SUM(CASE WHEN a.status IN ('PRESENT', 'LATE', 'EXCUSED') THEN 1 ELSE 0 END) / COUNT(*)) * 100, 2) " +
                    "   FROM attendance a WHERE a.enrollment_id = e.enrollment_id) as attendance_percentage, " +
                    "  (SELECT ROUND((SUM(m.marks_obtained) / SUM(m.max_marks)) * 100, 2) " +
                    "   FROM marks m WHERE m.enrollment_id = e.enrollment_id) as marks_percentage " +
                    "  FROM enrollments e " +
                    "  WHERE e.subject_id = ? AND e.academic_year = ? " +
                    ") as student_stats";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subjectId);
            pstmt.setString(2, academicYear);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                summary.put("totalStudents", rs.getInt("total_students"));
                summary.put("avgAttendance", rs.getDouble("avg_attendance"));
                summary.put("avgMarks", rs.getDouble("avg_marks"));
                summary.put("passed", rs.getInt("passed"));
                summary.put("failed", rs.getInt("failed"));
                
                int total = rs.getInt("total_students");
                int passed = rs.getInt("passed");
                double passPercentage = total > 0 ? ((double) passed / total) * 100 : 0.0;
                summary.put("passPercentage", passPercentage);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting subject performance summary: " + e.getMessage());
            e.printStackTrace();
        }
        
        return summary;
    }
    
    /**
     * Get class-wise attendance summary for a date range
     * @param subjectId Subject ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of daily attendance statistics
     */
    public List<java.util.Map<String, Object>> getDateRangeAttendanceSummary(
            int subjectId, Date startDate, Date endDate) {
        
        List<java.util.Map<String, Object>> dailyStats = new ArrayList<>();
        
        String sql = "SELECT a.attendance_date, " +
                    "COUNT(*) as total_students, " +
                    "SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) as present, " +
                    "SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) as absent, " +
                    "SUM(CASE WHEN a.status = 'LATE' THEN 1 ELSE 0 END) as late, " +
                    "ROUND((SUM(CASE WHEN a.status IN ('PRESENT', 'LATE', 'EXCUSED') THEN 1 ELSE 0 END) / COUNT(*)) * 100, 2) as percentage " +
                    "FROM attendance a " +
                    "JOIN enrollments e ON a.enrollment_id = e.enrollment_id " +
                    "WHERE e.subject_id = ? AND a.attendance_date BETWEEN ? AND ? " +
                    "GROUP BY a.attendance_date " +
                    "ORDER BY a.attendance_date";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subjectId);
            pstmt.setDate(2, startDate);
            pstmt.setDate(3, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                java.util.Map<String, Object> dayStat = new java.util.HashMap<>();
                dayStat.put("date", rs.getDate("attendance_date"));
                dayStat.put("totalStudents", rs.getInt("total_students"));
                dayStat.put("present", rs.getInt("present"));
                dayStat.put("absent", rs.getInt("absent"));
                dayStat.put("late", rs.getInt("late"));
                dayStat.put("percentage", rs.getDouble("percentage"));
                
                dailyStats.add(dayStat);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting date range attendance summary: " + e.getMessage());
            e.printStackTrace();
        }
        
        return dailyStats;
    }
}
