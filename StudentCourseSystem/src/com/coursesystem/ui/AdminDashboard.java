package com.coursesystem.ui;

import com.coursesystem.dao.CourseDAO;
import com.coursesystem.dao.StudentDAO;
import com.coursesystem.models.Course;
import com.coursesystem.models.Student;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {

    // ── Color Palette ─────────────────────────────────────────
    private static final Color HEADER_TOP     = new Color(10,  35,  66);
    private static final Color HEADER_BOT     = new Color(21,  67,  96);
    private static final Color TAB_ACTIVE     = new Color(41, 128, 185);
    private static final Color TAB_INACTIVE   = new Color(26,  82, 118);
    private static final Color CONTENT_BG     = new Color(245, 247, 250);
    private static final Color CARD_BG        = Color.WHITE;
    private static final Color CARD_BORDER    = new Color(220, 228, 235);
    private static final Color TBL_HEADER_BLU = new Color(21,  97, 141);
    private static final Color TBL_HEADER_PUR = new Color(142,  68, 173);
    private static final Color TBL_HEADER_GRN = new Color(39,  174,  96);
    private static final Color TBL_HEADER_RED = new Color(192,  57,  43);
    private static final Color TBL_ROW_ODD    = Color.WHITE;
    private static final Color TBL_ROW_EVEN   = new Color(235, 245, 251);
    private static final Color TBL_ROW_PUR    = new Color(245, 238, 248);
    private static final Color TBL_ROW_GRN    = new Color(235, 250, 240);
    private static final Color TBL_ROW_RED    = new Color(253, 240, 238);
    private static final Color TBL_SELECT_BLU = new Color(174, 214, 241);
    private static final Color TBL_SELECT_PUR = new Color(215, 189, 226);
    private static final Color TBL_SELECT_GRN = new Color(171, 235, 198);
    private static final Color TBL_SELECT_RED = new Color(245, 183, 177);
    private static final Color TBL_TEXT       = new Color(20,  40,  60);
    private static final Color BTN_PRIMARY    = new Color(41, 128, 185);
    private static final Color BTN_SUCCESS    = new Color(39, 174,  96);
    private static final Color BTN_DANGER     = new Color(192,  57,  43);
    private static final Color BTN_WARNING    = new Color(211, 126,   0);
    private static final Color STAT_BLUE      = new Color(41, 128, 185);
    private static final Color STAT_GREEN     = new Color(39, 174,  96);
    private static final Color STAT_ORANGE    = new Color(230, 126,  34);
    private static final Color STAT_PURPLE    = new Color(142,  68, 173);
    private static final Color TEXT_PRIMARY   = new Color(20,  40,  60);
    private static final Color TEXT_SECONDARY = new Color(80, 100, 120);
    private static final Color TEXT_WHITE     = Color.WHITE;
    private static final Color PRIORITY_1     = new Color(39,  174,  96);
    private static final Color PRIORITY_2     = new Color(41,  128, 185);
    private static final Color PRIORITY_3     = new Color(230, 126,  34);
    private static final Color PRIORITY_OTHER = new Color(142,  68, 173);

    // ── Table Models ──────────────────────────────────────────
    private JTable            studentTable, prefTable,
                              allocatedTable, notSubmittedTable;
    private DefaultTableModel studentModel,  prefModel,
                              allocatedModel, notSubmittedModel;

    // ── Filter Combos ─────────────────────────────────────────
    private JComboBox<String>  cbDept;
    private JComboBox<Integer> cbSem;

    // ── Stat Labels ───────────────────────────────────────────
    private JLabel lblStudentCount, lblPrefCount,
                   lblCourseCount,  lblDeptCount;

    // ── Allocation Count Labels ───────────────────────────────
    private JLabel lblAllocCount, lblNotSubCount;

    public AdminDashboard() {
        setTitle("Admin Dashboard — Course Allotment System");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(CONTENT_BG);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            getContentPane().removeAll();
            setLayout(new BorderLayout(0, 0));
            getContentPane().add(buildHeader(), BorderLayout.NORTH);

            JPanel body = new JPanel(new BorderLayout(0, 0));
            body.setBackground(CONTENT_BG);
            body.add(buildStatsBar(), BorderLayout.NORTH);
            body.add(buildTabs(),     BorderLayout.CENTER);
            getContentPane().add(body, BorderLayout.CENTER);
        }
        super.setVisible(b);
    }

    // ══════════════════════════════════════════════════════════
    //  HEADER
    // ══════════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0,           HEADER_TOP,
                    getWidth(), 0,  HEADER_BOT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 72));
        header.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 28));

        // Left — logo + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);
        left.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));

        JPanel logoCircle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TAB_ACTIVE);
                g2.fillOval(0, 0, 44, 44);
                g2.setColor(TEXT_WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                String t = "SC";
                g2.drawString(t,
                    (44 - fm.stringWidth(t)) / 2,
                    (44 + fm.getAscent() - fm.getDescent()) / 2);
            }
            @Override public Dimension getPreferredSize() {
                return new Dimension(44, 44);
            }
        };
        left.add(logoCircle);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 1));
        titleBlock.setOpaque(false);
        JLabel title = new JLabel("Course Allotment System");
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(TEXT_WHITE);
        JLabel subtitle = new JLabel("Admin Control Panel");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(new Color(174, 214, 241));
        titleBlock.add(title);
        titleBlock.add(subtitle);
        left.add(titleBlock);
        header.add(left, BorderLayout.WEST);

        // Right — admin badge + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 18));
        right.setOpaque(false);

        JLabel adminBadge = new JLabel("  ● ADMIN  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BTN_WARNING);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g);
            }
        };
        adminBadge.setFont(new Font("Arial", Font.BOLD, 12));
        adminBadge.setForeground(TEXT_WHITE);
        adminBadge.setOpaque(false);
        adminBadge.setPreferredSize(new Dimension(100, 30));

        JButton btnLogout = makeBtn("Logout", BTN_DANGER, new Dimension(90, 32));
        btnLogout.addActionListener(e -> {
            dispose();
            new MainFrame().setVisible(true);
        });

        right.add(adminBadge);
        right.add(btnLogout);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    // ══════════════════════════════════════════════════════════
    //  STATS BAR
    // ══════════════════════════════════════════════════════════
    private JPanel buildStatsBar() {
        JPanel bar = new JPanel(new GridLayout(1, 4, 14, 0));
        bar.setBackground(CONTENT_BG);
        bar.setBorder(BorderFactory.createEmptyBorder(16, 20, 10, 20));

        lblStudentCount = new JLabel("0");
        lblPrefCount    = new JLabel("0");
        lblCourseCount  = new JLabel("0");
        lblDeptCount    = new JLabel("5");

        bar.add(makeStatCard("Total Students",    lblStudentCount, "👨‍🎓", STAT_BLUE));
        bar.add(makeStatCard("Preferences Filed", lblPrefCount,    "📋",   STAT_GREEN));
        bar.add(makeStatCard("Courses Added",     lblCourseCount,  "📚",   STAT_ORANGE));
        bar.add(makeStatCard("Departments",       lblDeptCount,    "🏫",   STAT_PURPLE));

        refreshStats();
        return bar;
    }

    private JPanel makeStatCard(String label, JLabel valueLabel,
                                 String icon, Color color) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JPanel strip = new JPanel();
        strip.setBackground(color);
        strip.setPreferredSize(new Dimension(6, 0));
        card.add(strip, BorderLayout.WEST);

        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        ico.setHorizontalAlignment(SwingConstants.CENTER);
        ico.setPreferredSize(new Dimension(44, 44));
        card.add(ico, BorderLayout.EAST);

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 4));
        text.setOpaque(false);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(TEXT_SECONDARY);
        text.add(valueLabel);
        text.add(lbl);
        card.add(text, BorderLayout.CENTER);

        return card;
    }

    private void refreshStats() {
        int students = new StudentDAO().getAllStudents().size();
        int prefs    = new CourseDAO().getAllPreferences().size();
        int courses  = getCourseCount();
        lblStudentCount.setText(String.valueOf(students));
        lblPrefCount   .setText(String.valueOf(prefs));
        lblCourseCount .setText(String.valueOf(courses));
        lblDeptCount   .setText("5");
    }

    private int getCourseCount() {
        try (java.sql.Connection conn =
                     com.coursesystem.db.DBConnection.getConnection();
             java.sql.Statement st = conn.createStatement();
             java.sql.ResultSet rs =
                     st.executeQuery("SELECT COUNT(*) FROM courses")) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // ══════════════════════════════════════════════════════════
    //  TABS
    // ══════════════════════════════════════════════════════════
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Arial", Font.BOLD, 13));
        tabs.setBackground(CONTENT_BG);
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

        // Add tabs with empty titles
        tabs.addTab("", buildStudentsTab());
        tabs.addTab("", buildAddCourseTab());
        tabs.addTab("", buildPreferencesTab());
        tabs.addTab("", buildAllocationTab());

        // Tab titles
        String[] titles = {
            "👨‍🎓   Students",
            "📚   Add Course",
            "📋   Preferences",
            "📊   Allocation"
        };

        // Custom tab label for each tab
        for (int i = 0; i < titles.length; i++) {
            final int index = i;

            JPanel tabPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    // Dark blue when active, darker blue when inactive
                    g2.setColor(tabs.getSelectedIndex() == index
                        ? TAB_ACTIVE : TAB_INACTIVE);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    super.paintComponent(g);
                }
            };
            tabPanel.setOpaque(false);
            tabPanel.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

            JLabel lbl = new JLabel(titles[i], SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            lbl.setForeground(Color.WHITE);  // white text always visible
            lbl.setOpaque(false);
            tabPanel.add(lbl, BorderLayout.CENTER);

            tabs.setTabComponentAt(i, tabPanel);
        }

        // Repaint tab labels whenever tab changes
        tabs.addChangeListener(e -> {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                Component c = tabs.getTabComponentAt(i);
                if (c != null) c.repaint();
            }
        });

        return tabs;
    }
    // ══════════════════════════════════════════════════════════
    //  STUDENTS TAB
    // ══════════════════════════════════════════════════════════
    private JPanel buildStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 8, 8, 8));

        // Filter card
        JPanel filterCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterCard.setBackground(CARD_BG);
        filterCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JLabel filterTitle = new JLabel("  🔍  Filter Students");
        filterTitle.setFont(new Font("Arial", Font.BOLD, 13));
        filterTitle.setForeground(TEXT_PRIMARY);
        filterCard.add(filterTitle);

        JSeparator vs = new JSeparator(SwingConstants.VERTICAL);
        vs.setPreferredSize(new Dimension(2, 30));
        vs.setForeground(CARD_BORDER);
        filterCard.add(vs);

        JLabel deptLbl = new JLabel("Department:");
        deptLbl.setFont(new Font("Arial", Font.BOLD, 12));
        deptLbl.setForeground(TEXT_SECONDARY);
        filterCard.add(deptLbl);

        String[]  depts = {"All", "CSE", "ECE", "ME", "CE", "EEE"};
        Integer[] sems  = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        cbDept = styledCombo(depts, 130);
        cbSem  = styledCombo(sems, 90);
        filterCard.add(cbDept);

        JLabel semLbl = new JLabel("Semester:");
        semLbl.setFont(new Font("Arial", Font.BOLD, 12));
        semLbl.setForeground(TEXT_SECONDARY);
        filterCard.add(semLbl);
        filterCard.add(cbSem);

        JButton btnFilter  = makeBtn("Apply Filter", BTN_PRIMARY, new Dimension(120, 34));
        JButton btnShowAll = makeBtn("Show All",     BTN_SUCCESS, new Dimension(100, 34));
        filterCard.add(btnFilter);
        filterCard.add(btnShowAll);
        panel.add(filterCard, BorderLayout.NORTH);

        // Table card
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD_BG);
        tableCard.setBorder(new LineBorder(CARD_BORDER, 1, true));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(TBL_HEADER_BLU);
        tableHeader.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel tableTitle = new JLabel("Registered Students");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        tableTitle.setForeground(TEXT_WHITE);
        tableHeader.add(tableTitle, BorderLayout.WEST);
        JLabel tableHint = new JLabel("Select a row to view details");
        tableHint.setFont(new Font("Arial", Font.PLAIN, 11));
        tableHint.setForeground(new Color(174, 214, 241));
        tableHeader.add(tableHint, BorderLayout.EAST);
        tableCard.add(tableHeader, BorderLayout.NORTH);

        String[] cols = {
            "  Roll Number", "  Student Name",
            "  Email Address", "  Department", "  Semester"
        };
        studentModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        studentTable = buildStyledTable(studentModel, TBL_HEADER_BLU,
                                        TBL_ROW_ODD, TBL_ROW_EVEN,
                                        TBL_SELECT_BLU);
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(130);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(220);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(90);

        JScrollPane scroll = new JScrollPane(studentTable);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        tableCard.add(scroll, BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        loadAllStudents();

        btnFilter.addActionListener(e -> {
            String dept = (String)  cbDept.getSelectedItem();
            int    sem  = (Integer) cbSem.getSelectedItem();
            if ("All".equals(dept) || sem == 0) loadAllStudents();
            else loadFilteredStudents(dept, sem);
        });
        btnShowAll.addActionListener(e -> {
            cbDept.setSelectedIndex(0);
            cbSem.setSelectedIndex(0);
            loadAllStudents();
        });

        return panel;
    }

    private void loadAllStudents() {
        studentModel.setRowCount(0);
        for (Student s : new StudentDAO().getAllStudents()) {
            studentModel.addRow(new Object[]{
                "  " + s.getRollNumber(),
                "  " + s.getName(),
                "  " + s.getEmail(),
                "  " + s.getDepartment(),
                "  " + s.getSemester()
            });
        }
        studentModel.fireTableDataChanged();
        refreshStats();
    }

    private void loadFilteredStudents(String dept, int sem) {
        studentModel.setRowCount(0);
        for (Student s : new StudentDAO().filterStudents(dept, sem)) {
            studentModel.addRow(new Object[]{
                "  " + s.getRollNumber(),
                "  " + s.getName(),
                "  " + s.getEmail(),
                "  " + s.getDepartment(),
                "  " + s.getSemester()
            });
        }
        studentModel.fireTableDataChanged();
    }

    // ══════════════════════════════════════════════════════════
    //  ADD COURSE TAB
    // ══════════════════════════════════════════════════════════
    private JPanel buildAddCourseTab() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(CONTENT_BG);
        outer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new LineBorder(CARD_BORDER, 1, true));
        card.setPreferredSize(new Dimension(520, 510));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.gridx   = 0;
        gbc.gridy   = GridBagConstraints.RELATIVE;
        gbc.weightx = 1;

        JPanel cardHead = new JPanel(new BorderLayout());
        cardHead.setBackground(TBL_HEADER_BLU);
        cardHead.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));
        JLabel cardTitle = new JLabel("📚   Add New Course");
        cardTitle.setFont(new Font("Arial", Font.BOLD, 16));
        cardTitle.setForeground(TEXT_WHITE);
        JLabel cardSub = new JLabel("Fill all fields and click Add Course");
        cardSub.setFont(new Font("Arial", Font.PLAIN, 12));
        cardSub.setForeground(new Color(174, 214, 241));
        cardHead.add(cardTitle, BorderLayout.WEST);
        cardHead.add(cardSub,   BorderLayout.EAST);
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(cardHead, gbc);

        JTextField tfCode    = formTextField();
        JTextField tfName    = formTextField();
        JTextField tfCredits = formTextField();
        String[]   depts     = {"CSE", "ECE", "ME", "CE", "EEE"};
        Integer[]  sems      = {1, 2, 3, 4, 5, 6, 7, 8};
        JComboBox<String>  cbD = styledCombo(depts, 0);
        JComboBox<Integer> cbS = styledCombo(sems, 0);

        addFormRow(card, gbc, "Course Code",  tfCode);
        addFormRow(card, gbc, "Course Name",  tfName);
        addFormRow(card, gbc, "Department",   cbD);
        addFormRow(card, gbc, "Semester",     cbS);
        addFormRow(card, gbc, "Credits",      tfCredits);

        JButton btnAdd = makeBtn("  ✚   Add Course", BTN_SUCCESS,
                                  new Dimension(0, 44));
        btnAdd.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.insets = new Insets(22, 30, 20, 30);
        card.add(btnAdd, gbc);

        outer.add(card, new GridBagConstraints());

        btnAdd.addActionListener(e -> {
            String code    = tfCode.getText().trim();
            String name    = tfName.getText().trim();
            String dept    = (String)  cbD.getSelectedItem();
            int    sem     = (Integer) cbS.getSelectedItem();
            String credStr = tfCredits.getText().trim();

            if (code.isEmpty() || name.isEmpty() || credStr.isEmpty()) {
                showMsg("Please fill in all fields.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int credits = Integer.parseInt(credStr);
                Course course = new Course(code, name, dept, sem, credits);
                if (new CourseDAO().addCourse(course)) {
                    showMsg("Course \"" + name + "\" added successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    tfCode.setText("");
                    tfName.setText("");
                    tfCredits.setText("");
                    refreshStats();
                } else {
                    showMsg("Failed to add course. Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                showMsg("Credits must be a valid number.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        return outer;
    }

    // ══════════════════════════════════════════════════════════
    //  PREFERENCES TAB
    // ══════════════════════════════════════════════════════════
    private JPanel buildPreferencesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 8, 8, 8));

        // Filter bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(CARD_BG);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JPanel filterLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterLeft.setOpaque(false);

        JLabel filterTitle = new JLabel("🔍   Filter Preferences:");
        filterTitle.setFont(new Font("Arial", Font.BOLD, 13));
        filterTitle.setForeground(TEXT_PRIMARY);
        filterLeft.add(filterTitle);

        JSeparator vs1 = new JSeparator(SwingConstants.VERTICAL);
        vs1.setPreferredSize(new Dimension(2, 28));
        vs1.setForeground(CARD_BORDER);
        filterLeft.add(vs1);

        JLabel deptLbl = new JLabel("Department:");
        deptLbl.setFont(new Font("Arial", Font.BOLD, 12));
        deptLbl.setForeground(TEXT_SECONDARY);
        filterLeft.add(deptLbl);

        String[] prefDepts = {"All Departments", "CSE", "ECE", "ME", "CE", "EEE"};
        JComboBox<String> cbPrefDept = styledCombo(prefDepts, 160);
        filterLeft.add(cbPrefDept);

        JLabel semLbl = new JLabel("Semester:");
        semLbl.setFont(new Font("Arial", Font.BOLD, 12));
        semLbl.setForeground(TEXT_SECONDARY);
        filterLeft.add(semLbl);

        Integer[] prefSems = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        JComboBox<Integer> cbPrefSem = styledCombo(prefSems, 130);
        cbPrefSem.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                l.setText(((Integer) value == 0)
                    ? "All Semesters" : "Semester " + value);
                return l;
            }
        });
        filterLeft.add(cbPrefSem);

        JButton btnApply   = makeBtn("Apply Filter", BTN_PRIMARY, new Dimension(120, 34));
        JButton btnClear   = makeBtn("Clear",        BTN_WARNING, new Dimension(80,  34));
        JButton btnRefresh = makeBtn("🔄  Refresh",  BTN_SUCCESS, new Dimension(110, 34));
        filterLeft.add(btnApply);
        filterLeft.add(btnClear);
        filterLeft.add(btnRefresh);
        topBar.add(filterLeft, BorderLayout.WEST);

        JPanel rightSide = new JPanel(new GridLayout(2, 1, 0, 4));
        rightSide.setOpaque(false);
        lblPrefCount = new JLabel("Loading...", SwingConstants.RIGHT);
        lblPrefCount.setFont(new Font("Arial", Font.BOLD, 13));
        lblPrefCount.setForeground(TEXT_SECONDARY);
        rightSide.add(lblPrefCount);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        legend.setOpaque(false);
        legend.add(makeLegendDot("Priority 1", PRIORITY_1));
        legend.add(makeLegendDot("Priority 2", PRIORITY_2));
        legend.add(makeLegendDot("Priority 3", PRIORITY_3));
        legend.add(makeLegendDot("Others",     PRIORITY_OTHER));
        rightSide.add(legend);
        topBar.add(rightSide, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // Active filter banner
        JPanel filterBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filterBanner.setBackground(new Color(214, 234, 248));
        filterBanner.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(174, 214, 241), 1),
            BorderFactory.createEmptyBorder(0, 8, 0, 8)
        ));
        filterBanner.setVisible(false);
        JLabel bannerIcon = new JLabel("📌");
        bannerIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        JLabel bannerText = new JLabel("");
        bannerText.setFont(new Font("Arial", Font.BOLD, 12));
        bannerText.setForeground(new Color(21, 67, 96));
        JButton btnClearBanner = makeBtn("✕  Clear Filter", BTN_DANGER,
                                          new Dimension(115, 26));
        btnClearBanner.setFont(new Font("Arial", Font.BOLD, 11));
        filterBanner.add(bannerIcon);
        filterBanner.add(bannerText);
        filterBanner.add(btnClearBanner);

        // Table card
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD_BG);
        tableCard.setBorder(new LineBorder(CARD_BORDER, 1, true));

        JPanel tableHead = new JPanel(new BorderLayout());
        tableHead.setBackground(TBL_HEADER_PUR);
        tableHead.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel tableTitle = new JLabel("Student Course Preferences");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        tableTitle.setForeground(TEXT_WHITE);
        tableHead.add(tableTitle, BorderLayout.WEST);
        JLabel tableHint = new JLabel("Color coded by priority level");
        tableHint.setFont(new Font("Arial", Font.PLAIN, 11));
        tableHint.setForeground(new Color(215, 189, 226));
        tableHead.add(tableHint, BorderLayout.EAST);
        tableCard.add(tableHead, BorderLayout.NORTH);

        String[] cols = {
            "  Roll Number",  "  Student Name",  "  Department",
            "  Semester",     "  Course Code",   "  Course Name",
            "  Priority"
        };
        prefModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        prefTable = new JTable(prefModel);
        prefTable.setRowHeight(36);
        prefTable.setFont(new Font("Arial", Font.PLAIN, 13));
        prefTable.setForeground(TBL_TEXT);
        prefTable.setGridColor(new Color(210, 220, 230));
        prefTable.setShowGrid(true);
        prefTable.setShowHorizontalLines(true);
        prefTable.setShowVerticalLines(true);
        prefTable.setIntercellSpacing(new Dimension(1, 1));
        prefTable.setSelectionBackground(TBL_SELECT_PUR);
        prefTable.setSelectionForeground(new Color(10, 30, 60));
        prefTable.setFocusable(false);

        prefTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                    t, v, sel, foc, row, col);
                l.setFont(new Font("Arial", Font.PLAIN, 13));
                l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                l.setOpaque(true);
                if (sel) {
                    l.setBackground(TBL_SELECT_PUR);
                    l.setForeground(new Color(10, 30, 60));
                } else if (row % 2 == 0) {
                    l.setBackground(Color.WHITE);
                    l.setForeground(TBL_TEXT);
                } else {
                    l.setBackground(TBL_ROW_PUR);
                    l.setForeground(TBL_TEXT);
                }
                return l;
            }
        });

        prefTable.getColumnModel().getColumn(6).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean foc,
                        int row, int col) {
                    JLabel l = (JLabel) super.getTableCellRendererComponent(
                        t, v, sel, foc, row, col);
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                    l.setFont(new Font("Arial", Font.BOLD, 13));
                    l.setOpaque(true);
                    l.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                    if (sel) {
                        l.setBackground(TBL_SELECT_PUR);
                        l.setForeground(new Color(10, 30, 60));
                        l.setText(v == null ? "" : v.toString().trim());
                    } else if (v != null) {
                        try {
                            int p = Integer.parseInt(v.toString().trim());
                            switch (p) {
                                case 1:
                                    l.setBackground(new Color(39, 174, 96));
                                    l.setForeground(Color.WHITE);
                                    l.setText("★  1st");
                                    break;
                                case 2:
                                    l.setBackground(new Color(41, 128, 185));
                                    l.setForeground(Color.WHITE);
                                    l.setText("✦  2nd");
                                    break;
                                case 3:
                                    l.setBackground(new Color(230, 126, 34));
                                    l.setForeground(Color.WHITE);
                                    l.setText("◆  3rd");
                                    break;
                                default:
                                    l.setBackground(new Color(142, 68, 173));
                                    l.setForeground(Color.WHITE);
                                    l.setText("•  " + p + "th");
                                    break;
                            }
                        } catch (NumberFormatException ex) {
                            l.setBackground(Color.WHITE);
                            l.setForeground(TBL_TEXT);
                            l.setText(v.toString());
                        }
                    } else {
                        l.setBackground(Color.WHITE);
                        l.setForeground(TBL_TEXT);
                        l.setText("");
                    }
                    return l;
                }
            }
        );

        prefTable.getTableHeader().setBackground(TBL_HEADER_PUR);
        prefTable.getTableHeader().setForeground(TEXT_WHITE);
        prefTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        prefTable.getTableHeader().setPreferredSize(new Dimension(0, 42));
        prefTable.getTableHeader().setReorderingAllowed(false);
        prefTable.getTableHeader().setDefaultRenderer(
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean foc,
                        int row, int col) {
                    JLabel l = (JLabel) super.getTableCellRendererComponent(
                        t, v, sel, foc, row, col);
                    l.setBackground(TBL_HEADER_PUR);
                    l.setForeground(Color.WHITE);
                    l.setFont(new Font("Arial", Font.BOLD, 13));
                    l.setOpaque(true);
                    l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 0, 1,
                            new Color(160, 90, 190)),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)
                    ));
                    return l;
                }
            }
        );

        prefTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        prefTable.getColumnModel().getColumn(1).setPreferredWidth(170);
        prefTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        prefTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        prefTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        prefTable.getColumnModel().getColumn(5).setPreferredWidth(200);
        prefTable.getColumnModel().getColumn(6).setPreferredWidth(90);

        JScrollPane scroll = new JScrollPane(prefTable);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        tableCard.add(scroll, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 6));
        centerPanel.setBackground(CONTENT_BG);
        centerPanel.add(filterBanner, BorderLayout.NORTH);
        centerPanel.add(tableCard,    BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Button actions
        btnApply.addActionListener(e -> {
            String  dept    = (String)  cbPrefDept.getSelectedItem();
            Integer sem     = (Integer) cbPrefSem.getSelectedItem();
            boolean allDept = "All Departments".equals(dept);
            boolean allSem  = (sem == 0);
            if (allDept && allSem) {
                loadPreferences(null, 0);
                filterBanner.setVisible(false);
            } else {
                String deptText = allDept ? "All Departments" : dept;
                String semText  = allSem  ? "All Semesters"   : "Semester " + sem;
                bannerText.setText(
                    "Active Filter:    Department = " + deptText
                    + "      Semester = " + semText);
                filterBanner.setVisible(true);
                filterBanner.revalidate();
                filterBanner.repaint();
                loadPreferences(allDept ? null : dept, allSem ? 0 : sem);
            }
        });
        btnClear.addActionListener(e -> {
            cbPrefDept.setSelectedIndex(0);
            cbPrefSem.setSelectedIndex(0);
            filterBanner.setVisible(false);
            loadPreferences(null, 0);
        });
        btnClearBanner.addActionListener(e -> {
            cbPrefDept.setSelectedIndex(0);
            cbPrefSem.setSelectedIndex(0);
            filterBanner.setVisible(false);
            loadPreferences(null, 0);
        });
        btnRefresh.addActionListener(e -> {
            String  dept    = (String)  cbPrefDept.getSelectedItem();
            Integer sem     = (Integer) cbPrefSem.getSelectedItem();
            boolean allDept = "All Departments".equals(dept);
            boolean allSem  = (sem == 0);
            loadPreferences(allDept ? null : dept, allSem ? 0 : sem);
        });

        loadPreferences(null, 0);
        return panel;
    }

    private void loadPreferences(String department, int semester) {
        prefModel.setRowCount(0);
        try {
            List<Object[]> allList  = new CourseDAO().getAllPreferences();
            List<Object[]> filtered = new ArrayList<>();
            for (Object[] row : allList) {
                String rowDept = row[2].toString().trim();
                int    rowSem  = Integer.parseInt(row[3].toString().trim());
                boolean deptMatch = (department == null || department.equals(rowDept));
                boolean semMatch  = (semester   == 0    || semester  == rowSem);
                if (deptMatch && semMatch) filtered.add(row);
            }
            for (Object[] row : filtered) {
                prefModel.addRow(new Object[]{
                    "  " + row[0], "  " + row[1], "  " + row[2],
                    "  " + row[3], "  " + row[4], "  " + row[5],
                    row[6]
                });
            }
            if (filtered.isEmpty()) {
                lblPrefCount.setText("  No preferences found");
                lblPrefCount.setForeground(BTN_DANGER);
            } else {
                String fi = "";
                if (department != null) fi += "  " + department;
                if (semester   != 0)   fi += "  Sem " + semester;
                lblPrefCount.setText("  Showing " + filtered.size()
                    + " record(s)" + (fi.isEmpty() ? "" : "  —" + fi));
                lblPrefCount.setForeground(BTN_SUCCESS);
            }
            prefModel.fireTableDataChanged();
            prefTable.repaint();
            refreshStats();
        } catch (Exception e) {
            e.printStackTrace();
            showMsg("Error loading preferences:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPreferences() { loadPreferences(null, 0); }

    // ══════════════════════════════════════════════════════════
    //  ALLOCATION TAB
    // ══════════════════════════════════════════════════════════
    private JPanel buildAllocationTab() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 14));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 8, 8, 8));
        panel.add(buildAllocatedPanel());
        panel.add(buildNotSubmittedPanel());
        return panel;
    }

    // ── Top Half — Students who SUBMITTED preferences ─────────
    private JPanel buildAllocatedPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 0));
        wrapper.setBackground(CONTENT_BG);

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filterBar.setBackground(CARD_BG);
        filterBar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        JLabel ft = new JLabel("✅  Submitted Preferences — Filter:");
        ft.setFont(new Font("Arial", Font.BOLD, 13));
        ft.setForeground(TEXT_PRIMARY);
        filterBar.add(ft);

        JSeparator vs = new JSeparator(SwingConstants.VERTICAL);
        vs.setPreferredSize(new Dimension(2, 28));
        vs.setForeground(CARD_BORDER);
        filterBar.add(vs);

        String[]  ad = {"All Departments", "CSE", "ECE", "ME", "CE", "EEE"};
        Integer[] as = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        JComboBox<String>  cbAD = styledCombo(ad, 155);
        JComboBox<Integer> cbAS = styledCombo(as, 130);
        cbAS.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean s, boolean f) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(l,v,i,s,f);
                lbl.setText(((Integer)v == 0) ? "All Semesters" : "Semester " + v);
                return lbl;
            }
        });

        JLabel dl = new JLabel("Dept:"); dl.setFont(new Font("Arial",Font.BOLD,12));
        dl.setForeground(TEXT_SECONDARY);
        JLabel sl = new JLabel("Sem:");  sl.setFont(new Font("Arial",Font.BOLD,12));
        sl.setForeground(TEXT_SECONDARY);

        JButton btnAF  = makeBtn("Apply", BTN_PRIMARY, new Dimension(90,  32));
        JButton btnAC  = makeBtn("Clear", BTN_WARNING, new Dimension(75,  32));
        JButton btnAR  = makeBtn("🔄",    BTN_SUCCESS, new Dimension(50,  32));

        lblAllocCount = new JLabel("", SwingConstants.RIGHT);
        lblAllocCount.setFont(new Font("Arial", Font.BOLD, 12));
        lblAllocCount.setForeground(BTN_SUCCESS);

        filterBar.add(dl); filterBar.add(cbAD);
        filterBar.add(sl); filterBar.add(cbAS);
        filterBar.add(btnAF); filterBar.add(btnAC); filterBar.add(btnAR);
        filterBar.add(lblAllocCount);
        wrapper.add(filterBar, BorderLayout.NORTH);

        // Table
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD_BG);
        tableCard.setBorder(new LineBorder(CARD_BORDER, 1, true));

        JPanel th = new JPanel(new BorderLayout());
        th.setBackground(TBL_HEADER_GRN);
        th.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        JLabel ttl = new JLabel("✅   Students Who Have Submitted Their Course Preferences");
        ttl.setFont(new Font("Arial", Font.BOLD, 13));
        ttl.setForeground(Color.WHITE);
        th.add(ttl, BorderLayout.WEST);
        JLabel hint = new JLabel("These students have been allocated courses");
        hint.setFont(new Font("Arial", Font.PLAIN, 11));
        hint.setForeground(new Color(212, 239, 223));
        th.add(hint, BorderLayout.EAST);
        tableCard.add(th, BorderLayout.NORTH);

        String[] cols = {
            "  Roll Number", "  Student Name",
            "  Department",  "  Semester",
            "  Courses Selected", "  Submitted On"
        };
        allocatedModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        allocatedTable = buildStyledTable(allocatedModel, TBL_HEADER_GRN,
                                           TBL_ROW_ODD, TBL_ROW_GRN,
                                           TBL_SELECT_GRN);
        allocatedTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        allocatedTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        allocatedTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        allocatedTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        allocatedTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        allocatedTable.getColumnModel().getColumn(5).setPreferredWidth(160);

        JScrollPane sc = new JScrollPane(allocatedTable);
        sc.setBorder(null);
        sc.getViewport().setBackground(Color.WHITE);
        tableCard.add(sc, BorderLayout.CENTER);
        wrapper.add(tableCard, BorderLayout.CENTER);

        loadAllocatedStudents(null, 0);

        btnAF.addActionListener(e -> {
            String  d = (String)  cbAD.getSelectedItem();
            Integer s = (Integer) cbAS.getSelectedItem();
            loadAllocatedStudents(
                "All Departments".equals(d) ? null : d, s);
        });
        btnAC.addActionListener(e -> {
            cbAD.setSelectedIndex(0);
            cbAS.setSelectedIndex(0);
            loadAllocatedStudents(null, 0);
        });
        btnAR.addActionListener(e -> {
            String  d = (String)  cbAD.getSelectedItem();
            Integer s = (Integer) cbAS.getSelectedItem();
            loadAllocatedStudents(
                "All Departments".equals(d) ? null : d, s);
        });

        return wrapper;
    }

    private void loadAllocatedStudents(String dept, int sem) {
        allocatedModel.setRowCount(0);
        String sql =
            "SELECT s.roll_number, s.name, s.department, s.semester, " +
            "COUNT(p.course_id) as course_count, " +
            "MAX(p.submitted_at) as last_submitted " +
            "FROM students s " +
            "INNER JOIN preferences p ON s.roll_number = p.roll_number " +
            (dept != null ? "WHERE s.department = '" + dept + "' " +
                            (sem != 0 ? "AND s.semester = " + sem + " " : "")
                          : sem != 0 ? "WHERE s.semester = " + sem + " " : "") +
            "GROUP BY s.roll_number, s.name, s.department, s.semester " +
            "ORDER BY s.department, s.semester, s.roll_number";
        try (java.sql.Connection conn =
                     com.coursesystem.db.DBConnection.getConnection();
             java.sql.Statement st = conn.createStatement();
             java.sql.ResultSet rs = st.executeQuery(sql)) {
            int count = 0;
            while (rs.next()) {
                String submitted = rs.getString("last_submitted");
                if (submitted != null && submitted.length() > 16)
                    submitted = submitted.substring(0, 16);
                allocatedModel.addRow(new Object[]{
                    "  " + rs.getString("roll_number"),
                    "  " + rs.getString("name"),
                    "  " + rs.getString("department"),
                    "  " + rs.getInt("semester"),
                    "  " + rs.getInt("course_count") + " course(s)",
                    "  " + (submitted != null ? submitted : "—")
                });
                count++;
            }
            lblAllocCount.setText("  " + count + " student(s) submitted");
            lblAllocCount.setForeground(count > 0 ? BTN_SUCCESS : BTN_DANGER);
            allocatedModel.fireTableDataChanged();
        } catch (Exception e) {
            e.printStackTrace();
            showMsg("Error loading allocated students:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Bottom Half — Students who HAVE NOT submitted ─────────
    private JPanel buildNotSubmittedPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 0));
        wrapper.setBackground(CONTENT_BG);

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filterBar.setBackground(CARD_BG);
        filterBar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        JLabel ft = new JLabel("❌  Not Submitted — Filter:");
        ft.setFont(new Font("Arial", Font.BOLD, 13));
        ft.setForeground(TEXT_PRIMARY);
        filterBar.add(ft);

        JSeparator vs = new JSeparator(SwingConstants.VERTICAL);
        vs.setPreferredSize(new Dimension(2, 28));
        vs.setForeground(CARD_BORDER);
        filterBar.add(vs);

        String[]  nd = {"All Departments", "CSE", "ECE", "ME", "CE", "EEE"};
        Integer[] ns = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        JComboBox<String>  cbND = styledCombo(nd, 155);
        JComboBox<Integer> cbNS = styledCombo(ns, 130);
        cbNS.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object v, int i, boolean s, boolean f) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(l,v,i,s,f);
                lbl.setText(((Integer)v == 0) ? "All Semesters" : "Semester " + v);
                return lbl;
            }
        });

        JLabel dl = new JLabel("Dept:"); dl.setFont(new Font("Arial",Font.BOLD,12));
        dl.setForeground(TEXT_SECONDARY);
        JLabel sl = new JLabel("Sem:");  sl.setFont(new Font("Arial",Font.BOLD,12));
        sl.setForeground(TEXT_SECONDARY);

        JButton btnNF = makeBtn("Apply", BTN_PRIMARY, new Dimension(90,  32));
        JButton btnNC = makeBtn("Clear", BTN_WARNING, new Dimension(75,  32));
        JButton btnNR = makeBtn("🔄",    BTN_SUCCESS, new Dimension(50,  32));

        lblNotSubCount = new JLabel("", SwingConstants.RIGHT);
        lblNotSubCount.setFont(new Font("Arial", Font.BOLD, 12));
        lblNotSubCount.setForeground(BTN_DANGER);

        filterBar.add(dl); filterBar.add(cbND);
        filterBar.add(sl); filterBar.add(cbNS);
        filterBar.add(btnNF); filterBar.add(btnNC); filterBar.add(btnNR);
        filterBar.add(lblNotSubCount);
        wrapper.add(filterBar, BorderLayout.NORTH);

        // Table
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD_BG);
        tableCard.setBorder(new LineBorder(CARD_BORDER, 1, true));

        JPanel th = new JPanel(new BorderLayout());
        th.setBackground(TBL_HEADER_RED);
        th.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        JLabel ttl = new JLabel("❌   Students Who Have NOT Submitted Their Course Preferences");
        ttl.setFont(new Font("Arial", Font.BOLD, 13));
        ttl.setForeground(Color.WHITE);
        th.add(ttl, BorderLayout.WEST);
        JLabel hint = new JLabel("Pending — no preferences submitted yet");
        hint.setFont(new Font("Arial", Font.PLAIN, 11));
        hint.setForeground(new Color(245, 183, 177));
        th.add(hint, BorderLayout.EAST);
        tableCard.add(th, BorderLayout.NORTH);

        String[] cols = {
            "  Roll Number", "  Student Name",
            "  Email",       "  Department",  "  Semester", "  Status"
        };
        notSubmittedModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        notSubmittedTable = buildStyledTable(notSubmittedModel, TBL_HEADER_RED,
                                              TBL_ROW_ODD, TBL_ROW_RED,
                                              TBL_SELECT_RED);

        // Status column renderer
        notSubmittedTable.getColumnModel().getColumn(5).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean foc,
                        int row, int col) {
                    JLabel l = (JLabel) super.getTableCellRendererComponent(
                        t, v, sel, foc, row, col);
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                    l.setFont(new Font("Arial", Font.BOLD, 12));
                    l.setOpaque(true);
                    if (!sel) {
                        l.setBackground(new Color(253, 228, 225));
                        l.setForeground(new Color(150, 40, 27));
                    }
                    return l;
                }
            }
        );

        notSubmittedTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        notSubmittedTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        notSubmittedTable.getColumnModel().getColumn(2).setPreferredWidth(190);
        notSubmittedTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        notSubmittedTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        notSubmittedTable.getColumnModel().getColumn(5).setPreferredWidth(120);

        JScrollPane sc = new JScrollPane(notSubmittedTable);
        sc.setBorder(null);
        sc.getViewport().setBackground(Color.WHITE);
        tableCard.add(sc, BorderLayout.CENTER);
        wrapper.add(tableCard, BorderLayout.CENTER);

        loadNotSubmittedStudents(null, 0);

        btnNF.addActionListener(e -> {
            String  d = (String)  cbND.getSelectedItem();
            Integer s = (Integer) cbNS.getSelectedItem();
            loadNotSubmittedStudents(
                "All Departments".equals(d) ? null : d, s);
        });
        btnNC.addActionListener(e -> {
            cbND.setSelectedIndex(0);
            cbNS.setSelectedIndex(0);
            loadNotSubmittedStudents(null, 0);
        });
        btnNR.addActionListener(e -> {
            String  d = (String)  cbND.getSelectedItem();
            Integer s = (Integer) cbNS.getSelectedItem();
            loadNotSubmittedStudents(
                "All Departments".equals(d) ? null : d, s);
        });

        return wrapper;
    }

    private void loadNotSubmittedStudents(String dept, int sem) {
        notSubmittedModel.setRowCount(0);
        String sql =
            "SELECT s.roll_number, s.name, s.email, s.department, s.semester " +
            "FROM students s " +
            "WHERE s.roll_number NOT IN " +
            "(SELECT DISTINCT roll_number FROM preferences) " +
            (dept != null ? "AND s.department = '" + dept + "' " +
                            (sem != 0 ? "AND s.semester = " + sem + " " : "")
                          : sem != 0 ? "AND s.semester = " + sem + " " : "") +
            "ORDER BY s.department, s.semester, s.roll_number";
        try (java.sql.Connection conn =
                     com.coursesystem.db.DBConnection.getConnection();
             java.sql.Statement st = conn.createStatement();
             java.sql.ResultSet rs = st.executeQuery(sql)) {
            int count = 0;
            while (rs.next()) {
                notSubmittedModel.addRow(new Object[]{
                    "  " + rs.getString("roll_number"),
                    "  " + rs.getString("name"),
                    "  " + rs.getString("email"),
                    "  " + rs.getString("department"),
                    "  " + rs.getInt("semester"),
                    "⚠ Pending"
                });
                count++;
            }
            lblNotSubCount.setText("  " + count + " student(s) pending");
            lblNotSubCount.setForeground(count > 0 ? BTN_DANGER : BTN_SUCCESS);
            notSubmittedModel.fireTableDataChanged();
        } catch (Exception e) {
            e.printStackTrace();
            showMsg("Error loading pending students:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  SHARED TABLE BUILDER
    // ══════════════════════════════════════════════════════════
    private JTable buildStyledTable(DefaultTableModel model,
                                     Color headerColor,
                                     Color rowOdd,
                                     Color rowEven,
                                     Color selectColor) {
        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setForeground(TBL_TEXT);
        table.setGridColor(new Color(210, 220, 230));
        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(selectColor);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFocusable(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc,
                    int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                    t, v, sel, foc, row, col);
                l.setFont(new Font("Arial", Font.PLAIN, 13));
                l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (sel) {
                    l.setBackground(selectColor);
                    l.setForeground(new Color(10, 30, 60));
                } else if (row % 2 == 0) {
                    l.setBackground(rowOdd);
                    l.setForeground(TBL_TEXT);
                } else {
                    l.setBackground(rowEven);
                    l.setForeground(TBL_TEXT);
                }
                return l;
            }
        });

        table.getTableHeader().setBackground(headerColor);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);
        table.getTableHeader().setDefaultRenderer(
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean foc,
                        int row, int col) {
                    JLabel l = (JLabel) super.getTableCellRendererComponent(
                        t, v, sel, foc, row, col);
                    l.setBackground(headerColor);
                    l.setForeground(Color.WHITE);
                    l.setFont(new Font("Arial", Font.BOLD, 13));
                    l.setOpaque(true);
                    l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 0, 1,
                            headerColor.brighter()),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)
                    ));
                    return l;
                }
            }
        );

        return table;
    }

    // ══════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════
    private JButton makeBtn(String text, Color color, Dimension size) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.darker() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setForeground(TEXT_WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (size != null) btn.setPreferredSize(size);
        return btn;
    }

    private JTextField formTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Arial", Font.PLAIN, 14));
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(Color.WHITE);
        tf.setPreferredSize(new Dimension(0, 40));
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        return tf;
    }

    @SuppressWarnings("unchecked")
    private <T> JComboBox<T> styledCombo(T[] items, int width) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setFont(new Font("Arial", Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setForeground(TEXT_PRIMARY);
        if (width > 0) cb.setPreferredSize(new Dimension(width, 34));
        else           cb.setPreferredSize(new Dimension(0,     40));
        return cb;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc,
                             String labelText, JComponent field) {
        gbc.insets = new Insets(8, 30, 2, 30);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(TEXT_PRIMARY);
        panel.add(lbl, gbc);
        gbc.insets = new Insets(0, 30, 4, 30);
        panel.add(field, gbc);
    }

    private JPanel makeLegendDot(String text, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JLabel dot = new JLabel("●");
        dot.setFont(new Font("Arial", Font.BOLD, 14));
        dot.setForeground(color);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setForeground(TEXT_SECONDARY);
        p.add(dot); p.add(lbl);
        return p;
    }

    private void showMsg(String msg, String title, int type) {
        JOptionPane.showMessageDialog(this, msg, title, type);
    }
}