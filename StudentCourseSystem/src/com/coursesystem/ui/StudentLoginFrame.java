package com.coursesystem.ui;

import com.coursesystem.dao.StudentDAO;
import com.coursesystem.models.Student;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class StudentLoginFrame extends JFrame {

    private JTextField     tfRoll;
    private JPasswordField pfPass;

    public StudentLoginFrame() {
        setTitle("Student Login");
        setSize(460, 460);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        add(MainFrame.makeHeaderPanel(
            "Student Login",
            "Sign in to access your course preferences"
        ), BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(MainFrame.BG_LIGHT);
        card.setBorder(BorderFactory.createEmptyBorder(30, 45, 30, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0;
        gbc.gridy  = GridBagConstraints.RELATIVE;

        tfRoll = MainFrame.makeTextField();
        pfPass = MainFrame.makePasswordField();

        gbc.insets = new Insets(8, 0, 2, 0);
        card.add(MainFrame.makeFormLabel("Roll Number"), gbc);
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(tfRoll, gbc);

        gbc.insets = new Insets(8, 0, 2, 0);
        card.add(MainFrame.makeFormLabel("Password"), gbc);
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(pfPass, gbc);

        JButton btnLogin = MainFrame.makeDashBtn("Sign In", MainFrame.ACCENT);
        btnLogin.setPreferredSize(new Dimension(0, 44));
        gbc.insets = new Insets(22, 0, 8, 0);
        card.add(btnLogin, gbc);

        // Divider
        JSeparator div = new JSeparator();
        div.setForeground(MainFrame.BORDER_COLOR);
        gbc.insets = new Insets(8, 0, 8, 0);
        card.add(div, gbc);

        JLabel regLink = new JLabel("New student? Register here", SwingConstants.CENTER);
        regLink.setFont(new Font("Arial", Font.PLAIN, 12));
        regLink.setForeground(MainFrame.ACCENT);
        regLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(regLink, gbc);

        add(card, BorderLayout.CENTER);

        btnLogin.addActionListener(e -> login());
        tfRoll.addActionListener(e -> login());
        pfPass.addActionListener(e -> login());

        regLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new StudentRegistrationFrame().setVisible(true);
            }
        });
    }

    private void login() {
        String roll = tfRoll.getText().trim();
        String pass = new String(pfPass.getPassword());
        if (roll.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Student student = new StudentDAO().login(roll, pass);
        if (student != null) {
            dispose();
            new StudentDashboard(student).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid Roll Number or Password.", "Login Failed",
                JOptionPane.ERROR_MESSAGE);
            pfPass.setText("");
        }
    }
}