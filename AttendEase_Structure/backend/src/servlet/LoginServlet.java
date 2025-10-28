package servlet;

import dao.UserDAO;
import model.User;
import utils.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

/**
 * Login Servlet for AttendEase
 * Handles user authentication and session management
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
    }
    
    /**
     * Handle GET request - Show login page
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Forward to login page
        request.getRequestDispatcher("/pages/login.html").forward(request, response);
    }
    
    /**
     * Handle POST request - Process login
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            // Get login credentials
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String rememberMe = request.getParameter("rememberMe");
            
            // Validate input
            if (email == null || email.trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {
                
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Email and password are required");
                out.print(jsonResponse.toString());
                return;
            }
            
            // Authenticate user
            User user = userDAO.authenticateUser(email.trim(), password);
            
            if (user != null) {
                // Authentication successful
                
                // Create session
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("userRole", user.getRoleString());
                session.setAttribute("userEmail", user.getEmail());
                session.setAttribute("userName", user.getFullName());
                
                // Set session timeout (30 minutes)
                session.setMaxInactiveInterval(30 * 60);
                
                // Handle "Remember Me" functionality
                if ("true".equals(rememberMe)) {
                    Cookie userCookie = new Cookie("attendease_user", email);
                    userCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
                    userCookie.setPath(request.getContextPath());
                    response.addCookie(userCookie);
                }
                
                // Log login activity
                System.out.println("User logged in: " + user.getEmail() + " (Role: " + user.getRoleString() + ")");
                
                // Prepare success response
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Login successful");
                jsonResponse.put("user", new JSONObject()
                    .put("userId", user.getUserId())
                    .put("email", user.getEmail())
                    .put("fullName", user.getFullName())
                    .put("role", user.getRoleString())
                    .put("department", user.getDepartment())
                );
                
                // Determine redirect URL based on role
                String redirectUrl = getRedirectUrlByRole(user.getRole());
                jsonResponse.put("redirectUrl", request.getContextPath() + redirectUrl);
                
                out.print(jsonResponse.toString());
                
            } else {
                // Authentication failed
                
                // Log failed login attempt
                System.out.println("Failed login attempt for: " + email);
                
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid email or password");
                out.print(jsonResponse.toString());
            }
            
        } catch (Exception e) {
            System.err.println("Error in LoginServlet: " + e.getMessage());
            e.printStackTrace();
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred during login. Please try again.");
            out.print(jsonResponse.toString());
        }
    }
    
    /**
     * Get redirect URL based on user role
     */
    private String getRedirectUrlByRole(User.UserRole role) {
        switch (role) {
            case TEACHER:
                return "/pages/index.html?role=teacher";
            case STUDENT:
                return "/pages/index.html?role=student";
            case ADMIN:
                return "/pages/index.html?role=admin";
            default:
                return "/pages/index.html";
        }
    }
    
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
