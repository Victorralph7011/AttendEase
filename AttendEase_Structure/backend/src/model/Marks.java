package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Marks Model Class for AttendEase
 * Represents marks/grades for student assessments
 */
public class Marks {
    
    // Marks attributes
    private int markId;
    private int enrollmentId;
    private int assessmentTypeId;
    private double maxMarks;
    private double marksObtained;
    private Date assessmentDate;
    private int enteredBy;
    private Timestamp enteredAt;
    private Timestamp updatedAt;
    private String remarks;
    
    // Additional fields for display purposes
    private String studentName;
    private String rollNumber;
    private String subjectName;
    private String subjectCode;
    private String assessmentTypeName;
    private double weightage;
    private String enteredByName;
    private String grade;
    
    /**
     * Default constructor
     */
    public Marks() {
    }
    
    /**
     * Constructor with essential fields
     */
    public Marks(int enrollmentId, int assessmentTypeId, double maxMarks,
                 double marksObtained, Date assessmentDate, int enteredBy) {
        this.enrollmentId = enrollmentId;
        this.assessmentTypeId = assessmentTypeId;
        this.maxMarks = maxMarks;
        this.marksObtained = marksObtained;
        this.assessmentDate = assessmentDate;
        this.enteredBy = enteredBy;
        this.enteredAt = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Full constructor
     */
    public Marks(int markId, int enrollmentId, int assessmentTypeId,
                 double maxMarks, double marksObtained, Date assessmentDate,
                 int enteredBy, Timestamp enteredAt, Timestamp updatedAt, String remarks) {
        this.markId = markId;
        this.enrollmentId = enrollmentId;
        this.assessmentTypeId = assessmentTypeId;
        this.maxMarks = maxMarks;
        this.marksObtained = marksObtained;
        this.assessmentDate = assessmentDate;
        this.enteredBy = enteredBy;
        this.enteredAt = enteredAt;
        this.updatedAt = updatedAt;
        this.remarks = remarks;
    }
    
    // Getters and Setters
    
    public int getMarkId() {
        return markId;
    }
    
    public void setMarkId(int markId) {
        this.markId = markId;
    }
    
    public int getEnrollmentId() {
        return enrollmentId;
    }
    
    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }
    
    public int getAssessmentTypeId() {
        return assessmentTypeId;
    }
    
    public void setAssessmentTypeId(int assessmentTypeId) {
        this.assessmentTypeId = assessmentTypeId;
    }
    
    public double getMaxMarks() {
        return maxMarks;
    }
    
    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
    }
    
    public double getMarksObtained() {
        return marksObtained;
    }
    
    public void setMarksObtained(double marksObtained) {
        this.marksObtained = marksObtained;
    }
    
    public Date getAssessmentDate() {
        return assessmentDate;
    }
    
    public void setAssessmentDate(Date assessmentDate) {
        this.assessmentDate = assessmentDate;
    }
    
    public int getEnteredBy() {
        return enteredBy;
    }
    
    public void setEnteredBy(int enteredBy) {
        this.enteredBy = enteredBy;
    }
    
    public Timestamp getEnteredAt() {
        return enteredAt;
    }
    
    public void setEnteredAt(Timestamp enteredAt) {
        this.enteredAt = enteredAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
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
    
    public String getAssessmentTypeName() {
        return assessmentTypeName;
    }
    
    public void setAssessmentTypeName(String assessmentTypeName) {
        this.assessmentTypeName = assessmentTypeName;
    }
    
    public double getWeightage() {
        return weightage;
    }
    
    public void setWeightage(double weightage) {
        this.weightage = weightage;
    }
    
    public String getEnteredByName() {
        return enteredByName;
    }
    
    public void setEnteredByName(String enteredByName) {
        this.enteredByName = enteredByName;
    }
    
    public String getGrade() {
        return grade;
    }
    
    public void setGrade(String grade) {
        this.grade = grade;
    }
    
    /**
     * Calculate percentage
     */
    public double getPercentage() {
        if (maxMarks <= 0) return 0.0;
        return (marksObtained / maxMarks) * 100.0;
    }
    
    /**
     * Calculate grade based on percentage
     * O: 90-100, A+: 80-89, A: 70-79, B+: 60-69, B: 50-59, C: 40-49, F: <40
     */
    public String calculateGrade() {
        double percentage = getPercentage();
        
        if (percentage >= 90) return "O";
        else if (percentage >= 80) return "A+";
        else if (percentage >= 70) return "A";
        else if (percentage >= 60) return "B+";
        else if (percentage >= 50) return "B";
        else if (percentage >= 40) return "C";
        else return "F";
    }
    
    /**
     * Auto-set grade based on marks
     */
    public void autoCalculateGrade() {
        this.grade = calculateGrade();
    }
    
    /**
     * Get grade color for UI
     */
    public String getGradeColor() {
        String calculatedGrade = grade != null ? grade : calculateGrade();
        
        switch (calculatedGrade) {
            case "O":
            case "A+":
                return "#28a745"; // Green
            case "A":
            case "B+":
                return "#17a2b8"; // Blue
            case "B":
            case "C":
                return "#ffc107"; // Yellow
            case "F":
                return "#dc3545"; // Red
            default:
                return "#808080"; // Gray
        }
    }
    
    /**
     * Check if student passed
     */
    public boolean isPassed() {
        return getPercentage() >= 40.0;
    }
    
    /**
     * Get performance level
     */
    public String getPerformanceLevel() {
        double percentage = getPercentage();
        
        if (percentage >= 90) return "Outstanding";
        else if (percentage >= 80) return "Excellent";
        else if (percentage >= 70) return "Very Good";
        else if (percentage >= 60) return "Good";
        else if (percentage >= 50) return "Average";
        else if (percentage >= 40) return "Below Average";
        else return "Poor";
    }
    
    /**
     * Calculate weighted marks based on assessment weightage
     */
    public double getWeightedMarks() {
        if (weightage <= 0) return 0.0;
        return (marksObtained / maxMarks) * weightage;
    }
    
    /**
     * Validate marks data
     */
    public boolean isValid() {
        return enrollmentId > 0 && 
               assessmentTypeId > 0 &&
               maxMarks > 0 && 
               marksObtained >= 0 && 
               marksObtained <= maxMarks &&
               assessmentDate != null &&
               enteredBy > 0;
    }
    
    /**
     * Format percentage with 2 decimal places
     */
    public String getFormattedPercentage() {
        return String.format("%.2f%%", getPercentage());
    }
    
    /**
     * Format marks display
     */
    public String getMarksDisplay() {
        return String.format("%.2f / %.2f", marksObtained, maxMarks);
    }
    
    @Override
    public String toString() {
        return "Marks{" +
                "markId=" + markId +
                ", enrollmentId=" + enrollmentId +
                ", assessmentTypeId=" + assessmentTypeId +
                ", maxMarks=" + maxMarks +
                ", marksObtained=" + marksObtained +
                ", percentage=" + getFormattedPercentage() +
                ", grade=" + calculateGrade() +
                ", assessmentDate=" + assessmentDate +
                ", studentName='" + studentName + '\'' +
                ", subjectCode='" + subjectCode + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Marks marks = (Marks) o;
        return markId == marks.markId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(markId);
    }
}
