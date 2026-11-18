/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.admin.desktop.screens;

import com.mycompany.admin.desktop.api.ApiContext;
import com.mycompany.admin.desktop.dto.CreateUserRequest;
import com.mycompany.admin.desktop.dto.JabatanDto;
import com.mycompany.admin.desktop.dto.UpdateUserRequest;
import com.mycompany.admin.desktop.dto.UserDto;
import com.mycompany.admin.desktop.service.JabatanApiService;
import com.mycompany.admin.desktop.service.UserApiService;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author argag
 */
public class TambahKaryawan extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TambahKaryawan.class.getName());
    private final JTextField usernameField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JComboBox<JabatanDto> positionField = new JComboBox<>();
    private final JPasswordField passwordField = new JPasswordField();
    private final JButton saveButton = new JButton("Simpan karyawan");
    private final JButton editButton = new JButton("Ubah terpilih");
    private final JButton deleteButton = new JButton("Hapus terpilih");
    private final DefaultTableModel employeeModel = new DefaultTableModel(
            new String[]{"ID", "Username", "Nama", "Email", "Jabatan", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable employeeTable = new JTable(employeeModel);
    private List<UserDto> loadedEmployees = List.of();
    private List<JabatanDto> activeJabatans = List.of();

    /**
     * Creates new form TambahKaryawan
     */
    public TambahKaryawan() {
        initComponents();
        buildConnectedUi();
        loadEmployees();
        loadJabatanOptions();
    }

    private void buildConnectedUi() {
        setTitle("Master Karyawan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel root = new JPanel(new java.awt.BorderLayout(12, 12));
        JPanel form = new JPanel(new java.awt.GridLayout(0, 2, 8, 8));
        form.setBorder(javax.swing.BorderFactory.createTitledBorder("Tambah karyawan"));
        form.add(new JLabel("Username*")); form.add(usernameField);
        form.add(new JLabel("Password awal*")); form.add(passwordField);
        form.add(new JLabel("Nama lengkap*")); form.add(nameField);
        form.add(new JLabel("Email*")); form.add(emailField);
        form.add(new JLabel("Jabatan*")); form.add(positionField);
        form.add(new JLabel("Role")); form.add(new JLabel("EMPLOYEE (tetap)"));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        actions.add(saveButton);
        actions.add(editButton);
        actions.add(deleteButton);
        form.add(new JLabel()); form.add(actions);
        employeeTable.setAutoCreateRowSorter(true);
        root.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.add(form, java.awt.BorderLayout.NORTH);
        root.add(new JScrollPane(employeeTable), java.awt.BorderLayout.CENTER);
        setContentPane(root);
        setSize(850, 580);
        saveButton.addActionListener(evt -> createEmployee());
        editButton.addActionListener(evt -> editSelectedEmployee());
        deleteButton.addActionListener(evt -> deleteSelectedEmployee());
    }

    private void loadJabatanOptions() {
        setJabatanActionsEnabled(false);
        new SwingWorker<List<JabatanDto>, Void>() {
            @Override protected List<JabatanDto> doInBackground() throws Exception {
                return new JabatanApiService(ApiContext.client()).getActive();
            }
            @Override protected void done() {
                try {
                    activeJabatans = get();
                    positionField.removeAllItems();
                    activeJabatans.forEach(positionField::addItem);
                    setJabatanActionsEnabled(!activeJabatans.isEmpty());
                    if (activeJabatans.isEmpty()) {
                        JOptionPane.showMessageDialog(TambahKaryawan.this,
                                "Belum ada jabatan aktif. Tambahkan jabatan melalui Master Data → Jabatan.",
                                "Jabatan belum tersedia", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    setJabatanActionsEnabled(false);
                    JOptionPane.showMessageDialog(TambahKaryawan.this, rootMessage(ex),
                            "Gagal memuat jabatan", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void setJabatanActionsEnabled(boolean enabled) {
        saveButton.setEnabled(enabled);
        editButton.setEnabled(enabled);
        deleteButton.setEnabled(true);
    }

    private void loadEmployees() {
        new SwingWorker<List<UserDto>, Void>() {
            @Override protected List<UserDto> doInBackground() throws Exception {
                return new UserApiService(ApiContext.client()).getUsers();
            }
            @Override protected void done() {
                try {
                    employeeModel.setRowCount(0);
                    loadedEmployees = get().stream()
                            .filter(user -> "EMPLOYEE".equals(user.role()))
                            .toList();
                    for (UserDto user : loadedEmployees) {
                        employeeModel.addRow(new Object[]{user.id(), user.username(), user.namaLengkap(),
                                user.email(), user.jabatan(), user.status()});
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TambahKaryawan.this, ex.getMessage(),
                            "Gagal memuat karyawan", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private UserDto selectedEmployee() {
        int viewRow = employeeTable.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = employeeTable.convertRowIndexToModel(viewRow);
        long id = ((Number) employeeModel.getValueAt(modelRow, 0)).longValue();
        return loadedEmployees.stream().filter(user -> user.id() == id).findFirst().orElse(null);
    }

    private void editSelectedEmployee() {
        UserDto employee = selectedEmployee();
        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Pilih karyawan yang ingin diubah.",
                    "Master Karyawan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField username = new JTextField(employee.username());
        JTextField name = new JTextField(employee.namaLengkap());
        JTextField email = new JTextField(employee.email());
        JComboBox<JabatanDto> position = new JComboBox<>();
        activeJabatans.forEach(position::addItem);
        JabatanDto currentPosition = activeJabatans.stream()
                .filter(item -> employee.jabatanId() != null && item.id() == employee.jabatanId())
                .findFirst().orElse(null);
        if (currentPosition == null && employee.jabatanId() != null) {
            currentPosition = new JabatanDto(employee.jabatanId(), employee.jabatan(), "INACTIVE");
            position.addItem(currentPosition);
        }
        if (currentPosition != null) position.setSelectedItem(currentPosition);
        JComboBox<String> status = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        status.setSelectedItem(employee.status());
        JPanel form = new JPanel(new java.awt.GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Username*")); form.add(username);
        form.add(new JLabel("Nama lengkap*")); form.add(name);
        form.add(new JLabel("Email*")); form.add(email);
        form.add(new JLabel("Jabatan*")); form.add(position);
        form.add(new JLabel("Status")); form.add(status);

        int result = JOptionPane.showConfirmDialog(this, form, "Ubah Data Karyawan",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        if (username.getText().isBlank() || name.getText().isBlank() || email.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Username, nama lengkap, dan email wajib diisi.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JabatanDto selectedPosition = (JabatanDto) position.getSelectedItem();
        boolean retainingExistingInactivePosition = selectedPosition != null
                && employee.jabatanId() != null
                && selectedPosition.id() == employee.jabatanId();
        if (selectedPosition == null
                || (!"ACTIVE".equals(selectedPosition.status()) && !retainingExistingInactivePosition)) {
            JOptionPane.showMessageDialog(this, "Pilih jabatan yang aktif.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setManagementActionsEnabled(false);
        UpdateUserRequest request = new UpdateUserRequest(username.getText().trim(), name.getText().trim(),
                email.getText().trim(), selectedPosition.id(), "EMPLOYEE",
                (String) status.getSelectedItem());
        new SwingWorker<UserDto, Void>() {
            @Override protected UserDto doInBackground() throws Exception {
                return new UserApiService(ApiContext.client()).update(employee.id(), request);
            }
            @Override protected void done() {
                setManagementActionsEnabled(true);
                try {
                    get();
                    JOptionPane.showMessageDialog(TambahKaryawan.this, "Data karyawan berhasil diperbarui.");
                    loadEmployees();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TambahKaryawan.this, rootMessage(ex),
                            "Gagal mengubah karyawan", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void deleteSelectedEmployee() {
        UserDto employee = selectedEmployee();
        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Pilih karyawan yang ingin dihapus.",
                    "Master Karyawan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus akun karyawan " + employee.namaLengkap() + "? Tindakan ini tidak dapat dibatalkan.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        setManagementActionsEnabled(false);
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                new UserApiService(ApiContext.client()).delete(employee.id());
                return null;
            }
            @Override protected void done() {
                setManagementActionsEnabled(true);
                try {
                    get();
                    JOptionPane.showMessageDialog(TambahKaryawan.this, "Karyawan berhasil dihapus.");
                    loadEmployees();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TambahKaryawan.this,
                            rootMessage(ex) + " Jika masih terhubung ke absensi atau tugas, nonaktifkan akun saja.",
                            "Gagal menghapus karyawan", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void setManagementActionsEnabled(boolean enabled) {
        setJabatanActionsEnabled(enabled && !activeJabatans.isEmpty());
        deleteButton.setEnabled(enabled);
    }

    private String rootMessage(Exception ex) {
        Throwable cause = ex;
        while (cause.getCause() != null) cause = cause.getCause();
        return cause.getMessage() == null ? ex.getMessage() : cause.getMessage();
    }

    private void createEmployee() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        if (username.isBlank() || password.isBlank() || name.isBlank() || email.isBlank()) {
            JOptionPane.showMessageDialog(this, "Username, password, nama lengkap, dan email wajib diisi.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JabatanDto selectedPosition = (JabatanDto) positionField.getSelectedItem();
        if (selectedPosition == null) {
            JOptionPane.showMessageDialog(this, "Pilih jabatan aktif terlebih dahulu.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        saveButton.setEnabled(false);
        new SwingWorker<UserDto, Void>() {
            @Override protected UserDto doInBackground() throws Exception {
                return new UserApiService(ApiContext.client()).create(
                        new CreateUserRequest(username, password, name, email, selectedPosition.id(),
                                "EMPLOYEE"));
            }
            @Override protected void done() {
                saveButton.setEnabled(true);
                try {
                    UserDto created = get();
                    passwordField.setText(""); usernameField.setText(""); nameField.setText("");
                    emailField.setText("");
                    JOptionPane.showMessageDialog(TambahKaryawan.this,
                            "Karyawan " + created.username() + " berhasil ditambahkan.");
                    loadEmployees();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TambahKaryawan.this, ex.getMessage(),
                            "Gagal menambah karyawan", JOptionPane.ERROR_MESSAGE);
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Tambah Karyawan");

        jLabel2.setText("Nama Lengkap*");

        jLabel3.setText("Email*");

        jLabel4.setText("No HP*");

        jLabel5.setText("Jabatan*");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton1.setBackground(new java.awt.Color(0, 153, 255));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Simpan");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jTextField3)
                            .addComponent(jTextField2)
                            .addComponent(jComboBox1, 0, 405, Short.MAX_VALUE)
                            .addComponent(jTextField1)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(361, 361, 361)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(350, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(12, 12, 12)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(74, 74, 74)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(93, Short.MAX_VALUE))
        );

        jComboBox1.getAccessibleContext().setAccessibleName("351");
        jComboBox1.getAccessibleContext().setAccessibleDescription("31");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 24, Short.MAX_VALUE))
        );

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
        java.awt.EventQueue.invokeLater(() -> new TambahKaryawan().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
