package com.coursesystem.dao;

import com.coursesystem.db.DBConnection;
import com.coursesystem.models.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public boolean registerStudent(Student student) {
        String sql = "INSERT INTO students (roll_number, name, email, department, semester, password) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString(1, student.getRollNumber());
            ps.setString(2, student.getName());
            ps.setString(3, student.getEmail());
            ps.setString(4, student.getDepartment());
            ps.setInt   (5, student.getSemester());
            ps.setString(6, student.getPassword());
            int rows = ps.executeUpdate();
            System.out.println("Student registered: " + student.getRollNumber());
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error registering student: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (ps   != null) ps.close();  } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public Student login(String rollNumber, String password) {
        String sql = "SELECT * FROM students WHERE roll_number = ? AND password = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString(1, rollNumber);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setRollNumber(rs.getString("roll_number"));
                s.setName(rs.getString("name"));
                s.setEmail(rs.getString("email"));
                s.setDepartment(rs.getString("department"));
                s.setSemester(rs.getInt("semester"));
                s.setPassword(rs.getString("password"));
                System.out.println("Student login success: " + rollNumber);
                return s;
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs   != null) rs.close();  } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps   != null) ps.close();  } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY department, semester";
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            st   = conn.createStatement();
            rs   = st.executeQuery(sql);
            while (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setRollNumber(rs.getString("roll_number"));
                s.setName(rs.getString("name"));
                s.setEmail(rs.getString("email"));
                s.setDepartment(rs.getString("department"));
                s.setSemester(rs.getInt("semester"));
                list.add(s);
            }
            System.out.println("Total students fetched: " + list.size());
        } catch (SQLException e) {
            System.out.println("Error fetching students: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs   != null) rs.close();  } catch (SQLException e) { e.printStackTrace(); }
            try { if (st   != null) st.close();  } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }

    public List<Student> filterStudents(String department, int semester) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE department = ? AND semester = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString(1, department);
            ps.setInt   (2, semester);
            rs = ps.executeQuery();
            while (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setRollNumber(rs.getString("roll_number"));
                s.setName(rs.getString("name"));
                s.setEmail(rs.getString("email"));
                s.setDepartment(rs.getString("department"));
                s.setSemester(rs.getInt("semester"));
                list.add(s);
            }
            System.out.println("Filtered students: " + list.size());
        } catch (SQLException e) {
            System.out.println("Error filtering students: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs   != null) rs.close();  } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps   != null) ps.close();  } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }

    public boolean submitPreferences(String rollNumber, List<Integer> courseIds) {
        String deleteSql = "DELETE FROM preferences WHERE roll_number = ?";
        String insertSql = "INSERT INTO preferences (roll_number, course_id, priority) VALUES (?, ?, ?)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Step 1: Delete old preferences
            PreparedStatement del = conn.prepareStatement(deleteSql);
            del.setString(1, rollNumber);
            int deleted = del.executeUpdate();
            System.out.println("Deleted old preferences: " + deleted + " rows");
            del.close();

            // Step 2: Insert new preferences
            PreparedStatement ins = conn.prepareStatement(insertSql);
            for (int i = 0; i < courseIds.size(); i++) {
                ins.setString(1, rollNumber);
                ins.setInt   (2, courseIds.get(i));
                ins.setInt   (3, i + 1);
                ins.addBatch();
                System.out.println("Queuing - Roll: " + rollNumber
                        + ", CourseID: " + courseIds.get(i)
                        + ", Priority: " + (i + 1));
            }
            ins.executeBatch();
            ins.close();

            conn.commit();
            System.out.println("Preferences committed for: " + rollNumber);
            return true;

        } catch (SQLException e) {
            System.out.println("Error saving preferences: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("Transaction rolled back.");
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}