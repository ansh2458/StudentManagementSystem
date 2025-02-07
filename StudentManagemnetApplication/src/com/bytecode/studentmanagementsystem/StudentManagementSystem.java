package com.bytecode.studentmanagementsystem;

import java.sql.*;
import java.util.*;

// Class representing a student with a map of names and grades
class Student {
    private int id;
    private String gender;
    private int attendance;
    private Map<String, Float> nameGradeMap; // Map to store student names as keys and grades as values

    // Constructor to initialize student details
    public Student(int id, String gender, int attendance, Map<String, Float> nameGradeMap) {
        this.id = id;
        this.gender = gender;
        this.attendance = attendance;
        this.nameGradeMap = nameGradeMap;
    }

    // Getter methods
    public int getId() { return id; }
    public String getGender() { return gender; }
    public int getAttendance() { return attendance; }
    public Map<String, Float> getNameGradeMap() { return nameGradeMap; }

    // Method to display student information
    public void displayStudentInfo() {
        System.out.println("ID: " + id);
        System.out.println("Gender: " + gender);
        System.out.println("Attendance: " + attendance);
        System.out.println("Grades: ");
        for (Map.Entry<String, Float> entry : nameGradeMap.entrySet()) {
            System.out.println("Name: " + entry.getKey() + " | Grade: " + entry.getValue());
        }
    }
}

// Main class for the Student Management System
public class StudentManagementSystem {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "ansh";
    private static final String PASSWORD = "Ansh2024";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            while (true) {
                // Display menu options
                System.out.println("\nStudent Management System");
                System.out.println("1. Add Student");
                System.out.println("2. Add Attendance");
                System.out.println("3. Update Grades");
                System.out.println("4. View Student Info");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                // Process user choice
                switch (choice) {
                    case 1:
                        addStudent(conn);
                        break;
                    case 2:
                        addAttendance(conn);
                        break;
                    case 3:
                        updateGrades(conn);
                        break;
                    case 4:
                        viewStudentInfo(conn);
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to add a new student
    private static void addStudent(Connection conn) throws SQLException {
        System.out.print("Enter student ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        System.out.print("Enter student gender (M/F): ");
        String gender = scanner.nextLine();
        System.out.print("Enter student grade: ");
        float grade = scanner.nextFloat();
        scanner.nextLine();
        
        Map<String, Float> nameGradeMap = new HashMap<>();
        nameGradeMap.put(name, grade);
        
        String query = "INSERT INTO students (id, name, gender, grades, attendance) VALUES (?, ?, ?, ?, 0)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setString(3, gender);
            stmt.setFloat(4, grade);
            stmt.executeUpdate();
            System.out.println("Student added successfully!");
        }
    }

    // Method to add attendance for a student
    private static void addAttendance(Connection conn) throws SQLException {
        System.out.print("Enter student ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        String query = "UPDATE students SET attendance = attendance + 1 WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Attendance updated!");
            } else {
                System.out.println("Student not found!");
            }
        }
    }

    // Method to update grades for a student
    private static void updateGrades(Connection conn) throws SQLException {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new grade: ");
        float grade = scanner.nextFloat();
        scanner.nextLine();
        String query = "UPDATE students SET grades = ? WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setFloat(1, grade);
            stmt.setString(2, name);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Grade updated!");
            } else {
                System.out.println("Student not found!");
            }
        }
    }

    // Method to view student details
    private static void viewStudentInfo(Connection conn) throws SQLException {
        System.out.print("Enter student ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        String query = "SELECT * FROM students WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, Float> nameGradeMap = new HashMap<>();
                nameGradeMap.put(rs.getString("name"), rs.getFloat("grades"));
                Student student = new Student(rs.getInt("id"), rs.getString("gender"), rs.getInt("attendance"), nameGradeMap);
                student.displayStudentInfo();
            } else {
                System.out.println("Student not found!");
            }
        }
    }
}

