package dao;

import config.DBConnection;
import model.Attendance;
import model.Attendance.AttendanceStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Attendance operations
 * Handles all database operations related to attendance
 */
public class AttendanceDAO {
    
    /**
     * Mark attendance for a student
     * @param attendance Attendance object
     * @return true if attendance marked successfully, false otherwise
     */
    public boolean markAttendance(Attendance attendance) {
        String sql = "INSERT INTO attendance (enrollment_id, attendance_date, status, " +
                    "marked_by, remarks) VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE status = ?, marked_by = ?, " +
                    "marked_at = CURRENT_TIMESTAMP, remarks = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, attendance.getEnrollmentId());
            pstmt.setDate(2, attendance.getAttendanceDate());
            pstmt.setString(3, attendance.getStatusString());
            pstmt.setInt(4, attendance.getMarkedBy());
            pstmt.setString(5, attendance.getRemarks());
            
            // For ON DUPLICATE KEY UPDATE
            pstmt.setString(6, attendance.getStatusString());
            pstmt.setInt(7, attendance.getMarkedBy());
            pstmt.setString(8, attendance.getRemarks());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    attendance.setAttendanceId(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error marking attendance: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mark attendance for multiple students (bulk operation)
     * @param attendanceList List of attendance records
     * @return true if all marked successfully, false otherwise
     */
    public boolean markBulkAttendance(List<Attendance> attendanceList) {
        String sql = "INSERT INTO attendance (enrollment_id, attendance_date, status, " +
                    "marked_by, remarks) VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE status = ?, marked_by = ?, " +
                    "marked_at = CURRENT_TIMESTAMP, remarks = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            
            for (Attendance attendance : attendanceList) {
                pstmt.setInt(1, attendance.getEnrollmentId());
                pstmt.setDate(2, attendance.getAttendanceDate());
                pstmt.setString(3, attendance.getStatusString());
                pstmt.setInt(4, attendance.getMarkedBy());
                pstmt.setString(5, attendance.getRemarks());
                
                // For ON DUPLICATE KEY UPDATE
                pstmt.setString(6, attendance.getStatusString());
                pstmt.setInt(7, attendance.getMarkedBy());
                pstmt.setString(8, attendance.getRemarks());
                
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error marking bulk attendance: " + e.getMessage());
            e.printStackTrace();
            
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }
    
    /**
     * Get attendance by ID
     * @param attendanceId Attendance ID
     * @return Attendance object or null if not found
     */
    public Attendance getAttendanceById(int attendanceId) {
        String sql = "SELECT a.*, u.full_name as student_name, s.roll_number, " +
                    "sub.subject_name, sub.subject_code, u2.full_name as marked_by_name " +
                    "FROM attendance a " +
                    "JOIN enrollments e ON a.enrollment_id = e.enrollment_id " +
                    "JOIN students s ON e.student_id = s.student_id " +
                    "JOIN users u ON s.user_id = u.user_id " +
                    "JOIN subjects sub ON e.subject_id = sub.subject_id " +
                    "JOIN users u2 ON a.marked_by = u2.user_id " +
                    "WHERE a.attendance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, attendanceId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractAttendanceFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting attendance by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get attendance for a specific student and subject
     * @param studentId Student ID
     * @param subjectId Subject ID
     * @param academicYear Academic year
     * @return List of attendance records
     */
    public List<Attendance> getAttendanceByStudent(int studentId, int subjectId, String academicYear) {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name as student_name, s.roll_number, " +
                    "sub.subject_name, sub.subject_code, u2.full_name as marked_by_name " +
                    "FROM attendance a " +
                    "JOIN enrollments e ON a.enrollment_id = e.enrollment_id " +
                    "JOIN students s ON e.student_id = s.student_id " +
                    "JOIN users u ON s.user_id = u.user_id " +
                    "JOIN subjects sub ON e.subject_id = sub.subject_id " +
                    "JOIN users u2 ON a.marked_by = u2.user_id " +
                    "WHERE s.student_id = ? AND sub.subject_id = ? " +
                    "AND e.academic_year = ? " +
                    "ORDER BY a.attendance_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            pstmt.setString(3, academicYear);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                attendanceList.add(extractAttendanceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting attendance by student: " + e.getMessage());
            e.printStackTrace();
        }
        
        return attendanceList;
    }
    
    /**
     * Get attendance for a specific date and subject
     * @param subjectId Subject ID
     * @param date Attendance date
     * @param academicYear Academic year
     * @return List of attendance records
     */
    public List<Attendance> getAttendanceByDate(int subjectId, Date date, String academicYear) {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name as student_name, s.roll_number, " +
                    "sub.subject_name, sub.subject_code, u2.full_name as marked_by_name " +
                    "FROM attendance a " +
                    "JOIN enrollments e ON a.enrollment_id = e.enrollment_id " +
                    "JOIN students s ON e.student_id = s.student_id " +
                    "JOIN users u ON s.user_id = u.user_id " +
                    "JOIN subjects sub ON e.subject_id = sub.subject_id " +
                    "JOIN users u2 ON a.marked_by = u2.user_id " +
                    "WHERE sub.subject_id = ? AND a.attendance_date = ? " +
                    "AND e.academic_year = ? " +
                    "ORDER BY s.roll_number";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subjectId);
            pstmt.setDate(2, date);
            pstmt.setString(3, academicYear);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                attendanceList.add(extractAttendanceFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting attendance by date: " + e.getMessage());
            e.printStackTrace();
        }
        
        return attendanceList;
    }
    
    /**
     * Get attendance statistics for a student in a subject
     * @param studentId Student ID
     * @param subjectId Subject ID
     * @param academicYear Academic year
     * @return Map with attendance statistics
     */
    public Map<String, Object> getAttendanceStatistics(int studentId, int subjectId, String academicYear) {
        Map<String, Object> stats = new HashMap<>();
        
        String sql = "SELECT " +
                    "COUNT(*) as total_classes, " +
                    "SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) as present, " +
                    "SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) as absent, " +
                    "SUM(CASE WHEN a.status = 'LATE' THEN 1 ELSE 0 END) as late, " +
                    "SUM(CASE WHEN a.status = 'EXCUSED' THEN 1 ELSE 0 END) as excused, " +
                    "SUM(CASE WHEN a.status IN ('PRESENT', 'LATE', 'EXCUSED') THEN 1 ELSE 0 END) as attended " +
                    "FROM attendance a " +
                    "JOIN enrollments e ON a.enrollment_id = e.enrollment_id " +
                    "WHERE e.student_id = ? AND e.subject_id = ? AND e.academic_year = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            pstmt.setString(3, academicYear);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int totalClasses = rs.getInt("total_classes");
                int attended = rs.getInt("attended");
                
                stats.put("totalClasses", totalClasses);
                stats.put("present", rs.getInt("present"));
                stats.put("absent", rs.getInt("absent"));
                stats.put("late", rs.getInt("late"));
                stats.put("excused", rs.getInt("excused"));
                stats.put("attended", attended);
                
                // Calculate percentage
                double percentage = totalClasses > 0 ? ((double) attended / totalClasses) * 100 : 0.0;
                stats.put("percentage", percentage);
                stats.put("belowThreshold", percentage < 75.0);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting attendance statistics: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    
    /**
     * Get all students with low attendance (below threshold)
     * @param threshold Attendance percentage threshold
     * @param academicYear Academic year
     * @return List of student IDs with low attendance
     */
    public List<Map<String, Object>> getStudentsWithLowAttendance(double threshold, String academicYear) {
        List<Map<String, Object>> lowAttendanceStudents = new ArrayList<>();
        
        String sql = "SELECT s.student_id, u.full_name, s.roll_number, u.email, " +
                    "sub.subject_id, sub.subject_name, sub.subject_code, " +
                    "COUNT(*) as total_classes, " +
                    "SUM(CASE WHEN a.status IN ('PRESENT', 'LATE', 'EXCUSED') THEN 1 ELSE 0 END) as attended, " +
                    "ROUND((SUM(CASE WHEN a.status IN ('PRESENT', 'LATE', 'EXCUSED') THEN 1 ELSE 0 END) / COUNT(*)) * 100, 2) as percentage " +
                    "FROM attendance a " +
                    "JOIN enrollments e ON a.enrollment_id = e.enrollment_id " +
                    "JOIN students s ON e.student_id = s.student_id " +
                    "JOIN users u ON s.user_id = u.user_id " +
                    "JOIN subjects sub ON e.subject_id = sub.subject_id " +
                    "WHERE e.academic_year = ? " +
                    "GROUP BY s.student_id, sub.subject_id " +
                    "HAVING percentage < ? " +
                    "ORDER BY percentage ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, academicYear);
            pstmt.setDouble(2, threshold);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> studentData = new HashMap<>();
                studentData.put("studentId", rs.getInt("student_id"));
                studentData.put("fullName", rs.getString("full_name"));
                studentData.put("rollNumber", rs.getString("roll_number"));
                studentData.put("email", rs.getString("email"));
                studentData.put("subjectId", rs.getInt("subject_id"));
                studentData.put("subjectName", rs.getString("subject_name"));
                studentData.put("subjectCode", rs.getString("subject_code"));
                studentData.put("totalClasses", rs.getInt("total_classes"));
                studentData.put("attended", rs.getInt("attended"));
                studentData.put("percentage", rs.getDouble("percentage"));
                
                lowAttendanceStudents.add(studentData);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting students with low attendance: " + e.getMessage());
            e.printStackTrace();
        }
        
        return lowAttendanceStudents;
    }
    
    /**
     * Update attendance status
     * @param attendanceId Attendance ID
     * @param status New status
     * @param remarks Updated remarks
     * @return true if update successful, false otherwise
     */
    public boolean updateAttendance(int attendanceId, AttendanceStatus status, String remarks) {
        String sql = "UPDATE attendance SET status = ?, remarks = ?, " +
                    "marked_at = CURRENT_TIMESTAMP WHERE attendance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status.name());
            pstmt.setString(2, remarks);
            pstmt.setInt(3, attendanceId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating attendance: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete attendance record
     * @param attendanceId Attendance ID
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteAttendance(int attendanceId) {
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, attendanceId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting attendance: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Extract Attendance object from ResultSet
     * @param rs ResultSet
     * @return Attendance object
     * @throws SQLException
     */
    private Attendance extractAttendanceFromResultSet(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setAttendanceId(rs.getInt("attendance_id"));
        attendance.setEnrollmentId(rs.getInt("enrollment_id"));
        attendance.setAttendanceDate(rs.getDate("attendance_date"));
        attendance.setStatusFromString(rs.getString("status"));
        attendance.setMarkedBy(rs.getInt("marked_by"));
        attendance.setMarkedAt(rs.getTimestamp("marked_at"));
        attendance.setRemarks(rs.getString("remarks"));
        
        // Additional display fields
        attendance.setStudentName(rs.getString("student_name"));
        attendance.setRollNumber(rs.getString("roll_number"));
        attendance.setSubjectName(rs.getString("subject_name"));
        attendance.setSubjectCode(rs.getString("subject_code"));
        attendance.setMarkedByName(rs.getString("marked_by_name"));
        
        return attendance;
    }
}
