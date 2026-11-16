/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.admin.desktop;

import com.mycompany.admin.desktop.api.ApiContext;
import com.mycompany.admin.desktop.dto.TaskDto;
import com.mycompany.admin.desktop.dto.VerifyTaskRequest;
import com.mycompany.admin.desktop.service.TaskApiService;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author argag
 */
public class ListVisit extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ListVisit.class.getName());
    private List<TaskDto> loadedTasks = List.of();

    /**
     * Creates new form ListVisit
     */
    public ListVisit() {
        initComponents();
        buildTaskActions();
        loadTasks();
    }

    private final DefaultTableModel taskModel = new DefaultTableModel(
            new String[]{"ID", "Employee", "Pelanggan", "Jenis Tugas", "Judul Tugas", "Lokasi", "Tanggal", "Status", "Nominal"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private void buildTaskActions() {
        setTitle("Daftar Tugas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        JMenuItem verify = new JMenuItem("Verifikasi tugas terpilih");
        verify.addActionListener(evt -> verifySelected());
        menu.add(create); menu.add(refresh); menu.add(verify); bar.add(menu); setJMenuBar(bar);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) showSelectedTask();
            }
        });
    }

    private void loadTasks() {
        taskModel.setRowCount(0);
        new SwingWorker<List<TaskDto>, Void>() {
            @Override protected List<TaskDto> doInBackground() throws Exception {
                return new TaskApiService(ApiContext.client()).getAll();
            }
            @Override protected void done() {
                try {
                    loadedTasks = get();
                    for (TaskDto task : loadedTasks) {
                        taskModel.addRow(new Object[]{task.id(), task.user() == null ? "-" : task.user().namaLengkap(),
                                task.pelanggan() == null ? "Belum ditentukan" : task.pelanggan().nama(),
                                task.jenisTugas() == null ? "Belum dikategorikan"
                                        : task.jenisTugas().id() + " - " + task.jenisTugas().nama(),
                                task.judul(), task.lokasi(), task.tanggal(), task.status(), task.nominal()});
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ListVisit.this, ex.getMessage(),
                            "Gagal memuat tugas", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private TaskDto selectedTask() {
        int viewRow = jTable1.getSelectedRow();
        if (viewRow < 0) return null;
        int row = jTable1.convertRowIndexToModel(viewRow);
        long id = ((Number) taskModel.getValueAt(row, 0)).longValue();
        return loadedTasks.stream().filter(task -> task.id() == id).findFirst().orElse(null);
    }

    private void showSelectedTask() {
        TaskDto task = selectedTask();
        if (task == null) return;
        String employee = task.user() == null ? "-" : task.user().namaLengkap();
        JOptionPane.showMessageDialog(this,
                "Employee: " + employee + "\nPelanggan: " + (task.pelanggan() == null ? "Belum ditentukan" : task.pelanggan().nama())
                        + "\nJenis tugas: " + (task.jenisTugas() == null ? "Belum dikategorikan"
                                : task.jenisTugas().id() + " - " + task.jenisTugas().nama())
                        + "\nJudul: " + task.judul() + "\nTanggal: " + task.tanggal()
                        + "\nLokasi: " + task.lokasi() + "\nStatus: " + task.status()
                        + "\nDeskripsi: " + (task.deskripsi() == null ? "-" : task.deskripsi()),
                "Detail Tugas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void verifySelected() {
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
        BigDecimal nominal = null;
        if (choice == 0) {
            String value = JOptionPane.showInputDialog(this, "Nominal (angka >= 0):", "0");
            if (value == null) return;
            try {
                nominal = new BigDecimal(value.trim());
                if (nominal.signum() < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Nominal harus angka dan tidak boleh negatif.",
                        "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        VerifyTaskRequest request = new VerifyTaskRequest(choice == 0 ? "APPROVED" : "REJECTED", nominal);
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                new TaskApiService(ApiContext.client()).verify(task.id(), request);
                return null;
            }
            @Override protected void done() {
                try { get(); loadTasks(); }
                catch (Exception ex) { JOptionPane.showMessageDialog(ListVisit.this, ex.getMessage(),
                        "Gagal memverifikasi tugas", JOptionPane.ERROR_MESSAGE); }
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("List Visit");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Gambar/pnhkembali.png"))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Judul Tugas", "Client", "Lokasi", "Tanggal", "Status"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jTextField1.setText("Cari");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 744, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(109, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addGap(12, 12, 12)
                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        java.awt.EventQueue.invokeLater(() -> new ListVisit().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
