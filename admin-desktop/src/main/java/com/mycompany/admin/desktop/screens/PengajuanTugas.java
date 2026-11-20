/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.admin.desktop.screens;

import com.mycompany.admin.desktop.api.ApiContext;
import com.mycompany.admin.desktop.dto.CreateBulkTaskRequest;
import com.mycompany.admin.desktop.dto.JenisTugasDto;
import com.mycompany.admin.desktop.dto.TaskDto;
import com.mycompany.admin.desktop.dto.UpdateTaskRequest;
import com.mycompany.admin.desktop.dto.UserDto;
import com.mycompany.admin.desktop.service.JenisTugasApiService;
import com.mycompany.admin.desktop.service.TaskApiService;
import com.mycompany.admin.desktop.service.UserApiService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author argag
 */
public class PengajuanTugas extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PengajuanTugas.class.getName());
    private JList<UserDto> employeeList;
    private JComboBox<JenisTugasDto> jenisTugasBox;
    private boolean employeesLoaded;
    private boolean jenisTugasLoaded;
    private JTextField titleField;
    private JTextField customerNameField;
    private JTextField locationField;
    private JTextField dateField;
    private JTextField mapsField;
    private JTextArea descriptionField;
    private final TaskDto editingTask;
    private final Runnable onSaved;

    /**
     * Creates new form PengajuanTugas
     */
    public PengajuanTugas() {
        this(null, null);
    }

    public PengajuanTugas(TaskDto editingTask) {
        this(editingTask, null);
    }

    public PengajuanTugas(TaskDto editingTask, Runnable onSaved) {
        this.editingTask = editingTask;
        this.onSaved = onSaved;
        initComponents();
        com.mycompany.admin.desktop.ui.ResponsiveUi.install(this);
        configureFormActions();
        if (editingTask != null) populateForEdit(editingTask);
        loadEmployees();
        loadJenisTugas();
    }

    private void configureFormActions() {
        setTitle(editingTask == null ? "Buat Tugas" : "Ubah Tugas " + editingTask.id());
        lblTitle.setText(editingTask == null ? "Kelola Tugas" : "Ubah Tugas");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        titleField = txtTitle;
        customerNameField = txtCustomer;
        locationField = txtLocation;
        dateField = txtDate;
        mapsField = txtMaps;
        descriptionField = txtDescription;
        employeeList = (JList<UserDto>) (JList<?>) listEmployees;
        jenisTugasBox = (JComboBox<JenisTugasDto>) (JComboBox<?>) comboJenisTugas;
        dateField.setText(LocalDate.now().toString());
        employeeList.setSelectionModel(new CheckboxSelectionModel());
        employeeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        employeeList.setVisibleRowCount(4);
        employeeList.setFixedCellHeight(36);
        employeeList.setToolTipText("Klik baris untuk mencentang atau menghapus centang; pilih lebih dari dua karyawan.");
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        jButton1.setText(editingTask == null ? "Buat tugas" : "Simpan perubahan");
        setLocationRelativeTo(null);
        employeeList.setCellRenderer((list, user, index, selected, focus) -> {
            JCheckBox checkbox = new JCheckBox(user == null ? "Memuat employee..."
                    : user.namaLengkap() + " (" + user.username() + ")"
                            + ("ACTIVE".equals(user.status()) ? "" : " — nonaktif, dipertahankan"));
            checkbox.setSelected(selected);
            checkbox.setOpaque(true);
            checkbox.setBackground(selected ? new java.awt.Color(219, 234, 254) : list.getBackground());
            checkbox.setForeground(new java.awt.Color(31, 41, 55));
            checkbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
            return checkbox;
        });
        jenisTugasBox.setRenderer((list, jenis, index, selected, focus) ->
                new javax.swing.JLabel(jenis == null ? "Memuat jenis tugas..." : jenis.nama()));
    }

    private void populateForEdit(TaskDto task) {
        titleField.setText(task.judul() == null ? "" : task.judul());
        customerNameField.setText(task.namaPelanggan() == null ? "" : task.namaPelanggan());
        locationField.setText(task.lokasi() == null ? "" : task.lokasi());
        dateField.setText(task.tanggal() == null ? "" : task.tanggal());
        mapsField.setText(task.linkGmaps() == null ? "" : task.linkGmaps());
        descriptionField.setText(task.deskripsi() == null ? "" : task.deskripsi());
    }

    private void loadEmployees() {
        jButton1.setEnabled(false);
        new SwingWorker<List<UserDto>, Void>() {
            @Override protected List<UserDto> doInBackground() throws Exception {
                List<UserDto> users = new UserApiService(ApiContext.client()).getUsers();
                Set<Long> assignedIds = editingTask == null || editingTask.assignees() == null
                        ? Set.of() : editingTask.assignees().stream().map(UserDto::id).collect(Collectors.toSet());
                return users.stream().filter(user -> "EMPLOYEE".equals(user.role())
                        && ("ACTIVE".equals(user.status()) || assignedIds.contains(user.id()))).toList();
            }
            @Override protected void done() {
                try {
                    List<UserDto> employees = get();
                    employeeList.setListData(employees.toArray(UserDto[]::new));
                    if (editingTask != null && editingTask.assignees() != null) {
                        Set<Long> assignedIds = editingTask.assignees().stream()
                                .map(UserDto::id).collect(Collectors.toSet());
                        for (int i = 0; i < employees.size(); i++) {
                            if (assignedIds.contains(employees.get(i).id())) employeeList.addSelectionInterval(i, i);
                        }
                    }
                    if (employees.isEmpty()) {
                        JOptionPane.showMessageDialog(PengajuanTugas.this,
                                "Belum ada employee aktif sebagai penerima tugas.",
                                "Data employee", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(PengajuanTugas.this, ex.getMessage(),
                            "Gagal memuat employee", JOptionPane.ERROR_MESSAGE);
                } finally {
                    employeesLoaded = employeeList.getModel().getSize() > 0;
                    updateSubmitEnabled();
                }
            }
        }.execute();
    }

    private void loadJenisTugas() {
        jButton1.setEnabled(false);
        new SwingWorker<List<JenisTugasDto>, Void>() {
            @Override protected List<JenisTugasDto> doInBackground() throws Exception {
                return new JenisTugasApiService(ApiContext.client()).getAll();
            }
            @Override protected void done() {
                try {
                    List<JenisTugasDto> rows = get();
                    jenisTugasBox.setModel(new DefaultComboBoxModel<>(rows.toArray(JenisTugasDto[]::new)));
                    jenisTugasLoaded = !rows.isEmpty();
                    if (editingTask != null && editingTask.jenisTugas() != null) {
                        for (int i = 0; i < rows.size(); i++) {
                            if (rows.get(i).id().equals(editingTask.jenisTugas().id())) {
                                jenisTugasBox.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                    if (!jenisTugasLoaded) {
                        JOptionPane.showMessageDialog(PengajuanTugas.this,
                                "Jenis tugas belum tersedia. Terapkan migrasi database terlebih dahulu.",
                                "Data jenis tugas", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(PengajuanTugas.this, ex.getMessage(),
                            "Gagal memuat jenis tugas", JOptionPane.ERROR_MESSAGE);
                } finally {
                    updateSubmitEnabled();
                }
            }
        }.execute();
    }

    private void updateSubmitEnabled() {
        jButton1.setEnabled(employeesLoaded && jenisTugasLoaded);
    }

    private static final class CheckboxSelectionModel extends DefaultListSelectionModel {
        CheckboxSelectionModel() {
            setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }

        @Override
        public void setSelectionInterval(int index0, int index1) {
            if (index0 < 0 || index1 < 0) return;
            if (index0 == index1 && isSelectedIndex(index0)) {
                super.removeSelectionInterval(index0, index1);
            } else {
                super.addSelectionInterval(index0, index1);
            }
        }
    }

    private void submitTask() {
        List<UserDto> employees = employeeList.getSelectedValuesList();
        JenisTugasDto jenisTugas = (JenisTugasDto) jenisTugasBox.getSelectedItem();
        if (employees.isEmpty() || jenisTugas == null || customerNameField.getText().isBlank()
                || titleField.getText().isBlank() || locationField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Pilih minimal satu employee; jenis tugas, nama pelanggan, judul, dan lokasi wajib diisi.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        final String date;
        try {
            date = LocalDate.parse(dateField.getText().trim()).toString();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Tanggal harus menggunakan format YYYY-MM-DD.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        jButton1.setEnabled(false);
        List<Long> employeeIds = employees.stream().map(UserDto::id).toList();
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                TaskApiService service = new TaskApiService(ApiContext.client());
                if (editingTask == null) {
                    CreateBulkTaskRequest request = new CreateBulkTaskRequest(employeeIds, jenisTugas.id(),
                            customerNameField.getText().trim(), titleField.getText().trim(),
                            locationField.getText().trim(), descriptionField.getText().trim(), date,
                            mapsField.getText().trim());
                    service.createBulk(request);
                } else {
                    UpdateTaskRequest request = new UpdateTaskRequest(employeeIds, jenisTugas.id(),
                            customerNameField.getText().trim(), titleField.getText().trim(),
                            locationField.getText().trim(), descriptionField.getText().trim(), date,
                            mapsField.getText().trim());
                    service.update(editingTask.id(), request);
                }
                return null;
            }
            @Override protected void done() {
                jButton1.setEnabled(true);
                try {
                    get();
                    JOptionPane.showMessageDialog(PengajuanTugas.this,
                            editingTask == null
                                    ? "Tugas berhasil dibuat untuk " + employees.size() + " karyawan."
                                    : "Data tugas berhasil diperbarui.");
                    if (onSaved != null) onSaved.run();
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(PengajuanTugas.this, ex.getMessage(),
                            "Gagal membuat tugas", JOptionPane.ERROR_MESSAGE);
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
        lblTitle = new javax.swing.JLabel();
        lblEmployees = new javax.swing.JLabel();
        scrollEmployees = new javax.swing.JScrollPane();
        listEmployees = new javax.swing.JList<>();
        lblJenisTugas = new javax.swing.JLabel();
        comboJenisTugas = new javax.swing.JComboBox<>();
        lblCustomer = new javax.swing.JLabel();
        txtCustomer = new javax.swing.JTextField();
        lblTaskTitle = new javax.swing.JLabel();
        txtTitle = new javax.swing.JTextField();
        lblLocation = new javax.swing.JLabel();
        txtLocation = new javax.swing.JTextField();
        lblDate = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        lblMaps = new javax.swing.JLabel();
        txtMaps = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        scrollDescription = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pengajuan Tugas");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(244, 247, 251));
        jPanel1.setPreferredSize(new java.awt.Dimension(1080, 690));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(31, 41, 55));
        lblTitle.setText("Kelola Tugas");
        jPanel1.add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 22, 500, 36));

        lblEmployees.setText("Karyawan penerima* (centang satu atau lebih)");
        jPanel1.add(lblEmployees, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 76, 410, 24));

        scrollEmployees.setViewportView(listEmployees);

        jPanel1.add(scrollEmployees, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 104, 410, 500));

        lblJenisTugas.setText("Jenis tugas*");
        jPanel1.add(lblJenisTugas, new org.netbeans.lib.awtextra.AbsoluteConstraints(474, 76, 574, 24));
        jPanel1.add(comboJenisTugas, new org.netbeans.lib.awtextra.AbsoluteConstraints(474, 104, 574, 36));

        lblCustomer.setText("Nama pelanggan*");
        jPanel1.add(lblCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(474, 152, 574, 24));
        jPanel1.add(txtCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(474, 180, 574, 36));

        lblTaskTitle.setText("Judul tugas*");
        jPanel1.add(lblTaskTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(474, 228, 574, 24));
        jPanel1.add(txtTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(474, 256, 574, 36));

        lblLocation.setText("Lokasi tugas*");
        jPanel1.add(lblLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(474, 304, 278, 24));
        jPanel1.add(txtLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(474, 332, 278, 36));

        lblDate.setText("Tanggal (YYYY-MM-DD)*");
        jPanel1.add(lblDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 304, 278, 24));
        jPanel1.add(txtDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 332, 278, 36));

        lblMaps.setText("Link Google Maps");
        jPanel1.add(lblMaps, new org.netbeans.lib.awtextra.AbsoluteConstraints(474, 380, 574, 24));
        jPanel1.add(txtMaps, new org.netbeans.lib.awtextra.AbsoluteConstraints(474, 408, 574, 36));

        lblDescription.setText("Deskripsi tugas");
        jPanel1.add(lblDescription, new org.netbeans.lib.awtextra.AbsoluteConstraints(474, 456, 574, 24));

        txtDescription.setColumns(20);
        txtDescription.setRows(4);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        scrollDescription.setViewportView(txtDescription);

        jPanel1.add(scrollDescription, new org.netbeans.lib.awtextra.AbsoluteConstraints(474, 484, 574, 116));

        jButton1.setBackground(new java.awt.Color(37, 99, 235));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Buat tugas");
        jButton1.addActionListener(this::jButton1ActionPerformed);
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(858, 620, 190, 36));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1080, 690));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) { submitTask(); }

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
        java.awt.EventQueue.invokeLater(() -> new PengajuanTugas().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<com.mycompany.admin.desktop.dto.JenisTugasDto> comboJenisTugas;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblCustomer;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblEmployees;
    private javax.swing.JLabel lblJenisTugas;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblMaps;
    private javax.swing.JLabel lblTaskTitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JList<com.mycompany.admin.desktop.dto.UserDto> listEmployees;
    private javax.swing.JScrollPane scrollDescription;
    private javax.swing.JScrollPane scrollEmployees;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtMaps;
    private javax.swing.JTextField txtTitle;
    // End of variables declaration//GEN-END:variables
}
