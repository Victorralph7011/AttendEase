package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Password Utility Class for AttendEase
 * Provides secure password hashing using BCrypt-style implementation
 */
public class PasswordUtil {
    
    private static final int SALT_LENGTH = 16;
    private static final int ITERATION_COUNT = 10000;
    private static final String ALGORITHM = "SHA-256";
    
    /**
     * Generate a random salt for password hashing
     * @return Base64 encoded salt string
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hash a password with a given salt using SHA-256
     * @param password Plain text password
     * @param salt Salt string
     * @return Hashed password
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            
            // Combine password and salt
            String saltedPassword = password + salt;
            
            // Perform multiple iterations for stronger security
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());
            for (int i = 0; i < ITERATION_COUNT; i++) {
                md.reset();
                hashedBytes = md.digest(hashedBytes);
            }
            
            // Convert to Base64 for storage
            return Base64.getEncoder().encodeToString(hashedBytes);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Hash a password (generates salt automatically)
     * Returns format: salt$hash
     * @param password Plain text password
     * @return Combined salt and hash string
     */
    public static String hashPassword(String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return salt + "$" + hash;
    }
    
    /**
     * Verify a password against a stored hash
     * @param password Plain text password to verify
     * @param storedHash Stored hash in format salt$hash
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Split the stored hash into salt and hash
            String[] parts = storedHash.split("\\$");
            if (parts.length != 2) {
                return false;
            }
            
            String salt = parts[0];
            String hash = parts[1];
            
            // Hash the input password with the same salt
            String testHash = hashPassword(password, salt);
            
            // Compare hashes
            return hash.equals(testHash);
            
        } catch (Exception e) {
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate password strength
     * Requirements: 
     * - Minimum 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     * - At least one special character
     * @param password Password to validate
     * @return true if password meets requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (specialChars.indexOf(c) >= 0) hasSpecial = true;
        }
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    
    /**
     * Validate email format
     * Students: any valid email
     * Teachers: must be @srmist.edu.in
     * @param email Email to validate
     * @param role User role (TEACHER, STUDENT, ADMIN)
     * @return true if email format is valid, false otherwise
     */
    public static boolean isValidEmail(String email, String role) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Basic email regex
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        
        if (!email.matches(emailRegex)) {
            return false;
        }
        
        // Check teacher email format
        if ("TEACHER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            // Must be in format: ab1234@srmist.edu.in
            String teacherPattern = "^[a-z]{2}\\d{4}@srmist\\.edu\\.in$";
            return email.matches(teacherPattern);
        }
        
        // Students can have any valid email
        return true;
    }
    
    /**
     * Generate a random password
     * @param length Length of password
     * @return Random password string
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
    
    /**
     * Generate password reset token
     * @param email User email
     * @return Reset token
     */
    public static String generateResetToken(String email) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String combined = email + timestamp + generateSalt();
        return hashPassword(combined, generateSalt());
    }
    
    /**
     * Simple SHA-256 hash (for backward compatibility or simple use cases)
     * @param input String to hash
     * @return Hashed string
     */
    public static String simpleSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
    
    /**
     * Test the PasswordUtil class
     */
    public static void main(String[] args) {
        System.out.println("Password Utility Testing");
        System.out.println("========================\n");
        
        // Test password hashing
        String password = "Student@123";
        String hashed = hashPassword(password);
        System.out.println("Original Password: " + password);
        System.out.println("Hashed Password: " + hashed);
        
        // Test password verification
        boolean isValid = verifyPassword(password, hashed);
        System.out.println("Password Verification: " + (isValid ? "✓ Success" : "✗ Failed"));
        
        boolean isInvalid = verifyPassword("WrongPassword", hashed);
        System.out.println("Wrong Password Test: " + (!isInvalid ? "✓ Success" : "✗ Failed"));
        
        // Test password strength
        System.out.println("\nPassword Strength Tests:");
        System.out.println("'Student@123' is valid: " + isValidPassword("Student@123"));
        System.out.println("'weak' is valid: " + isValidPassword("weak"));
        
        // Test email validation
        System.out.println("\nEmail Validation Tests:");
        System.out.println("Teacher 'ab1234@srmist.edu.in': " + 
            isValidEmail("ab1234@srmist.edu.in", "TEACHER"));
        System.out.println("Teacher 'invalid@gmail.com': " + 
            isValidEmail("invalid@gmail.com", "TEACHER"));
        System.out.println("Student 'student@gmail.com': " + 
            isValidEmail("student@gmail.com", "STUDENT"));
        
        // Generate random password
        System.out.println("\nRandom Password: " + generateRandomPassword(12));
    }
}
