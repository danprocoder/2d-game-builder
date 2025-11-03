package com.gamebuilder.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import com.gamebuilder.model.CanvasModel;
import com.gamebuilder.CanvasTool;
import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.model.CanvasModelUpdateListener;
import com.gamebuilder.model.SceneUpdateListener;
import com.gamebuilder.scene.Scene;
import com.gamebuilder.service.CanvasService;
import com.gamebuilder.service.SceneService;
import com.gamebuilder.util.Log;


class ListItem {
    CanvasObject object;
    boolean renameMode = false;

    public ListItem(CanvasObject object) {
        this.object = object;
    }
}

public class ObjectListView extends JPanel implements CanvasModelUpdateListener, SceneUpdateListener {
    private DefaultListModel<ListItem> listModel = new DefaultListModel<>();
    private JList<ListItem> list = new JList<>(listModel);
    private CanvasService canvasService;
    private SceneService sceneService;

    public ObjectListView(CanvasService service, SceneService sceneService) {
        this.canvasService = service;
        this.sceneService = sceneService;
        this.sceneService.addUpdateListener(this);
        this.setupCanvasUpdateListeners();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(LEFT_ALIGNMENT);

        list.setAlignmentX(LEFT_ALIGNMENT);
        list.setCellRenderer(new LayerCellRenderer());
        list.setDragEnabled(true);
        list.setDropMode(DropMode.INSERT);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onClick((JList<ListItem>) e.getSource());
            }
        });
        this.list.setTransferHandler(new ObjectListView.ListTransferHandler());
        list.addMouseListener(new MouseAdapter() {
            @Override()
            public void mouseClicked(MouseEvent e) {
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

            @Override()
            public void mousePressed(MouseEvent event) {
                CanvasService service = ObjectListView.this.getService();

                if (event.isPopupTrigger()) {
                    int index = list.locationToIndex(event.getPoint());
                    CanvasObject selectedObject = list.getModel().getElementAt(index).object;
                    service.setActive(selectedObject);
                    service.selectObject(selectedObject, false);
                    service.setSelectedTool(CanvasTool.MOVE_TOOL);

                    JPopupMenu menu = new JPopupMenu();
                    // Delete option
                    menu.add("Delete").addActionListener(ev -> {
                        service.deleteObjectAt(
                            // Subtracting selected index from the last index of the items
                            // will give us the index of the object in the canvas model because
                            // the list is in reversed order
                            list.getModel().getSize() - 1 - index
                        );
                    });

                    menu.show(list, event.getX(), event.getY());
                }
            }
        });
        this.add(new JScrollPane(this.list));
    }

    @Override()
    public void onSceneUpdate() {
        Log.d("ObjectListView.onSceneUpdate()", "Scene updated, refreshing objects");

        // When the scene changes, we need to listen to the canvas model for
        // the new scene so we can display the objects in the new scene

        this.setupCanvasUpdateListeners();

        SwingUtilities.invokeLater(() -> {
            this.refreshObjects();
            this.revalidate(); // triggers layout pass
            this.repaint();    // paints new child
        });
    }

    @Override()
    public void onCanvasModelUpdated(CanvasModel model, String update) {
        Log.d("ObjectListView.onCanvasModelUpdated()", "update = " + update);
        SwingUtilities.invokeLater(() -> {
            if (update.equals("add_object")
                    || update.equals("delete_object_at")
                    || update.equals("move_object_to_position")) {
                this.refreshObjects();
            }
            this.revalidate(); // triggers layout pass
            this.repaint();    // paints new child
        });
    }

    private void setupCanvasUpdateListeners() {
        // TODO: refactor so that it's more readable and maintainable
        this.canvasService.removeUpdateListener(this);
        for (Scene scene : this.sceneService.getScenes()) {
            scene.getCanvasService().removeUpdateListener(this);
        }
        Log.v("ObjectListView.setupUpdateListeners()", "Adding update listener to canvas service");
        this.getService().addUpdateListener(this);
    }

    // TODO: refactor to avoid code duplication with other views
    private CanvasService getService() {
        Scene scene = this.sceneService.getActiveScene();
        if (scene != null) {
            return scene.getCanvasService();
        }
        return this.canvasService;
    }

    private void onClick(JList<ListItem> source) {
        ListItem selectedItem = source.getSelectedValue();
        if (selectedItem != null && !selectedItem.renameMode) {
            this.getService().setActive(selectedItem.object);
            this.getService().selectObject(selectedItem.object, false);
            this.getService().setSelectedTool(CanvasTool.MOVE_TOOL);
        }
    }

    private void refreshObjects() {
        this.listModel.clear();

        ArrayList<CanvasObject> objs = this.getService().getObjects();
        Log.d("ObjectListView.refreshObjects()", "Refreshing object list with " + objs.size() + " objects");
        for (int i = objs.size() - 1; i >= 0; i--) {
            this.listModel.addElement(new ListItem(objs.get(i)));
        }
    }

    /**
     * JPanel class representing a single item in the object list view.
     */
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
            add(name);

            if (item.object.isActive()) {
                setBackground(Color.BLUE);
                name.setForeground(Color.WHITE);
            }

            if (item.renameMode) {
                showEditField();
            }
        }

        private void showEditField() {
            JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this.list);
            if (viewport == null) return;

            JTextField editField = getEditField();

            LayoutManager oldLayout = viewport.getLayout();
            viewport.setLayout(null);

            java.awt.Rectangle cell = list.getCellBounds(this.index, this.index);

            java.awt.Point p = SwingUtilities.convertPoint(list, cell.getLocation(), viewport);

            editField.setBounds(p.x, p.y, cell.width, cell.height);
            viewport.add(editField);
            viewport.setComponentZOrder(editField, 0);

            viewport.setLayout(oldLayout);
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
                        refreshObjects();
                    });
                });
            };

            editField.registerKeyboardAction(e -> commit.run(), KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_FOCUSED);
            editField.registerKeyboardAction(e -> commit.run(), KeyStroke.getKeyStroke("ENTER"), JComponent.WHEN_FOCUSED);
            editField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
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

    /** Handles drag & drop operation on this list. */
    class ListTransferHandler extends TransferHandler {
        @Override()
        public boolean canImport(TransferSupport support) {
            // TODO: check that it is the correct dataflavor
            return true;
        }

        @Override()
        public boolean importData(TransferSupport support) {
            Log.v("ObjectListView.importData()", "Import data called");
            try {
                // TODO: implement custom dataflavor
                int draggedIndex = Integer.parseInt((String) support.getTransferable().getTransferData(DataFlavor.stringFlavor));

                JList.DropLocation dropLocation = (JList.DropLocation) support.getDropLocation();
                int size = getService().getObjects().size();
                int d = dropLocation.getIndex();

                Log.d("ObjectListView.importData()", "Original canvas_from=" + draggedIndex + " list_to=" + d);
                int dropIndex = size - d;
                if (draggedIndex > dropIndex) {
                    if (dropIndex > size - 1) {
                        dropIndex = size - 1;
                    }
                } else {
                    dropIndex = dropIndex - 1;
                }

                if (draggedIndex != dropIndex) {
                    getService().moveObjectToPosition(draggedIndex, dropIndex);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override()
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override()
        public Transferable createTransferable(JComponent c) {
            JList<ListItem> source = (JList<ListItem>) c;
            ListItem selectedItem = source.getSelectedValue();
            if (selectedItem != null) {
                Log.d("ObjectListView.createTransferable()", "Creating transferable for item: " + selectedItem);
                return new ListTransferHandler.ObjectTransferable(selectedItem.object);
            }
            Log.d("ObjectListView.createTransferable()", "No valid selection");
            return null;
        }

        @Override()
        public void exportDone(JComponent source, Transferable data, int action) {
            super.exportDone(source, data, action);
        }

        class ObjectTransferable implements Transferable {
            private int index;

            public ObjectTransferable(CanvasObject object) {
                Log.v("new ObjectTransferable()", "Creating transferable for object: " + object.getName());
                this.index = getService().getObjects().indexOf(object);
            }

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] { DataFlavor.stringFlavor };
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.stringFlavor.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) {
                return String.valueOf(index);
            }
        }
    }
}
