package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Attendance Model Class for AttendEase
 * Represents attendance records for students
 */
public class Attendance {
    
    // Attendance attributes
    private int attendanceId;
    private int enrollmentId;
    private Date attendanceDate;
    private AttendanceStatus status;
    private int markedBy;
    private Timestamp markedAt;
    private String remarks;
    
    // Additional fields for display purposes
    private String studentName;
    private String rollNumber;
    private String subjectName;
    private String subjectCode;
    private String markedByName;
    
    /**
     * Attendance Status Enumeration
     */
    public enum AttendanceStatus {
        PRESENT, ABSENT, LATE, EXCUSED
    }
    
    /**
     * Default constructor
     */
    public Attendance() {
    }
    
    /**
     * Constructor with essential fields
     */
    public Attendance(int enrollmentId, Date attendanceDate, 
                     AttendanceStatus status, int markedBy) {
        this.enrollmentId = enrollmentId;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.markedBy = markedBy;
        this.markedAt = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Full constructor
     */
    public Attendance(int attendanceId, int enrollmentId, Date attendanceDate,
                     AttendanceStatus status, int markedBy, Timestamp markedAt, 
                     String remarks) {
        this.attendanceId = attendanceId;
        this.enrollmentId = enrollmentId;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.markedBy = markedBy;
        this.markedAt = markedAt;
        this.remarks = remarks;
    }
    
    // Getters and Setters
    
    public int getAttendanceId() {
        return attendanceId;
    }
    
    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }
    
    public int getEnrollmentId() {
        return enrollmentId;
    }
    
    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }
    
    public Date getAttendanceDate() {
        return attendanceDate;
    }
    
    public void setAttendanceDate(Date attendanceDate) {
        this.attendanceDate = attendanceDate;
    }
    
    public AttendanceStatus getStatus() {
        return status;
    }
    
    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }
    
    public int getMarkedBy() {
        return markedBy;
    }
    
    public void setMarkedBy(int markedBy) {
        this.markedBy = markedBy;
    }
    
    public Timestamp getMarkedAt() {
        return markedAt;
    }
    
    public void setMarkedAt(Timestamp markedAt) {
        this.markedAt = markedAt;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    public String getRollNumber() {
        return rollNumber;
    }
    
    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }
    
    public String getSubjectName() {
        return subjectName;
    }
    
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
    
    public String getSubjectCode() {
        return subjectCode;
    }
    
    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }
    
    public String getMarkedByName() {
        return markedByName;
    }
    
    public void setMarkedByName(String markedByName) {
        this.markedByName = markedByName;
    }
    
    /**
     * Get status as string
     */
    public String getStatusString() {
        return status != null ? status.name() : "UNKNOWN";
    }
    
    /**
     * Set status from string
     */
    public void setStatusFromString(String statusStr) {
        try {
            this.status = AttendanceStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.status = null;
        }
    }
    
    /**
     * Check if attendance is marked as present
     */
    public boolean isPresent() {
        return this.status == AttendanceStatus.PRESENT;
    }
    
    /**
     * Check if attendance is marked as absent
     */
    public boolean isAbsent() {
        return this.status == AttendanceStatus.ABSENT;
    }
    
    /**
     * Check if attendance counts as present (including LATE and EXCUSED)
     */
    public boolean countsAsPresent() {
        return this.status == AttendanceStatus.PRESENT || 
               this.status == AttendanceStatus.LATE ||
               this.status == AttendanceStatus.EXCUSED;
    }
    
    /**
     * Get attendance status color code for UI
     */
    public String getStatusColor() {
        if (status == null) return "#808080";
        
        switch (status) {
            case PRESENT:
                return "#28a745"; // Green
            case ABSENT:
                return "#dc3545"; // Red
            case LATE:
                return "#ffc107"; // Yellow
            case EXCUSED:
                return "#17a2b8"; // Blue
            default:
                return "#808080"; // Gray
        }
    }
    
    /**
     * Get attendance status icon for UI
     */
    public String getStatusIcon() {
        if (status == null) return "❓";
        
        switch (status) {
            case PRESENT:
                return "✓";
            case ABSENT:
                return "✗";
            case LATE:
                return "⏰";
            case EXCUSED:
                return "📝";
            default:
                return "❓";
        }
    }
    
    /**
     * Validate attendance data
     */
    public boolean isValid() {
        return enrollmentId > 0 && 
               attendanceDate != null && 
               status != null && 
               markedBy > 0;
    }
    
    /**
     * Format attendance date as string
     */
    public String getFormattedDate() {
        if (attendanceDate == null) return "";
        return attendanceDate.toString();
    }
    
    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", enrollmentId=" + enrollmentId +
                ", attendanceDate=" + attendanceDate +
                ", status=" + status +
                ", markedBy=" + markedBy +
                ", markedAt=" + markedAt +
                ", studentName='" + studentName + '\'' +
                ", subjectCode='" + subjectCode + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendance that = (Attendance) o;
        return attendanceId == that.attendanceId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(attendanceId);
    }
}
