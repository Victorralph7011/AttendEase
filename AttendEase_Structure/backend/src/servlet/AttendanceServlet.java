package servlet;

import dao.MarksDAO;
import model.Marks;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Marks Servlet for AttendEase
 * Handles marks entry and retrieval operations
 */
@WebServlet("/marks")
public class MarksServlet extends HttpServlet {
    
    private MarksDAO marksDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        marksDAO = new MarksDAO();
    }
    
    /**
     * Handle GET request - Retrieve marks data
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                sendErrorResponse(out, "Unauthorized", 401);
                return;
            }
            
            String action = request.getParameter("action");
            
            if (action == null) {
                sendErrorResponse(out, "Action parameter is required", 400);
                return;
            }
            
            switch (action) {
                case "getByStudent":
                    getMarksByStudent(request, out);
                    break;
                    
                case "getBySubjectAndAssessment":
                    getMarksBySubjectAndAssessment(request, out);
                    break;
                    
                case "getStatistics":
                    getMarksStatistics(request, out);
                    break;
                    
                case "getWeightedMarks":
                    getWeightedMarks(request, out);
                    break;
                    
                case "getFailingStudents":
                    getFailingStudents(request, out);
                    break;
                    
                case "getTopPerformers":
                    getTopPerformers(request, out);
                    break;
                    
                default:
                    sendErrorResponse(out, "Invalid action", 400);
            }
            
        } catch (Exception e) {
            System.err.println("Error in MarksServlet GET: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(out, "Internal server error", 500);
        }
    }
    
    /**
     * Handle POST request - Add marks
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                sendErrorResponse(out, "Unauthorized", 401);
                return;
            }
            
            int userId = (Integer) session.getAttribute("userId");
            String userRole = (String) session.getAttribute("userRole");
            
            if (!"TEACHER".equals(userRole) && !"ADMIN".equals(userRole)) {
                sendErrorResponse(out, "Only teachers can enter marks", 403);
                return;
            }
            
            String action = request.getParameter("action");
            
            if (action == null) {
                sendErrorResponse(out, "Action parameter is required", 400);
                return;
            }
            
            switch (action) {
                case "add":
                    addMarks(request, userId, out);
                    break;
                    
                case "addBulk":
                    addBulkMarks(request, userId, out);
                    break;
                    
                case "update":
                    updateMarks(request, out);
                    break;
                    
                default:
                    sendErrorResponse(out, "Invalid action", 400);
            }
            
        } catch (Exception e) {
            System.err.println("Error in MarksServlet POST: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(out, "Internal server error", 500);
        }
    }
    
    /**
     * Handle DELETE request
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                sendErrorResponse(out, "Unauthorized", 401);
                return;
            }
            
            String userRole = (String) session.getAttribute("userRole");
            if (!"TEACHER".equals(userRole) && !"ADMIN".equals(userRole)) {
                sendErrorResponse(out, "Unauthorized", 403);
                return;
            }
            
            String markIdStr = request.getParameter("markId");
            if (markIdStr == null) {
                sendErrorResponse(out, "Mark ID is required", 400);
                return;
            }
            
            int markId = Integer.parseInt(markIdStr);
            boolean deleted = marksDAO.deleteMarks(markId);
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", deleted);
            jsonResponse.put("message", deleted ? "Marks deleted successfully" : "Failed to delete marks");
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error in MarksServlet DELETE: " + e.getMessage());
            sendErrorResponse(out, "Internal server error", 500);
        }
    }
    
    /**
     * Get marks by student
     */
    private void getMarksByStudent(HttpServletRequest request, PrintWriter out) {
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
            
            List<Marks> marksList = marksDAO.getMarksByStudent(studentId, subjectId, academicYear);
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", true);
            jsonResponse.put("data", convertMarksListToJSON(marksList));
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error getting marks by student: " + e.getMessage());
            sendErrorResponse(out, "Error retrieving marks", 500);
        }
    }
    
    /**
     * Get marks by subject and assessment
     */
    private void getMarksBySubjectAndAssessment(HttpServletRequest request, PrintWriter out) {
        try {
            String subjectIdStr = request.getParameter("subjectId");
            String assessmentTypeIdStr = request.getParameter("assessmentTypeId");
            String academicYear = request.getParameter("academicYear");
            
            if (subjectIdStr == null || assessmentTypeIdStr == null || academicYear == null) {
                sendErrorResponse(out, "Missing required parameters", 400);
                return;
            }
            
            int subjectId = Integer.parseInt(subjectIdStr);
            int assessmentTypeId = Integer.parseInt(assessmentTypeIdStr);
            
            List<Marks> marksList = marksDAO.getMarksBySubjectAndAssessment(
                subjectId, assessmentTypeId, academicYear
            );
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", true);
            jsonResponse.put("data", convertMarksListToJSON(marksList));
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error getting marks: " + e.getMessage());
            sendErrorResponse(out, "Error retrieving marks", 500);
        }
    }
    
    /**
     * Get marks statistics
     */
    private void getMarksStatistics(HttpServletRequest request, PrintWriter out) {
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
            
            Map<String, Object> stats = marksDAO.getMarksStatistics(
                studentId, subjectId, academicYear
            );
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", true);
            jsonResponse.put("data", new JSONObject(stats));
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error getting marks statistics: " + e.getMessage());
            sendErrorResponse(out, "Error retrieving statistics", 500);
        }
    }
    
    /**
     * Get weighted marks
     */
    private void getWeightedMarks(HttpServletRequest request, PrintWriter out) {
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
            
            double weightedMarks = marksDAO.getWeightedMarks(studentId, subjectId, academicYear);
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", true);
            jsonResponse.put("data", weightedMarks);
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error getting weighted marks: " + e.getMessage());
            sendErrorResponse(out, "Error retrieving weighted marks", 500);
        }
    }
    
    /**
     * Get failing students
     */
    private void getFailingStudents(HttpServletRequest request, PrintWriter out) {
        try {
            String subjectIdStr = request.getParameter("subjectId");
            String academicYear = request.getParameter("academicYear");
            
            if (subjectIdStr == null || academicYear == null) {
                sendErrorResponse(out, "Missing required parameters", 400);
                return;
            }
            
            int subjectId = Integer.parseInt(subjectIdStr);
            
            List<Map<String, Object>> failingStudents = 
                marksDAO.getFailingStudents(subjectId, academicYear);
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", true);
            jsonResponse.put("data", new JSONArray(failingStudents));
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error getting failing students: " + e.getMessage());
            sendErrorResponse(out, "Error retrieving data", 500);
        }
    }
    
    /**
     * Get top performers
     */
    private void getTopPerformers(HttpServletRequest request, PrintWriter out) {
        try {
            String subjectIdStr = request.getParameter("subjectId");
            String academicYear = request.getParameter("academicYear");
            String limitStr = request.getParameter("limit");
            
            if (subjectIdStr == null || academicYear == null) {
                sendErrorResponse(out, "Missing required parameters", 400);
                return;
            }
            
            int subjectId = Integer.parseInt(subjectIdStr);
            int limit = limitStr != null ? Integer.parseInt(limitStr) : 10;
            
            List<Map<String, Object>> topPerformers = 
                marksDAO.getTopPerformers(subjectId, academicYear, limit);
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", true);
            jsonResponse.put("data", new JSONArray(topPerformers));
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error getting top performers: " + e.getMessage());
            sendErrorResponse(out, "Error retrieving data", 500);
        }
    }
    
    /**
     * Add marks
     */
    private void addMarks(HttpServletRequest request, int enteredBy, PrintWriter out) {
        try {
            String enrollmentIdStr = request.getParameter("enrollmentId");
            String assessmentTypeIdStr = request.getParameter("assessmentTypeId");
            String maxMarksStr = request.getParameter("maxMarks");
            String marksObtainedStr = request.getParameter("marksObtained");
            String dateStr = request.getParameter("date");
            String remarks = request.getParameter("remarks");
            
            if (enrollmentIdStr == null || assessmentTypeIdStr == null || 
                maxMarksStr == null || marksObtainedStr == null || dateStr == null) {
                sendErrorResponse(out, "Missing required parameters", 400);
                return;
            }
            
            int enrollmentId = Integer.parseInt(enrollmentIdStr);
            int assessmentTypeId = Integer.parseInt(assessmentTypeIdStr);
            double maxMarks = Double.parseDouble(maxMarksStr);
            double marksObtained = Double.parseDouble(marksObtainedStr);
            Date date = Date.valueOf(dateStr);
            
            Marks marks = new Marks(enrollmentId, assessmentTypeId, maxMarks, 
                                   marksObtained, date, enteredBy);
            marks.setRemarks(remarks);
            
            boolean success = marksDAO.addMarks(marks);
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", success);
            jsonResponse.put("message", success ? "Marks added successfully" : "Failed to add marks");
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error adding marks: " + e.getMessage());
            sendErrorResponse(out, "Error adding marks", 500);
        }
    }
    
    /**
     * Add bulk marks
     */
    private void addBulkMarks(HttpServletRequest request, int enteredBy, PrintWriter out) {
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            
            JSONArray jsonArray = new JSONArray(sb.toString());
            List<Marks> marksList = new java.util.ArrayList<>();
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                
                int enrollmentId = jsonObj.getInt("enrollmentId");
                int assessmentTypeId = jsonObj.getInt("assessmentTypeId");
                double maxMarks = jsonObj.getDouble("maxMarks");
                double marksObtained = jsonObj.getDouble("marksObtained");
                Date date = Date.valueOf(jsonObj.getString("date"));
                String remarks = jsonObj.optString("remarks", null);
                
                Marks marks = new Marks(enrollmentId, assessmentTypeId, maxMarks, 
                                       marksObtained, date, enteredBy);
                marks.setRemarks(remarks);
                marksList.add(marks);
            }
            
            boolean success = marksDAO.addBulkMarks(marksList);
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", success);
            jsonResponse.put("message", success ? 
                "Bulk marks added successfully" : "Failed to add bulk marks");
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error adding bulk marks: " + e.getMessage());
            sendErrorResponse(out, "Error adding bulk marks", 500);
        }
    }
    
    /**
     * Update marks
     */
    private void updateMarks(HttpServletRequest request, PrintWriter out) {
        try {
            String markIdStr = request.getParameter("markId");
            String marksObtainedStr = request.getParameter("marksObtained");
            String maxMarksStr = request.getParameter("maxMarks");
            String dateStr = request.getParameter("date");
            String remarks = request.getParameter("remarks");
            
            if (markIdStr == null || marksObtainedStr == null) {
                sendErrorResponse(out, "Missing required parameters", 400);
                return;
            }
            
            Marks marks = marksDAO.getMarksById(Integer.parseInt(markIdStr));
            if (marks == null) {
                sendErrorResponse(out, "Marks record not found", 404);
                return;
            }
            
            marks.setMarksObtained(Double.parseDouble(marksObtainedStr));
            if (maxMarksStr != null) marks.setMaxMarks(Double.parseDouble(maxMarksStr));
            if (dateStr != null) marks.setAssessmentDate(Date.valueOf(dateStr));
            marks.setRemarks(remarks);
            
            boolean success = marksDAO.updateMarks(marks);
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", success);
            jsonResponse.put("message", success ? "Marks updated successfully" : "Failed to update marks");
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.err.println("Error updating marks: " + e.getMessage());
            sendErrorResponse(out, "Error updating marks", 500);
        }
    }
    
    /**
     * Convert marks list to JSON
     */
    private JSONArray convertMarksListToJSON(List<Marks> marksList) {
        JSONArray jsonArray = new JSONArray();
        
        for (Marks marks : marksList) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("markId", marks.getMarkId());
            jsonObj.put("enrollmentId", marks.getEnrollmentId());
            jsonObj.put("assessmentTypeId", marks.getAssessmentTypeId());
            jsonObj.put("assessmentTypeName", marks.getAssessmentTypeName());
            jsonObj.put("maxMarks", marks.getMaxMarks());
            jsonObj.put("marksObtained", marks.getMarksObtained());
            jsonObj.put("percentage", marks.getPercentage());
            jsonObj.put("grade", marks.calculateGrade());
            jsonObj.put("date", marks.getAssessmentDate() != null ? 
                marks.getAssessmentDate().toString() : null);
            jsonObj.put("studentName", marks.getStudentName());
            jsonObj.put("rollNumber", marks.getRollNumber());
            jsonObj.put("subjectName", marks.getSubjectName());
            jsonObj.put("subjectCode", marks.getSubjectCode());
            jsonObj.put("remarks", marks.getRemarks());
            
            jsonArray.put(jsonObj);
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
