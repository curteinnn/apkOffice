package com.mycompany.admin.desktop.screens;


import com.mycompany.admin.desktop.api.ApiContext;
import com.mycompany.admin.desktop.dto.ChangePasswordRequest;
import com.mycompany.admin.desktop.service.UserApiService;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class ChangePasswordDialog extends javax.swing.JDialog {

    public ChangePasswordDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        setLocationRelativeTo(parent);
        getRootPane().setDefaultButton(btnSave);
    }

    private void changePassword() {
        char[] currentPassword = txtCurrentPassword.getPassword();
        char[] newPassword = txtNewPassword.getPassword();
        char[] confirmation = txtConfirmPassword.getPassword();

        if (currentPassword.length == 0 || newPassword.length == 0 || confirmation.length == 0) {
            clear(currentPassword, newPassword, confirmation);
            JOptionPane.showMessageDialog(this, "Semua kolom password wajib diisi.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (newPassword.length < 6) {
            clear(currentPassword, newPassword, confirmation);
            JOptionPane.showMessageDialog(this, "Password baru minimal 6 karakter.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!Arrays.equals(newPassword, confirmation)) {
            clear(currentPassword, newPassword, confirmation);
            JOptionPane.showMessageDialog(this, "Konfirmasi password baru tidak sama.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ChangePasswordRequest request = new ChangePasswordRequest(
                new String(currentPassword), new String(newPassword));
        clear(currentPassword, newPassword, confirmation);
        btnSave.setEnabled(false);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                new UserApiService(ApiContext.client()).changePassword(request);
                return null;
            }

            @Override
            protected void done() {
                btnSave.setEnabled(true);
                txtCurrentPassword.setText("");
                txtNewPassword.setText("");
                txtConfirmPassword.setText("");
                try {
                    get();
                    JOptionPane.showMessageDialog(ChangePasswordDialog.this,
                            "Password berhasil diubah.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() == null ? ex : ex.getCause();
                    JOptionPane.showMessageDialog(ChangePasswordDialog.this,
                            cause.getMessage() == null ? "Gagal mengubah password." : cause.getMessage(),
                            "Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void clear(char[]... values) {
        for (char[] value : values) Arrays.fill(value, '\0');
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitle = new javax.swing.JLabel();
        lblCurrentPassword = new javax.swing.JLabel();
        txtCurrentPassword = new javax.swing.JPasswordField();
        lblNewPassword = new javax.swing.JLabel();
        txtNewPassword = new javax.swing.JPasswordField();
        lblConfirmPassword = new javax.swing.JLabel();
        txtConfirmPassword = new javax.swing.JPasswordField();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ubah Password");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 20));
        lblTitle.setText("Ubah Password");
        getContentPane().add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 20, -1, -1));

        lblCurrentPassword.setText("Password saat ini");
        getContentPane().add(lblCurrentPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 68, 145, 25));
        getContentPane().add(txtCurrentPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 68, 230, 28));

        lblNewPassword.setText("Password baru");
        getContentPane().add(lblNewPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 112, 145, 25));
        getContentPane().add(txtNewPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 112, 230, 28));

        lblConfirmPassword.setText("Konfirmasi password");
        getContentPane().add(lblConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 156, 145, 25));
        getContentPane().add(txtConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 156, 230, 28));

        btnSave.setText("Simpan");
        btnSave.addActionListener(this::btnSaveActionPerformed);
        getContentPane().add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 208, 85, 30));
        btnCancel.setText("Batal");
        btnCancel.addActionListener(this::btnCancelActionPerformed);
        getContentPane().add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(325, 208, 85, 30));

        setSize(new java.awt.Dimension(450, 290));
    }
    // </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
        changePassword();
    }

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblCurrentPassword;
    private javax.swing.JLabel lblNewPassword;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JPasswordField txtCurrentPassword;
    private javax.swing.JPasswordField txtNewPassword;
    // End of variables declaration//GEN-END:variables
}
