package com.mycompany.admin.desktop;

import com.mycompany.admin.desktop.screens.loginadmin;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Entry point for the Myin Admin Desktop application. */
public class AdminDesktop {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {
                // Use the platform default look and feel if Nimbus is unavailable.
            }
            new loginadmin().setVisible(true);
        });
    }
}
