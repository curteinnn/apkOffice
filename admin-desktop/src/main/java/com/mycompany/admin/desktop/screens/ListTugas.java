/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.admin.desktop.screens;

import com.mycompany.admin.desktop.api.ApiContext;
import com.mycompany.admin.desktop.dto.TaskDto;
import com.mycompany.admin.desktop.dto.VerifyTaskRequest;
import com.mycompany.admin.desktop.service.TaskApiService;
import java.math.BigDecimal;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.util.function.Consumer;

/**
 *
 * @author argag
 */
public class ListTugas extends javax.swing.JFrame {

    public enum PageMode { MANAGEMENT, VERIFICATION, REPORT }

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ListTugas.class.getName());
    private List<TaskDto> loadedTasks = List.of();
    private final PageMode pageMode;
    private Consumer<TaskDto> editHandler;
    private DefaultTableModel taskModel;

    /**
     * Creates new form ListTugas
     */
    public ListTugas() {
        this(PageMode.MANAGEMENT);
    }

    public ListTugas(PageMode pageMode) {
        this.pageMode = pageMode;
        initComponents();
        com.mycompany.admin.desktop.ui.ResponsiveUi.install(this);
        buildTaskActions();
        loadTasks();
    }

    /** Detaches the existing filter field so the dashboard can place it beside page actions. */
    public javax.swing.JTextField detachSearchField() {
        java.awt.Container parent = jTextField1.getParent();
        if (parent != null) {
            parent.remove(jTextField1);
            parent.revalidate();
            parent.repaint();
        }
        return jTextField1;
    }

    public void setEditHandler(Consumer<TaskDto> editHandler) {
        this.editHandler = editHandler;
    }

    private void buildTaskActions() {
        setTitle(switch (pageMode) {
            case MANAGEMENT -> "Kelola Tugas";
            case VERIFICATION -> "Verifikasi Tugas";
            case REPORT -> "Daftar Tugas";
        });
        jLabel1.setText(getTitle());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        String[] columns = pageMode == PageMode.REPORT
                ? new String[]{"ID Tugas", "Employee", "Pelanggan", "Jenis Tugas", "Judul Tugas", "Lokasi", "Tanggal", "Status"}
                : new String[]{"ID Tugas", "Employee", "Pelanggan", "Jenis Tugas", "Judul Tugas", "Lokasi", "Tanggal", "Status", "Nominal"};
        taskModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        jTable1.setModel(taskModel);
        jTable1.setAutoCreateRowSorter(true);

        jTextField1.setText("");
        jTextField1.setToolTipText("Cari employee, tugas, lokasi, atau status");
        jTextField1.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filter() {
                javax.swing.RowFilter<DefaultTableModel, Integer> rowFilter =
                        jTextField1.getText().isBlank() ? null
                        : javax.swing.RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(jTextField1.getText()));
                if (!(jTable1.getRowSorter() instanceof javax.swing.table.TableRowSorter<?>)) {
                    jTable1.setRowSorter(new javax.swing.table.TableRowSorter<>(taskModel));
                }
                @SuppressWarnings("unchecked") javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                        (javax.swing.table.TableRowSorter<DefaultTableModel>) jTable1.getRowSorter();
                sorter.setRowFilter(rowFilter);
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Tugas");
        JMenuItem create = new JMenuItem("Buat tugas");
        create.addActionListener(evt -> {
            PengajuanTugas form = new PengajuanTugas();
            form.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosed(java.awt.event.WindowEvent e) { loadTasks(); }
            });
            form.setLocationRelativeTo(this);
            form.setVisible(true);
        });
        JMenuItem refresh = new JMenuItem("Muat ulang");
        refresh.addActionListener(evt -> loadTasks());
        JMenuItem edit = new JMenuItem("Ubah data tugas terpilih");
        edit.addActionListener(evt -> editSelected());
        JMenuItem delete = new JMenuItem("Hapus tugas terpilih");
        delete.addActionListener(evt -> deleteSelected());
        JMenuItem verify = new JMenuItem("Verifikasi tugas terpilih");
        verify.addActionListener(evt -> verifySelected());
        btnVerify.setVisible(pageMode == PageMode.VERIFICATION);
        btnEdit.setVisible(pageMode == PageMode.REPORT);
        btnDelete.setVisible(pageMode == PageMode.REPORT);
        btnRefresh.setVisible(pageMode != PageMode.MANAGEMENT);
        btnVerify.addActionListener(evt -> verifySelected());
        btnEdit.addActionListener(evt -> editSelected());
        btnDelete.addActionListener(evt -> deleteSelected());
        btnRefresh.addActionListener(evt -> loadTasks());
        if (pageMode == PageMode.MANAGEMENT) {
            menu.add(create); menu.add(edit); menu.add(delete);
        } else if (pageMode == PageMode.VERIFICATION) {
            menu.add(verify);
        }
        if (pageMode != PageMode.REPORT) menu.addSeparator();
        menu.add(refresh);
        bar.add(menu);
        setJMenuBar(bar);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) showSelectedTask();
            }
        });
    }

    public void loadTasks() {
        taskModel.setRowCount(0);
        new SwingWorker<List<TaskDto>, Void>() {
            @Override protected List<TaskDto> doInBackground() throws Exception {
                return new TaskApiService(ApiContext.client()).getAll();
            }
            @Override protected void done() {
                try {
                    loadedTasks = get();
                    for (TaskDto task : loadedTasks) {
                        Object[] row = new Object[]{task.id(), assignedEmployees(task),
                                task.namaPelanggan() == null ? "Belum ditentukan" : task.namaPelanggan(),
                                task.jenisTugas() == null ? "Belum dikategorikan" : task.jenisTugas().nama(),
                                task.judul(), task.lokasi(), task.tanggal(), task.status()};
                        if (pageMode != PageMode.REPORT) {
                            row = java.util.Arrays.copyOf(row, row.length + 1);
                            row[row.length - 1] = nominalSummary(task);
                        }
                        taskModel.addRow(row);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ListTugas.this, ex.getMessage(),
                            "Gagal memuat tugas", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private TaskDto selectedTask() {
        int viewRow = jTable1.getSelectedRow();
        if (viewRow < 0) return null;
        int row = jTable1.convertRowIndexToModel(viewRow);
        String id = String.valueOf(taskModel.getValueAt(row, 0));
        return loadedTasks.stream().filter(task -> task.id().equals(id)).findFirst().orElse(null);
    }

    private String assignedEmployees(TaskDto task) {
        if (task.assignees() != null && !task.assignees().isEmpty()) {
            return task.assignees().stream().map(com.mycompany.admin.desktop.dto.UserDto::namaLengkap)
                    .collect(java.util.stream.Collectors.joining(", "));
        }
        return "-";
    }

    private String nominalSummary(TaskDto task) {
        if (task.nominalByAssignee() == null || task.nominalByAssignee().isEmpty()) return "-";
        Map<Long, String> names = task.assignees() == null ? Map.of() : task.assignees().stream()
                .collect(java.util.stream.Collectors.toMap(
                        com.mycompany.admin.desktop.dto.UserDto::id,
                        com.mycompany.admin.desktop.dto.UserDto::namaLengkap));
        return task.nominalByAssignee().entrySet().stream()
                .map(entry -> names.getOrDefault(entry.getKey(), "Karyawan " + entry.getKey()) + ": "
                        + (entry.getValue() == null ? "belum diisi" : entry.getValue().toPlainString()))
                .collect(java.util.stream.Collectors.joining(", "));
    }

    private void showSelectedTask() {
        TaskDto task = selectedTask();
        if (task == null) return;
        String employee = assignedEmployees(task);
        JOptionPane.showMessageDialog(this,
                "ID Tugas: " + task.id() + "\nEmployee: " + employee
                        + "\nPelanggan: " + (task.namaPelanggan() == null ? "Belum ditentukan" : task.namaPelanggan())
                        + "\nJenis tugas: " + (task.jenisTugas() == null ? "Belum dikategorikan" : task.jenisTugas().nama())
                        + "\nJudul: " + task.judul() + "\nTanggal: " + task.tanggal()
                        + "\nLokasi: " + task.lokasi() + "\nStatus: " + task.status()
                        + (task.alasanPenolakan() == null || task.alasanPenolakan().isBlank()
                                ? "" : "\nAlasan penolakan: " + task.alasanPenolakan())
                        + "\nDeskripsi: " + (task.deskripsi() == null ? "-" : task.deskripsi()),
                "Detail Tugas", JOptionPane.INFORMATION_MESSAGE);
    }

    public void verifySelected() {
        TaskDto task = selectedTask();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Pilih tugas terlebih dahulu.", "Verifikasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!"SUBMITTED".equals(task.status())) {
            JOptionPane.showMessageDialog(this, "Hanya tugas berstatus SUBMITTED yang dapat diverifikasi.",
                    "Verifikasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object[] choices = {"APPROVED", "REJECTED", "Batal"};
        int choice = JOptionPane.showOptionDialog(this, "Pilih hasil verifikasi tugas.", "Verifikasi Tugas",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[2]);
        if (choice < 0 || choice == 2) return;
        Map<Long, BigDecimal> nominalByAssignee = null;
        String alasanPenolakan = null;
        if (choice == 0) {
            nominalByAssignee = new LinkedHashMap<>();
            if (task.assignees() == null || task.assignees().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data karyawan yang ditugaskan tidak tersedia.",
                        "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            for (var employee : task.assignees()) {
                String value = JOptionPane.showInputDialog(this,
                        "Nominal untuk " + employee.namaLengkap() + " (angka >= 0):", "0");
                if (value == null) return;
                try {
                    BigDecimal nominal = new BigDecimal(value.trim());
                    if (nominal.signum() < 0) throw new NumberFormatException();
                    nominalByAssignee.put(employee.id(), nominal);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Nominal harus angka dan tidak boleh negatif.",
                            "Validasi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        } else {
            javax.swing.JTextArea reasonInput = new javax.swing.JTextArea(5, 32);
            reasonInput.setLineWrap(true);
            reasonInput.setWrapStyleWord(true);
            int reasonChoice = JOptionPane.showConfirmDialog(
                    this,
                    new javax.swing.JScrollPane(reasonInput),
                    "Masukkan alasan penolakan",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );
            if (reasonChoice != JOptionPane.OK_OPTION) return;
            alasanPenolakan = reasonInput.getText().trim();
            if (alasanPenolakan.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Alasan penolakan wajib diisi.",
                        "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (alasanPenolakan.length() > 2000) {
                JOptionPane.showMessageDialog(this, "Alasan penolakan maksimal 2000 karakter.",
                        "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        VerifyTaskRequest request = new VerifyTaskRequest(
                choice == 0 ? "APPROVED" : "REJECTED",
                nominalByAssignee,
                alasanPenolakan
        );
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                new TaskApiService(ApiContext.client()).verify(task.id(), request);
                return null;
            }
            @Override protected void done() {
                try { get(); loadTasks(); }
                catch (Exception ex) { JOptionPane.showMessageDialog(ListTugas.this, ex.getMessage(),
                        "Gagal memverifikasi tugas", JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }

    public void editSelected() {
        TaskDto task = selectedTask();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Pilih tugas terlebih dahulu.",
                    "Ubah Tugas", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!"PENDING".equals(task.status()) && !"REJECTED".equals(task.status())) {
            JOptionPane.showMessageDialog(this,
                    "Data hanya dapat diubah saat tugas berstatus PENDING atau REJECTED.",
                    "Ubah Tugas", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if ("REJECTED".equals(task.status())) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Tugas yang ditolak akan kembali ke status PENDING setelah diubah. "
                            + "Atur tanggal penugasan ulang pada form.",
                    "Jadwalkan Ulang Tugas",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );
            if (choice != JOptionPane.OK_OPTION) return;
        }
        if (editHandler != null) {
            editHandler.accept(task);
            return;
        }
        PengajuanTugas form = new PengajuanTugas(task);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) { loadTasks(); }
        });
        form.setLocationRelativeTo(this);
        form.setVisible(true);
    }

    public void deleteSelected() {
        TaskDto task = selectedTask();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Pilih tugas terlebih dahulu.",
                    "Hapus Tugas", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!"PENDING".equals(task.status())) {
            JOptionPane.showMessageDialog(this,
                    "Hanya tugas PENDING yang dapat dihapus agar riwayat proses tetap tersimpan.",
                    "Hapus Tugas", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus tugas " + task.id() + "? Penugasan karyawan yang terkait juga akan dihapus.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                new TaskApiService(ApiContext.client()).delete(task.id());
                return null;
            }
            @Override protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(ListTugas.this, "Tugas berhasil dihapus.",
                            "Hapus Tugas", JOptionPane.INFORMATION_MESSAGE);
                    loadTasks();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ListTugas.this, ex.getMessage(),
                            "Gagal menghapus tugas", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        btnVerify = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(153, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1080, 690));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Daftar Tugas");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 22, 500, 36));

        jTextField1.setText("Cari");
        jPanel1.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 76, 250, 36));

        btnVerify.setText("Verifikasi Tugas Terpilih");
        jPanel1.add(btnVerify, new org.netbeans.lib.awtextra.AbsoluteConstraints(292, 76, 230, 36));

        btnEdit.setText("Ubah Tugas Terpilih");
        jPanel1.add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(532, 76, 160, 36));

        btnDelete.setText("Hapus Tugas Terpilih");
        jPanel1.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(702, 76, 170, 36));

        btnRefresh.setText("Muat Ulang");
        jPanel1.add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(882, 76, 150, 36));

        jTable1.setRowHeight(30);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Tugas", "Employee", "Pelanggan", "Jenis Tugas", "Judul Tugas", "Lokasi", "Tanggal", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 130, 1016, 520));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1080, 690));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new ListTugas().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnVerify;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
