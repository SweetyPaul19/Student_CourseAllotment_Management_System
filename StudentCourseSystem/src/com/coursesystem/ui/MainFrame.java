package com.coursesystem.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class MainFrame extends JFrame {

    // Corporate Blue Palette
    static final Color PRIMARY       = new Color(26,  82,  118);   // Deep navy blue
    static final Color SECONDARY     = new Color(33,  97,  140);   // Medium blue
    static final Color ACCENT        = new Color(52,  152, 219);   // Bright blue
    static final Color SUCCESS       = new Color(39,  174, 96);    // Green
    static final Color WARNING       = new Color(243, 156, 18);    // Orange
    static final Color BG_DARK       = new Color(18,  52,  86);    // Very dark blue
    static final Color BG_LIGHT      = new Color(234, 242, 248);   // Light blue-grey
    static final Color TEXT_WHITE    = Color.WHITE;
    static final Color TEXT_DARK     = new Color(44,  62,  80);
    static final Color TEXT_MUTED    = new Color(127, 140, 141);
    static final Color CARD_BG       = Color.WHITE;
    static final Color BORDER_COLOR  = new Color(213, 219, 219);

    public MainFrame() {
        setTitle("Student Course Allotment Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 580);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        // ── Left Panel (Branding) ──────────────────────────────
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                     RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0,           BG_DARK,
                    0, getHeight(), SECONDARY);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setPreferredSize(new Dimension(340, 0));
        leftPanel.setLayout(new GridBagLayout());

        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0; lc.gridy = GridBagConstraints.RELATIVE;
        lc.insets = new Insets(8, 20, 8, 20);
        lc.fill   = GridBagConstraints.HORIZONTAL;

        // Logo circle
        JLabel logo = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT);
                g2.fillOval(0, 0, 80, 80);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 32));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("SC", (80 - fm.stringWidth("SC")) / 2,
                              (80 + fm.getAscent() - fm.getDescent()) / 2);
            }
            @Override public Dimension getPreferredSize() { return new Dimension(80, 80); }
        };
        lc.fill   = GridBagConstraints.NONE;
        lc.anchor = GridBagConstraints.CENTER;
        lc.insets = new Insets(50, 20, 10, 20);
        leftPanel.add(logo, lc);

        JLabel appName = makeLabel("COURSE MATRIX", new Font("Arial", Font.BOLD, 22), TEXT_WHITE);
        lc.insets = new Insets(4, 20, 2, 20);
        leftPanel.add(appName, lc);

        JLabel appSub = makeLabel("Student Course Allotment", new Font("Arial", Font.PLAIN, 13), TEXT_MUTED);
        leftPanel.add(appSub, lc);
        JLabel appSub2 = makeLabel("Management System", new Font("Arial", Font.PLAIN, 13), TEXT_MUTED);
        leftPanel.add(appSub2, lc);

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255,255,255,50));
        lc.insets = new Insets(20, 20, 20, 20);
        lc.fill   = GridBagConstraints.HORIZONTAL;
        leftPanel.add(sep, lc);

        // Info cards on left
        leftPanel.add(makeInfoCard("👨‍🎓", "Students", "Register & Submit Preferences"), lc);
        lc.insets = new Insets(8, 20, 8, 20);
        leftPanel.add(makeInfoCard("🏫", "Courses",  "Manage & Assign Courses"), lc);
        leftPanel.add(makeInfoCard("📋", "Admin",    "Monitor & Review System"), lc);

        // Footer version
        JLabel version = makeLabel("2026", new Font("Arial", Font.PLAIN, 11), TEXT_MUTED);
        lc.insets = new Insets(30, 20, 20, 20);
        leftPanel.add(version, lc);

        add(leftPanel, BorderLayout.WEST);

        // ── Right Panel (Actions) ──────────────────────────────
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(BG_LIGHT);

        GridBagConstraints rc = new GridBagConstraints();
        rc.gridx = 0; rc.gridy = GridBagConstraints.RELATIVE;
        rc.fill  = GridBagConstraints.HORIZONTAL;
        rc.insets = new Insets(8, 50, 8, 50);

        JLabel welcome = makeLabel("Welcome Back", new Font("Arial", Font.BOLD, 28), TEXT_DARK);
        rc.insets = new Insets(50, 50, 4, 50);
        rightPanel.add(welcome, rc);

        JLabel sub = makeLabel("Please select how you want to continue",
                               new Font("Arial", Font.PLAIN, 14), TEXT_MUTED);
        rc.insets = new Insets(0, 50, 30, 50);
        rightPanel.add(sub, rc);

        // Action Cards
        rc.insets = new Insets(8, 50, 8, 50);
        rightPanel.add(makeActionCard(
            "Student Registration",
            "New student? Create your account here",
            SUCCESS, "REGISTER", () -> new StudentRegistrationFrame().setVisible(true)
        ), rc);

        rightPanel.add(makeActionCard(
            "Student Login",
            "Already registered? Login to submit preferences",
            ACCENT, "LOGIN", () -> new StudentLoginFrame().setVisible(true)
        ), rc);

        rightPanel.add(makeActionCard(
            "Admin Portal",
            "Manage students, courses and preferences",
            PRIMARY, "ADMIN LOGIN", () -> new AdminLoginFrame().setVisible(true)
        ), rc);

        // Bottom note
        JLabel note = makeLabel("© 2026 Course Matrix  •  All rights reserved",
                                new Font("Arial", Font.PLAIN, 11), TEXT_MUTED);
        rc.insets = new Insets(20, 50, 20, 50);
        rightPanel.add(note, rc);

        add(rightPanel, BorderLayout.CENTER);
    }

    // ── Helpers ───────────────────────────────────────────────
    private JLabel makeLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    private JPanel makeInfoCard(String icon, String title, String desc) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        card.add(ico, BorderLayout.WEST);

        JPanel txt = new JPanel(new GridLayout(2, 1));
        txt.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 13));
        t.setForeground(TEXT_WHITE);
        JLabel d = new JLabel(desc);
        d.setFont(new Font("Arial", Font.PLAIN, 11));
        d.setForeground(TEXT_MUTED);
        txt.add(t); txt.add(d);
        card.add(txt, BorderLayout.CENTER);
        return card;
    }

    private JPanel makeActionCard(String title, String desc,
                                   Color color, String btnText, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        // Left color bar
        JPanel bar = new JPanel();
        bar.setBackground(color);
        bar.setPreferredSize(new Dimension(5, 0));
        card.add(bar, BorderLayout.WEST);

        // Text
        JPanel txt = new JPanel(new GridLayout(2, 1, 0, 3));
        txt.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 15));
        t.setForeground(TEXT_DARK);
        JLabel d = new JLabel(desc);
        d.setFont(new Font("Arial", Font.PLAIN, 12));
        d.setForeground(TEXT_MUTED);
        txt.add(t); txt.add(d);
        card.add(txt, BorderLayout.CENTER);

        // Button
        JButton btn = makeDashBtn(btnText, color);
        btn.addActionListener(e -> action.run());
        card.add(btn, BorderLayout.EAST);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 250, 252));
                card.repaint();
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BG);
                card.repaint();
            }
        });

        return card;
    }

    static JButton makeDashBtn(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.darker() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(120, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    static JTextField makeTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Arial", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        tf.setBackground(Color.WHITE);
        tf.setPreferredSize(new Dimension(0, 40));
        return tf;
    }

    static JPasswordField makePasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Arial", Font.PLAIN, 14));
        pf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        pf.setBackground(Color.WHITE);
        pf.setPreferredSize(new Dimension(0, 40));
        return pf;
    }

    static JComboBox<?> makeComboBox(Object[] items) {
        JComboBox<Object> cb = new JComboBox<>(items);
        cb.setFont(new Font("Arial", Font.PLAIN, 14));
        cb.setBackground(Color.WHITE);
        cb.setPreferredSize(new Dimension(0, 40));
        cb.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        return cb;
    }

    static JLabel makeFormLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setForeground(TEXT_DARK);
        return l;
    }

    static JPanel makeHeaderPanel(String title, String subtitle) {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0,           BG_DARK,
                    getWidth(), 0,  SECONDARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setLayout(new GridBagLayout());
        header.setPreferredSize(new Dimension(0, 75));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = GridBagConstraints.RELATIVE;
        gc.insets = new Insets(2, 25, 2, 25);
        gc.fill   = GridBagConstraints.HORIZONTAL;

        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 20));
        t.setForeground(TEXT_WHITE);
        header.add(t, gc);

        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Arial", Font.PLAIN, 12));
        s.setForeground(new Color(174, 214, 241));
        header.add(s, gc);

        return header;
    }
}