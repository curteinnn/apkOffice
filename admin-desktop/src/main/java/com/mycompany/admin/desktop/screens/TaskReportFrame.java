package com.mycompany.admin.desktop.screens;

import com.mycompany.admin.desktop.api.ApiContext;
import com.mycompany.admin.desktop.dto.TaskDto;
import com.mycompany.admin.desktop.dto.UserDto;
import com.mycompany.admin.desktop.service.TaskApiService;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/** Shared Swing view for task productivity and approved-assignment amount reports. */
public class TaskReportFrame extends javax.swing.JFrame {
    public enum ReportMode { PRODUCTIVITY, APPROVED_AMOUNT }

    private record EmployeeOption(Long id, String name) {
        @Override public String toString() { return id == null ? name : name + " (ID " + id + ")"; }
    }

    private static final class EmployeeTotals {
        private final UserDto employee;
        private int total;
        private int pending;
        private int inProgress;
        private int submitted;
        private int approved;
        private int rejected;
        private int approvedTasks;
        private int missingNominal;
        private BigDecimal nominal = BigDecimal.ZERO;
        private final List<String> taskIds = new ArrayList<>();

        private EmployeeTotals(UserDto employee) { this.employee = employee; }
    }

    private final ReportMode mode;
    private final DefaultTableModel tableModel = new DefaultTableModel() {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private List<TaskDto> tasks = List.of();

    public TaskReportFrame(ReportMode mode) {
        this.mode = mode;
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(mode == ReportMode.PRODUCTIVITY
                ? "Rekap Tugas per Karyawan" : "Rekap Nominal Tugas per Karyawan");
        lblTitle.setText(getTitle());
        tableReport.setModel(tableModel);
        tableReport.setAutoCreateRowSorter(true);
        tableReport.setFillsViewportHeight(true);
        tableReport.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableModel.setColumnIdentifiers(mode == ReportMode.PRODUCTIVITY
                ? new String[]{"Karyawan", "Total Penugasan", "Pending", "In Progress", "Submitted", "Approved", "Rejected", "ID Tugas Selesai/Dikirim"}
                : new String[]{"Karyawan", "Tugas Approved", "Nominal Tidak Tersedia", "Total Nominal Tugas", "ID Tugas Approved"});
        int[] columnWidths = mode == ReportMode.PRODUCTIVITY
                ? new int[]{170, 120, 80, 100, 100, 90, 85, 520}
                : new int[]{170, 120, 180, 190, 520};
        for (int i = 0; i < columnWidths.length; i++) {
            tableReport.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        setLocationRelativeTo(null);
        loadTasks();
    }

    private void loadTasks() {
        btnApply.setEnabled(false);
        btnRefresh.setEnabled(false);
        new SwingWorker<List<TaskDto>, Void>() {
            @Override protected List<TaskDto> doInBackground() throws Exception {
                return new TaskApiService(ApiContext.client()).getAll();
            }

            @Override protected void done() {
                try {
                    tasks = get();
                    populateEmployees();
                    renderReport();
                } catch (Exception ex) {
                    showError(ex);
                } finally {
                    btnApply.setEnabled(true);
                    btnRefresh.setEnabled(true);
                }
            }
        }.execute();
    }

    private void populateEmployees() {
        Long previousId = selectedEmployeeId();
        Map<Long, UserDto> employees = new LinkedHashMap<>();
        for (TaskDto task : tasks) {
            if (task.assignees() == null) continue;
            for (UserDto employee : task.assignees()) {
                if (employee != null) employees.putIfAbsent(employee.id(), employee);
            }
        }
        List<UserDto> sorted = new ArrayList<>(employees.values());
        sorted.sort(Comparator.comparing(UserDto::namaLengkap, String.CASE_INSENSITIVE_ORDER));
        DefaultComboBoxModel<EmployeeOption> comboModel = new DefaultComboBoxModel<>();
        comboModel.addElement(new EmployeeOption(null, "Semua karyawan"));
        for (UserDto employee : sorted) comboModel.addElement(new EmployeeOption(employee.id(), employee.namaLengkap()));
        cmbEmployee.setModel(comboModel);
        if (previousId != null) {
            for (int i = 1; i < comboModel.getSize(); i++) {
                if (previousId.equals(comboModel.getElementAt(i).id())) {
                    cmbEmployee.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private Long selectedEmployeeId() {
        Object selected = cmbEmployee.getSelectedItem();
        return selected instanceof EmployeeOption option ? option.id() : null;
    }

    private void applyFilters() {
        try {
            LocalDate start = parseOptionalDate(txtStartDate.getText());
            LocalDate end = parseOptionalDate(txtEndDate.getText());
            if (start != null && end != null && start.isAfter(end)) {
                JOptionPane.showMessageDialog(this, "Tanggal mulai tidak boleh melewati tanggal akhir.",
                        "Rentang tanggal tidak valid", JOptionPane.WARNING_MESSAGE);
                return;
            }
            renderReport(start, end, selectedEmployeeId());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Gunakan format tanggal yyyy-MM-dd, contoh 2026-11-18.",
                    "Format tanggal tidak valid", JOptionPane.WARNING_MESSAGE);
        }
    }

    private LocalDate parseOptionalDate(String value) {
        String text = value == null ? "" : value.trim();
        return text.isEmpty() ? null : LocalDate.parse(text);
    }

    private void renderReport() {
        renderReport(parseOptionalDate(txtStartDate.getText()), parseOptionalDate(txtEndDate.getText()),
                selectedEmployeeId());
    }

    private void renderReport(LocalDate start, LocalDate end, Long employeeId) {
        List<TaskDto> filtered = tasks.stream().filter(task -> isInRange(task, start, end)).toList();
        Map<Long, EmployeeTotals> totals = new LinkedHashMap<>();
        for (TaskDto task : filtered) {
            if (task.assignees() == null) continue;
            for (UserDto employee : task.assignees()) {
                if (employee == null || (employeeId != null && employee.id() != employeeId.longValue())) continue;
                EmployeeTotals row = totals.computeIfAbsent(employee.id(), ignored -> new EmployeeTotals(employee));
                if (mode == ReportMode.PRODUCTIVITY) {
                    row.total++;
                    switch (task.status() == null ? "" : task.status()) {
                        case "PENDING" -> row.pending++;
                        case "IN_PROGRESS" -> row.inProgress++;
                        case "SUBMITTED" -> { row.submitted++; row.taskIds.add(task.id()); }
                        case "APPROVED" -> { row.approved++; row.taskIds.add(task.id()); }
                        case "REJECTED" -> { row.rejected++; row.taskIds.add(task.id()); }
                        default -> { }
                    }
                } else if ("APPROVED".equals(task.status())) {
                    row.approvedTasks++;
                    row.taskIds.add(task.id());
                    BigDecimal amount = task.nominalByAssignee() == null
                            ? null : task.nominalByAssignee().get(employee.id());
                    if (amount == null) row.missingNominal++;
                    else row.nominal = row.nominal.add(amount);
                }
            }
        }

        List<EmployeeTotals> rows = new ArrayList<>(totals.values());
        rows.sort(Comparator.comparing(row -> row.employee.namaLengkap(), String.CASE_INSENSITIVE_ORDER));
        tableModel.setRowCount(0);
        NumberFormat money = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        money.setMaximumFractionDigits(2);
        int totalAssignments = 0;
        int totalApprovedTasks = 0;
        int missingAmounts = 0;
        BigDecimal totalNominal = BigDecimal.ZERO;
        for (EmployeeTotals row : rows) {
            if (mode == ReportMode.PRODUCTIVITY) {
                tableModel.addRow(new Object[]{row.employee.namaLengkap(), row.total, row.pending,
                        row.inProgress, row.submitted, row.approved, row.rejected,
                        String.join(", ", row.taskIds)});
                totalAssignments += row.total;
            } else {
                tableModel.addRow(new Object[]{row.employee.namaLengkap(), row.approvedTasks,
                        row.missingNominal, money.format(row.nominal), String.join(", ", row.taskIds)});
                totalApprovedTasks += row.approvedTasks;
                missingAmounts += row.missingNominal;
                totalNominal = totalNominal.add(row.nominal);
            }
        }
        if (mode == ReportMode.PRODUCTIVITY) {
            lblSummary.setText("Karyawan: " + rows.size() + " | Total penugasan pada filter: " + totalAssignments);
        } else {
            lblSummary.setText("Karyawan: " + rows.size() + " | Tugas approved: " + totalApprovedTasks
                    + " | Nominal belum tersedia: " + missingAmounts + " | Total: " + money.format(totalNominal));
        }
        if (rows.isEmpty()) lblSummary.setText(lblSummary.getText() + " | Tidak ada data pada filter ini.");
    }

    private boolean isInRange(TaskDto task, LocalDate start, LocalDate end) {
        if (start == null && end == null) return true;
        if (task.tanggal() == null || task.tanggal().isBlank()) return false;
        LocalDate date = LocalDate.parse(task.tanggal());
        return (start == null || !date.isBefore(start)) && (end == null || !date.isAfter(end));
    }

    private void showError(Exception ex) {
        Throwable cause = ex;
        while (cause.getCause() != null) cause = cause.getCause();
        String message = cause.getMessage() == null ? ex.getMessage() : cause.getMessage();
        JOptionPane.showMessageDialog(this, message == null ? "Gagal memuat data report." : message,
                "Report Tugas", JOptionPane.ERROR_MESSAGE);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitle = new javax.swing.JLabel();
        lblStartDate = new javax.swing.JLabel();
        txtStartDate = new javax.swing.JTextField();
        lblEndDate = new javax.swing.JLabel();
        txtEndDate = new javax.swing.JTextField();
        lblEmployee = new javax.swing.JLabel();
        cmbEmployee = new javax.swing.JComboBox<>();
        btnApply = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        lblSummary = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        tableReport = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 22));
        lblTitle.setText("Report Tugas");
        getContentPane().add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 16, -1, 32));
        lblStartDate.setText("Dari tanggal");
        getContentPane().add(lblStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 62, 82, 28));
        txtStartDate.setToolTipText("Kosongkan untuk tanpa batas awal; format yyyy-MM-dd");
        getContentPane().add(txtStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(108, 62, 122, 28));
        lblEndDate.setText("Sampai");
        getContentPane().add(lblEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(244, 62, 55, 28));
        txtEndDate.setToolTipText("Kosongkan untuk tanpa batas akhir; format yyyy-MM-dd");
        getContentPane().add(txtEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 62, 122, 28));
        lblEmployee.setText("Karyawan");
        getContentPane().add(lblEmployee, new org.netbeans.lib.awtextra.AbsoluteConstraints(438, 62, 65, 28));
        getContentPane().add(cmbEmployee, new org.netbeans.lib.awtextra.AbsoluteConstraints(506, 62, 190, 28));
        btnApply.setText("Terapkan");
        btnApply.addActionListener(this::btnApplyActionPerformed);
        getContentPane().add(btnApply, new org.netbeans.lib.awtextra.AbsoluteConstraints(708, 62, 90, 28));
        btnRefresh.setText("Muat Ulang");
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);
        getContentPane().add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(808, 62, 100, 28));
        lblSummary.setText("Memuat data...");
        getContentPane().add(lblSummary, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 105, 880, 28));
        scrollPane.setViewportView(tableReport);
        getContentPane().add(scrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 140, 884, 370));
        setSize(new java.awt.Dimension(950, 570));
    }
    // </editor-fold>//GEN-END:initComponents

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) { applyFilters(); }
    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) { loadTasks(); }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JComboBox<EmployeeOption> cmbEmployee;
    private javax.swing.JLabel lblEmployee;
    private javax.swing.JLabel lblEndDate;
    private javax.swing.JLabel lblStartDate;
    private javax.swing.JLabel lblSummary;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tableReport;
    private javax.swing.JTextField txtEndDate;
    private javax.swing.JTextField txtStartDate;
    // End of variables declaration//GEN-END:variables
}
