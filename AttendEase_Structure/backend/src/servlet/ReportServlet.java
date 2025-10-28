package servlet;

import dao.ReportDAO;
import model.Report;
import utils.ExportUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Report Servlet for AttendEase
 * Handles report generation and export operations
 */
@WebServlet("/reports")
public class ReportServlet extends HttpServlet {
    
    private ReportDAO reportDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        reportDAO = new ReportDAO();
    }
    
    /**
     * Handle GET request - Generate and retrieve reports
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }
        
        String action = request.getParameter("action");
        
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action parameter is required");
            return;
        }
        
        try {
            switch (action) {
                case "generateStudent":
                    generateStudentReport(request, response);
                    break;
                    
                case "generateSubjectAttendance":
                    generateSubjectAttendanceReport(request, response);
                    break;
                    
                case "generateSubjectMarks":
                    generateSubjectMarksReport(request, response);
                    break;
                    
                case "generateAllSubjects":
                    generateAllSubjectsReport(request, response);
                    break;
                    
                case "getAtRiskStudents":
                    getAtRiskStudents(request, response);
                    break;
                    
                case "getSubjectSummary":
                    getSubjectSummary(request, response);
                    break;
                    
                case "exportCSV":
                    exportReportCSV(request, response);
                    break;
                    
                case "exportText":
                    exportReportText(request, response);
                    break;
                    
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
            
        } catch (Exception e) {
            System.err.println("Error in ReportServlet: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error generating report");
        }
    }
    
    /**
     * Generate student report
     */
    private void generateStudentReport(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String studentIdStr = request.getParameter("studentId");
            String subjectIdStr = request.getParameter("subjectId");
            String academicYear = request.getParameter("academicYear");
            
            if (studentIdStr == null || subjectIdStr == null || academicYear == null) {
                sendErrorResponse(out, "Missing required parameters", 400);
                return;
            }
            
            int studentId = Integer.parseInt(studentIdStr);
            int subjectId = Integer.parseInt(subjectIdStr);
            
            Report report = reportDAO.generateStudentReport(studentId, subjectId, academicYear);
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", true);
            jsonResponse.put("data", convertReportToJSON(report));
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error generating student report: " + e.getMessage());
            sendErrorResponse(out, "Error generating report", 500);
        }
    }
    
    /**
     * Generate subject attendance report
     */
    private void generateSubjectAttendanceReport(HttpServletRequest request, 
                                                 HttpServletResponse response) throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String subjectIdStr = request.getParameter("subjectId");
            String academicYear = request.getParameter("academicYear");
            
            if (subjectIdStr == null || academicYear == null) {
                sendErrorResponse(out, "Missing required parameters", 400);
                return;
            }
            
            int subjectId = Integer.parseInt(subjectIdStr);
            
            List<Report> reports = reportDAO.generateSubjectAttendanceReport(
                subjectId, academicYear
            );
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", true);
            jsonResponse.put("data", convertReportListToJSON(reports));
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error generating attendance report: " + e.getMessage());
            sendErrorResponse(out, "Error generating report", 500);
        }
    }
    
    /**
     * Generate subject marks report
     */
    private void generateSubjectMarksReport(HttpServletRequest request, 
                                           HttpServletResponse response) throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String subjectIdStr = request.getParameter("subjectId");
            String academicYear = request.getParameter("academicYear");
            
            if (subjectIdStr == null || academicYear == null) {
                sendErrorResponse(out, "Missing required parameters", 400);
                return;
            }
            
            int subjectId = Integer.parseInt(subjectIdStr);
            
            List<Report> reports = reportDAO.generateSubjectMarksReport(subjectId, academicYear);
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", true);
            jsonResponse.put("data", convertReportListToJSON(reports));
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error generating marks report: " + e.getMessage());
            sendErrorResponse(out, "Error generating report", 500);
        }
    }
    
    /**
     * Generate all subjects report for a student
     */
    private void generateAllSubjectsReport(HttpServletRequest request, 
                                          HttpServletResponse response) throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String studentIdStr = request.getParameter("studentId");
            String academicYear = request.getParameter("academicYear");
            
            if (studentIdStr == null || academicYear == null) {
                sendErrorResponse(out, "Missing required parameters", 400);
                return;
            }
            
            int studentId = Integer.parseInt(studentIdStr);
            
            List<Report> reports = reportDAO.generateStudentAllSubjectsReport(
                studentId, academicYear
            );
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", true);
            jsonResponse.put("data", convertReportListToJSON(reports));
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error generating all subjects report: " + e.getMessage());
            sendErrorResponse(out, "Error generating report", 500);
        }
    }
    
    /**
     * Get at-risk students
     */
    private void getAtRiskStudents(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String academicYear = request.getParameter("academicYear");
            
            if (academicYear == null) {
                sendErrorResponse(out, "Academic year is required", 400);
                return;
            }
            
            List<Report> reports = reportDAO.getAtRiskStudents(academicYear);
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", true);
            jsonResponse.put("data", convertReportListToJSON(reports));
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error getting at-risk students: " + e.getMessage());
            sendErrorResponse(out, "Error retrieving data", 500);
        }
    }
    
    /**
     * Get subject performance summary
     */
    private void getSubjectSummary(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String subjectIdStr = request.getParameter("subjectId");
            String academicYear = request.getParameter("academicYear");
            
            if (subjectIdStr == null || academicYear == null) {
                sendErrorResponse(out, "Missing required parameters", 400);
                return;
            }
            
            int subjectId = Integer.parseInt(subjectIdStr);
            
            Map<String, Object> summary = reportDAO.getSubjectPerformanceSummary(
                subjectId, academicYear
            );
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", true);
            jsonResponse.put("data", new JSONObject(summary));
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error getting subject summary: " + e.getMessage());
            sendErrorResponse(out, "Error retrieving summary", 500);
        }
    }
    
    /**
     * Export report to CSV
     */
    private void exportReportCSV(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            String studentIdStr = request.getParameter("studentId");
            String subjectIdStr = request.getParameter("subjectId");
            String academicYear = request.getParameter("academicYear");
            
            if (studentIdStr == null || subjectIdStr == null || academicYear == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
                return;
            }
            
            int studentId = Integer.parseInt(studentIdStr);
            int subjectId = Integer.parseInt(subjectIdStr);
            
            Report report = reportDAO.generateStudentReport(studentId, subjectId, academicYear);
            
            String filename = "report_" + report.getRollNumber() + "_" + 
                            report.getSubjectCode();
            String filePath = ExportUtil.exportStudentReportToCSV(report, filename);
            
            if (filePath != null) {
                // Send file for download
                File file = new File(filePath);
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", 
                    "attachment; filename=\"" + file.getName() + "\"");
                
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        response.getOutputStream().write(buffer, 0, bytesRead);
                    }
                }
                
                // Delete file after sending
                file.delete();
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Failed to generate report");
            }
            
        } catch (Exception e) {
            System.err.println("Error exporting report: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error exporting report");
        }
    }
    
    /**
     * Export report to text
     */
    private void exportReportText(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            String studentIdStr = request.getParameter("studentId");
            String subjectIdStr = request.getParameter("subjectId");
            String academicYear = request.getParameter("academicYear");
            
            if (studentIdStr == null || subjectIdStr == null || academicYear == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
                return;
            }
            
            int studentId = Integer.parseInt(studentIdStr);
            int subjectId = Integer.parseInt(subjectIdStr);
            
            Report report = reportDAO.generateStudentReport(studentId, subjectId, academicYear);
            
            String filename = "report_" + report.getRollNumber() + "_" + 
                            report.getSubjectCode();
            String filePath = ExportUtil.exportReportToText(report, filename);
            
            if (filePath != null) {
                File file = new File(filePath);
                response.setContentType("text/plain");
                response.setHeader("Content-Disposition", 
                    "attachment; filename=\"" + file.getName() + "\"");
                
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        response.getOutputStream().write(buffer, 0, bytesRead);
                    }
                }
                
                file.delete();
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Failed to generate report");
            }
            
        } catch (Exception e) {
            System.err.println("Error exporting report: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error exporting report");
        }
    }
    
    /**
     * Convert Report to JSON
     */
    private JSONObject convertReportToJSON(Report report) {
        JSONObject jsonObj = new JSONObject();
        
        // Student info
        jsonObj.put("studentName", report.getStudentName());
        jsonObj.put("rollNumber", report.getRollNumber());
        jsonObj.put("email", report.getEmail());
        jsonObj.put("semester", report.getSemester());
        
        // Subject info
        jsonObj.put("subjectName", report.getSubjectName());
        jsonObj.put("subjectCode", report.getSubjectCode());
        jsonObj.put("credits", report.getCredits());
        jsonObj.put("academicYear", report.getAcademicYear());
        
        // Attendance
        jsonObj.put("totalClasses", report.getTotalClasses());
        jsonObj.put("classesAttended", report.getClassesAttended());
        jsonObj.put("classesAbsent", report.getClassesAbsent());
        jsonObj.put("attendancePercentage", report.getAttendancePercentage());
        
        // Marks
        jsonObj.put("totalMarksObtained", report.getTotalMarksObtained());
        jsonObj.put("totalMaxMarks", report.getTotalMaxMarks());
        jsonObj.put("overallPercentage", report.getOverallPercentage());
        jsonObj.put("overallGrade", report.getOverallGrade());
        jsonObj.put("performanceLevel", report.getPerformanceLevel());
        
        // Assessment marks
        JSONArray assessments = new JSONArray();
        if (report.getAssessmentMarks() != null) {
            for (Report.AssessmentMark am : report.getAssessmentMarks()) {
                JSONObject assessment = new JSONObject();
                assessment.put("type", am.getAssessmentType());
                assessment.put("marksObtained", am.getMarksObtained());
                assessment.put("maxMarks", am.getMaxMarks());
                assessment.put("percentage", am.getPercentage());
                assessment.put("grade", am.getGrade());
                assessments.put(assessment);
            }
        }
        jsonObj.put("assessments", assessments);
        
        // Risk analysis
        jsonObj.put("isAtRisk", report.isAtRisk());
        jsonObj.put("riskLevel", report.getRiskLevel());
        
        // Insights
        jsonObj.put("strengths", new JSONArray(report.getStrengths()));
        jsonObj.put("weaknesses", new JSONArray(report.getWeaknesses()));
        jsonObj.put("recommendations", new JSONArray(report.getRecommendations()));
        
        return jsonObj;
    }
    
    /**
     * Convert Report list to JSON
     */
    private JSONArray convertReportListToJSON(List<Report> reports) {
        JSONArray jsonArray = new JSONArray();
        
        for (Report report : reports) {
            jsonArray.put(convertReportToJSON(report));
        }
        
        return jsonArray;
    }
    
    /**
     * Send error response
     */
    private void sendErrorResponse(PrintWriter out, String message, int statusCode) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", false);
        jsonResponse.put("message", message);
        jsonResponse.put("statusCode", statusCode);
        out.print(jsonResponse.toString());
    }
}
