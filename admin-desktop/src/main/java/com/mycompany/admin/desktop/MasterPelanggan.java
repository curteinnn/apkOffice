package com.mycompany.admin.desktop;

import com.mycompany.admin.desktop.api.ApiContext;
import com.mycompany.admin.desktop.dto.PelangganDto;
import com.mycompany.admin.desktop.dto.SavePelangganRequest;
import com.mycompany.admin.desktop.service.PelangganApiService;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class MasterPelanggan extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Nama Pelanggan", "Kontak", "Telepon", "Alamat", "Link Maps"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private List<PelangganDto> rows = List.of();

    public MasterPelanggan() {
        setTitle("Master Pelanggan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 460);
        setLocationRelativeTo(null);
        JPanel actions = new JPanel();
        JButton add = new JButton("Tambah");
        JButton edit = new JButton("Ubah");
        JButton delete = new JButton("Hapus");
        JButton refresh = new JButton("Muat ulang");
        add.addActionListener(e -> editPelanggan(null));
        edit.addActionListener(e -> editPelanggan(selected()));
        delete.addActionListener(e -> deleteSelected());
        refresh.addActionListener(e -> load());
        actions.add(add); actions.add(edit); actions.add(delete); actions.add(refresh);
        add(actions, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        load();
    }

    private PelangganDto selected() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        long id = ((Number) model.getValueAt(table.convertRowIndexToModel(row), 0)).longValue();
        return rows.stream().filter(item -> item.id() == id).findFirst().orElse(null);
    }

    private void load() {
        new SwingWorker<List<PelangganDto>, Void>() {
            @Override protected List<PelangganDto> doInBackground() throws Exception {
                return new PelangganApiService(ApiContext.client()).getAll();
            }
            @Override protected void done() {
                try {
                    rows = get();
                    model.setRowCount(0);
                    for (PelangganDto item : rows) {
                        model.addRow(new Object[]{item.id(), item.nama(), item.namaKontak(), item.telepon(),
                                item.alamat(), item.linkGmaps()});
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MasterPelanggan.this, ex.getMessage(),
                            "Gagal memuat pelanggan", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void editPelanggan(PelangganDto current) {
        JTextField nama = new JTextField(current == null ? "" : current.nama());
        JTextField kontak = new JTextField(current == null ? "" : value(current.namaKontak()));
        JTextField telepon = new JTextField(current == null ? "" : value(current.telepon()));
        JTextField alamat = new JTextField(current == null ? "" : value(current.alamat()));
        JTextField maps = new JTextField(current == null ? "" : value(current.linkGmaps()));
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Nama pelanggan*")); form.add(nama);
        form.add(new JLabel("Nama kontak")); form.add(kontak);
        form.add(new JLabel("Telepon")); form.add(telepon);
        form.add(new JLabel("Alamat*")); form.add(alamat);
        form.add(new JLabel("Link Google Maps")); form.add(maps);
        int answer = JOptionPane.showConfirmDialog(this, form,
                current == null ? "Tambah Pelanggan" : "Ubah Pelanggan",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer != JOptionPane.OK_OPTION) return;
        if (nama.getText().isBlank() || alamat.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Nama dan alamat wajib diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SavePelangganRequest request = new SavePelangganRequest(nama.getText().trim(), kontak.getText().trim(),
                telepon.getText().trim(), alamat.getText().trim(), maps.getText().trim());
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                PelangganApiService api = new PelangganApiService(ApiContext.client());
                if (current == null) api.create(request); else api.update(current.id(), request);
                return null;
            }
            @Override protected void done() {
                try { get(); load(); }
                catch (Exception ex) { JOptionPane.showMessageDialog(MasterPelanggan.this,
                        ex.getMessage(), "Gagal menyimpan pelanggan", JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }

    private void deleteSelected() {
        PelangganDto item = selected();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan terlebih dahulu.", "Hapus Pelanggan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Hapus pelanggan " + item.nama() + "?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                new PelangganApiService(ApiContext.client()).delete(item.id());
                return null;
            }
            @Override protected void done() {
                try { get(); load(); }
                catch (Exception ex) { JOptionPane.showMessageDialog(MasterPelanggan.this,
                        ex.getMessage(), "Gagal menghapus pelanggan", JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }

    private String value(String text) { return text == null ? "" : text; }
}
