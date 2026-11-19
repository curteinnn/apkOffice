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
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
    private JTextField usernameField;
    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<JabatanDto> positionField;
    private javax.swing.JPasswordField passwordField;
    private JButton saveButton;
    private JButton editButton;
    private JButton deleteButton;
    private final DefaultTableModel employeeModel = new DefaultTableModel(
            new String[]{"ID", "Username", "Nama", "Email", "Jabatan", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private JTable employeeTable;
    private List<UserDto> loadedEmployees = List.of();
    private List<JabatanDto> activeJabatans = List.of();

    /**
     * Creates new form TambahKaryawan
     */
    public TambahKaryawan() {
        initComponents();
        configureFormActions();
        loadEmployees();
        loadJabatanOptions();
    }

    private void configureFormActions() {
        setTitle("Master Karyawan");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        positionField = (JComboBox<JabatanDto>) (JComboBox<?>) positionCombo;
        usernameField = txtUsername;
        passwordField = txtPassword;
        nameField = txtName;
        emailField = txtEmail;
        saveButton = btnSave;
        editButton = btnEdit;
        deleteButton = btnDelete;
        employeeTable = tblEmployees;
        employeeTable.setModel(employeeModel);
        employeeTable.setAutoCreateRowSorter(true);
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

        rootPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblJabatan = new javax.swing.JLabel();
        positionCombo = new javax.swing.JComboBox<>();
        lblRole = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        scrollEmployees = new javax.swing.JScrollPane();
        tblEmployees = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Master Karyawan");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        rootPanel.setBackground(new java.awt.Color(244, 247, 251));
        rootPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(31, 41, 55));
        lblTitle.setText("Master Karyawan");
        rootPanel.add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 22, 420, 36));

        lblUsername.setText("Username*");
        rootPanel.add(lblUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 76, 220, 22));
        rootPanel.add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 100, 220, 36));

        lblPassword.setText("Password awal*");
        rootPanel.add(lblPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(274, 76, 220, 22));
        rootPanel.add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(274, 100, 220, 36));

        lblName.setText("Nama lengkap*");
        rootPanel.add(lblName, new org.netbeans.lib.awtextra.AbsoluteConstraints(516, 76, 220, 22));
        rootPanel.add(txtName, new org.netbeans.lib.awtextra.AbsoluteConstraints(516, 100, 220, 36));

        lblEmail.setText("Email*");
        rootPanel.add(lblEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(758, 76, 250, 22));
        rootPanel.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(758, 100, 250, 36));

        lblJabatan.setText("Jabatan*");
        rootPanel.add(lblJabatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 154, 220, 22));
        rootPanel.add(positionCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 178, 300, 36));

        lblRole.setForeground(new java.awt.Color(107, 120, 139));
        lblRole.setText("Role akun: EMPLOYEE");
        rootPanel.add(lblRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 178, 240, 36));

        btnSave.setBackground(new java.awt.Color(37, 99, 235));
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setText("Tambah Karyawan");
        btnSave.addActionListener(this::btnSaveActionPerformed);
        rootPanel.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(768, 178, 240, 36));

        btnEdit.setText("Ubah Terpilih");
        btnEdit.addActionListener(this::btnEditActionPerformed);
        rootPanel.add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 244, 140, 34));

        btnDelete.setText("Hapus Terpilih");
        btnDelete.addActionListener(this::btnDeleteActionPerformed);
        rootPanel.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(184, 244, 140, 34));

        tblEmployees.setRowHeight(30);
        scrollEmployees.setViewportView(tblEmployees);

        rootPanel.add(scrollEmployees, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 294, 1016, 360));

        getContentPane().add(rootPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1080, 690));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) { createEmployee(); }
    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) { editSelectedEmployee(); }
    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) { deleteSelectedEmployee(); }

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
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblJabatan;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblRole;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JComboBox<com.mycompany.admin.desktop.dto.JabatanDto> positionCombo;
    private javax.swing.JPanel rootPanel;
    private javax.swing.JScrollPane scrollEmployees;
    private javax.swing.JTable tblEmployees;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtName;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
