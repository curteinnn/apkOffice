package com.mycompany.admin.desktop.screens;

import com.mycompany.admin.desktop.api.ApiContext;
import com.mycompany.admin.desktop.dto.JenisTugasDto;
import com.mycompany.admin.desktop.dto.JenisTugasRequest;
import com.mycompany.admin.desktop.service.JenisTugasApiService;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class MasterJenisTugas extends javax.swing.JFrame {
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nama Jenis Tugas"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private List<JenisTugasDto> rows = List.of();

    public MasterJenisTugas() {
        initComponents();
        tableJenisTugas.setModel(model);
        tableJenisTugas.setAutoCreateRowSorter(true);
        setLocationRelativeTo(null);

        loadRows();
    }

    private JenisTugasDto selectedRow() {
        int viewRow = tableJenisTugas.getSelectedRow();
        if (viewRow < 0) return null;
        int modelRow = tableJenisTugas.convertRowIndexToModel(viewRow);
        String id = String.valueOf(model.getValueAt(modelRow, 0));
        return rows.stream().filter(item -> item.id().equals(id)).findFirst().orElse(null);
    }

    private void loadRows() {
        setActionsEnabled(false);
        new SwingWorker<List<JenisTugasDto>, Void>() {
            @Override protected List<JenisTugasDto> doInBackground() throws Exception {
                return new JenisTugasApiService(ApiContext.client()).getAll();
            }
            @Override protected void done() {
                try {
                    rows = get();
                    model.setRowCount(0);
                    for (JenisTugasDto item : rows) model.addRow(new Object[]{item.id(), item.nama()});
                } catch (Exception ex) {
                    showError("Gagal memuat jenis tugas", ex);
                } finally {
                    setActionsEnabled(true);
                }
            }
        }.execute();
    }

    private void addRow() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("ID (contoh: INS-001):"));
        panel.add(idField);
        panel.add(new JLabel("Nama jenis tugas:"));
        panel.add(nameField);
        if (JOptionPane.showConfirmDialog(this, panel, "Tambah Jenis Tugas",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        String id = idField.getText().trim();
        String nama = nameField.getText().trim();
        if (id.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID dan nama jenis tugas wajib diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mutate("Jenis tugas berhasil ditambahkan.", service -> service.create(new JenisTugasRequest(id, nama)));
    }

    private void editRow() {
        JenisTugasDto selected = requireSelection();
        if (selected == null) return;
        String nama = JOptionPane.showInputDialog(this, "Nama jenis tugas:", selected.nama());
        if (nama == null) return;
        if (nama.isBlank()) {
            JOptionPane.showMessageDialog(this, "Nama jenis tugas wajib diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mutate("Nama jenis tugas berhasil diperbarui.", service -> {
            service.update(selected.id(), new JenisTugasRequest(selected.id(), nama.trim()));
            return null;
        });
    }

    private void deleteRow() {
        JenisTugasDto selected = requireSelection();
        if (selected == null) return;
        int answer = JOptionPane.showConfirmDialog(this,
                "Hapus jenis tugas " + selected.nama() + "? Data yang masih digunakan tugas akan ditolak server.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;
        mutate("Jenis tugas berhasil dihapus.", service -> {
            service.delete(selected.id());
            return null;
        });
    }

    private JenisTugasDto requireSelection() {
        JenisTugasDto selected = selectedRow();
        if (selected == null) JOptionPane.showMessageDialog(this, "Pilih jenis tugas terlebih dahulu.",
                "Master Jenis Tugas", JOptionPane.WARNING_MESSAGE);
        return selected;
    }

    private void mutate(String successMessage, JenisTugasOperation operation) {
        setActionsEnabled(false);
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                operation.run(new JenisTugasApiService(ApiContext.client()));
                return null;
            }
            @Override protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(MasterJenisTugas.this, successMessage,
                            "Master Jenis Tugas", JOptionPane.INFORMATION_MESSAGE);
                    loadRows();
                } catch (Exception ex) {
                    showError("Operasi jenis tugas gagal", ex);
                    setActionsEnabled(true);
                }
            }
        }.execute();
    }

    private void setActionsEnabled(boolean enabled) {
        btnAdd.setEnabled(enabled);
        btnEdit.setEnabled(enabled);
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
    private interface JenisTugasOperation {
        Object run(JenisTugasApiService service) throws Exception;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        lblTitle = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        tableJenisTugas = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Master Jenis Tugas");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 22));
        lblTitle.setText("Master Jenis Tugas");
        getContentPane().add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 18, -1, 32));
        scrollPane.setViewportView(tableJenisTugas);
        getContentPane().add(scrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 64, 690, 320));
        btnAdd.setText("Tambah");
        btnAdd.addActionListener(this::btnAddActionPerformed);
        getContentPane().add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 400, 105, 32));
        btnEdit.setText("Ubah Nama");
        btnEdit.addActionListener(this::btnEditActionPerformed);
        getContentPane().add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 400, 115, 32));
        btnDelete.setText("Hapus");
        btnDelete.addActionListener(this::btnDeleteActionPerformed);
        getContentPane().add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(266, 400, 95, 32));
        btnRefresh.setText("Muat Ulang");
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);
        getContentPane().add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(372, 400, 110, 32));
        setSize(new java.awt.Dimension(760, 500));
    }
    // </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) { addRow(); }
    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) { editRow(); }
    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) { deleteRow(); }
    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) { loadRows(); }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tableJenisTugas;
    // End of variables declaration//GEN-END:variables
}
