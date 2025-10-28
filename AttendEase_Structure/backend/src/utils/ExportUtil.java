package utils;

import model.Report;
import model.Attendance;
import model.Marks;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Export Utility Class for AttendEase
 * Handles PDF and Excel export functionality
 * Note: Requires Apache POI for Excel and iText for PDF (add to lib folder)
 */
public class ExportUtil {
    
    private static final String EXPORT_DIR = "reports/";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    
    /**
     * Initialize export directory
     */
    static {
        File dir = new File(EXPORT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * Export attendance report to CSV
     * @param attendanceList List of attendance records
     * @param fileName File name
     * @return File path if successful, null otherwise
     */
    public static String exportAttendanceToCSV(List<Attendance> attendanceList, String fileName) {
        String timestamp = dateFormat.format(new Date());
        String filePath = EXPORT_DIR + fileName + "_" + timestamp + ".csv";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            
            // Write CSV header
            writer.println("Date,Roll Number,Student Name,Subject,Status,Remarks,Marked By");
            
            // Write attendance data
            for (Attendance attendance : attendanceList) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s%n",
                    attendance.getFormattedDate(),
                    attendance.getRollNumber() != null ? attendance.getRollNumber() : "",
                    attendance.getStudentName() != null ? attendance.getStudentName() : "",
                    attendance.getSubjectCode() != null ? attendance.getSubjectCode() : "",
                    attendance.getStatusString(),
                    attendance.getRemarks() != null ? attendance.getRemarks().replace(",", ";") : "",
                    attendance.getMarkedByName() != null ? attendance.getMarkedByName() : ""
                );
            }
            
            System.out.println("Attendance report exported to: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.err.println("Error exporting attendance to CSV: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Export marks report to CSV
     * @param marksList List of marks records
     * @param fileName File name
     * @return File path if successful, null otherwise
     */
    public static String exportMarksToCSV(List<Marks> marksList, String fileName) {
        String timestamp = dateFormat.format(new Date());
        String filePath = EXPORT_DIR + fileName + "_" + timestamp + ".csv";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            
            // Write CSV header
            writer.println("Roll Number,Student Name,Subject,Assessment Type,Marks Obtained,Max Marks,Percentage,Grade,Date");
            
            // Write marks data
            for (Marks marks : marksList) {
                writer.printf("%s,%s,%s,%s,%.2f,%.2f,%.2f%%,%s,%s%n",
                    marks.getRollNumber() != null ? marks.getRollNumber() : "",
                    marks.getStudentName() != null ? marks.getStudentName() : "",
                    marks.getSubjectCode() != null ? marks.getSubjectCode() : "",
                    marks.getAssessmentTypeName() != null ? marks.getAssessmentTypeName() : "",
                    marks.getMarksObtained(),
                    marks.getMaxMarks(),
                    marks.getPercentage(),
                    marks.calculateGrade(),
                    marks.getAssessmentDate() != null ? marks.getAssessmentDate().toString() : ""
                );
            }
            
            System.out.println("Marks report exported to: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.err.println("Error exporting marks to CSV: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Export comprehensive student report to CSV
     * @param report Report object
     * @param fileName File name
     * @return File path if successful, null otherwise
     */
    public static String exportStudentReportToCSV(Report report, String fileName) {
        String timestamp = dateFormat.format(new Date());
        String filePath = EXPORT_DIR + fileName + "_" + timestamp + ".csv";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            
            // Student Information
            writer.println("STUDENT PERFORMANCE REPORT");
            writer.println();
            writer.println("Student Information");
            writer.printf("Name,%s%n", report.getStudentName());
            writer.printf("Roll Number,%s%n", report.getRollNumber());
            writer.printf("Email,%s%n", report.getEmail());
            writer.printf("Semester,%d%n", report.getSemester());
            writer.println();
            
            // Subject Information
            writer.println("Subject Information");
            writer.printf("Subject,%s%n", report.getSubjectName());
            writer.printf("Subject Code,%s%n", report.getSubjectCode());
            writer.printf("Credits,%d%n", report.getCredits());
            writer.printf("Academic Year,%s%n", report.getAcademicYear());
            writer.println();
            
            // Attendance Statistics
            writer.println("Attendance Statistics");
            writer.printf("Total Classes,%d%n", report.getTotalClasses());
            writer.printf("Classes Attended,%d%n", report.getClassesAttended());
            writer.printf("Classes Absent,%d%n", report.getClassesAbsent());
            writer.printf("Classes Late,%d%n", report.getClassesLate());
            writer.printf("Attendance Percentage,%s%n", report.getFormattedAttendancePercentage());
            writer.println();
            
            // Marks Statistics
            writer.println("Marks Statistics");
            writer.printf("Total Marks Obtained,%.2f%n", report.getTotalMarksObtained());
            writer.printf("Total Max Marks,%.2f%n", report.getTotalMaxMarks());
            writer.printf("Overall Percentage,%s%n", report.getFormattedOverallPercentage());
            writer.printf("Overall Grade,%s%n", report.getOverallGrade());
            writer.printf("Performance Level,%s%n", report.getPerformanceLevel());
            writer.println();
            
            // Assessment-wise Marks
            if (report.getAssessmentMarks() != null && !report.getAssessmentMarks().isEmpty()) {
                writer.println("Assessment-wise Performance");
                writer.println("Assessment Type,Marks Obtained,Max Marks,Percentage,Grade,Weightage");
                
                for (Report.AssessmentMark am : report.getAssessmentMarks()) {
                    writer.printf("%s,%.2f,%.2f,%.2f%%,%s,%.2f%%%n",
                        am.getAssessmentType(),
                        am.getMarksObtained(),
                        am.getMaxMarks(),
                        am.getPercentage(),
                        am.getGrade(),
                        am.getWeightage()
                    );
                }
                writer.println();
            }
            
            // Risk Analysis
            writer.println("Risk Analysis");
            writer.printf("At Risk,%s%n", report.isAtRisk() ? "Yes" : "No");
            writer.printf("Risk Level,%s%n", report.getRiskLevel());
            writer.println();
            
            // Strengths and Weaknesses
            if (report.getStrengths() != null && !report.getStrengths().isEmpty()) {
                writer.println("Strengths");
                for (String strength : report.getStrengths()) {
                    writer.printf("- %s%n", strength);
                }
                writer.println();
            }
            
            if (report.getWeaknesses() != null && !report.getWeaknesses().isEmpty()) {
                writer.println("Areas for Improvement");
                for (String weakness : report.getWeaknesses()) {
                    writer.printf("- %s%n", weakness);
                }
                writer.println();
            }
            
            if (report.getRecommendations() != null && !report.getRecommendations().isEmpty()) {
                writer.println("Recommendations");
                for (String recommendation : report.getRecommendations()) {
                    writer.printf("- %s%n", recommendation);
                }
            }
            
            System.out.println("Student report exported to: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.err.println("Error exporting student report to CSV: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Export multiple student reports to CSV
     * @param reports List of reports
     * @param fileName File name
     * @return File path if successful, null otherwise
     */
    public static String exportMultipleReportsToCSV(List<Report> reports, String fileName) {
        String timestamp = dateFormat.format(new Date());
        String filePath = EXPORT_DIR + fileName + "_" + timestamp + ".csv";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            
            // Write CSV header
            writer.println("Roll Number,Student Name,Subject,Attendance %,Total Classes,Classes Attended," +
                         "Overall Marks %,Grade,Performance Level,At Risk,Risk Level");
            
            // Write report data
            for (Report report : reports) {
                writer.printf("%s,%s,%s,%.2f,%d,%d,%.2f,%s,%s,%s,%s%n",
                    report.getRollNumber() != null ? report.getRollNumber() : "",
                    report.getStudentName() != null ? report.getStudentName() : "",
                    report.getSubjectCode() != null ? report.getSubjectCode() : "",
                    report.getAttendancePercentage(),
                    report.getTotalClasses(),
                    report.getClassesAttended(),
                    report.getOverallPercentage(),
                    report.getOverallGrade() != null ? report.getOverallGrade() : "",
                    report.getPerformanceLevel() != null ? report.getPerformanceLevel() : "",
                    report.isAtRisk() ? "Yes" : "No",
                    report.getRiskLevel() != null ? report.getRiskLevel() : ""
                );
            }
            
            System.out.println("Multiple reports exported to: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.err.println("Error exporting multiple reports to CSV: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Export attendance summary to CSV
     * @param summaryData List of summary data maps
     * @param fileName File name
     * @return File path if successful, null otherwise
     */
    public static String exportAttendanceSummaryToCSV(List<Map<String, Object>> summaryData, String fileName) {
        String timestamp = dateFormat.format(new Date());
        String filePath = EXPORT_DIR + fileName + "_" + timestamp + ".csv";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            
            // Write CSV header
            writer.println("Date,Total Students,Present,Absent,Late,Attendance Percentage");
            
            // Write summary data
            for (Map<String, Object> data : summaryData) {
                writer.printf("%s,%d,%d,%d,%d,%.2f%%%n",
                    data.get("date"),
                    data.get("totalStudents"),
                    data.get("present"),
                    data.get("absent"),
                    data.get("late"),
                    data.get("percentage")
                );
            }
            
            System.out.println("Attendance summary exported to: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.err.println("Error exporting attendance summary to CSV: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Generate simple text-based PDF report (without external libraries)
     * This is a placeholder - for production, use iText or Apache PDFBox
     * @param report Report object
     * @param fileName File name
     * @return File path if successful, null otherwise
     */
    public static String exportReportToText(Report report, String fileName) {
        String timestamp = dateFormat.format(new Date());
        String filePath = EXPORT_DIR + fileName + "_" + timestamp + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            
            writer.println("═══════════════════════════════════════════════════════════");
            writer.println("              STUDENT PERFORMANCE REPORT");
            writer.println("         SRM Institute of Science and Technology");
            writer.println("═══════════════════════════════════════════════════════════");
            writer.println();
            
            // Student Information
            writer.println("STUDENT INFORMATION");
            writer.println("───────────────────────────────────────────────────────────");
            writer.printf("Name           : %s%n", report.getStudentName());
            writer.printf("Roll Number    : %s%n", report.getRollNumber());
            writer.printf("Email          : %s%n", report.getEmail());
            writer.printf("Semester       : %d%n", report.getSemester());
            writer.println();
            
            // Subject Information
            writer.println("SUBJECT INFORMATION");
            writer.println("───────────────────────────────────────────────────────────");
            writer.printf("Subject        : %s%n", report.getSubjectName());
            writer.printf("Subject Code   : %s%n", report.getSubjectCode());
            writer.printf("Credits        : %d%n", report.getCredits());
            writer.printf("Academic Year  : %s%n", report.getAcademicYear());
            writer.println();
            
            // Attendance Statistics
            writer.println("ATTENDANCE STATISTICS");
            writer.println("───────────────────────────────────────────────────────────");
            writer.printf("Total Classes       : %d%n", report.getTotalClasses());
            writer.printf("Classes Attended    : %d%n", report.getClassesAttended());
            writer.printf("Classes Absent      : %d%n", report.getClassesAbsent());
            writer.printf("Classes Late        : %d%n", report.getClassesLate());
            writer.printf("Attendance %%        : %s%n", report.getFormattedAttendancePercentage());
            writer.println();
            
            // Marks Statistics
            writer.println("MARKS STATISTICS");
            writer.println("───────────────────────────────────────────────────────────");
            writer.printf("Total Marks Obtained : %.2f%n", report.getTotalMarksObtained());
            writer.printf("Total Max Marks      : %.2f%n", report.getTotalMaxMarks());
            writer.printf("Overall Percentage   : %s%n", report.getFormattedOverallPercentage());
            writer.printf("Overall Grade        : %s%n", report.getOverallGrade());
            writer.printf("Performance Level    : %s%n", report.getPerformanceLevel());
            writer.println();
            
            // Assessment-wise Performance
            if (report.getAssessmentMarks() != null && !report.getAssessmentMarks().isEmpty()) {
                writer.println("ASSESSMENT-WISE PERFORMANCE");
                writer.println("───────────────────────────────────────────────────────────");
                writer.printf("%-20s %-10s %-10s %-12s %-8s%n", 
                    "Assessment", "Obtained", "Max", "Percentage", "Grade");
                writer.println("───────────────────────────────────────────────────────────");
                
                for (Report.AssessmentMark am : report.getAssessmentMarks()) {
                    writer.printf("%-20s %-10.2f %-10.2f %-12.2f%% %-8s%n",
                        am.getAssessmentType(),
                        am.getMarksObtained(),
                        am.getMaxMarks(),
                        am.getPercentage(),
                        am.getGrade()
                    );
                }
                writer.println();
            }
            
            // Risk Analysis
            writer.println("RISK ANALYSIS");
            writer.println("───────────────────────────────────────────────────────────");
            writer.printf("At Risk        : %s%n", report.isAtRisk() ? "Yes" : "No");
            writer.printf("Risk Level     : %s%n", report.getRiskLevel());
            writer.println();
            
            // Strengths, Weaknesses, and Recommendations
            if (report.getStrengths() != null && !report.getStrengths().isEmpty()) {
                writer.println("STRENGTHS");
                writer.println("───────────────────────────────────────────────────────────");
                for (String strength : report.getStrengths()) {
                    writer.printf("• %s%n", strength);
                }
                writer.println();
            }
            
            if (report.getWeaknesses() != null && !report.getWeaknesses().isEmpty()) {
                writer.println("AREAS FOR IMPROVEMENT");
                writer.println("───────────────────────────────────────────────────────────");
                for (String weakness : report.getWeaknesses()) {
                    writer.printf("• %s%n", weakness);
                }
                writer.println();
            }
            
            if (report.getRecommendations() != null && !report.getRecommendations().isEmpty()) {
                writer.println("RECOMMENDATIONS");
                writer.println("───────────────────────────────────────────────────────────");
                for (String recommendation : report.getRecommendations()) {
                    writer.printf("• %s%n", recommendation);
                }
                writer.println();
            }
            
            writer.println("═══════════════════════════════════════════════════════════");
            writer.printf("Generated on: %s%n", new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date()));
            writer.println("═══════════════════════════════════════════════════════════");
            
            System.out.println("Report exported to: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.err.println("Error exporting report to text: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Delete old export files (cleanup)
     * @param daysOld Delete files older than specified days
     * @return Number of files deleted
     */
    public static int cleanupOldExports(int daysOld) {
        File dir = new File(EXPORT_DIR);
        File[] files = dir.listFiles();
        int deletedCount = 0;
        
        if (files != null) {
            long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60 * 60 * 1000);
            
            for (File file : files) {
                if (file.isFile() && file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        deletedCount++;
                    }
                }
            }
        }
        
        System.out.println("Cleaned up " + deletedCount + " old export files");
        return deletedCount;
    }
}
