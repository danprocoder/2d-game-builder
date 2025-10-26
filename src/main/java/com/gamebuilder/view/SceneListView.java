package com.gamebuilder.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;

import com.gamebuilder.model.SceneUpdateListener;
import com.gamebuilder.scene.Scene;
import com.gamebuilder.service.SceneService;


public class SceneListView extends JPanel implements SceneUpdateListener {
    SceneService sceneService;
    DefaultListModel<Scene> model = new DefaultListModel<>();
    JList<Scene> list = new JList<>();

    public SceneListView(SceneService sceneService) {
        this.sceneService = sceneService;
        this.sceneService.addUpdateListener(this);

        // TODO: initialize model with existing scenes

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Scenes");
        Font titleFont = title.getFont();
        title.setFont(titleFont.deriveFont(titleFont.getStyle() | Font.BOLD));
        add(title);

        list.setModel(model);
        list.setCellRenderer(new SceneListView.SceneListItemRenderer());
        list.addMouseListener(new SceneListView.MouseClickListener());
        add(list);
    }

    @Override()
    public void onSceneUpdate() {
        model.clear();

        for (Scene scene: this.sceneService.getScenes()) {
            model.addElement(scene);
        }

        list.revalidate();
        list.repaint();
    }

    class MouseClickListener extends MouseAdapter {
        @Override()
        public void mouseClicked(MouseEvent e) {
            JList<Scene> source = (JList<Scene>) e.getSource();
            int index = source.locationToIndex(e.getPoint());
            Scene selectedScene = source.getModel().getElementAt(index);

            sceneService.setActive(selectedScene);
        }

        @Override()
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                JList<Scene> source = (JList<Scene>) e.getSource();
                int index = source.locationToIndex(e.getPoint());
                Scene selectedScene = source.getModel().getElementAt(index);
                SceneListView.ContextMenu menu = new SceneListView.ContextMenu(selectedScene);
                menu.show(source, e.getX(), e.getY());
            }
        }
    }

    class ContextMenu extends JPopupMenu {
        private Scene scene;

        public ContextMenu(Scene scene) {
            this.scene = scene;

            add(new JMenuItem("Rename Scene"));
            add(new JMenuItem("Delete Scene"));
            add(new JMenuItem("Set Properties"));
        }
    }

    class SceneListItemRenderer implements ListCellRenderer<Scene> {
        @Override()
        public Component getListCellRendererComponent(
                JList<? extends Scene> list,
                Scene value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.setOpaque(true); // Make sure background is visible
            
            JLabel label = new JLabel(value.getName());
            panel.add(label);
            
            if (list.getModel().getElementAt(index).isActive()) {
                panel.setBackground(Color.BLUE);
                label.setForeground(Color.WHITE);
            } else {
                panel.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }

            return panel;
        }
    }
}
