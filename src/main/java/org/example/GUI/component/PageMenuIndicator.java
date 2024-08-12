package org.example.GUI.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class PageMenuIndicator extends JPanel {

    private JLabel label;
    private int pageNumber = 0;
    private static final int TOTAL_PAGES = 6;
    private static final int TOTAL_INDICATORS = 5;
    private Color indicatorColor = Color.decode("#16a34a");  // Default indicator color
    private static final Color COMPLETED_COLOR = Color.decode("#a0e2a2"); // Light green for completed pages
    private static final Color FINAL_PAGE_COLOR = Color.decode("#34d399"); // Color for the final page

    public PageMenuIndicator() {
        init();
    }

    private void init() {
        putClientProperty(FlatClientProperties.STYLE, "background:null");
        setLayout(new MigLayout("fill,insets 0", "3[100,fill,grow0][]", "[fill,grow 0]"));
        label = new JLabel("none");
        label.setVisible(false);
        add(new EllipticalStatus());
        add(label);
    }

    public void setPageNumber(int pageNumber) {
        // Wrap around the page number if it exceeds TOTAL_PAGES
        if (pageNumber >= 0) {
            this.pageNumber = (pageNumber % TOTAL_PAGES);
            repaint();
        } else {
            throw new IllegalArgumentException("Invalid page number");
        }
    }


    private Color interpolateColor(Color startColor, Color endColor, float ratio) {
        int r = (int) (startColor.getRed() * (1 - ratio) + endColor.getRed() * ratio);
        int g = (int) (startColor.getGreen() * (1 - ratio) + endColor.getGreen() * ratio);
        int b = (int) (startColor.getBlue() * (1 - ratio) + endColor.getBlue() * ratio);
        return new Color(r, g, b);
    }

    private Color getIndicatorColor(int index) {
        float ratio = (float) (index + 1) / TOTAL_INDICATORS;
        if (index == pageNumber % TOTAL_INDICATORS) {
            // Color for the current page, darker as it gets closer to the last page
            return interpolateColor(indicatorColor, FINAL_PAGE_COLOR, ratio);
        } else if (index < pageNumber % TOTAL_INDICATORS) {
            // Color for completed pages
            return COMPLETED_COLOR;
        } else {
            // Default color for other indicators
            return Color.decode(FlatLaf.isLafDark() ? "#404040" : "#CECECE");
        }
    }

    private class EllipticalStatus extends JLabel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth();
            int height = getHeight();
            int size = (int) (height * 0.3f); // Size of the indicators
            Graphics2D g2 = (Graphics2D) g.create();
            FlatUIUtils.setRenderingHints(g2);
            int gap = UIScale.scale(5);
            int w = (width - gap * (TOTAL_INDICATORS - 1)) / TOTAL_INDICATORS; // Width of each indicator
            int y = (height - size) / 2;

            for (int i = 0; i < TOTAL_INDICATORS; i++) {
                g2.setColor(getIndicatorColor(i));
                FlatUIUtils.paintComponentBackground(g2, (w + gap) * i, y, w, size, 0, 999);
            }
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Page Menu Indicator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            PageMenuIndicator indicator = new PageMenuIndicator();
            frame.add(indicator);
            frame.setSize(400, 100);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Demonstrate cycling through pages
            for (int i = 0; i < 7; i++) {  // Try to exceed TOTAL_PAGES to see wrapping
                int finalI = i;
                SwingUtilities.invokeLater(() -> {
                    indicator.setPageNumber(finalI);
                });
                try {
                    Thread.sleep(1000);  // Wait 1 second between page changes
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
