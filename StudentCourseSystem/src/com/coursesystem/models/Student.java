package com.coursesystem.models;

public class Student {
    private int id;
    private String rollNumber, name, email, department, password;
    private int semester;

    public Student() {}

    public Student(String rollNumber, String name, String email,
                   String department, int semester, String password) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.email = email;
        this.department = department;
        this.semester = semester;
        this.password = password;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}