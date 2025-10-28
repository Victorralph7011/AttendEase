-- AttendEase Database Schema
-- Database Creation
CREATE DATABASE IF NOT EXISTS attendease;
USE attendease;

-- Users Table (Teachers and Students)
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('TEACHER', 'STUDENT', 'ADMIN') NOT NULL,
    department VARCHAR(50),
    phone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- Students Table (Extended Info)
CREATE TABLE students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE NOT NULL,
    roll_number VARCHAR(20) UNIQUE NOT NULL,
    semester INT NOT NULL,
    batch_year INT NOT NULL,
    parent_email VARCHAR(100),
    parent_phone VARCHAR(15),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Teachers Table (Extended Info)
CREATE TABLE teachers (
    teacher_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE NOT NULL,
    employee_id VARCHAR(20) UNIQUE NOT NULL,
    specialization VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Subjects Table
CREATE TABLE subjects (
    subject_id INT PRIMARY KEY AUTO_INCREMENT,
    subject_code VARCHAR(20) UNIQUE NOT NULL,
    subject_name VARCHAR(100) NOT NULL,
    credits INT DEFAULT 3,
    semester INT NOT NULL,
    department VARCHAR(50)
);

-- Teacher-Subject Mapping
CREATE TABLE teacher_subjects (
    id INT PRIMARY KEY AUTO_INCREMENT,
    teacher_id INT NOT NULL,
    subject_id INT NOT NULL,
    academic_year VARCHAR(10) NOT NULL,
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE,
    UNIQUE KEY unique_assignment (teacher_id, subject_id, academic_year)
);

-- Student-Subject Enrollment
CREATE TABLE enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    academic_year VARCHAR(10) NOT NULL,
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE,
    UNIQUE KEY unique_enrollment (student_id, subject_id, academic_year)
);

-- Attendance Table
CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('PRESENT', 'ABSENT', 'LATE', 'EXCUSED') NOT NULL,
    marked_by INT NOT NULL,
    marked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarks TEXT,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    FOREIGN KEY (marked_by) REFERENCES users(user_id),
    UNIQUE KEY unique_attendance (enrollment_id, attendance_date),
    INDEX idx_date (attendance_date),
    INDEX idx_status (status)
);

-- Assessment Types
CREATE TABLE assessment_types (
    type_id INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(50) UNIQUE NOT NULL,
    weightage DECIMAL(5,2) NOT NULL,
    description TEXT
);

-- Marks Table
CREATE TABLE marks (
    mark_id INT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id INT NOT NULL,
    assessment_type_id INT NOT NULL,
    max_marks DECIMAL(6,2) NOT NULL,
    marks_obtained DECIMAL(6,2) NOT NULL,
    assessment_date DATE NOT NULL,
    entered_by INT NOT NULL,
    entered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remarks TEXT,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    FOREIGN KEY (assessment_type_id) REFERENCES assessment_types(type_id),
    FOREIGN KEY (entered_by) REFERENCES users(user_id),
    INDEX idx_assessment (assessment_type_id),
    CHECK (marks_obtained <= max_marks)
);

-- Notifications Table
CREATE TABLE notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type ENUM('ATTENDANCE', 'MARKS', 'ANNOUNCEMENT', 'WARNING') NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_created (created_at)
);

-- Gamification Points
CREATE TABLE gamification_points (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    points INT DEFAULT 0,
    academic_year VARCHAR(10) NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    UNIQUE KEY unique_points (student_id, academic_year)
);

-- Attendance Threshold Alerts
CREATE TABLE attendance_alerts (
    alert_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    attendance_percentage DECIMAL(5,2) NOT NULL,
    alert_sent BOOLEAN DEFAULT FALSE,
    alert_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE
);

-- Insert Default Assessment Types
INSERT INTO assessment_types (type_name, weightage, description) VALUES
('Quiz', 10.00, 'Weekly quizzes'),
('Assignment', 15.00, 'Practical assignments'),
('Mid-term', 25.00, 'Mid-semester examination'),
('Final Exam', 50.00, 'End-semester examination');

-- Insert Sample Admin User (Password: Admin@123 - hashed with BCrypt)
-- Note: This is a placeholder hash. Use BCrypt to generate actual hash
INSERT INTO users (email, password_hash, full_name, role, department) VALUES
('admin@srmist.edu.in', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System Administrator', 'ADMIN', 'IT');

-- Sample Teacher (Password: Teacher@123)
INSERT INTO users (email, password_hash, full_name, role, department, phone) VALUES
('ab1234@srmist.edu.in', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Dr. Amit Kumar', 'TEACHER', 'Computer Science', '9876543210');

INSERT INTO teachers (user_id, employee_id, specialization) VALUES
(2, 'EMP001', 'Data Structures and Algorithms');

-- Sample Student (Password: Student@123)
INSERT INTO users (email, password_hash, full_name, role, department, phone) VALUES
('student1@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Rahul Sharma', 'STUDENT', 'Computer Science', '9876543211');

INSERT INTO students (user_id, roll_number, semester, batch_year, parent_email, parent_phone) VALUES
(3, 'RA2111001010101', 3, 2023, 'parent1@gmail.com', '9876543212');

-- Sample Subjects
INSERT INTO subjects (subject_code, subject_name, credits, semester, department) VALUES
('CSE101', 'Data Structures', 4, 3, 'Computer Science'),
('CSE102', 'Database Management Systems', 3, 3, 'Computer Science'),
('CSE103', 'Operating Systems', 4, 3, 'Computer Science');

-- Map Teacher to Subjects
INSERT INTO teacher_subjects (teacher_id, subject_id, academic_year) VALUES
(1, 1, '2024-25'),
(1, 2, '2024-25');

-- Enroll Student in Subjects
INSERT INTO enrollments (student_id, subject_id, academic_year) VALUES
(1, 1, '2024-25'),
(1, 2, '2024-25'),
(1, 3, '2024-25');

-- Initialize Gamification Points
INSERT INTO gamification_points (student_id, points, academic_year) VALUES
(1, 0, '2024-25');
