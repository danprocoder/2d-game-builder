package com.gamebuilder.view;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.gamebuilder.model.Asset;
import com.gamebuilder.model.AssetModel;
import com.gamebuilder.model.AssetModelUpdateListener;
import com.gamebuilder.model.Project;
import com.gamebuilder.model.ProjectModel;
import com.gamebuilder.model.ProjectTemplate;
import com.gamebuilder.view.dnd.ImageAssetFlavor;
import com.gamebuilder.view.dnd.SoundAssetFlavor;

public class AssetsPanelView extends JPanel implements AssetModelUpdateListener {
    ProjectModel projectModel;
    AssetModel assetModel;

    public AssetsPanelView(ProjectModel projectModel, AssetModel assetModel) {
        this.projectModel = projectModel;
        this.assetModel = assetModel;
        this.assetModel.addUpdateListener(this);

        this.setLayout(new BorderLayout());

        this.showView();
    }

    @Override()
    public void onAssetModelUpdated() {
        this.showView();
    }

    private void showView() {
        // TODO: find a way to do this without removing all and repainting
        this.removeAll();

        JButton importBtn = new JButton("Import File");
        importBtn.addActionListener(e -> importAsset());
        this.add(importBtn, BorderLayout.NORTH);

        JTree assetTree = new JTree(this.getTreeModel());
        assetTree.setTransferHandler(new AssetsPanelView.AssetTransferHandler());
        
        // Add mouse listener to initiate drag operations
        assetTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Check if we're clicking on a leaf node (actual asset)
                int row = assetTree.getRowForLocation(e.getX(), e.getY());
                if (row != -1) {
                    assetTree.setSelectionRow(row);
                    // INSERT_YOUR_CODE
                    TreePath path = assetTree.getPathForRow(row);
                    if (path == null) return;
                    Object nodeObj = path.getLastPathComponent();
                    if (nodeObj instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodeObj;
                        if (!node.isLeaf() || node.getLevel() != 2) {
                            return;
                        }
                        
                        TransferHandler handler = assetTree.getTransferHandler();
                        handler.exportAsDrag(assetTree, e, TransferHandler.COPY);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(assetTree);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public TreeModel getTreeModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Assets");

        DefaultMutableTreeNode images = new DefaultMutableTreeNode("Images");

        DefaultMutableTreeNode sounds = new DefaultMutableTreeNode("Sounds");

        DefaultMutableTreeNode objects = new DefaultMutableTreeNode("Objects");

        for (Asset asset: this.assetModel.getAssets()) {
            if (asset.getType().equals("image")) {
                images.add(new AssetTreeLeaf(asset));
            } else if (asset.getType().equals("audio")) {
                sounds.add(new AssetTreeLeaf(asset));
            }
        }

        // TODO: don't show empty folder
        root.add(images);
        root.add(sounds);
        root.add(objects);

        return new DefaultTreeModel(root);
    }

    /** Shows a file chooser dialog to import a new asset */
    private void importAsset() {
        Project project = projectModel.getProject();
        if (project == null) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            // TODO: asset should save maybe in RAM until project is saved.
            File selectedFile = fileChooser.getSelectedFile();
            try {
                Path assetsDir = Paths.get(project.getDirectory(), ProjectTemplate.getImageDirectory());
                if (!Files.exists(assetsDir)) {
                    Files.createDirectories(assetsDir);
                }

                String relativePath = Paths.get(ProjectTemplate.getImageDirectory(), selectedFile.getName()).toString();
                
                Path dest = Paths.get(assetsDir.toString(), selectedFile.getName());
                Files.copy(selectedFile.toPath(), dest);

                this.assetModel.addAsset(new Asset(relativePath, dest.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class AssetTreeLeaf extends DefaultMutableTreeNode {
        private Asset asset;

        public AssetTreeLeaf(Asset asset) {
            super(asset);
            this.asset = asset;
        }

        @Override()
        public String toString() {
            return this.asset.getName();
        }
    }

    class AssetTransferHandler extends TransferHandler {
        @Override()
        public int getSourceActions(JComponent c) {
            return COPY;
        }

        @Override()
        public Transferable createTransferable(JComponent c) {
            JTree tree = (JTree) c;

            TreePath path = tree.getSelectionPath();
            DefaultMutableTreeNode leaf = (DefaultMutableTreeNode) path.getLastPathComponent();
            Asset asset = null;
            if (leaf.getUserObject() instanceof Asset) {
                asset = (Asset) leaf.getUserObject();
            }

            if (asset == null) {
                return null;
            }

            DataFlavor flavor = null;
            if (asset.getType().equals("audio")) {
                flavor = new SoundAssetFlavor();
            } else if (asset.getType().equals("image")) {
                flavor = new ImageAssetFlavor();
            }
            if (flavor == null) {
                return null;
            }
            final DataFlavor finalFlavor = flavor;

            final Asset payload = asset;
            return new Transferable() {
                @Override()
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[] { finalFlavor };
                }

                @Override()
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return flavor.equals(finalFlavor);
                }

                @Override()
                public Object getTransferData(DataFlavor flavor) {
                    return payload;
                }
            };
        }
    }
}
