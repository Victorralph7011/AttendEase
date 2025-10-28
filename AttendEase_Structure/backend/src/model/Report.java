package model;

import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Report Model Class for AttendEase
 * Represents various types of reports (Attendance, Marks, Performance)
 */
public class Report {
    
    // Report attributes
    private int reportId;
    private String reportType;
    private int studentId;
    private int subjectId;
    private String academicYear;
    private Date generatedDate;
    private int generatedBy;
    
    // Student information
    private String studentName;
    private String rollNumber;
    private String email;
    private int semester;
    
    // Subject information
    private String subjectName;
    private String subjectCode;
    private int credits;
    
    // Attendance statistics
    private int totalClasses;
    private int classesAttended;
    private int classesAbsent;
    private int classesLate;
    private int classesExcused;
    private double attendancePercentage;
    
    // Marks statistics
    private double totalMarksObtained;
    private double totalMaxMarks;
    private double overallPercentage;
    private String overallGrade;
    
    // Assessment-wise marks
    private List<AssessmentMark> assessmentMarks;
    
    // Performance insights
    private String performanceLevel;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> recommendations;
    
    // Report status
    private boolean isAtRisk;
    private String riskLevel; // LOW, MEDIUM, HIGH
    
    /**
     * Inner class for assessment-wise marks
     */
    public static class AssessmentMark {
        private String assessmentType;
        private double marksObtained;
        private double maxMarks;
        private double percentage;
        private String grade;
        private double weightage;
        
        public AssessmentMark() {}
        
        public AssessmentMark(String assessmentType, double marksObtained, 
                            double maxMarks, double weightage) {
            this.assessmentType = assessmentType;
            this.marksObtained = marksObtained;
            this.maxMarks = maxMarks;
            this.weightage = weightage;
            this.percentage = (maxMarks > 0) ? (marksObtained / maxMarks) * 100 : 0;
            this.grade = calculateGrade(this.percentage);
        }
        
        private String calculateGrade(double percentage) {
            if (percentage >= 90) return "O";
            else if (percentage >= 80) return "A+";
            else if (percentage >= 70) return "A";
            else if (percentage >= 60) return "B+";
            else if (percentage >= 50) return "B";
            else if (percentage >= 40) return "C";
            else return "F";
        }
        
        // Getters and Setters
        public String getAssessmentType() { return assessmentType; }
        public void setAssessmentType(String assessmentType) { this.assessmentType = assessmentType; }
        public double getMarksObtained() { return marksObtained; }
        public void setMarksObtained(double marksObtained) { this.marksObtained = marksObtained; }
        public double getMaxMarks() { return maxMarks; }
        public void setMaxMarks(double maxMarks) { this.maxMarks = maxMarks; }
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
        public double getWeightage() { return weightage; }
        public void setWeightage(double weightage) { this.weightage = weightage; }
    }
    
    /**
     * Default constructor
     */
    public Report() {
        this.assessmentMarks = new ArrayList<>();
        this.strengths = new ArrayList<>();
        this.weaknesses = new ArrayList<>();
        this.recommendations = new ArrayList<>();
    }
    
    /**
     * Constructor for attendance report
     */
    public Report(int studentId, int subjectId, String academicYear, String reportType) {
        this();
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.academicYear = academicYear;
        this.reportType = reportType;
        this.generatedDate = new Date(System.currentTimeMillis());
    }
    
    // Getters and Setters
    
    public int getReportId() {
        return reportId;
    }
    
    public void setReportId(int reportId) {
        this.reportId = reportId;
    }
    
    public String getReportType() {
        return reportType;
    }
    
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    
    public int getStudentId() {
        return studentId;
    }
    
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    
    public int getSubjectId() {
        return subjectId;
    }
    
    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }
    
    public String getAcademicYear() {
        return academicYear;
    }
    
    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
    
    public Date getGeneratedDate() {
        return generatedDate;
    }
    
    public void setGeneratedDate(Date generatedDate) {
        this.generatedDate = generatedDate;
    }
    
    public int getGeneratedBy() {
        return generatedBy;
    }
    
    public void setGeneratedBy(int generatedBy) {
        this.generatedBy = generatedBy;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public int getSemester() {
        return semester;
    }
    
    public void setSemester(int semester) {
        this.semester = semester;
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
    
    public int getCredits() {
        return credits;
    }
    
    public void setCredits(int credits) {
        this.credits = credits;
    }
    
    public int getTotalClasses() {
        return totalClasses;
    }
    
    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }
    
    public int getClassesAttended() {
        return classesAttended;
    }
    
    public void setClassesAttended(int classesAttended) {
        this.classesAttended = classesAttended;
    }
    
    public int getClassesAbsent() {
        return classesAbsent;
    }
    
    public void setClassesAbsent(int classesAbsent) {
        this.classesAbsent = classesAbsent;
    }
    
    public int getClassesLate() {
        return classesLate;
    }
    
    public void setClassesLate(int classesLate) {
        this.classesLate = classesLate;
    }
    
    public int getClassesExcused() {
        return classesExcused;
    }
    
    public void setClassesExcused(int classesExcused) {
        this.classesExcused = classesExcused;
    }
    
    public double getAttendancePercentage() {
        return attendancePercentage;
    }
    
    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }
    
    public double getTotalMarksObtained() {
        return totalMarksObtained;
    }
    
    public void setTotalMarksObtained(double totalMarksObtained) {
        this.totalMarksObtained = totalMarksObtained;
    }
    
    public double getTotalMaxMarks() {
        return totalMaxMarks;
    }
    
    public void setTotalMaxMarks(double totalMaxMarks) {
        this.totalMaxMarks = totalMaxMarks;
    }
    
    public double getOverallPercentage() {
        return overallPercentage;
    }
    
    public void setOverallPercentage(double overallPercentage) {
        this.overallPercentage = overallPercentage;
    }
    
    public String getOverallGrade() {
        return overallGrade;
    }
    
    public void setOverallGrade(String overallGrade) {
        this.overallGrade = overallGrade;
    }
    
    public List<AssessmentMark> getAssessmentMarks() {
        return assessmentMarks;
    }
    
    public void setAssessmentMarks(List<AssessmentMark> assessmentMarks) {
        this.assessmentMarks = assessmentMarks;
    }
    
    public String getPerformanceLevel() {
        return performanceLevel;
    }
    
    public void setPerformanceLevel(String performanceLevel) {
        this.performanceLevel = performanceLevel;
    }
    
    public List<String> getStrengths() {
        return strengths;
    }
    
    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }
    
    public List<String> getWeaknesses() {
        return weaknesses;
    }
    
    public void setWeaknesses(List<String> weaknesses) {
        this.weaknesses = weaknesses;
    }
    
    public List<String> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
    
    public boolean isAtRisk() {
        return isAtRisk;
    }
    
    public void setAtRisk(boolean atRisk) {
        isAtRisk = atRisk;
    }
    
    public String getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    /**
     * Calculate attendance percentage
     */
    public void calculateAttendancePercentage() {
        if (totalClasses > 0) {
            this.attendancePercentage = ((double) classesAttended / totalClasses) * 100.0;
        } else {
            this.attendancePercentage = 0.0;
        }
    }
    
    /**
     * Calculate overall percentage from marks
     */
    public void calculateOverallPercentage() {
        if (totalMaxMarks > 0) {
            this.overallPercentage = (totalMarksObtained / totalMaxMarks) * 100.0;
        } else {
            this.overallPercentage = 0.0;
        }
    }
    
    /**
     * Calculate overall grade
     */
    public void calculateOverallGrade() {
        if (overallPercentage >= 90) this.overallGrade = "O";
        else if (overallPercentage >= 80) this.overallGrade = "A+";
        else if (overallPercentage >= 70) this.overallGrade = "A";
        else if (overallPercentage >= 60) this.overallGrade = "B+";
        else if (overallPercentage >= 50) this.overallGrade = "B";
        else if (overallPercentage >= 40) this.overallGrade = "C";
        else this.overallGrade = "F";
    }
    
    /**
     * Analyze risk level based on attendance and marks
     */
    public void analyzeRiskLevel() {
        boolean lowAttendance = attendancePercentage < 75;
        boolean failingGrades = overallPercentage < 40;
        boolean poorPerformance = overallPercentage < 50;
        
        if (failingGrades || (lowAttendance && poorPerformance)) {
            this.isAtRisk = true;
            this.riskLevel = "HIGH";
        } else if (lowAttendance || poorPerformance) {
            this.isAtRisk = true;
            this.riskLevel = "MEDIUM";
        } else if (attendancePercentage < 85 || overallPercentage < 60) {
            this.isAtRisk = false;
            this.riskLevel = "LOW";
        } else {
            this.isAtRisk = false;
            this.riskLevel = "NONE";
        }
    }
    
    /**
     * Generate performance insights
     */
    public void generateInsights() {
        // Analyze strengths
        if (attendancePercentage >= 90) {
            strengths.add("Excellent attendance record");
        }
        if (overallPercentage >= 80) {
            strengths.add("Strong academic performance");
        }
        
        // Analyze weaknesses
        if (attendancePercentage < 75) {
            weaknesses.add("Low attendance - below 75% threshold");
        }
        if (overallPercentage < 50) {
            weaknesses.add("Poor academic performance");
        }
        
        // Generate recommendations
        if (attendancePercentage < 75) {
            recommendations.add("Improve attendance to meet minimum requirement");
        }
        if (overallPercentage < 40) {
            recommendations.add("Seek additional tutoring or academic support");
        }
        if (overallPercentage >= 40 && overallPercentage < 60) {
            recommendations.add("Focus on consistent study habits");
        }
    }
    
    /**
     * Add assessment mark to report
     */
    public void addAssessmentMark(AssessmentMark mark) {
        this.assessmentMarks.add(mark);
    }
    
    /**
     * Get formatted attendance percentage
     */
    public String getFormattedAttendancePercentage() {
        return String.format("%.2f%%", attendancePercentage);
    }
    
    /**
     * Get formatted overall percentage
     */
    public String getFormattedOverallPercentage() {
        return String.format("%.2f%%", overallPercentage);
    }
    
    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", reportType='" + reportType + '\'' +
                ", studentName='" + studentName + '\'' +
                ", rollNumber='" + rollNumber + '\'' +
                ", subjectCode='" + subjectCode + '\'' +
                ", attendancePercentage=" + getFormattedAttendancePercentage() +
                ", overallPercentage=" + getFormattedOverallPercentage() +
                ", overallGrade='" + overallGrade + '\'' +
                ", isAtRisk=" + isAtRisk +
                ", riskLevel='" + riskLevel + '\'' +
                '}';
    }
}
