package com.coursesystem.dao;

import com.coursesystem.db.DBConnection;
import com.coursesystem.models.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    // ══════════════════════════════════════════════════════════
    //  ADD COURSE
    // ══════════════════════════════════════════════════════════
    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses " +
                     "(course_code, course_name, department, semester, credits) " +
                     "VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString(1, course.getCourseCode());
            ps.setString(2, course.getCourseName());
            ps.setString(3, course.getDepartment());
            ps.setInt   (4, course.getSemester());
            ps.setInt   (5, course.getCredits());
            int rows = ps.executeUpdate();
            System.out.println("Course added: " + course.getCourseCode());
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (ps   != null) ps.close();   } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close();  } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // ══════════════════════════════════════════════════════════
    //  GET COURSES BY DEPARTMENT AND SEMESTER
    // ══════════════════════════════════════════════════════════
    public List<Course> getCoursesByDeptSem(String department, int semester) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses " +
                     "WHERE department = ? AND semester = ? " +
                     "ORDER BY course_code";
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
                Course c = new Course();
                c.setId        (rs.getInt   ("id"));
                c.setCourseCode(rs.getString("course_code"));
                c.setCourseName(rs.getString("course_name"));
                c.setDepartment(rs.getString("department"));
                c.setSemester  (rs.getInt   ("semester"));
                c.setCredits   (rs.getInt   ("credits"));
                list.add(c);
            }
            System.out.println("Courses fetched for "
                + department + " Sem " + semester
                + ": " + list.size());
        } catch (SQLException e) {
            System.out.println("Error fetching courses: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs   != null) rs.close();   } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps   != null) ps.close();   } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close();  } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    //  GET ALL PREFERENCES — joins 3 tables
    // ══════════════════════════════════════════════════════════
    public List<Object[]> getAllPreferences() {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT s.roll_number, s.name, s.department, s.semester, " +
            "       c.course_code, c.course_name, p.priority " +
            "FROM preferences p " +
            "INNER JOIN students s ON p.roll_number = s.roll_number " +
            "INNER JOIN courses  c ON p.course_id   = c.id " +
            "ORDER BY s.roll_number ASC, p.priority ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            rs   = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getString("roll_number");
                row[1] = rs.getString("name");
                row[2] = rs.getString("department");
                row[3] = rs.getInt   ("semester");
                row[4] = rs.getString("course_code");
                row[5] = rs.getString("course_name");
                row[6] = rs.getInt   ("priority");
                list.add(row);
            }
            System.out.println("Total preferences fetched: " + list.size());
        } catch (SQLException e) {
            System.out.println("SQL Error in getAllPreferences: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs   != null) rs.close();   } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps   != null) ps.close();   } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close();  } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    //  GET COURSE COUNT FOR DEPT AND SEMESTER
    //  Used to check how many courses student must select
    // ══════════════════════════════════════════════════════════
    public int getCourseCountForDeptSem(String department, int semester) {
        String sql = "SELECT COUNT(*) FROM courses " +
                     "WHERE department = ? AND semester = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString(1, department);
            ps.setInt   (2, semester);
            rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Courses required for "
                    + department + " Sem " + semester + ": " + count);
                return count;
            }
        } catch (SQLException e) {
            System.out.println("Error getting course count: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs   != null) rs.close();   } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps   != null) ps.close();   } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close();  } catch (SQLException e) { e.printStackTrace(); }
        }
        return 0;
    }

    // ══════════════════════════════════════════════════════════
    //  CHECK IF STUDENT ALREADY SUBMITTED PREFERENCES
    //  Used to show submitted view instead of selection view
    // ══════════════════════════════════════════════════════════
    public boolean hasStudentAlreadySubmitted(String rollNumber) {
        String sql = "SELECT COUNT(*) FROM preferences " +
                     "WHERE roll_number = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString(1, rollNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                boolean submitted = rs.getInt(1) > 0;
                System.out.println("Student " + rollNumber
                    + " already submitted: " + submitted);
                return submitted;
            }
        } catch (SQLException e) {
            System.out.println("Error checking submission: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs   != null) rs.close();   } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps   != null) ps.close();   } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close();  } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    // ══════════════════════════════════════════════════════════
    //  GET SUBMITTED PREFERENCES FOR A SPECIFIC STUDENT
    //  Used in StudentDashboard submitted view
    // ══════════════════════════════════════════════════════════
    public List<Object[]> getStudentSubmittedPreferences(String rollNumber) {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT c.course_code, c.course_name, c.credits, p.priority " +
            "FROM preferences p " +
            "INNER JOIN courses c ON p.course_id = c.id " +
            "WHERE p.roll_number = ? " +
            "ORDER BY p.priority ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps   = conn.prepareStatement(sql);
            ps.setString(1, rollNumber);
            rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("course_code");
                row[1] = rs.getString("course_name");
                row[2] = rs.getInt   ("credits");
                row[3] = rs.getInt   ("priority");
                list.add(row);
            }
            System.out.println("Submitted preferences for "
                + rollNumber + ": " + list.size());
        } catch (SQLException e) {
            System.out.println("Error fetching student preferences: "
                + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs   != null) rs.close();   } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps   != null) ps.close();   } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close();  } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }

    // ══════════════════════════════════════════════════════════
    //  GET ALL COURSES — used in admin stats
    // ══════════════════════════════════════════════════════════
    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY department, semester, course_code";
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            st   = conn.createStatement();
            rs   = st.executeQuery(sql);
            while (rs.next()) {
                Course c = new Course();
                c.setId        (rs.getInt   ("id"));
                c.setCourseCode(rs.getString("course_code"));
                c.setCourseName(rs.getString("course_name"));
                c.setDepartment(rs.getString("department"));
                c.setSemester  (rs.getInt   ("semester"));
                c.setCredits   (rs.getInt   ("credits"));
                list.add(c);
            }
            System.out.println("All courses fetched: " + list.size());
        } catch (SQLException e) {
            System.out.println("Error fetching all courses: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs   != null) rs.close();   } catch (SQLException e) { e.printStackTrace(); }
            try { if (st   != null) st.close();   } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close();  } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }
}