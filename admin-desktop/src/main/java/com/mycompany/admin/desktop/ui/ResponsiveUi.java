package com.mycompany.admin.desktop.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Runtime resizing support for NetBeans GUI Builder forms.
 * The .form files remain the source of the original design and can be edited
 * normally; this class adapts the generated Swing component bounds at runtime.
 */
public final class ResponsiveUi {
    private ResponsiveUi() { }

    public static void install(Window window) {
        if (!(window instanceof JFrame) && !(window instanceof JDialog)) return;

        if (window instanceof JFrame frame) frame.setResizable(true);
        else ((JDialog) window).setResizable(true);
        Container root = window instanceof JFrame frame
                ? frame.getContentPane() : ((JDialog) window).getContentPane();
        ButtonStyler.apply(root);
        Dimension designWindow = window.getSize();
        if (designWindow.width <= 0 || designWindow.height <= 0) {
            window.pack();
            designWindow = window.getSize();
        }

        int minWidth = Math.max(window instanceof JDialog ? 360 : 560,
                (int) Math.round(designWindow.width * 0.68));
        int minHeight = Math.max(window instanceof JDialog ? 240 : 380,
                (int) Math.round(designWindow.height * 0.68));
        window.setMinimumSize(new Dimension(minWidth, minHeight));

        PanelLayout layout = PanelLayout.capture(root);
        if (layout != null) observeResize(window, root, layout);
    }

    /** Installs the same proportional layout behavior on a panel embedded in a card. */
    public static void installPanel(JPanel panel) {
        ButtonStyler.apply(panel);
        PanelLayout layout = PanelLayout.capture(panel);
        if (layout != null) observeResize(panel, panel, layout);
    }

    private static void observeResize(Component source, Container repaintTarget, PanelLayout layout) {
        source.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent event) {
                if (!layout.isApplying()) {
                    SwingUtilities.invokeLater(() -> {
                        layout.apply();
                        repaintTarget.revalidate();
                        repaintTarget.repaint();
                    });
                }
            }
        });
    }

    private static final class PanelLayout {
        private final JPanel panel;
        private final Dimension designSize;
        private final List<ChildLayout> children;
        private boolean applying;

        private PanelLayout(JPanel panel, Dimension designSize, List<ChildLayout> children) {
            this.panel = panel;
            this.designSize = designSize;
            this.children = children;
        }

        static PanelLayout capture(Container container) {
            if (!(container instanceof JPanel panel)) return null;
            Dimension size = panel.getSize();
            if (size.width <= 0 || size.height <= 0) size = panel.getPreferredSize();
            if (size.width <= 0 || size.height <= 0) return null;

            List<ChildLayout> children = new ArrayList<>();
            for (Component child : panel.getComponents()) {
                Rectangle bounds = child.getBounds();
                PanelLayout nested = child instanceof JPanel childPanel
                        ? capture(childPanel) : null;
                children.add(new ChildLayout(child, bounds, nested));
            }
            // Preserve the GUI Builder layout in the .form and generated init code;
            // only the live component tree uses explicit bounds after construction.
            panel.setLayout(null);
            return new PanelLayout(panel, new Dimension(size), children);
        }

        boolean isApplying() { return applying; }

        void apply() {
            if (applying) return;
            applying = true;
            try {
                double sx = Math.max(0.1, panel.getWidth() / (double) designSize.width);
                double sy = Math.max(0.1, panel.getHeight() / (double) designSize.height);
                for (ChildLayout child : children) {
                    Rectangle b = child.designBounds;
                    child.component.setBounds((int) Math.round(b.x * sx),
                            (int) Math.round(b.y * sy),
                            Math.max(1, (int) Math.round(b.width * sx)),
                            Math.max(1, (int) Math.round(b.height * sy)));
                    if (child.nested != null) child.nested.apply();
                }
            } finally {
                applying = false;
            }
        }
    }

    private static final class ChildLayout {
        private final Component component;
        private final Rectangle designBounds;
        private final PanelLayout nested;

        ChildLayout(Component component, Rectangle designBounds, PanelLayout nested) {
            this.component = component;
            this.designBounds = new Rectangle(designBounds);
            this.nested = nested;
        }
    }
}
