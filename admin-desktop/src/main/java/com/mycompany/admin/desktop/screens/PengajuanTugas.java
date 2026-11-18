/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.admin.desktop.screens;

import com.mycompany.admin.desktop.api.ApiContext;
import com.mycompany.admin.desktop.dto.CreateBulkTaskRequest;
import com.mycompany.admin.desktop.dto.UpdateTaskRequest;
import com.mycompany.admin.desktop.dto.TaskDto;
import com.mycompany.admin.desktop.dto.UserDto;
import com.mycompany.admin.desktop.dto.JenisTugasDto;
import com.mycompany.admin.desktop.service.JenisTugasApiService;
import com.mycompany.admin.desktop.service.TaskApiService;
import com.mycompany.admin.desktop.service.UserApiService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author argag
 */
public class PengajuanTugas extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PengajuanTugas.class.getName());
    private final JList<UserDto> employeeList = new JList<>();
    private final JComboBox<JenisTugasDto> jenisTugasBox = new JComboBox<>();
    private boolean employeesLoaded;
    private boolean jenisTugasLoaded;
    private final JTextField titleField = new JTextField();
    private final JTextField customerNameField = new JTextField();
    private final JTextField locationField = new JTextField();
    private final JTextField dateField = new JTextField(LocalDate.now().toString());
    private final JTextField mapsField = new JTextField();
    private final JTextArea descriptionField = new JTextArea(4, 24);
    private final TaskDto editingTask;

    /**
     * Creates new form PengajuanTugas
     */
    public PengajuanTugas() {
        this(null);
    }

    public PengajuanTugas(TaskDto editingTask) {
        this.editingTask = editingTask;
        initComponents();
        buildConnectedUi();
        if (editingTask != null) populateForEdit(editingTask);
        loadEmployees();
        loadJenisTugas();
    }

    private void buildConnectedUi() {
        setTitle(editingTask == null ? "Buat Tugas" : "Ubah Tugas " + editingTask.id());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel form = new JPanel(new java.awt.GridBagLayout());
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 18, 18, 18));
        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
        c.insets = new java.awt.Insets(6, 6, 6, 6);
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        employeeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        employeeList.setVisibleRowCount(4);
        addField(form, c, 0, "Employee penerima* (pilih satu atau beberapa)", new JScrollPane(employeeList));
        addField(form, c, 1, "Jenis tugas*", jenisTugasBox);
        addField(form, c, 2, "Nama pelanggan*", customerNameField);
        addField(form, c, 3, "Judul tugas*", titleField);
        addField(form, c, 4, "Lokasi*", locationField);
        addField(form, c, 5, "Tanggal (YYYY-MM-DD)*", dateField);
        addField(form, c, 6, "Link Google Maps", mapsField);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        addField(form, c, 7, "Deskripsi", new JScrollPane(descriptionField));
        jButton1.setText(editingTask == null ? "Buat tugas" : "Simpan perubahan");
        c.gridx = 1; c.gridy = 8; c.weightx = 1;
        form.add(jButton1, c);
        setContentPane(form);
        setSize(650, 520);
        jButton1.addActionListener(evt -> submitTask());
        employeeList.setCellRenderer((list, user, index, selected, focus) ->
                new javax.swing.JLabel(user == null ? "Memuat employee..."
                        : user.namaLengkap() + " (" + user.username() + ")"
                                + ("ACTIVE".equals(user.status()) ? "" : " — nonaktif, dipertahankan")));
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

    private void addField(JPanel panel, java.awt.GridBagConstraints c, int row, String label,
            java.awt.Component component) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        panel.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1;
        panel.add(component, c);
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
        pnhkembali = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));

        pnhkembali.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Gambar/pnhkembali.png"))); // NOI18N
        pnhkembali.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnhkembaliMouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setText("Pengajuan Tugas");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Nama Client*");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Alamat Lokasi*");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Judul Tugas*");

        jLabel6.setText("Tanggal Tugas*");

        jLabel7.setText("Link Gmeet*");

        jLabel8.setText("Deskripsi / Keterangan*");

        jTextField5.setText("jTextField5");

        jTextField6.setText("jTextField6");

        jButton1.setBackground(new java.awt.Color(51, 153, 255));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Kirim");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("tanggal Tugas");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Link Gmeet");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("Deskripsi / Keterangan");

        jTextField7.addActionListener(this::jTextField7ActionPerformed);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(pnhkembali)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel5)
                        .addComponent(jLabel4)
                        .addComponent(jLabel3)
                        .addComponent(jLabel2)
                        .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                        .addComponent(jTextField2)))
                .addGap(128, 128, 128)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel9)
                            .addComponent(jLabel11)
                            .addComponent(jTextField4)
                            .addComponent(jTextField7)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE))
                        .addGap(2610, 2610, 2610)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                .addComponent(jLabel6)
                                .addComponent(jLabel7)
                                .addComponent(jTextField5))
                            .addComponent(jLabel8))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(pnhkembali))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel11)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 69, Short.MAX_VALUE)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(148, 148, 148)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 946, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 19344, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pnhkembaliMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnhkembaliMouseClicked

      
            // TODO add your handling code here:
    }//GEN-LAST:event_pnhkembaliMouseClicked

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

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
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JLabel pnhkembali;
    // End of variables declaration//GEN-END:variables
}
