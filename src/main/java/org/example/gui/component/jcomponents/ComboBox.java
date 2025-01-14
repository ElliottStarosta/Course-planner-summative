package org.example.gui.component.jcomponents;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.icons.FlatCheckBoxIcon;
import com.formdev.flatlaf.ui.FlatComboBoxUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.ComboPopup;
import net.miginfocom.swing.MigLayout;

/**
 * A custom multi-select ComboBox implementation for selecting multiple items.
 * It supports custom rendering and editing to manage selected items visually.
 */
public class ComboBox extends JComboBox {

    /**
     * The list of selected items in the combo box.
     * <p>
     * This list stores all the items currently selected by the user. The selected items are used
     * to update the editor panel and the renderer dynamically.
     * </p>
     */
    private final List<Object> selectedItems = new ArrayList<>();

    /**
     * The custom editor for the combo box.
     * <p>
     * This editor is responsible for displaying the selected items in a scrollable panel.
     * It provides methods to add and remove items dynamically based on user actions.
     * </p>
     */
    private final ComboBoxMultiCellEditor comboBoxMultiCellEditor;

    /**
     * A reference to the {@code JList} component used for rendering the dropdown list of items.
     * <p>
     * This component is updated whenever the combo box's list cell renderer is accessed.
     * </p>
     */
    private Component comboList;


    /**
     * Returns the list of selected items.
     *
     * @return the list of selected objects.
     */
    public List<Object> getSelectedItems() {
        return selectedItems;
    }

    /**
     * Sets the selected items in the combo box.
     * Items not present in the combo box are ignored.
     *
     * @param selectedItems the list of objects to be selected.
     */
    public void setSelectedItems(List<Object> selectedItems) {
        List<Object> comboItem = new ArrayList<>();
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            comboItem.add(getItemAt(i));
        }
        for (Object obj : selectedItems) {
            if (comboItem.contains(obj)) {
                addItemObject(obj);
            }
        }
        comboItem.clear();
    }

    /**
     * Clears all selected items from the combo box.
     */
    public void clearSelectedItems() {
        selectedItems.clear();
        Component editorCom = getEditor().getEditorComponent();
        if (editorCom instanceof JScrollPane) {
            JScrollPane scroll = (JScrollPane) editorCom;
            JPanel panel = (JPanel) scroll.getViewport().getComponent(0);
            panel.removeAll();
            revalidate();
            repaint();
            comboList.repaint();
        }
    }

    /**
     * Removes the specified item from the selected items list.
     *
     * @param obj the object to be removed.
     */
    private void removeItemObject(Object obj) {
        selectedItems.remove(obj);
        comboBoxMultiCellEditor.removeItem(obj);
        if (comboList != null) {
            comboList.repaint();
        }
    }

    /**
     * Adds the specified item to the selected items list.
     *
     * @param obj the object to be added.
     */
    private void addItemObject(Object obj) {
        selectedItems.add(obj);
        comboBoxMultiCellEditor.addItem(obj);
        if (comboList != null) {
            comboList.repaint();
        }
    }

    /**
     * Creates a new ComboBox instance with a custom UI, renderer, and editor.
     */
    public ComboBox() {
        setUI(new ComboBoxMultiUI());
        comboBoxMultiCellEditor = new ComboBoxMultiCellEditor();
        setRenderer(new ComboBoxMultiCellRenderer());
        setEditor(comboBoxMultiCellEditor);
        setEditable(true);
        addActionListener((e) -> {
            if (e.getModifiers() == ActionEvent.MOUSE_EVENT_MASK) {
                JComboBox combo = (JComboBox) e.getSource();
                Object obj = combo.getSelectedItem();
                if (selectedItems.contains(obj)) {
                    removeItemObject(obj);
                } else {
                    addItemObject(obj);
                }
            }
        });
    }

    /**
     *
     * @param v if {@code true} shows the popup, otherwise, hides the popup.
     */
    @Override
    public void setPopupVisible(boolean v) {

    }

    /**
     * A custom UI implementation for the multi-select ComboBox.
     */
    private class ComboBoxMultiUI extends FlatComboBoxUI {

        @Override
        protected ComboPopup createPopup() {
            return new MultiComboPopup(comboBox);
        }

        private class MultiComboPopup extends FlatComboPopup {

            public MultiComboPopup(JComboBox combo) {
                super(combo);
            }
        }

        @Override
        protected Dimension getDisplaySize() {
            Dimension size = super.getDefaultSize();
            return new Dimension(0, 45);
        }

    }

    /**
     * A custom renderer for displaying items in the ComboBox with checkbox icons.
     */
    private class ComboBoxMultiCellRenderer extends BasicComboBoxRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (comboList != list) {
                comboList = list;
            }
            setIcon(new CheckBoxIcon(selectedItems.contains(value)));
            return this;
        }
    }

    /**
     * A custom editor for the ComboBox supporting a scrollable panel for selected items.
     */
    private class ComboBoxMultiCellEditor extends BasicComboBoxEditor {

        /**
         * The {@code JScrollPane} that serves as the container for the panel displaying selected items.
         * <p>
         * This scroll pane is customized to provide horizontal scrolling for the selected items
         * and to maintain a compact height. It includes FlatLaf styling for consistent integration
         * with the application's theme.
         * </p>
         */
        protected final JScrollPane scroll;

        /**
         * The {@code JPanel} used to display the selected items in the combo box editor.
         * <p>
         * This panel is initialized with a MigLayout, allowing flexible arrangement of the items.
         * Each selected item is dynamically added or removed from this panel based on user interactions.
         * </p>
         */
        protected final JPanel panel;


        /**
         * Adds an item to the editor panel.
         *
         * @param obj the item to be added.
         */
        protected void addItem(Object obj) {
            Item item = new Item(obj);
            panel.add(item);
            panel.repaint();
            panel.revalidate();
        }

        /**
         * Removes an item from the editor panel.
         *
         * @param obj the item to be removed.
         */
        protected void removeItem(Object obj) {
            int count = panel.getComponentCount();
            for (int i = 0; i < count; i++) {
                Item item = (Item) panel.getComponent(i);
                if (item.getItem() == obj) {
                    panel.remove(i);
                    panel.revalidate();
                    panel.repaint();
                    break;
                }
            }
        }

        /**
         * Constructs a new {@code ComboBoxMultiCellEditor}.
         * This editor is customized to display multiple selected items within a scrollable panel.
         * <p>
         * The editor contains a {@link JScrollPane} that holds a {@link JPanel}. The panel is used
         * to dynamically display the selected items. Styling is applied using FlatLaf properties
         * for a consistent look and feel with the application's theme.
         * </p>
         * <ul>
         * <li>The scroll pane has a fixed height of 20 pixels and horizontal scrolling enabled.</li>
         * <li>Custom styles are applied to the scroll bar to match the FlatLaf design guidelines.</li>
         * <li>The panel is initialized with a MigLayout for flexible arrangement of items.</li>
         * </ul>
         */
        public ComboBoxMultiCellEditor() {
            this.panel = new JPanel(new MigLayout("insets 0,filly,gapx 2", "", "fill"));
            this.scroll = new JScrollPane(panel);
            scroll.setPreferredSize(new Dimension(scroll.getPreferredSize().width, 20));
            scroll.putClientProperty(FlatClientProperties.STYLE, ""
                    + "border:2,2,2,2;"
                    + "background:$ComboBox.editableBackground");
            panel.putClientProperty(FlatClientProperties.STYLE, ""
                    + "background:$ComboBox.editableBackground");
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            JScrollBar scrollBar = scroll.getHorizontalScrollBar();
            scrollBar.putClientProperty(FlatClientProperties.STYLE, ""
                    + "width:3;"
                    + "thumbInsets:0,0,0,1;"
                    + "hoverTrackColor:null");
            scrollBar.setUnitIncrement(10);

        }

        @Override
        public Component getEditorComponent() {
            return scroll;
        }

    }

    /**
     * A custom icon representing a checkbox state for ComboBox items.
     */
    private class CheckBoxIcon extends FlatCheckBoxIcon {

        private final boolean selected;

        /**
         * Creates a new CheckBoxIcon instance.
         *
         * @param selected true if the checkbox is selected, false otherwise.
         */
        public CheckBoxIcon(boolean selected) {
            this.selected = selected;
        }

        @Override
        protected boolean isSelected(Component c) {
            return selected;
        }
    }

    /**
     * Represents an individual selected item in the ComboBox editor.
     */
    private class Item extends JLabel {

        /**
         * Returns the associated object for this item.
         *
         * @return the associated object.
         */
        public Object getItem() {
            return item;
        }

        private final Object item;

        /**
         * Creates a new Item instance for the specified object.
         *
         * @param item the object to be represented.
         */
        public Item(Object item) {
            super(item.toString());
            this.item = item;
            init();
        }

        /**
         * Initializes the item with a close button and custom styling.
         */
        private void init() {
            putClientProperty(FlatClientProperties.STYLE, ""
                    + "border:0,5,0,20;"
                    + "background:darken($ComboBox.background,10%)");
            JButton cmd = new JButton(new FlatSVGIcon("assets/icons/close.svg", 0.6f));
            cmd.putClientProperty(FlatClientProperties.STYLE, ""
                    + "arc:999;"
                    + "margin:1,1,1,1;"
                    + "background:null;"
                    + "focusWidth:0");
            cmd.addActionListener((e) -> {
                removeItemObject(item);
            });
            cmd.setFocusable(false);
            setLayout(new MigLayout("fill"));
            add(cmd, "pos 1al 0.5al 10 10");
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            FlatUIUtils.setRenderingHints(g2);
            int arc = UIScale.scale(10);
            g2.setColor(getBackground());
            FlatUIUtils.paintComponentBackground(g2, 0, 0, getWidth(), getHeight(), 0, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}