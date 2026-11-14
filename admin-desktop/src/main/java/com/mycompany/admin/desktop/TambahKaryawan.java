/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.admin.desktop;

import com.mycompany.admin.desktop.api.ApiContext;
import com.mycompany.admin.desktop.dto.CreateUserRequest;
import com.mycompany.admin.desktop.dto.UserDto;
import com.mycompany.admin.desktop.service.UserApiService;
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
    private final JTextField positionField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JComboBox<String> roleField = new JComboBox<>(new String[]{"EMPLOYEE"});
    private final JButton saveButton = new JButton("Simpan karyawan");
    private final DefaultTableModel employeeModel = new DefaultTableModel(
            new String[]{"ID", "Username", "Nama", "Email", "Jabatan", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    /**
     * Creates new form TambahKaryawan
     */
    public TambahKaryawan() {
        initComponents();
        buildConnectedUi();
        loadEmployees();
    }

    private void buildConnectedUi() {
        setTitle("Manajemen Karyawan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel root = new JPanel(new java.awt.BorderLayout(12, 12));
        JPanel form = new JPanel(new java.awt.GridLayout(0, 2, 8, 8));
        form.setBorder(javax.swing.BorderFactory.createTitledBorder("Tambah karyawan"));
        form.add(new JLabel("Username*")); form.add(usernameField);
        form.add(new JLabel("Password awal*")); form.add(passwordField);
        form.add(new JLabel("Nama lengkap*")); form.add(nameField);
        form.add(new JLabel("Email*")); form.add(emailField);
        form.add(new JLabel("Jabatan")); form.add(positionField);
        form.add(new JLabel("Role")); form.add(roleField);
        form.add(new JLabel()); form.add(saveButton);
        JTable table = new JTable(employeeModel);
        table.setAutoCreateRowSorter(true);
        root.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.add(form, java.awt.BorderLayout.NORTH);
        root.add(new JScrollPane(table), java.awt.BorderLayout.CENTER);
        setContentPane(root);
        setSize(850, 580);
        saveButton.addActionListener(evt -> createEmployee());
    }

    private void loadEmployees() {
        new SwingWorker<List<UserDto>, Void>() {
            @Override protected List<UserDto> doInBackground() throws Exception {
                return new UserApiService(ApiContext.client()).getUsers();
            }
            @Override protected void done() {
                try {
                    employeeModel.setRowCount(0);
                    for (UserDto user : get()) {
                        if ("EMPLOYEE".equals(user.role())) {
                            employeeModel.addRow(new Object[]{user.id(), user.username(), user.namaLengkap(),
                                    user.email(), user.jabatan(), user.status()});
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TambahKaryawan.this, ex.getMessage(),
                            "Gagal memuat karyawan", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void createEmployee() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String position = positionField.getText().trim();
        if (username.isBlank() || password.isBlank() || name.isBlank() || email.isBlank()) {
            JOptionPane.showMessageDialog(this, "Username, password, nama lengkap, dan email wajib diisi.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        saveButton.setEnabled(false);
        new SwingWorker<UserDto, Void>() {
            @Override protected UserDto doInBackground() throws Exception {
                return new UserApiService(ApiContext.client()).create(
                        new CreateUserRequest(username, password, name, email, position,
                                (String) roleField.getSelectedItem()));
            }
            @Override protected void done() {
                saveButton.setEnabled(true);
                try {
                    UserDto created = get();
                    passwordField.setText(""); usernameField.setText(""); nameField.setText("");
                    emailField.setText(""); positionField.setText("");
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
