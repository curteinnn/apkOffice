package com.mycompany.admin.desktop.screens;

import com.mycompany.admin.desktop.api.ApiContext;
import com.mycompany.admin.desktop.auth.AuthSession;
import com.mycompany.admin.desktop.dto.AttendanceDto;
import com.mycompany.admin.desktop.dto.TaskDto;
import com.mycompany.admin.desktop.dto.UserDto;
import com.mycompany.admin.desktop.service.AttendanceApiService;
import com.mycompany.admin.desktop.service.TaskApiService;
import com.mycompany.admin.desktop.service.UserApiService;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/** Dashboard view designed in Dashboard.form; this class supplies navigation and live API data. */
public class Dashboard extends javax.swing.JFrame {
    private JPanel contentCards;
    private final Map<String, JPanel> pages = new HashMap<>();
    private ListTugas managementTasks;
    private JScrollPane taskManagementScroll;

    public Dashboard() {
        initComponents();
        setTitle("MyIn | Dashboard");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        configureWorkspace();
        setLocationRelativeTo(null);
        configureNavigation();
        configureDashboardDataView();
        loadDashboardData();
    }

    private void configureNavigation() {
        btnHome.addActionListener(evt -> showPage("dashboard", () -> mainPanel));
        btnEmployees.addActionListener(evt -> showFramePage("master-employees", TambahKaryawan::new));
        btnJabatan.addActionListener(evt -> showFramePage("master-jabatan", MasterJabatan::new));
        btnJenisTugas.addActionListener(evt -> showFramePage("master-jenis-tugas", MasterJenisTugas::new));
        btnAttendance.addActionListener(evt -> showFramePage("attendance", AbsensiHariIn::new));
        btnTaskManagement.setText("Kelola Tugas");
        btnTaskManagement.addActionListener(evt -> showPage("task-management", this::createTaskManagementPage));
        btnTaskVerification.addActionListener(evt -> showPage("task-verification", this::createTaskVerificationPage));
        btnTaskList.addActionListener(evt -> showPage("report-task-list", this::createTaskReportPage));
        btnAttendanceReport.addActionListener(evt -> showFramePage("report-attendance", RekapAbsensi::new));
        btnTaskReport.addActionListener(evt -> showFramePage("report-productivity",
                () -> new TaskReportFrame(TaskReportFrame.ReportMode.PRODUCTIVITY)));
        btnNominalReport.addActionListener(evt -> showFramePage("report-nominal",
                () -> new TaskReportFrame(TaskReportFrame.ReportMode.APPROVED_AMOUNT)));
        btnChangePassword.addActionListener(evt -> new ChangePasswordDialog(this, true).setVisible(true));
        btnLogout.addActionListener(evt -> {
            AuthSession.clear();
            new loginadmin().setVisible(true);
            dispose();
        });
        var current = AuthSession.get();
        lblAccount.setText(current == null ? "Administrator" : current.namaLengkap + "  ·  Administrator");
        lblWelcome.setText("Selamat datang, " + (current == null ? "Admin" : current.namaLengkap)
                + " · Ringkasan operasional MyIn");
        lblDate.setText(LocalDate.now(ZoneId.of("Asia/Jakarta")).toString());
    }

    private void configureWorkspace() {
        Dimension dashboardSize = mainPanel.getSize();
        if (dashboardSize.width > 0 && dashboardSize.height > 0) mainPanel.setPreferredSize(dashboardSize);
        com.mycompany.admin.desktop.ui.ResponsiveUi.installPanel(mainPanel);

        rootPanel.removeAll();
        rootPanel.setLayout(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(225, 720));
        JScrollPane sidebar = new JScrollPane(sidePanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sidebar.setPreferredSize(new Dimension(225, 720));
        sidebar.setBorder(null);
        rootPanel.add(sidebar, BorderLayout.WEST);

        contentCards = new JPanel(new CardLayout());
        contentCards.add(mainPanel, "dashboard");
        pages.put("dashboard", mainPanel);
        rootPanel.add(contentCards, BorderLayout.CENTER);

        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(rootPanel, BorderLayout.CENTER);
        setMinimumSize(new Dimension(900, 600));
    }

    private void configureDashboardDataView() {
        jTable1.setModel(new DefaultTableModel(new Object[0][0],
                new String[]{"ID Tugas", "Judul Tugas", "Jenis Tugas", "Karyawan", "Tanggal", "Lokasi", "Status"}) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        jTable1.setAutoCreateRowSorter(true);

    }

    private void showFramePage(String key, Supplier<? extends javax.swing.JFrame> factory) {
        showPage(key, () -> takeFrameContent(factory.get()));
    }

    private void showPage(String key, Supplier<JPanel> factory) {
        JPanel page = pages.get(key);
        if (page == null) {
            page = factory.get();
            pages.put(key, page);
            contentCards.add(page, key);
        }
        ((CardLayout) contentCards.getLayout()).show(contentCards, key);
        contentCards.revalidate();
        contentCards.repaint();
    }

    private JPanel takeFrameContent(javax.swing.JFrame frame) {
        java.awt.Container content = frame.getContentPane();
        Dimension preferred = content.getSize();
        if (preferred.width <= 0 || preferred.height <= 0) preferred = frame.getSize();
        frame.setContentPane(new JPanel());
        frame.dispose();
        JPanel panel;
        if (content instanceof JPanel contentPanel) {
            panel = contentPanel;
        } else {
            panel = new JPanel(new BorderLayout());
            panel.add(content, BorderLayout.CENTER);
        }
        panel.setPreferredSize(preferred);
        com.mycompany.admin.desktop.ui.ResponsiveUi.installPanel(panel);
        return panel;
    }

    private JPanel createTaskManagementPage() {
        managementTasks = new ListTugas(ListTugas.PageMode.MANAGEMENT);
        managementTasks.setEditHandler(this::showTaskEditor);
        JPanel taskTable = takeFrameContent(managementTasks);

        JPanel editorHost = new JPanel(new BorderLayout());
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton edit = new JButton("Ubah Tugas Terpilih");
        JButton delete = new JButton("Delete Tugas Terpilih");
        JButton refresh = new JButton("Muat Ulang");
        edit.addActionListener(evt -> managementTasks.editSelected());
        delete.addActionListener(evt -> managementTasks.deleteSelected());
        refresh.addActionListener(evt -> managementTasks.loadTasks());
        toolbar.add(edit);
        toolbar.add(delete);
        toolbar.add(refresh);

        JPanel taskSection = new JPanel(new BorderLayout());
        taskSection.add(toolbar, BorderLayout.NORTH);
        taskSection.add(taskTable, BorderLayout.CENTER);
        Dimension taskSectionSize = taskSection.getPreferredSize();
        taskSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, taskSectionSize.height));

        ScrollableColumn column = new ScrollableColumn();
        column.add(editorHost);
        column.add(taskSection);
        installTaskEditor(editorHost, null);
        taskManagementScroll = new JScrollPane(column,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel page = new JPanel(new BorderLayout());
        page.add(taskManagementScroll, BorderLayout.CENTER);
        return page;
    }

    private void installTaskEditor(JPanel editorHost, TaskDto task) {
        PengajuanTugas editor = new PengajuanTugas(task, () -> {
            managementTasks.loadTasks();
            installTaskEditor(editorHost, null);
        });
        JPanel editorPanel = takeFrameContent(editor);
        editorHost.removeAll();
        editorHost.add(editorPanel, BorderLayout.CENTER);
        editorHost.setPreferredSize(editorPanel.getPreferredSize());
        editorHost.setMaximumSize(new Dimension(Integer.MAX_VALUE, editorPanel.getPreferredSize().height));
        editorHost.revalidate();
        editorHost.repaint();
    }

    private void showTaskEditor(TaskDto task) {
        if (taskManagementScroll == null) return;
        ScrollableColumn column = (ScrollableColumn) taskManagementScroll.getViewport().getView();
        JPanel editorHost = (JPanel) column.getComponent(0);
        installTaskEditor(editorHost, task);
        taskManagementScroll.getViewport().setViewPosition(new java.awt.Point(0, 0));
    }

    private JPanel createTaskVerificationPage() {
        ListTugas verification = new ListTugas(ListTugas.PageMode.VERIFICATION);
        JPanel table = takeFrameContent(verification);
        JPanel page = new JPanel(new BorderLayout(0, 8));
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton verify = new JButton("Verifikasi Tugas Terpilih");
        JButton refresh = new JButton("Muat Ulang");
        verify.addActionListener(evt -> verification.verifySelected());
        refresh.addActionListener(evt -> verification.loadTasks());
        toolbar.add(verify);
        toolbar.add(refresh);
        page.add(toolbar, BorderLayout.NORTH);
        page.add(table, BorderLayout.CENTER);
        return page;
    }

    private JPanel createTaskReportPage() {
        ListTugas report = new ListTugas(ListTugas.PageMode.REPORT);
        JPanel table = takeFrameContent(report);
        JPanel page = new JPanel(new BorderLayout(0, 8));
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton refresh = new JButton("Muat Ulang");
        refresh.addActionListener(evt -> report.loadTasks());
        toolbar.add(refresh);
        page.add(toolbar, BorderLayout.NORTH);
        page.add(table, BorderLayout.CENTER);
        return page;
    }

    private static final class ScrollableColumn extends JPanel implements javax.swing.Scrollable {
        ScrollableColumn() {
            setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
        }
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) { return 32; }
        @Override public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
            return Math.max(32, visible.height - 32);
        }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }

    private void loadDashboardData() {
        lblEmployeeCount.setText("…");
        lblPresentCount.setText("…");
        lblActiveTaskCount.setText("…");
        lblApprovedCount.setText("…");
        new SwingWorker<Object[], Void>() {
            @Override protected Object[] doInBackground() throws Exception {
                var api = ApiContext.client();
                List<UserDto> users = new UserApiService(api).getUsers();
                List<AttendanceDto> attendance = new AttendanceApiService(api).getAll();
                List<TaskDto> tasks = new TaskApiService(api).getAll();
                return new Object[]{users, attendance, tasks};
            }
            @Override protected void done() {
                try {
                    Object[] result = get();
                    @SuppressWarnings("unchecked") List<UserDto> users = (List<UserDto>) result[0];
                    @SuppressWarnings("unchecked") List<AttendanceDto> attendance = (List<AttendanceDto>) result[1];
                    @SuppressWarnings("unchecked") List<TaskDto> tasks = (List<TaskDto>) result[2];
                    LocalDate today = LocalDate.now(ZoneId.of("Asia/Jakarta"));
                    lblEmployeeCount.setText(Long.toString(users.stream()
                            .filter(user -> "EMPLOYEE".equals(user.role())).count()));
                    lblPresentCount.setText(Long.toString(attendance.stream()
                            .filter(row -> today.toString().equals(row.tanggal()) && "PRESENT".equals(row.status())).count()));
                    lblActiveTaskCount.setText(Long.toString(tasks.stream()
                            .filter(task -> Set.of("PENDING", "IN_PROGRESS", "SUBMITTED", "REJECTED")
                                    .contains(task.status())).count()));
                    lblApprovedCount.setText(Long.toString(tasks.stream()
                            .filter(task -> "APPROVED".equals(task.status())).count()));
                    tasks.sort((left, right) -> String.valueOf(right.createdAt())
                            .compareTo(String.valueOf(left.createdAt())));
                    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                    model.setRowCount(0);
                    for (TaskDto task : tasks.stream().limit(8).toList()) {
                        String assignees = task.assignees() == null || task.assignees().isEmpty() ? "-"
                                : task.assignees().stream().map(UserDto::namaLengkap)
                                        .collect(java.util.stream.Collectors.joining(", "));
                        model.addRow(new Object[]{task.id(), task.judul(),
                                task.jenisTugas() == null ? "-" : task.jenisTugas().nama(),
                                assignees, task.tanggal(), task.lokasi(), task.status()});
                    }
                } catch (Exception ex) {
                    lblEmployeeCount.setText("-");
                    lblPresentCount.setText("-");
                    lblActiveTaskCount.setText("-");
                    lblApprovedCount.setText("-");
                    JOptionPane.showMessageDialog(Dashboard.this, ex.getMessage(),
                            "Gagal memuat dashboard", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rootPanel = new javax.swing.JPanel();
        sidePanel = new javax.swing.JPanel();
        lblBrand = new javax.swing.JLabel();
        lblOverviewGroup = new javax.swing.JLabel();
        btnHome = new javax.swing.JButton();
        lblMasterGroup = new javax.swing.JLabel();
        btnEmployees = new javax.swing.JButton();
        btnJabatan = new javax.swing.JButton();
        btnJenisTugas = new javax.swing.JButton();
        lblTransactionGroup = new javax.swing.JLabel();
        btnAttendance = new javax.swing.JButton();
        btnTaskManagement = new javax.swing.JButton();
        btnTaskVerification = new javax.swing.JButton();
        lblReportGroup = new javax.swing.JLabel();
        btnTaskList = new javax.swing.JButton();
        btnAttendanceReport = new javax.swing.JButton();
        btnTaskReport = new javax.swing.JButton();
        btnNominalReport = new javax.swing.JButton();
        lblAccount = new javax.swing.JLabel();
        btnChangePassword = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        lblPageTitle = new javax.swing.JLabel();
        lblWelcome = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        cardEmployees = new javax.swing.JPanel();
        lblEmployeeTitle = new javax.swing.JLabel();
        lblEmployeeCount = new javax.swing.JLabel();
        cardPresent = new javax.swing.JPanel();
        lblPresentTitle = new javax.swing.JLabel();
        lblPresentCount = new javax.swing.JLabel();
        cardActiveTasks = new javax.swing.JPanel();
        lblActiveTitle = new javax.swing.JLabel();
        lblActiveTaskCount = new javax.swing.JLabel();
        cardApproved = new javax.swing.JPanel();
        lblApprovedTitle = new javax.swing.JLabel();
        lblApprovedCount = new javax.swing.JLabel();
        lblRecentTitle = new javax.swing.JLabel();
        scrollRecent = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1440, 1024));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        rootPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sidePanel.setBackground(new java.awt.Color(255, 255, 255));
        sidePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBrand.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblBrand.setText("  MyIn");
        sidePanel.add(lblBrand, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 190, 34));

        lblOverviewGroup.setText("OVERVIEW");
        sidePanel.add(lblOverviewGroup, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 57, 190, 16));

        btnHome.setText("Dashboard");
        sidePanel.add(btnHome, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 76, 198, 36));

        lblMasterGroup.setText("MASTER DATA");
        sidePanel.add(lblMasterGroup, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 124, 190, 18));

        btnEmployees.setBackground(new java.awt.Color(51, 153, 255));
        btnEmployees.setText("Karyawan");
        sidePanel.add(btnEmployees, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 145, 198, 34));

        btnJabatan.setBackground(new java.awt.Color(102, 153, 255));
        btnJabatan.setText("Jabatan");
        sidePanel.add(btnJabatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 181, 198, 34));

        btnJenisTugas.setText("Jenis Tugas");
        btnJenisTugas.addActionListener(this::btnJenisTugasActionPerformed);
        sidePanel.add(btnJenisTugas, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 217, 198, 34));

        lblTransactionGroup.setText("TRANSAKSI");
        sidePanel.add(lblTransactionGroup, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 264, 190, 18));

        btnAttendance.setText("Absensi Hari Ini");
        sidePanel.add(btnAttendance, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 285, 198, 34));

        btnTaskManagement.setText("Kelola Tugas");
        sidePanel.add(btnTaskManagement, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 321, 198, 34));

        btnTaskVerification.setText("Verifikasi Tugas");
        sidePanel.add(btnTaskVerification, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 357, 198, 34));

        lblReportGroup.setText("REPORT");
        sidePanel.add(lblReportGroup, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 402, 190, 18));

        btnTaskList.setText("Daftar Tugas");
        sidePanel.add(btnTaskList, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 423, 198, 34));

        btnAttendanceReport.setText("Rekap Absensi");
        sidePanel.add(btnAttendanceReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 459, 198, 34));

        btnTaskReport.setText("Rekap Tugas");
        sidePanel.add(btnTaskReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 495, 198, 34));

        btnNominalReport.setText("Rekap Nominal Tugas");
        sidePanel.add(btnNominalReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 531, 198, 34));

        lblAccount.setText("Administrator");
        sidePanel.add(lblAccount, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 580, 190, 24));

        btnChangePassword.setText("Ubah Password");
        sidePanel.add(btnChangePassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 610, 198, 34));

        btnLogout.setText("Logout");
        btnLogout.addActionListener(this::btnLogoutActionPerformed);
        sidePanel.add(btnLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 650, 198, 34));

        rootPanel.add(sidePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 225, 720));

        mainPanel.setBackground(new java.awt.Color(51, 153, 255));
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblPageTitle.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblPageTitle.setText("Dashboard");
        mainPanel.add(lblPageTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 20, 350, 36));
        mainPanel.add(lblWelcome, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 57, 600, 24));

        lblDate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        mainPanel.add(lblDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 24, 230, 26));

        cardEmployees.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblEmployeeTitle.setText("Karyawan");
        cardEmployees.add(lblEmployeeTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 14, 195, 24));

        lblEmployeeCount.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblEmployeeCount.setText("-");
        cardEmployees.add(lblEmployeeCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 195, 48));

        mainPanel.add(cardEmployees, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 98, 230, 112));

        cardPresent.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblPresentTitle.setText("Hadir Hari Ini");
        cardPresent.add(lblPresentTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 14, 195, 24));

        lblPresentCount.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblPresentCount.setText("-");
        cardPresent.add(lblPresentCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 195, 48));

        mainPanel.add(cardPresent, new org.netbeans.lib.awtextra.AbsoluteConstraints(274, 98, 230, 112));

        cardActiveTasks.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblActiveTitle.setText("Tugas Aktif");
        cardActiveTasks.add(lblActiveTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 14, 195, 24));

        lblActiveTaskCount.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblActiveTaskCount.setText("-");
        cardActiveTasks.add(lblActiveTaskCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 195, 48));

        mainPanel.add(cardActiveTasks, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 98, 230, 112));

        cardApproved.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblApprovedTitle.setText("Tugas Disetujui");
        cardApproved.add(lblApprovedTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 14, 195, 24));

        lblApprovedCount.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblApprovedCount.setText("-");
        cardApproved.add(lblApprovedCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 195, 48));

        mainPanel.add(cardApproved, new org.netbeans.lib.awtextra.AbsoluteConstraints(766, 98, 230, 112));

        lblRecentTitle.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        lblRecentTitle.setText("Tugas Terbaru");
        mainPanel.add(lblRecentTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 234, 300, 30));

        scrollRecent.setViewportView(jTable1);

        mainPanel.add(scrollRecent, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 270, 968, 390));

        rootPanel.add(mainPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 0, 1035, 770));

        getContentPane().add(rootPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1260, 770));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnJenisTugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJenisTugasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnJenisTugasActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAttendance;
    private javax.swing.JButton btnAttendanceReport;
    private javax.swing.JButton btnChangePassword;
    private javax.swing.JButton btnEmployees;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnJabatan;
    private javax.swing.JButton btnJenisTugas;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNominalReport;
    private javax.swing.JButton btnTaskList;
    private javax.swing.JButton btnTaskManagement;
    private javax.swing.JButton btnTaskReport;
    private javax.swing.JButton btnTaskVerification;
    private javax.swing.JPanel cardActiveTasks;
    private javax.swing.JPanel cardApproved;
    private javax.swing.JPanel cardEmployees;
    private javax.swing.JPanel cardPresent;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblAccount;
    private javax.swing.JLabel lblActiveTaskCount;
    private javax.swing.JLabel lblActiveTitle;
    private javax.swing.JLabel lblApprovedCount;
    private javax.swing.JLabel lblApprovedTitle;
    private javax.swing.JLabel lblBrand;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblEmployeeCount;
    private javax.swing.JLabel lblEmployeeTitle;
    private javax.swing.JLabel lblMasterGroup;
    private javax.swing.JLabel lblOverviewGroup;
    private javax.swing.JLabel lblPageTitle;
    private javax.swing.JLabel lblPresentCount;
    private javax.swing.JLabel lblPresentTitle;
    private javax.swing.JLabel lblRecentTitle;
    private javax.swing.JLabel lblReportGroup;
    private javax.swing.JLabel lblTransactionGroup;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel rootPanel;
    private javax.swing.JScrollPane scrollRecent;
    private javax.swing.JPanel sidePanel;
    // End of variables declaration//GEN-END:variables
}
