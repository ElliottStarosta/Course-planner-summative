package org.example.gui.component.jcomponents;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * A custom component that visually indicates the current page number and completed pages
 * in a multi-page interface. The indicator uses elliptical elements to represent each page,
 * with the current page being highlighted and completed pages shown in a light green color.
 * The final page is highlighted in yellow.
 */
public class PageMenuIndicator extends JPanel {

    /**
     * A JLabel used to display the page number or any relevant information about the page status.
     */
    private JLabel label;

    /**
     * The current page number. It tracks the active page and determines the corresponding indicator.
     * The value is wrapped around if it exceeds the total number of pages.
     */
    private int pageNumber = 0;

    /**
     * The total number of pages in the application. This value is used to determine how many pages exist
     * and how to handle page wrapping.
     */
    private static final int TOTAL_PAGES = 6;

    /**
     * The total number of indicators that will be displayed. It corresponds to the number of status indicators
     * shown for the pages.
     */
    private static final int TOTAL_INDICATORS = 5;

    /**
     * The default color used for the page indicators. This color is applied to the current page indicator.
     * The default value is a green color (#16a34a).
     */
    private Color indicatorColor = Color.decode("#16a34a");

    /**
     * The color used for completed pages. A light green (#a0e2a2) is applied to indicators representing pages
     * that have already been visited.
     */
    private static final Color COMPLETED_COLOR = Color.decode("#a0e2a2");

    /**
     * The color used for the final page indicator. A yellow (#fff808) color is applied to the indicator representing
     * the last page of the sequence.
     */
    private static final Color FINAL_PAGE_COLOR = Color.decode("#fff808");

    /**
     * Constructor that initializes the PageMenuIndicator component.
     */
    public PageMenuIndicator() {
        init();
    }

    /**
     * Initializes the layout and adds the necessary components to the indicator panel.
     */
    private void init() {
        putClientProperty(FlatClientProperties.STYLE, "background:null");
        setLayout(new MigLayout("fill,insets 0", "3[100,fill,grow0][]", "[fill,grow 0]"));
        label = new JLabel("none");
        label.setVisible(false);
        add(new EllipticalStatus());
        add(label);
    }

    /**
     * Sets the current page number and triggers a repaint. The page number wraps around
     * if it exceeds the maximum number of pages.
     *
     * @param pageNumber The current page number to set
     * @throws IllegalArgumentException If the page number is negative
     */
    public void setPageNumber(int pageNumber) {
        // Wrap around the page number if it exceeds TOTAL_PAGES
        if (pageNumber >= 0) {
            this.pageNumber = (pageNumber % TOTAL_PAGES);
            repaint();
        } else {
            throw new IllegalArgumentException("Invalid page number");
        }
    }

    /**
     * Interpolates between the default indicator color and the final page color based on the page's position.
     *
     * @param startColor The starting color for the interpolation
     * @param ratio The ratio at which to interpolate (0.0 - 1.0)
     * @return The interpolated color
     */
    private Color interpolateColor(Color startColor, float ratio) {
        int r = (int) (startColor.getRed() * (1 - ratio) + PageMenuIndicator.FINAL_PAGE_COLOR.getRed() * ratio);
        int g = (int) (startColor.getGreen() * (1 - ratio) + PageMenuIndicator.FINAL_PAGE_COLOR.getGreen() * ratio);
        int b = (int) (startColor.getBlue() * (1 - ratio) + PageMenuIndicator.FINAL_PAGE_COLOR.getBlue() * ratio);
        return new Color(r, g, b);
    }

    /**
     * Returns the color for a specific indicator based on the current page number and the state of the indicator.
     *
     * @param index The index of the indicator
     * @return The color for the indicator
     */
    private Color getIndicatorColor(int index) {
        float ratio = (float) (index + 1) / TOTAL_INDICATORS;
        if (index == pageNumber % TOTAL_INDICATORS) {
            // Color for the current page, darker as it gets closer to the last page
            return interpolateColor(indicatorColor, ratio);
        } else if (index < pageNumber % TOTAL_INDICATORS) {
            // Color for completed pages
            return COMPLETED_COLOR;
        } else {
            // Default color for other indicators
            return Color.decode(FlatLaf.isLafDark() ? "#404040" : "#CECECE");
        }
    }

    /**
     * A custom JLabel used to paint the elliptical status indicators on the page menu.
     */
    private class EllipticalStatus extends JLabel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth();
            int height = getHeight();
            int size = (int) (height * 0.5f); // Size of the indicators
            Graphics2D g2 = (Graphics2D) g.create();
            FlatUIUtils.setRenderingHints(g2);
            int gap = UIScale.scale(5);
            int w = (width - gap * (TOTAL_INDICATORS - 1)) / TOTAL_INDICATORS; // Width of each indicator
            int y = (height - size) / 2;

            // Paint each indicator with its respective color
            for (int i = 0; i < TOTAL_INDICATORS; i++) {
                g2.setColor(getIndicatorColor(i));
                FlatUIUtils.paintComponentBackground(g2, (w + gap) * i, y, w, size, 0, 999);
            }
            g2.dispose();
        }
    }
}