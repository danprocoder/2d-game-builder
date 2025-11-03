package com.gamebuilder.view.events;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.scene.Scene;
import com.gamebuilder.service.CanvasService;
import com.gamebuilder.service.SceneService;

public class EventLeftTreeView extends JPanel {
    private EventService eventService;
    private CanvasService canvasService;
    private SceneService sceneService;
    private JDialog parentDialog;

    public EventLeftTreeView(
                JDialog parentDialog, EventService eventService,
                CanvasService canvasService, SceneService sceneService) {
        this.parentDialog = parentDialog;
        this.eventService = eventService;
        this.canvasService = canvasService;
        this.sceneService = sceneService;

        setLayout(new BorderLayout());

        JButton addButton = new JButton("Add Event");
        addButton.addActionListener((e) -> {
            JDialog dialog = new JDialog(this.parentDialog);
            dialog.setTitle("Select Event");
            dialog.setSize(400, 300);

            dialog.getContentPane().add(
                new SelectEventDialogView(new EventSelectedListener() {
                    @Override()
                    public void onEventSelected(String eventType) {
                        dialog.setVisible(true);
                        eventService.notifyUpdate(eventType);
                        System.out.println("Selected Event: " + eventType);
                    }

                    @Override()
                    public void onObjectSelected(Object object) {}
                })
            );

            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });
        add(addButton, BorderLayout.SOUTH);

        JTree tree = new JTree(createSimpleTreeModel());
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override()
            public void valueChanged(TreeSelectionEvent event) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }

                Object selected = node.getUserObject();
                eventService.notifySelected(selected);
            }
        });
        add(tree, BorderLayout.CENTER);
    }

    public TreeModel createSimpleTreeModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Event Map");

        DefaultMutableTreeNode entities = new DefaultMutableTreeNode("Entities");
        root.add(entities);

        for (CanvasObject object: this.canvasService.getObjects()) {
            entities.add(new CanvasObjectNode(object));
        }

        DefaultMutableTreeNode scenesGroup = new DefaultMutableTreeNode("Scenes");
        root.add(scenesGroup);
        for (Scene scene: this.sceneService.getScenes()) {
            DefaultMutableTreeNode sceneRoot = new SceneNode(scene);

            for (CanvasObject object: scene.getCanvasService().getObjects()) {
                sceneRoot.add(new CanvasObjectNode(object));
            }

            scenesGroup.add(sceneRoot);
        }

        return new DefaultTreeModel(root);
    }

    class SceneNode extends DefaultMutableTreeNode {
        private Scene scene;

        public SceneNode(Scene scene) {
            super(scene);

            this.scene = scene;
        }

        @Override()
        public String toString() {
            return this.scene.getName();
        }
    }

    class CanvasObjectNode extends DefaultMutableTreeNode {
        private CanvasObject canvasObject;

        public CanvasObjectNode(CanvasObject canvasObject) {
            super(canvasObject);

            this.canvasObject = canvasObject;
        }

        @Override()
        public String toString() {
            return this.canvasObject.getName();
        }
    }
}
