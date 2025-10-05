package com.gamebuilder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import com.gamebuilder.model.CanvasModel;
import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.model.CanvasModelAddListener;


public class LayersPanel extends JPanel implements CanvasModelAddListener {
    private CanvasModel model;

    private DefaultListModel<CanvasObject> listModel = new DefaultListModel<>();
    private JList<CanvasObject> list = new JList<>(listModel);

    public LayersPanel(GameBuilder gameBuilder, CanvasModel model) {
        this.model = model;
        setPreferredSize(new Dimension(150, 600));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        list.setCellRenderer(new LayerCellRenderer());
        this.add(new JScrollPane(this.list));
    }

    private void refreshObjects() {
        this.listModel.clear();

        ArrayList<CanvasObject> objs = model.getObjects();
        for (int i = objs.size() - 1; i >= 0; i--) this.listModel.addElement(objs.get(i));
    }

    @Override()
    public void onAdd(CanvasObject object, CanvasModel model) {
        Runnable ui = () -> {
            this.refreshObjects();
            this.revalidate(); // triggers layout pass
            this.repaint();    // paints new child
        };
        if (SwingUtilities.isEventDispatchThread()) ui.run();
        else SwingUtilities.invokeLater(ui);
    }

    class LayerCellRenderer extends JPanel implements ListCellRenderer<CanvasObject> {
        private JLabel name = new JLabel();

        @Override()
        public Component getListCellRendererComponent(JList<? extends CanvasObject> list, CanvasObject value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            
            JLabel name = new JLabel();
            name.setText(value.getName());

            panel.add(name);

            return panel;
        }
    }
}
