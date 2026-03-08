package com.coursesystem.ui;

import com.coursesystem.dao.StudentDAO;
import com.coursesystem.models.Student;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;

public class StudentRegistrationFrame extends JFrame {

    private JTextField    tfName, tfEmail;
    private JComboBox<?>  cbDept, cbSem;
    private JPasswordField pfPass, pfConfirm;

    public StudentRegistrationFrame() {
        setTitle("Student Registration");
        setSize(520, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Header
        add(MainFrame.makeHeaderPanel(
            "Student Registration",
            "Create your account to get started"
        ), BorderLayout.NORTH);

        // Card
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(MainFrame.BG_LIGHT);
        card.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0;
        gbc.gridy  = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(5, 0, 2, 0);

        String[] depts = {"CSE", "ECE", "ME", "CE", "EEE"};
        Integer[] sems  = {1, 2, 3, 4, 5, 6, 7, 8};

        tfName    = MainFrame.makeTextField();
        tfEmail   = MainFrame.makeTextField();
        cbDept    = MainFrame.makeComboBox(depts);
        cbSem     = MainFrame.makeComboBox(sems);
        pfPass    = MainFrame.makePasswordField();
        pfConfirm = MainFrame.makePasswordField();

        addField(card, gbc, "Full Name",    tfName);
        addField(card, gbc, "Email Address", tfEmail);
        addField(card, gbc, "Department",   cbDept);
        addField(card, gbc, "Semester",     cbSem);
        addField(card, gbc, "Password",     pfPass);
        addField(card, gbc, "Confirm Password", pfConfirm);

        // Register button
        JButton btnRegister = MainFrame.makeDashBtn("Create Account", MainFrame.SUCCESS);
        btnRegister.setPreferredSize(new Dimension(0, 44));
        gbc.insets = new Insets(20, 0, 5, 0);
        card.add(btnRegister, gbc);

        // Login link
        JLabel loginLink = new JLabel("Already have an account? Login here",
                                       SwingConstants.CENTER);
        loginLink.setFont(new Font("Arial", Font.PLAIN, 12));
        loginLink.setForeground(MainFrame.ACCENT);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.insets = new Insets(5, 0, 0, 0);
        card.add(loginLink, gbc);

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        btnRegister.addActionListener(e -> register());
        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new StudentLoginFrame().setVisible(true);
            }
        });
    }

    private void addField(JPanel panel, GridBagConstraints gbc,
                           String label, JComponent field) {
        gbc.insets = new Insets(8, 0, 2, 0);
        panel.add(MainFrame.makeFormLabel(label), gbc);
        gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(field, gbc);
    }

    private void register() {
        String name    = tfName.getText().trim();
        String email   = tfEmail.getText().trim();
        String dept    = (String)  cbDept.getSelectedItem();
        int    sem     = (Integer) cbSem.getSelectedItem();
        String pass    = new String(pfPass.getPassword());
        String confirm = new String(pfConfirm.getPassword());

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }
        if (!pass.equals(confirm)) {
            showError("Passwords do not match.");
            return;
        }
        if (pass.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        String year = String.valueOf(LocalDate.now().getYear()).substring(2);
        String roll = dept + year + String.format("%04d", (int)(Math.random() * 9000 + 1000));

        Student student = new Student(roll, name, email, dept, sem, pass);
        if (new StudentDAO().registerStudent(student)) {
            JPanel msg = new JPanel(new GridLayout(4, 1, 0, 6));
            msg.setBackground(Color.WHITE);
            JLabel t = new JLabel("Registration Successful!");
            t.setFont(new Font("Arial", Font.BOLD, 16));
            t.setForeground(MainFrame.SUCCESS);
            msg.add(t);
            msg.add(new JLabel("Your Roll Number has been generated:"));
            JLabel rollLabel = new JLabel(roll);
            rollLabel.setFont(new Font("Arial", Font.BOLD, 20));
            rollLabel.setForeground(MainFrame.PRIMARY);
            msg.add(rollLabel);
            msg.add(new JLabel("Please save this Roll Number for login."));
            JOptionPane.showMessageDialog(this, msg, "Success",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            showError("Registration failed. Email may already be in use.");
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}