package com.gamebuilder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import com.gamebuilder.model.CanvasModel;
import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.model.CanvasModelAddListener;
import com.gamebuilder.model.CanvasModelUpdateListener;

class ListItem {
    CanvasObject object;
    boolean renameMode = false;

    public ListItem(CanvasObject object) {
        this.object = object;
    }
}

public class LayersPanel extends JPanel implements CanvasModelAddListener, CanvasModelUpdateListener {
    private CanvasModel model;

    private DefaultListModel<ListItem> listModel = new DefaultListModel<>();
    private JList<ListItem> list = new JList<>(listModel);

    public LayersPanel(GameBuilder gameBuilder, CanvasModel model) {
        this.model = model;
        setPreferredSize(new Dimension(150, 600));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        list.setCellRenderer(new LayerCellRenderer());
        list.setDragEnabled(true);
        list.setDropMode(DropMode.ON);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onClick((JList<ListItem>) e.getSource());
        });
        list.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = list.locationToIndex(e.getPoint());
                    if (index != -1) {
                        ListItem item = listModel.getElementAt(index);
                        item.renameMode = true;
                        System.out.println("Double clicked on: " + item.object.getName());

                        Runnable ui = () -> {
                            revalidate(); // triggers layout pass
                            repaint();    // paints new child
                        };
                        SwingUtilities.invokeLater(ui);
                    }
                }
            }
        });
        this.add(new JScrollPane(this.list));
    }

    private void onClick(JList<ListItem> source) {
        ListItem selectedItem = source.getSelectedValue();
        if (selectedItem != null && !selectedItem.renameMode) {
            this.model.deselectAll();
            selectedItem.object.setSelected(true);
        }
    }

    private void refreshObjects() {
        this.listModel.clear();

        ArrayList<CanvasObject> objs = model.getObjects();
        for (int i = objs.size() - 1; i >= 0; i--) this.listModel.addElement(new ListItem(objs.get(i)));
    }

    @Override()
    public void onAdd(CanvasObject object, CanvasModel model) {
        this.onCanvasModelUpdated(model);
    }

    @Override()
    public void onCanvasModelUpdated(CanvasModel model) {
        Runnable ui = () -> {
            this.refreshObjects();
            this.revalidate(); // triggers layout pass
            this.repaint();    // paints new child
        };
        SwingUtilities.invokeLater(ui);
    }

    class PanelListItem extends JPanel {
        private ListItem item;
        private int index;
        private JList list;

        PanelListItem(ListItem item, int index, JList list) {
            this.item = item;
            this.index = index;
            this.list = list;
            this.setLayout(new BorderLayout());
            
            JLabel name = new JLabel();
            name.setText(item.object.getName());

            if (item.object.isSelected()) {
                name.setForeground(Color.WHITE);
            }

            this.add(name);

            if (item.object.isSelected()) {
                setBackground(Color.BLUE);
            }

            if (item.renameMode) {
                showEditField();
            }
        }

        private void showEditField() {
            JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this.list);
            if (viewport == null) return;

            JTextField editField = getEditField();

            viewport.setLayout(null);
            viewport.add(editField);
            viewport.setComponentZOrder(editField, 0);

            java.awt.Rectangle cell = list.getCellBounds(this.index, this.index);

            java.awt.Point p = SwingUtilities.convertPoint(list, cell.getLocation(), viewport);
            editField.setBounds(p.x, p.y, cell.width, cell.height);
        }
        
        private JTextField getEditField() {
            JTextField editField = new JTextField(item.object.getName());

            Runnable commit = () -> {
                System.out.println("Committing name: " + editField.getText());
                SwingUtilities.invokeLater(() -> {
                    item.renameMode = false;
                    item.object.setName(editField.getText());

                    SwingUtilities.invokeLater(() -> {
                        revalidate(); // triggers layout pass
                        repaint();    // paints new child
                    });
                });
            };

            editField.registerKeyboardAction(e -> commit.run(), KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_FOCUSED);
            editField.registerKeyboardAction(e -> commit.run(), KeyStroke.getKeyStroke("ENTER"), JComponent.WHEN_FOCUSED);
            editField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    commit.run();
                }
            });

            SwingUtilities.invokeLater(() -> {
                editField.requestFocusInWindow();
                editField.selectAll();
            });

            return editField;
        }
    }

    class LayerCellRenderer extends JPanel implements ListCellRenderer<ListItem> {
        @Override()
        public Component getListCellRendererComponent(JList<? extends ListItem> list, ListItem value, int index, boolean isSelected, boolean cellHasFocus) {
            return new PanelListItem(value, index, list);
        }
    }
}
