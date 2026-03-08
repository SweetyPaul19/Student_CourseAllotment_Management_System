package com.coursesystem.ui;

import com.coursesystem.db.DBConnection;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class AdminLoginFrame extends JFrame {

    private JTextField     tfUser;
    private JPasswordField pfPass;

    public AdminLoginFrame() {
        setTitle("Admin Login");
        setSize(460, 420);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        add(MainFrame.makeHeaderPanel(
            "Admin Portal",
            "Restricted access — authorized personnel only"
        ), BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(MainFrame.BG_LIGHT);
        card.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0;
        gbc.gridy  = GridBagConstraints.RELATIVE;

        tfUser = MainFrame.makeTextField();
        pfPass = MainFrame.makePasswordField();

        gbc.insets = new Insets(8, 0, 2, 0);
        card.add(MainFrame.makeFormLabel("Username"), gbc);
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(tfUser, gbc);

        gbc.insets = new Insets(8, 0, 2, 0);
        card.add(MainFrame.makeFormLabel("Password"), gbc);
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(pfPass, gbc);

        // Admin badge
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        badge.setOpaque(false);
        JLabel dot = new JLabel("●");
        dot.setForeground(MainFrame.WARNING);
        dot.setFont(new Font("Arial", Font.PLAIN, 10));
        JLabel badgeText = new JLabel("Default: admin / admin123");
        badgeText.setFont(new Font("Arial", Font.ITALIC, 11));
        badgeText.setForeground(MainFrame.TEXT_MUTED);
        badge.add(dot); badge.add(badgeText);
        gbc.insets = new Insets(4, 0, 4, 0);
        card.add(badge, gbc);

        JButton btnLogin = MainFrame.makeDashBtn("Sign In as Admin", MainFrame.PRIMARY);
        btnLogin.setPreferredSize(new Dimension(0, 44));
        gbc.insets = new Insets(20, 0, 8, 0);
        card.add(btnLogin, gbc);

        add(card, BorderLayout.CENTER);

        btnLogin.addActionListener(e -> login());
        tfUser.addActionListener(e -> login());
        pfPass.addActionListener(e -> login());
    }

    private void login() {
        String user = tfUser.getText().trim();
        String pass = new String(pfPass.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (validateAdmin(user, pass)) {
            dispose();
            new AdminDashboard().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid admin credentials.", "Access Denied",
                JOptionPane.ERROR_MESSAGE);
            pfPass.setText("");
        }
    }

    private boolean validateAdmin(String user, String pass) {
        String sql = "SELECT * FROM admins WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}