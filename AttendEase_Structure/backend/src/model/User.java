package model;

import java.sql.Timestamp;

/**
 * User Model Class for AttendEase
 * Represents users in the system (Teachers, Students, Admins)
 */
public class User {
    
    // User attributes
    private int userId;
    private String email;
    private String passwordHash;
    private String fullName;
    private UserRole role;
    private String department;
    private String phone;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private boolean isActive;
    
    /**
     * User Role Enumeration
     */
    public enum UserRole {
        TEACHER, STUDENT, ADMIN
    }
    
    /**
     * Default constructor
     */
    public User() {
        this.isActive = true;
    }
    
    /**
     * Constructor with essential fields
     */
    public User(String email, String passwordHash, String fullName, UserRole role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.isActive = true;
    }
    
    /**
     * Full constructor
     */
    public User(int userId, String email, String passwordHash, String fullName, 
                UserRole role, String department, String phone, 
                Timestamp createdAt, Timestamp lastLogin, boolean isActive) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.department = department;
        this.phone = phone;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    /**
     * Check if user is a teacher
     */
    public boolean isTeacher() {
        return this.role == UserRole.TEACHER;
    }
    
    /**
     * Check if user is a student
     */
    public boolean isStudent() {
        return this.role == UserRole.STUDENT;
    }
    
    /**
     * Check if user is an admin
     */
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }
    
    /**
     * Get role as string
     */
    public String getRoleString() {
        return role != null ? role.name() : "UNKNOWN";
    }
    
    /**
     * Set role from string
     */
    public void setRoleFromString(String roleStr) {
        try {
            this.role = UserRole.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.role = null;
        }
    }
    
    /**
     * Validate user data
     */
    public boolean isValid() {
        return email != null && !email.trim().isEmpty() &&
               fullName != null && !fullName.trim().isEmpty() &&
               role != null;
    }
    
    /**
     * Get display name (for UI)
     */
    public String getDisplayName() {
        return fullName != null ? fullName : email;
    }
    
    /**
     * Update last login timestamp
     */
    public void updateLastLogin() {
        this.lastLogin = new Timestamp(System.currentTimeMillis());
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                ", department='" + department + '\'' +
                ", phone='" + phone + '\'' +
                ", isActive=" + isActive +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(userId);
    }
}
