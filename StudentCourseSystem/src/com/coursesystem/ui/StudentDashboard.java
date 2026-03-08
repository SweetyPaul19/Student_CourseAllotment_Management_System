package com.coursesystem.ui;

import com.coursesystem.dao.CourseDAO;
import com.coursesystem.dao.StudentDAO;
import com.coursesystem.models.Course;
import com.coursesystem.models.Student;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDashboard extends JFrame {

    private Student student;
    private DefaultListModel<Course> availableModel = new DefaultListModel<>();
    private DefaultListModel<Course> selectedModel  = new DefaultListModel<>();
    private JList<Course> availableList, selectedList;
    private JLabel lblStatus, lblRequired, lblSelected;
    private JButton btnSubmit;
    private int totalCoursesRequired = 0;
    private boolean alreadySubmitted = false;

    public StudentDashboard(Student student) {
        this.student = student;
        setTitle("Student Dashboard — " + student.getName());
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Check if student already submitted
        CourseDAO courseDAO = new CourseDAO();
        alreadySubmitted = courseDAO.hasStudentAlreadySubmitted(
                               student.getRollNumber());
        totalCoursesRequired = courseDAO.getCourseCountForDeptSem(
                                   student.getDepartment(),
                                   student.getSemester());

        // ── Header ────────────────────────────────────────────
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0,           MainFrame.BG_DARK,
                    getWidth(), 0,  MainFrame.SECONDARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));

        JPanel headerLeft = new JPanel(new GridLayout(2, 1));
        headerLeft.setOpaque(false);
        JLabel title = new JLabel("Student Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel(
            alreadySubmitted
            ? "You have already submitted your preferences"
            : "Select and prioritize your course preferences"
        );
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(new Color(174, 214, 241));
        headerLeft.add(title);
        headerLeft.add(sub);
        header.add(headerLeft, BorderLayout.WEST);

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 20));
        badges.setOpaque(false);
        badges.add(makeBadge(student.getName(),       new Color(52, 152, 219)));
        badges.add(makeBadge(student.getDepartment(), new Color(39, 174,  96)));
        badges.add(makeBadge("Sem " + student.getSemester(), new Color(243, 156, 18)));
        badges.add(makeBadge(student.getRollNumber(), new Color(155,  89, 182)));
        header.add(badges, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── Main Content ──────────────────────────────────────
        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(MainFrame.BG_LIGHT);
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        // ── Info / Warning Banner ─────────────────────────────
        if (alreadySubmitted) {
            // Already submitted — show success banner
            JPanel successBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            successBanner.setBackground(new Color(212, 239, 223));
            successBanner.setBorder(new LineBorder(new Color(130, 210, 160), 1, true));
            JLabel icon = new JLabel("✅");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            JLabel text = new JLabel(
                "You have already submitted your preferences. "
                + "Your submitted courses are shown below.");
            text.setFont(new Font("Arial", Font.BOLD, 13));
            text.setForeground(new Color(30, 130, 70));
            successBanner.add(icon);
            successBanner.add(text);
            content.add(successBanner, BorderLayout.NORTH);
        } else {
            // Not submitted — show instruction banner
            JPanel infoBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            infoBanner.setBackground(new Color(214, 234, 248));
            infoBanner.setBorder(new LineBorder(new Color(174, 214, 241), 1, true));
            JLabel icon = new JLabel("ℹ");
            icon.setFont(new Font("Arial", Font.BOLD, 16));
            icon.setForeground(MainFrame.PRIMARY);
            JLabel text = new JLabel(
                "You must select ALL " + totalCoursesRequired
                + " assigned course(s) to submit. "
                + "Use ↑ ↓ buttons to set your priority order.");
            text.setFont(new Font("Arial", Font.PLAIN, 13));
            text.setForeground(MainFrame.TEXT_DARK);
            infoBanner.add(icon);
            infoBanner.add(text);
            content.add(infoBanner, BorderLayout.NORTH);
        }

        if (alreadySubmitted) {
            // ── Show Already Submitted View ───────────────────
            content.add(buildSubmittedView(), BorderLayout.CENTER);
        } else {
            // ── Show Course Selection View ────────────────────
            content.add(buildSelectionView(), BorderLayout.CENTER);
        }

        add(content, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════
    //  ALREADY SUBMITTED VIEW
    // ══════════════════════════════════════════════════════════
    private JPanel buildSubmittedView() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        // Load submitted preferences from DB
        String sql =
            "SELECT c.course_code, c.course_name, c.credits, p.priority " +
            "FROM preferences p " +
            "INNER JOIN courses c ON p.course_id = c.id " +
            "WHERE p.roll_number = ? " +
            "ORDER BY p.priority ASC";

        String[]   colNames = {
            "  Priority", "  Course Code",
            "  Course Name", "  Credits"
        };
        javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel(colNames, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

        try (java.sql.Connection conn =
                     com.coursesystem.db.DBConnection.getConnection();
             java.sql.PreparedStatement ps =
                     conn.prepareStatement(sql)) {
            ps.setString(1, student.getRollNumber());
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int p = rs.getInt("priority");
                String prefix = p == 1 ? "★ 1st"
                              : p == 2 ? "✦ 2nd"
                              : p == 3 ? "◆ 3rd"
                              :          "• " + p + "th";
                model.addRow(new Object[]{
                    "  " + prefix,
                    "  " + rs.getString("course_code"),
                    "  " + rs.getString("course_name"),
                    "  " + rs.getInt("credits") + " credits"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Table card
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(130, 210, 160), 2, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        JPanel tableHead = new JPanel(new BorderLayout());
        tableHead.setBackground(new Color(39, 174, 96));
        tableHead.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel title = new JLabel("✅   Your Submitted Course Preferences");
        title.setFont(new Font("Arial", Font.BOLD, 15));
        title.setForeground(Color.WHITE);
        JLabel hint = new JLabel("Submission is final and cannot be changed");
        hint.setFont(new Font("Arial", Font.PLAIN, 12));
        hint.setForeground(new Color(212, 239, 223));
        tableHead.add(title, BorderLayout.WEST);
        tableHead.add(hint,  BorderLayout.EAST);
        tableCard.add(tableHead, BorderLayout.NORTH);

        JTable table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setForeground(new Color(20, 40, 60));
        table.setGridColor(new Color(200, 235, 215));
        table.setShowGrid(true);
        table.setFocusable(false);
        table.setSelectionBackground(new Color(171, 235, 198));
        table.setIntercellSpacing(new Dimension(0, 1));

        // Priority column renderer
        table.getColumnModel().getColumn(0).setCellRenderer(
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean foc,
                        int row, int col) {
                    JLabel l = (JLabel) super.getTableCellRendererComponent(
                        t, v, sel, foc, row, col);
                    l.setFont(new Font("Arial", Font.BOLD, 13));
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                    l.setOpaque(true);
                    if (!sel) {
                        switch (row) {
                            case 0:
                                l.setBackground(new Color(39, 174, 96));
                                l.setForeground(Color.WHITE);
                                break;
                            case 1:
                                l.setBackground(new Color(41, 128, 185));
                                l.setForeground(Color.WHITE);
                                break;
                            case 2:
                                l.setBackground(new Color(230, 126, 34));
                                l.setForeground(Color.WHITE);
                                break;
                            default:
                                l.setBackground(new Color(142, 68, 173));
                                l.setForeground(Color.WHITE);
                                break;
                        }
                    }
                    return l;
                }
            }
        );

        // Row renderer
        table.setDefaultRenderer(Object.class,
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean foc,
                        int row, int col) {
                    JLabel l = (JLabel) super.getTableCellRendererComponent(
                        t, v, sel, foc, row, col);
                    l.setFont(new Font("Arial", Font.PLAIN, 14));
                    l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                    l.setOpaque(true);
                    if (!sel) {
                        l.setBackground(row % 2 == 0
                            ? Color.WHITE
                            : new Color(235, 250, 240));
                        l.setForeground(new Color(20, 40, 60));
                    }
                    return l;
                }
            }
        );

        // Header renderer
        table.getTableHeader().setBackground(new Color(39, 174, 96));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setDefaultRenderer(
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean foc,
                        int row, int col) {
                    JLabel l = (JLabel) super.getTableCellRendererComponent(
                        t, v, sel, foc, row, col);
                    l.setBackground(new Color(39, 174, 96));
                    l.setForeground(Color.WHITE);
                    l.setFont(new Font("Arial", Font.BOLD, 13));
                    l.setOpaque(true);
                    l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                    return l;
                }
            }
        );

        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(400);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        tableCard.add(scroll, BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        // Bottom info bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        bottomBar.setBackground(new Color(212, 239, 223));
        bottomBar.setBorder(new LineBorder(new Color(130, 210, 160), 1));
        JLabel info = new JLabel(
            "✅  Your " + model.getRowCount()
            + " course preferences have been recorded successfully. "
            + "Please contact admin if any changes are needed.");
        info.setFont(new Font("Arial", Font.BOLD, 13));
        info.setForeground(new Color(30, 130, 70));
        bottomBar.add(info);
        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    // ══════════════════════════════════════════════════════════
    //  COURSE SELECTION VIEW
    // ══════════════════════════════════════════════════════════
    private JPanel buildSelectionView() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        // Load available courses
        CourseDAO courseDAO = new CourseDAO();
        List<Course> courses = courseDAO.getCoursesByDeptSem(
            student.getDepartment(), student.getSemester());

        if (courses.isEmpty()) {
            // No courses assigned yet
            JPanel noCoursePanel = new JPanel(new GridBagLayout());
            noCoursePanel.setBackground(Color.WHITE);
            noCoursePanel.setBorder(new LineBorder(
                new Color(220, 228, 235), 1, true));
            JLabel noMsg = new JLabel(
                "⚠   No courses have been assigned to your group yet. "
                + "Please check back later.");
            noMsg.setFont(new Font("Arial", Font.BOLD, 14));
            noMsg.setForeground(new Color(180, 120, 0));
            noCoursePanel.add(noMsg);
            panel.add(noCoursePanel, BorderLayout.CENTER);
            return panel;
        }

        for (Course c : courses) availableModel.addElement(c);

        // Counter bar
        JPanel counterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        counterBar.setBackground(Color.WHITE);
        counterBar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 228, 235), 1),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        lblRequired = new JLabel("Required: " + totalCoursesRequired + " courses");
        lblRequired.setFont(new Font("Arial", Font.BOLD, 13));
        lblRequired.setForeground(new Color(41, 128, 185));

        lblSelected = new JLabel("Selected: 0 / " + totalCoursesRequired);
        lblSelected.setFont(new Font("Arial", Font.BOLD, 13));
        lblSelected.setForeground(new Color(192, 57, 43));

        JLabel arrow = new JLabel("→");
        arrow.setFont(new Font("Arial", Font.BOLD, 16));
        arrow.setForeground(new Color(150, 150, 150));

        JLabel instruction = new JLabel(
            "Add ALL courses to the right panel then arrange priority order");
        instruction.setFont(new Font("Arial", Font.PLAIN, 12));
        instruction.setForeground(new Color(100, 100, 100));

        counterBar.add(lblRequired);
        counterBar.add(arrow);
        counterBar.add(lblSelected);
        counterBar.add(new JSeparator(SwingConstants.VERTICAL) {{
            setPreferredSize(new Dimension(2, 22));
        }});
        counterBar.add(instruction);
        panel.add(counterBar, BorderLayout.NORTH);

        // Lists
        JPanel listsArea = new JPanel(new GridLayout(1, 3, 12, 0));
        listsArea.setOpaque(false);

        availableList = makeStyledList(availableModel);
        selectedList  = makeStyledList(selectedModel);

        listsArea.add(makeListCard(
            "Available Courses",
            "Courses assigned to your group — add ALL",
            availableList,
            new Color(41, 128, 185)
        ));

        // Middle buttons
        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setOpaque(false);
        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0;
        bc.gridy = GridBagConstraints.RELATIVE;
        bc.fill  = GridBagConstraints.HORIZONTAL;
        bc.insets = new Insets(6, 5, 6, 5);

        JButton btnAdd    = makeIconBtn("Add →",    new Color(39, 174,  96));
        JButton btnRemove = makeIconBtn("← Remove", new Color(231,  76,  60));
        JButton btnUp     = makeIconBtn("↑  Up",    new Color(41,  128, 185));
        JButton btnDown   = makeIconBtn("↓  Down",  new Color(41,  128, 185));
        JButton btnAddAll = makeIconBtn("Add All →", new Color(142, 68, 173));

        btnPanel.add(btnAddAll, bc);
        bc.insets = new Insets(12, 5, 6, 5);
        btnPanel.add(btnAdd,    bc);
        bc.insets = new Insets(6,  5, 6, 5);
        btnPanel.add(btnRemove, bc);
        bc.insets = new Insets(18, 5, 6, 5);
        btnPanel.add(btnUp,     bc);
        bc.insets = new Insets(6,  5, 6, 5);
        btnPanel.add(btnDown,   bc);
        listsArea.add(btnPanel);

        listsArea.add(makeListCard(
            "My Preferences (Priority Order)",
            "Must contain ALL " + totalCoursesRequired + " course(s)",
            selectedList,
            new Color(39, 174, 96)
        ));

        panel.add(listsArea, BorderLayout.CENTER);

        // Bottom bar
        JPanel bottomBar = new JPanel(new BorderLayout(10, 0));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 228, 235), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        lblStatus = new JLabel(
            "⚠  Select all " + totalCoursesRequired
            + " course(s) to enable submission.");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 13));
        lblStatus.setForeground(new Color(192, 57, 43));
        bottomBar.add(lblStatus, BorderLayout.WEST);

        btnSubmit = MainFrame.makeDashBtn("Submit Preferences",
                                           new Color(41, 128, 185));
        btnSubmit.setPreferredSize(new Dimension(200, 40));
        btnSubmit.setEnabled(false); // Disabled until all courses selected
        btnSubmit.setToolTipText(
            "You must select all " + totalCoursesRequired
            + " courses before submitting");
        bottomBar.add(btnSubmit, BorderLayout.EAST);
        panel.add(bottomBar, BorderLayout.SOUTH);

        // ── Button Actions ────────────────────────────────────
        btnAdd.addActionListener(e -> {
            Course c = availableList.getSelectedValue();
            if (c != null) {
                availableModel.removeElement(c);
                selectedModel.addElement(c);
                updateCounterAndButton();
            }
        });

        btnRemove.addActionListener(e -> {
            Course c = selectedList.getSelectedValue();
            if (c != null) {
                selectedModel.removeElement(c);
                availableModel.addElement(c);
                updateCounterAndButton();
            }
        });

        btnAddAll.addActionListener(e -> {
            // Move all available courses to selected
            List<Course> toMove = new ArrayList<>();
            for (int i = 0; i < availableModel.size(); i++)
                toMove.add(availableModel.get(i));
            for (Course c : toMove) {
                availableModel.removeElement(c);
                selectedModel.addElement(c);
            }
            updateCounterAndButton();
        });

        btnUp.addActionListener(e -> {
            int i = selectedList.getSelectedIndex();
            if (i > 0) {
                Course c = selectedModel.remove(i);
                selectedModel.add(i - 1, c);
                selectedList.setSelectedIndex(i - 1);
            }
        });

        btnDown.addActionListener(e -> {
            int i = selectedList.getSelectedIndex();
            if (i >= 0 && i < selectedModel.size() - 1) {
                Course c = selectedModel.remove(i);
                selectedModel.add(i + 1, c);
                selectedList.setSelectedIndex(i + 1);
            }
        });

        btnSubmit.addActionListener(e -> submitPreferences());

        return panel;
    }

    // ══════════════════════════════════════════════════════════
    //  UPDATE COUNTER AND SUBMIT BUTTON
    // ══════════════════════════════════════════════════════════
    private void updateCounterAndButton() {
        int selected = selectedModel.size();
        int required = totalCoursesRequired;

        lblSelected.setText("Selected: " + selected + " / " + required);

        if (selected == required) {
            // All courses selected — enable submit
            lblSelected.setForeground(new Color(39, 174, 96));
            lblStatus.setText(
                "✅  All " + required
                + " course(s) selected! Arrange priority order then submit.");
            lblStatus.setForeground(new Color(39, 174, 96));
            btnSubmit.setEnabled(true);
            btnSubmit.setToolTipText("Click to submit your preferences");
        } else if (selected > required) {
            // More than required — should not happen but handle it
            lblSelected.setForeground(new Color(192, 57, 43));
            lblStatus.setText(
                "⚠  You have selected more than required. Remove "
                + (selected - required) + " course(s).");
            lblStatus.setForeground(new Color(192, 57, 43));
            btnSubmit.setEnabled(false);
        } else {
            // Less than required
            lblSelected.setForeground(new Color(192, 57, 43));
            lblStatus.setText(
                "⚠  Select " + (required - selected)
                + " more course(s) to enable submission.");
            lblStatus.setForeground(new Color(192, 57, 43));
            btnSubmit.setEnabled(false);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  SUBMIT PREFERENCES
    // ══════════════════════════════════════════════════════════
    private void submitPreferences() {
        int selected = selectedModel.size();
        int required = totalCoursesRequired;

        // Double check
        if (selected != required) {
            JOptionPane.showMessageDialog(this,
                "You must select exactly " + required + " course(s).\n"
                + "Currently selected: " + selected,
                "Cannot Submit", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirm dialog
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to submit your preferences?\n"
            + "This action cannot be undone.\n\n"
            + "You have selected " + selected + " course(s) in priority order.",
            "Confirm Submission",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < selectedModel.size(); i++)
            ids.add(selectedModel.get(i).getId());

        StudentDAO dao = new StudentDAO();
        if (dao.submitPreferences(student.getRollNumber(), ids)) {
            // Show success then reload the dashboard
            // to switch to submitted view
            JOptionPane.showMessageDialog(this,
                "✅  Preferences submitted successfully!\n\n"
                + "Your " + ids.size() + " course(s) have been recorded.\n"
                + "The dashboard will now show your submitted courses.",
                "Submission Successful",
                JOptionPane.INFORMATION_MESSAGE);

            // Reload dashboard to show submitted view
            dispose();
            new StudentDashboard(student).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "Submission failed. Please try again.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════
    private JPanel makeListCard(String title, String subtitle,
                                 JList<Course> list, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(220, 228, 235), 1, true));

        JPanel cardHeader = new JPanel(new GridLayout(2, 1));
        cardHeader.setBackground(accent);
        cardHeader.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 13));
        t.setForeground(Color.WHITE);
        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Arial", Font.PLAIN, 11));
        s.setForeground(new Color(255, 255, 255, 200));
        cardHeader.add(t);
        cardHeader.add(s);
        card.add(cardHeader, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JList<Course> makeStyledList(DefaultListModel<Course> model) {
        JList<Course> list = new JList<>(model);
        list.setFont(new Font("Arial", Font.PLAIN, 13));
        list.setSelectionBackground(new Color(214, 234, 248));
        list.setSelectionForeground(new Color(20, 40, 60));
        list.setFixedCellHeight(44);
        list.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                l.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                l.setFont(new Font("Arial", Font.PLAIN, 13));
                if (!isSelected) {
                    l.setBackground(index % 2 == 0
                        ? Color.WHITE
                        : new Color(248, 250, 252));
                    l.setForeground(new Color(20, 40, 60));
                }
                return l;
            }
        });
        return list;
    }

    private JButton makeIconBtn(String text, Color color) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.darker() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setPreferredSize(new Dimension(115, 36));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JLabel makeBadge(String text, Color color) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(),
                                      color.getBlue(), 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
            }
        };
        l.setFont(new Font("Arial", Font.BOLD, 11));
        l.setForeground(Color.WHITE);
        l.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        l.setOpaque(false);
        return l;
    }
}