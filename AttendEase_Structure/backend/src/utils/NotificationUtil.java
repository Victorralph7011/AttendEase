package utils;

import config.DBConnection;

import java.sql.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Notification Utility Class for AttendEase
 * Handles email notifications and system notifications
 */
public class NotificationUtil {
    
    // Email configuration (Gmail SMTP)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SENDER_EMAIL = "attendease@srmist.edu.in"; // Change this
    private static final String SENDER_PASSWORD = "your_app_password"; // Use App Password for Gmail
    private static final String SENDER_NAME = "AttendEase System";
    
    /**
     * Send email notification
     * @param recipientEmail Recipient email address
     * @param subject Email subject
     * @param message Email message body
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendEmail(String recipientEmail, String subject, String message) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });
        
        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            mimeMessage.setSubject(subject);
            
            // Create HTML content
            String htmlContent = generateEmailTemplate(subject, message);
            mimeMessage.setContent(htmlContent, "text/html; charset=utf-8");
            
            Transport.send(mimeMessage);
            
            System.out.println("Email sent successfully to: " + recipientEmail);
            return true;
            
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Send attendance alert email
     * @param recipientEmail Student email
     * @param studentName Student name
     * @param subjectName Subject name
     * @param attendancePercentage Current attendance percentage
     * @return true if email sent successfully
     */
    public static boolean sendAttendanceAlert(String recipientEmail, String studentName, 
                                             String subjectName, double attendancePercentage) {
        String subject = "⚠️ Attendance Alert - " + subjectName;
        
        String message = String.format(
            "Dear %s,<br><br>" +
            "This is to inform you that your attendance in <b>%s</b> has dropped to <b>%.2f%%</b>.<br><br>" +
            "The minimum required attendance is <b>75%%</b>. Please ensure regular attendance to avoid any issues.<br><br>" +
            "Current Status:<br>" +
            "- Subject: %s<br>" +
            "- Attendance: %.2f%%<br>" +
            "- Required: 75%%<br>" +
            "- Deficit: %.2f%%<br><br>" +
            "Please take necessary action to improve your attendance.<br><br>" +
            "Best regards,<br>" +
            "AttendEase System",
            studentName, subjectName, attendancePercentage, 
            subjectName, attendancePercentage, (75.0 - attendancePercentage)
        );
        
        return sendEmail(recipientEmail, subject, message);
    }
    
    /**
     * Send marks alert email (for poor performance)
     * @param recipientEmail Student email
     * @param studentName Student name
     * @param subjectName Subject name
     * @param percentage Current marks percentage
     * @param grade Current grade
     * @return true if email sent successfully
     */
    public static boolean sendMarksAlert(String recipientEmail, String studentName, 
                                        String subjectName, double percentage, String grade) {
        String subject = "📊 Academic Performance Alert - " + subjectName;
        
        String message = String.format(
            "Dear %s,<br><br>" +
            "This is to inform you about your current academic performance in <b>%s</b>.<br><br>" +
            "Current Performance:<br>" +
            "- Subject: %s<br>" +
            "- Percentage: %.2f%%<br>" +
            "- Grade: %s<br><br>" +
            (percentage < 40 ? 
                "⚠️ <b style='color: red;'>Warning:</b> You are currently failing this subject. " +
                "Please seek academic support immediately.<br><br>" :
                "Your performance needs improvement. Consider attending tutoring sessions.<br><br>") +
            "Best regards,<br>" +
            "AttendEase System",
            studentName, subjectName, subjectName, percentage, grade
        );
        
        return sendEmail(recipientEmail, subject, message);
    }
    
    /**
     * Send parent notification email
     * @param parentEmail Parent email
     * @param studentName Student name
     * @param rollNumber Student roll number
     * @param attendancePercentage Attendance percentage
     * @param overallPercentage Marks percentage
     * @return true if email sent successfully
     */
    public static boolean sendParentNotification(String parentEmail, String studentName, 
                                                String rollNumber, double attendancePercentage, 
                                                double overallPercentage) {
        String subject = "📋 Student Performance Report - " + studentName;
        
        String message = String.format(
            "Dear Parent/Guardian,<br><br>" +
            "This is a periodic performance update for your ward <b>%s</b> (Roll No: %s).<br><br>" +
            "Performance Summary:<br>" +
            "- Attendance: %.2f%%<br>" +
            "- Overall Marks: %.2f%%<br><br>" +
            (attendancePercentage < 75.0 ? 
                "⚠️ <b style='color: red;'>Attendance Alert:</b> Attendance is below the required 75%% threshold.<br><br>" : "") +
            (overallPercentage < 40.0 ? 
                "⚠️ <b style='color: red;'>Academic Alert:</b> Performance needs immediate attention.<br><br>" : "") +
            "For detailed reports, please contact the respective faculty or check the student portal.<br><br>" +
            "Best regards,<br>" +
            "AttendEase System<br>" +
            "SRMIST",
            studentName, rollNumber, attendancePercentage, overallPercentage
        );
        
        return sendEmail(parentEmail, subject, message);
    }
    
    /**
     * Send test/assignment reminder
     * @param recipientEmail Student email
     * @param studentName Student name
     * @param assessmentType Assessment type (Quiz, Test, Assignment)
     * @param subjectName Subject name
     * @param dueDate Due date
     * @return true if email sent successfully
     */
    public static boolean sendAssessmentReminder(String recipientEmail, String studentName, 
                                                String assessmentType, String subjectName, 
                                                String dueDate) {
        String subject = "📅 Reminder: Upcoming " + assessmentType + " - " + subjectName;
        
        String message = String.format(
            "Dear %s,<br><br>" +
            "This is a reminder about the upcoming <b>%s</b> in <b>%s</b>.<br><br>" +
            "Details:<br>" +
            "- Assessment Type: %s<br>" +
            "- Subject: %s<br>" +
            "- Date: %s<br><br>" +
            "Please prepare accordingly and ensure you are present on time.<br><br>" +
            "Best regards,<br>" +
            "AttendEase System",
            studentName, assessmentType, subjectName, assessmentType, subjectName, dueDate
        );
        
        return sendEmail(recipientEmail, subject, message);
    }
    
    /**
     * Send welcome email to new user
     * @param recipientEmail User email
     * @param userName User name
     * @param role User role
     * @param tempPassword Temporary password
     * @return true if email sent successfully
     */
    public static boolean sendWelcomeEmail(String recipientEmail, String userName, 
                                          String role, String tempPassword) {
        String subject = "🎓 Welcome to AttendEase - Your Account is Ready";
        
        String message = String.format(
            "Dear %s,<br><br>" +
            "Welcome to <b>AttendEase</b> - SRMIST's Attendance and Marks Management System!<br><br>" +
            "Your account has been created successfully.<br><br>" +
            "Login Credentials:<br>" +
            "- Email: %s<br>" +
            "- Temporary Password: %s<br>" +
            "- Role: %s<br><br>" +
            "⚠️ <b>Important:</b> Please change your password after first login for security purposes.<br><br>" +
            "You can access the system at: <a href='http://localhost:8080/AttendEase'>AttendEase Portal</a><br><br>" +
            "If you have any questions, please contact the administrator.<br><br>" +
            "Best regards,<br>" +
            "AttendEase System",
            userName, recipientEmail, tempPassword, role
        );
        
        return sendEmail(recipientEmail, subject, message);
    }
    
    /**
     * Create system notification in database
     * @param userId User ID
     * @param title Notification title
     * @param message Notification message
     * @param type Notification type
     * @return true if notification created successfully
     */
    public static boolean createSystemNotification(int userId, String title, 
                                                   String message, String type) {
        String sql = "INSERT INTO notifications (user_id, title, message, type) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, message);
            pstmt.setString(4, type);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating system notification: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mark notification as read
     * @param notificationId Notification ID
     * @return true if marked successfully
     */
    public static boolean markNotificationAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, notificationId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get unread notifications for a user
     * @param userId User ID
     * @return List of unread notifications
     */
    public static java.util.List<java.util.Map<String, Object>> getUnreadNotifications(int userId) {
        java.util.List<java.util.Map<String, Object>> notifications = new java.util.ArrayList<>();
        
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = FALSE " +
                    "ORDER BY created_at DESC LIMIT 10";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                java.util.Map<String, Object> notification = new java.util.HashMap<>();
                notification.put("notificationId", rs.getInt("notification_id"));
                notification.put("title", rs.getString("title"));
                notification.put("message", rs.getString("message"));
                notification.put("type", rs.getString("type"));
                notification.put("createdAt", rs.getTimestamp("created_at"));
                
                notifications.add(notification);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting unread notifications: " + e.getMessage());
            e.printStackTrace();
        }
        
        return notifications;
    }
    
    /**
     * Generate HTML email template
     * @param title Email title
     * @param content Email content
     * @return HTML formatted email
     */
    private static String generateEmailTemplate(String title, String content) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<style>" +
               "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
               ".container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; }" +
               ".header { background-color: #0066cc; color: white; padding: 20px; text-align: center; }" +
               ".content { background-color: white; padding: 30px; margin-top: 20px; border-radius: 5px; }" +
               ".footer { text-align: center; margin-top: 20px; font-size: 12px; color: #666; }" +
               "a { color: #0066cc; text-decoration: none; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='container'>" +
               "<div class='header'>" +
               "<h1>AttendEase</h1>" +
               "<p>SRM Institute of Science and Technology</p>" +
               "</div>" +
               "<div class='content'>" +
               content +
               "</div>" +
               "<div class='footer'>" +
               "<p>This is an automated message from AttendEase System.</p>" +
               "<p>Please do not reply to this email.</p>" +
               "<p>&copy; 2025 SRMIST. All rights reserved.</p>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
    
    /**
     * Send SMS notification (placeholder - requires SMS gateway integration)
     * @param phoneNumber Recipient phone number
     * @param message SMS message
     * @return true if SMS sent successfully
     */
    public static boolean sendSMS(String phoneNumber, String message) {
        // TODO: Integrate with SMS gateway (Twilio, AWS SNS, etc.)
        System.out.println("SMS to " + phoneNumber + ": " + message);
        return true;
    }
    
    /**
     * Test notification system
     */
    public static void main(String[] args) {
        System.out.println("Testing Notification System...");
        System.out.println("================================\n");
        
        // Test attendance alert
        boolean result = sendAttendanceAlert(
            "student@example.com",
            "John Doe",
            "Data Structures",
            72.5
        );
        
        System.out.println("Attendance Alert: " + (result ? "✓ Sent" : "✗ Failed"));
    }
}
