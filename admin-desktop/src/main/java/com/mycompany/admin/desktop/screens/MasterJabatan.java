package com.mycompany.admin.desktop.screens;

import com.mycompany.admin.desktop.api.ApiContext;
import com.mycompany.admin.desktop.dto.JabatanDto;
import com.mycompany.admin.desktop.dto.JabatanRequest;
import com.mycompany.admin.desktop.dto.UpdateJabatanStatusRequest;
import com.mycompany.admin.desktop.service.JabatanApiService;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class MasterJabatan extends javax.swing.JFrame {
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Nama Jabatan", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private List<JabatanDto> jabatanRows = List.of();

    public MasterJabatan() {
        initComponents();
        com.mycompany.admin.desktop.ui.ResponsiveUi.install(this);
        tableJabatan.setModel(model);
        tableJabatan.setAutoCreateRowSorter(true);
        setLocationRelativeTo(null);

        loadJabatan();
    }

    private JabatanDto selectedJabatan() {
        int viewRow = tableJabatan.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = tableJabatan.convertRowIndexToModel(viewRow);
        long id = ((Number) model.getValueAt(modelRow, 0)).longValue();
        return jabatanRows.stream().filter(item -> item.id() == id).findFirst().orElse(null);
    }

    private void loadJabatan() {
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(false);
        btnToggle.setEnabled(false);
        btnDelete.setEnabled(false);
        new SwingWorker<List<JabatanDto>, Void>() {
            @Override protected List<JabatanDto> doInBackground() throws Exception {
                return new JabatanApiService(ApiContext.client()).getAll();
            }
            @Override protected void done() {
                try {
                    jabatanRows = get();
                    model.setRowCount(0);
                    for (JabatanDto item : jabatanRows) {
                        model.addRow(new Object[]{item.id(), item.nama(), item.status()});
                    }
                } catch (Exception ex) {
                    showError("Gagal memuat master jabatan", ex);
                } finally {
                    btnAdd.setEnabled(true);
                    btnEdit.setEnabled(true);
                    btnToggle.setEnabled(true);
                    btnDelete.setEnabled(true);
                }
            }
        }.execute();
    }

    private void addJabatan() {
        String name = JOptionPane.showInputDialog(this, "Nama jabatan:", "Tambah Jabatan",
                JOptionPane.PLAIN_MESSAGE);
        if (name == null) return;
        if (name.isBlank()) {
            JOptionPane.showMessageDialog(this, "Nama jabatan wajib diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        runMutation("Jabatan berhasil ditambahkan.",
                service -> service.create(new JabatanRequest(name.trim())));
    }

    private void editJabatan() {
        JabatanDto selected = requireSelection();
        if (selected == null) return;
        String name = JOptionPane.showInputDialog(this, "Nama jabatan:", selected.nama());
        if (name == null) return;
        if (name.isBlank()) {
            JOptionPane.showMessageDialog(this, "Nama jabatan wajib diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        runMutation("Nama jabatan berhasil diperbarui.",
                service -> service.update(selected.id(), new JabatanRequest(name.trim())));
    }

    private void toggleJabatan() {
        JabatanDto selected = requireSelection();
        if (selected == null) return;
        String nextStatus = "ACTIVE".equals(selected.status()) ? "INACTIVE" : "ACTIVE";
        int answer = JOptionPane.showConfirmDialog(this,
                ("ACTIVE".equals(nextStatus) ? "Aktifkan" : "Nonaktifkan")
                        + " jabatan " + selected.nama() + "?",
                "Konfirmasi Status", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;
        runMutation("Status jabatan berhasil diperbarui.",
                service -> service.updateStatus(selected.id(), new UpdateJabatanStatusRequest(nextStatus)));
    }

    private void deleteJabatan() {
        JabatanDto selected = requireSelection();
        if (selected == null) return;
        int answer = JOptionPane.showConfirmDialog(this,
                "Hapus jabatan " + selected.nama() + "? Jabatan yang digunakan karyawan akan ditolak oleh server.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;
        runMutation("Jabatan berhasil dihapus.", service -> {
            service.delete(selected.id());
            return null;
        });
    }

    private JabatanDto requireSelection() {
        JabatanDto selected = selectedJabatan();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Pilih jabatan terlebih dahulu.",
                    "Master Jabatan", JOptionPane.WARNING_MESSAGE);
        }
        return selected;
    }

    private void runMutation(String successMessage, JabatanOperation operation) {
        setActionsEnabled(false);
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                operation.run(new JabatanApiService(ApiContext.client()));
                return null;
            }
            @Override protected void done() {
                setActionsEnabled(true);
                try {
                    get();
                    JOptionPane.showMessageDialog(MasterJabatan.this, successMessage,
                            "Master Jabatan", JOptionPane.INFORMATION_MESSAGE);
                    loadJabatan();
                } catch (Exception ex) {
                    showError("Operasi jabatan gagal", ex);
                }
            }
        }.execute();
    }

    private void setActionsEnabled(boolean enabled) {
        btnAdd.setEnabled(enabled);
        btnEdit.setEnabled(enabled);
        btnToggle.setEnabled(enabled);
        btnDelete.setEnabled(enabled);
        btnRefresh.setEnabled(enabled);
    }

    private void showError(String title, Exception ex) {
        Throwable cause = ex;
        while (cause.getCause() != null) cause = cause.getCause();
        String message = cause.getMessage() == null ? ex.getMessage() : cause.getMessage();
        JOptionPane.showMessageDialog(this, message == null ? "Terjadi kesalahan." : message,
                title, JOptionPane.ERROR_MESSAGE);
    }

    @FunctionalInterface
    private interface JabatanOperation {
        Object run(JabatanApiService service) throws Exception;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitle = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        tableJabatan = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnToggle = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Master Jabatan");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 22));
        lblTitle.setText("Master Jabatan");
        getContentPane().add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 18, -1, 32));
        tableJabatan.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{}, new String[]{"ID", "Nama Jabatan", "Status"}));
        scrollPane.setViewportView(tableJabatan);
        getContentPane().add(scrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 64, 690, 320));
        btnAdd.setText("Tambah");
        btnAdd.addActionListener(this::btnAddActionPerformed);
        getContentPane().add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 400, 105, 32));
        btnEdit.setText("Ubah Nama");
        btnEdit.addActionListener(this::btnEditActionPerformed);
        getContentPane().add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 400, 115, 32));
        btnToggle.setText("Aktif/Nonaktif");
        btnToggle.addActionListener(this::btnToggleActionPerformed);
        getContentPane().add(btnToggle, new org.netbeans.lib.awtextra.AbsoluteConstraints(266, 400, 130, 32));
        btnDelete.setText("Hapus");
        btnDelete.addActionListener(this::btnDeleteActionPerformed);
        getContentPane().add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(407, 400, 95, 32));
        btnRefresh.setText("Muat Ulang");
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);
        getContentPane().add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(513, 400, 110, 32));
        setSize(new java.awt.Dimension(760, 500));
    }
    // </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) { addJabatan(); }
    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) { editJabatan(); }
    private void btnToggleActionPerformed(java.awt.event.ActionEvent evt) { toggleJabatan(); }
    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) { deleteJabatan(); }
    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) { loadJabatan(); }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnToggle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tableJabatan;
    // End of variables declaration//GEN-END:variables
}
