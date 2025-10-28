package dao;

import config.DBConnection;
import model.Marks;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Marks operations
 * Handles all database operations related to marks
 */
public class MarksDAO {
    
    /**
     * Add marks for a student
     * @param marks Marks object
     * @return true if marks added successfully, false otherwise
     */
    public boolean addMarks(Marks marks) {
        String sql = "INSERT INTO marks (enrollment_id, assessment_type_id, max_marks, " +
                    "marks_obtained, assessment_date, entered_by, remarks) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, marks.getEnrollmentId());
            pstmt.setInt(2, marks.getAssessmentTypeId());
            pstmt.setDouble(3, marks.getMaxMarks());
            pstmt.setDouble(4, marks.getMarksObtained());
            pstmt.setDate(5, marks.getAssessmentDate());
            pstmt.setInt(6, marks.getEnteredBy());
            pstmt.setString(7, marks.getRemarks());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    marks.setMarkId(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding marks: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Add marks for multiple students (bulk operation)
     * @param marksList List of marks records
     * @return true if all added successfully, false otherwise
     */
    public boolean addBulkMarks(List<Marks> marksList) {
        String sql = "INSERT INTO marks (enrollment_id, assessment_type_id, max_marks, " +
                    "marks_obtained, assessment_date, entered_by, remarks) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            
            for (Marks marks : marksList) {
                pstmt.setInt(1, marks.getEnrollmentId());
                pstmt.setInt(2, marks.getAssessmentTypeId());
                pstmt.setDouble(3, marks.getMaxMarks());
                pstmt.setDouble(4, marks.getMarksObtained());
                pstmt.setDate(5, marks.getAssessmentDate());
                pstmt.setInt(6, marks.getEnteredBy());
                pstmt.setString(7, marks.getRemarks());
                
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error adding bulk marks: " + e.getMessage());
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
     * Get marks by ID
     * @param markId Mark ID
     * @return Marks object or null if not found
     */
    public Marks getMarksById(int markId) {
        String sql = "SELECT m.*, u.full_name as student_name, s.roll_number, " +
                    "sub.subject_name, sub.subject_code, at.type_name, at.weightage, " +
                    "u2.full_name as entered_by_name " +
                    "FROM marks m " +
                    "JOIN enrollments e ON m.enrollment_id = e.enrollment_id " +
                    "JOIN students s ON e.student_id = s.student_id " +
                    "JOIN users u ON s.user_id = u.user_id " +
                    "JOIN subjects sub ON e.subject_id = sub.subject_id " +
                    "JOIN assessment_types at ON m.assessment_type_id = at.type_id " +
                    "JOIN users u2 ON m.entered_by = u2.user_id " +
                    "WHERE m.mark_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, markId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractMarksFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting marks by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all marks for a student in a subject
     * @param studentId Student ID
     * @param subjectId Subject ID
     * @param academicYear Academic year
     * @return List of marks records
     */
    public List<Marks> getMarksByStudent(int studentId, int subjectId, String academicYear) {
        List<Marks> marksList = new ArrayList<>();
        String sql = "SELECT m.*, u.full_name as student_name, s.roll_number, " +
                    "sub.subject_name, sub.subject_code, at.type_name, at.weightage, " +
                    "u2.full_name as entered_by_name " +
                    "FROM marks m " +
                    "JOIN enrollments e ON m.enrollment_id = e.enrollment_id " +
                    "JOIN students s ON e.student_id = s.student_id " +
                    "JOIN users u ON s.user_id = u.user_id " +
                    "JOIN subjects sub ON e.subject_id = sub.subject_id " +
                    "JOIN assessment_types at ON m.assessment_type_id = at.type_id " +
                    "JOIN users u2 ON m.entered_by = u2.user_id " +
                    "WHERE s.student_id = ? AND sub.subject_id = ? " +
                    "AND e.academic_year = ? " +
                    "ORDER BY m.assessment_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            pstmt.setString(3, academicYear);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                marksList.add(extractMarksFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting marks by student: " + e.getMessage());
            e.printStackTrace();
        }
        
        return marksList;
    }
    
    /**
     * Get marks for all students in a subject for a specific assessment
     * @param subjectId Subject ID
     * @param assessmentTypeId Assessment type ID
     * @param academicYear Academic year
     * @return List of marks records
     */
    public List<Marks> getMarksBySubjectAndAssessment(int subjectId, int assessmentTypeId, String academicYear) {
        List<Marks> marksList = new ArrayList<>();
        String sql = "SELECT m.*, u.full_name as student_name, s.roll_number, " +
                    "sub.subject_name, sub.subject_code, at.type_name, at.weightage, " +
                    "u2.full_name as entered_by_name " +
                    "FROM marks m " +
                    "JOIN enrollments e ON m.enrollment_id = e.enrollment_id " +
                    "JOIN students s ON e.student_id = s.student_id " +
                    "JOIN users u ON s.user_id = u.user_id " +
                    "JOIN subjects sub ON e.subject_id = sub.subject_id " +
                    "JOIN assessment_types at ON m.assessment_type_id = at.type_id " +
                    "JOIN users u2 ON m.entered_by = u2.user_id " +
                    "WHERE sub.subject_id = ? AND at.type_id = ? " +
                    "AND e.academic_year = ? " +
                    "ORDER BY s.roll_number";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subjectId);
            pstmt.setInt(2, assessmentTypeId);
            pstmt.setString(3, academicYear);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                marksList.add(extractMarksFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting marks by subject and assessment: " + e.getMessage());
            e.printStackTrace();
        }
        
        return marksList;
    }
    
    /**
     * Get marks statistics for a student in a subject
     * @param studentId Student ID
     * @param subjectId Subject ID
     * @param academicYear Academic year
     * @return Map with marks statistics
     */
    public Map<String, Object> getMarksStatistics(int studentId, int subjectId, String academicYear) {
        Map<String, Object> stats = new HashMap<>();
        
        String sql = "SELECT " +
                    "SUM(m.marks_obtained) as total_obtained, " +
                    "SUM(m.max_marks) as total_max, " +
                    "COUNT(*) as total_assessments, " +
                    "ROUND((SUM(m.marks_obtained) / SUM(m.max_marks)) * 100, 2) as overall_percentage " +
                    "FROM marks m " +
                    "JOIN enrollments e ON m.enrollment_id = e.enrollment_id " +
                    "WHERE e.student_id = ? AND e.subject_id = ? AND e.academic_year = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            pstmt.setString(3, academicYear);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double totalObtained = rs.getDouble("total_obtained");
                double totalMax = rs.getDouble("total_max");
                double percentage = rs.getDouble("overall_percentage");
                
                stats.put("totalObtained", totalObtained);
                stats.put("totalMax", totalMax);
                stats.put("totalAssessments", rs.getInt("total_assessments"));
                stats.put("percentage", percentage);
                
                // Calculate grade
                String grade = calculateGrade(percentage);
                stats.put("grade", grade);
                stats.put("passed", percentage >= 40.0);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting marks statistics: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    
    /**
     * Get weighted marks for a student (based on assessment weightage)
     * @param studentId Student ID
     * @param subjectId Subject ID
     * @param academicYear Academic year
     * @return Weighted marks percentage
     */
    public double getWeightedMarks(int studentId, int subjectId, String academicYear) {
        String sql = "SELECT SUM((m.marks_obtained / m.max_marks) * at.weightage) as weighted_marks " +
                    "FROM marks m " +
                    "JOIN enrollments e ON m.enrollment_id = e.enrollment_id " +
                    "JOIN assessment_types at ON m.assessment_type_id = at.type_id " +
                    "WHERE e.student_id = ? AND e.subject_id = ? AND e.academic_year = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, subjectId);
            pstmt.setString(3, academicYear);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("weighted_marks");
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating weighted marks: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    /**
     * Get students with failing grades
     * @param subjectId Subject ID
     * @param academicYear Academic year
     * @return List of student data with failing grades
     */
    public List<Map<String, Object>> getFailingStudents(int subjectId, String academicYear) {
        List<Map<String, Object>> failingStudents = new ArrayList<>();
        
        String sql = "SELECT s.student_id, u.full_name, s.roll_number, u.email, " +
                    "sub.subject_name, sub.subject_code, " +
                    "SUM(m.marks_obtained) as total_obtained, " +
                    "SUM(m.max_marks) as total_max, " +
                    "ROUND((SUM(m.marks_obtained) / SUM(m.max_marks)) * 100, 2) as percentage " +
                    "FROM marks m " +
                    "JOIN enrollments e ON m.enrollment_id = e.enrollment_id " +
                    "JOIN students s ON e.student_id = s.student_id " +
                    "JOIN users u ON s.user_id = u.user_id " +
                    "JOIN subjects sub ON e.subject_id = sub.subject_id " +
                    "WHERE sub.subject_id = ? AND e.academic_year = ? " +
                    "GROUP BY s.student_id, sub.subject_id " +
                    "HAVING percentage < 40 " +
                    "ORDER BY percentage ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subjectId);
            pstmt.setString(2, academicYear);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> studentData = new HashMap<>();
                studentData.put("studentId", rs.getInt("student_id"));
                studentData.put("fullName", rs.getString("full_name"));
                studentData.put("rollNumber", rs.getString("roll_number"));
                studentData.put("email", rs.getString("email"));
                studentData.put("subjectName", rs.getString("subject_name"));
                studentData.put("subjectCode", rs.getString("subject_code"));
                studentData.put("totalObtained", rs.getDouble("total_obtained"));
                studentData.put("totalMax", rs.getDouble("total_max"));
                studentData.put("percentage", rs.getDouble("percentage"));
                
                failingStudents.add(studentData);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting failing students: " + e.getMessage());
            e.printStackTrace();
        }
        
        return failingStudents;
    }
    
    /**
     * Update marks
     * @param marks Marks object with updated values
     * @return true if update successful, false otherwise
     */
    public boolean updateMarks(Marks marks) {
        String sql = "UPDATE marks SET marks_obtained = ?, max_marks = ?, " +
                    "assessment_date = ?, remarks = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE mark_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, marks.getMarksObtained());
            pstmt.setDouble(2, marks.getMaxMarks());
            pstmt.setDate(3, marks.getAssessmentDate());
            pstmt.setString(4, marks.getRemarks());
            pstmt.setInt(5, marks.getMarkId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating marks: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete marks record
     * @param markId Mark ID
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteMarks(int markId) {
        String sql = "DELETE FROM marks WHERE mark_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, markId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting marks: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get top performers in a subject
     * @param subjectId Subject ID
     * @param academicYear Academic year
     * @param limit Number of top performers to retrieve
     * @return List of top performers
     */
    public List<Map<String, Object>> getTopPerformers(int subjectId, String academicYear, int limit) {
        List<Map<String, Object>> topPerformers = new ArrayList<>();
        
        String sql = "SELECT s.student_id, u.full_name, s.roll_number, " +
                    "ROUND((SUM(m.marks_obtained) / SUM(m.max_marks)) * 100, 2) as percentage " +
                    "FROM marks m " +
                    "JOIN enrollments e ON m.enrollment_id = e.enrollment_id " +
                    "JOIN students s ON e.student_id = s.student_id " +
                    "JOIN users u ON s.user_id = u.user_id " +
                    "WHERE e.subject_id = ? AND e.academic_year = ? " +
                    "GROUP BY s.student_id " +
                    "ORDER BY percentage DESC " +
                    "LIMIT ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, subjectId);
            pstmt.setString(2, academicYear);
            pstmt.setInt(3, limit);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> performer = new HashMap<>();
                performer.put("studentId", rs.getInt("student_id"));
                performer.put("fullName", rs.getString("full_name"));
                performer.put("rollNumber", rs.getString("roll_number"));
                performer.put("percentage", rs.getDouble("percentage"));
                performer.put("grade", calculateGrade(rs.getDouble("percentage")));
                
                topPerformers.add(performer);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting top performers: " + e.getMessage());
            e.printStackTrace();
        }
        
        return topPerformers;
    }
    
    /**
     * Calculate grade from percentage
     * @param percentage Marks percentage
     * @return Grade string
     */
    private String calculateGrade(double percentage) {
        if (percentage >= 90) return "O";
        else if (percentage >= 80) return "A+";
        else if (percentage >= 70) return "A";
        else if (percentage >= 60) return "B+";
        else if (percentage >= 50) return "B";
        else if (percentage >= 40) return "C";
        else return "F";
    }
    
    /**
     * Extract Marks object from ResultSet
     * @param rs ResultSet
     * @return Marks object
     * @throws SQLException
     */
    private Marks extractMarksFromResultSet(ResultSet rs) throws SQLException {
        Marks marks = new Marks();
        marks.setMarkId(rs.getInt("mark_id"));
        marks.setEnrollmentId(rs.getInt("enrollment_id"));
        marks.setAssessmentTypeId(rs.getInt("assessment_type_id"));
        marks.setMaxMarks(rs.getDouble("max_marks"));
        marks.setMarksObtained(rs.getDouble("marks_obtained"));
        marks.setAssessmentDate(rs.getDate("assessment_date"));
        marks.setEnteredBy(rs.getInt("entered_by"));
        marks.setEnteredAt(rs.getTimestamp("entered_at"));
        marks.setUpdatedAt(rs.getTimestamp("updated_at"));
        marks.setRemarks(rs.getString("remarks"));
        
        // Additional display fields
        marks.setStudentName(rs.getString("student_name"));
        marks.setRollNumber(rs.getString("roll_number"));
        marks.setSubjectName(rs.getString("subject_name"));
        marks.setSubjectCode(rs.getString("subject_code"));
        marks.setAssessmentTypeName(rs.getString("type_name"));
        marks.setWeightage(rs.getDouble("weightage"));
        marks.setEnteredByName(rs.getString("entered_by_name"));
        
        // Auto-calculate grade
        marks.autoCalculateGrade();
        
        return marks;
    }
}
