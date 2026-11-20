package com.mycompany.admin.desktop.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

/** Shared visual style for action buttons throughout the admin application. */
public final class ButtonStyler {
    private static final Color BLUE = new Color(37, 99, 235);
    private static final Color BLUE_HOVER = new Color(29, 78, 216);
    private static final Color BLUE_PRESSED = new Color(30, 64, 175);
    private static final Color RED = new Color(220, 38, 38);
    private static final Color RED_HOVER = new Color(185, 28, 28);
    private static final Color DISABLED = new Color(170, 185, 210);
    private static final Color WHITE = Color.WHITE;
    private static final String STYLED = ButtonStyler.class.getName() + ".styled";

    private ButtonStyler() { }

    /** Sets defaults used by buttons created by Swing dialogs and option panes. */
    public static void installDefaults() {
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("Button.margin", new Insets(8, 14, 8, 14));
        UIManager.put("Button.background", BLUE);
        UIManager.put("Button.foreground", WHITE);
        UIManager.put("Button.disabledText", WHITE);
        UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.PLAIN, 13));
    }

    /** Applies the shared style to all buttons in a screen or embedded page. */
    public static void apply(Component component) {
        if (component instanceof JButton button) style(button);
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) apply(child);
        }
    }

    private static void style(JButton button) {
        if (Boolean.TRUE.equals(button.getClientProperty(STYLED))) return;
        button.putClientProperty(STYLED, Boolean.TRUE);
        boolean danger = isDanger(button);
        Color base = danger ? RED : BLUE;
        Color hover = danger ? RED_HOVER : BLUE_HOVER;
        Color pressed = danger ? new Color(153, 27, 27) : BLUE_PRESSED;

        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setMargin(new Insets(8, 14, 8, 14));
        button.setForeground(WHITE);
        button.setBackground(base);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setRolloverEnabled(true);
        button.setBorder(new CompoundBorder(new LineBorder(base.darker(), 1, true),
                BorderFactory.createEmptyBorder(3, 7, 3, 7)));
        button.getModel().addChangeListener(event -> {
            if (!button.isEnabled()) button.setBackground(DISABLED);
            else if (button.getModel().isPressed()) button.setBackground(pressed);
            else if (button.getModel().isRollover()) button.setBackground(hover);
            else button.setBackground(base);
        });
        button.addPropertyChangeListener("enabled", event ->
                button.setBackground(button.isEnabled() ? base : DISABLED));
        if (!button.isEnabled()) button.setBackground(DISABLED);
    }

    private static boolean isDanger(AbstractButton button) {
        String label = button.getText();
        return label != null && (label.toLowerCase(java.util.Locale.ROOT).contains("hapus")
                || label.toLowerCase(java.util.Locale.ROOT).contains("delete"));
    }
}
