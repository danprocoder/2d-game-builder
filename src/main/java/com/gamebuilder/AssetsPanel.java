package com.gamebuilder;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import com.gamebuilder.model.Asset;
import com.gamebuilder.model.AssetModel;
import com.gamebuilder.model.AssetModelUpdateListener;
import com.gamebuilder.model.Project;
import com.gamebuilder.model.ProjectModel;
import com.gamebuilder.model.ProjectTemplate;

public class AssetsPanel extends JPanel implements AssetModelUpdateListener {
    ProjectModel projectModel;
    AssetModel assetModel;

    public AssetsPanel(ProjectModel projectModel, AssetModel assetModel) {
        this.projectModel = projectModel;
        this.assetModel = assetModel;
        this.assetModel.addUpdateListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.showView();
    }

    @Override
    public void onAssetModelUpdated() {
        this.showView();
    }

    private void showView() {
        this.removeAll();
        this.add(new JLabel("Assets"));

        JButton importBtn = new JButton("Import Asset");
        importBtn.addActionListener(e -> importAsset());
        this.add(importBtn);

        for (Asset asset: this.assetModel.getAssets()) {
            try {
                add(createAssetListItem(asset));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Create the list item view for an asset */
    private JPanel createAssetListItem(Asset asset) throws IOException {
        File path = asset.getPath();
        JPanel listItem = new JPanel();

        listItem.setTransferHandler(new TransferHandler() {
            @Override public int getSourceActions(JComponent c) { return COPY; }

            @Override
            public Transferable createTransferable(JComponent c) {
                return new StringSelection(path.toString());
            }

            @Override
            protected void exportDone(JComponent source, Transferable data, int action) {
                super.exportDone(source, data, action);
            }
        });

        listItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TransferHandler handler = listItem.getTransferHandler();
                handler.exportAsDrag(listItem, e, TransferHandler.COPY);
            }
        });

        BufferedImage img = ImageIO.read(path);
        listItem.add(new JLabel(new ImageIcon(img.getScaledInstance(32, 32, 0))));
        listItem.add(new JLabel(path.getName()));

        return listItem;
    }

    /** Shows a file chooser dialog to import a new asset */
    private void importAsset() {
        Project project = projectModel.getProject();
        if (project == null) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                Path assetsDir = Paths.get(project.getDirectory(), ProjectTemplate.getImageDirectory());
                if (!Files.exists(assetsDir)) {
                    Files.createDirectories(assetsDir);
                }
                
                Path dest = Paths.get(assetsDir.toString(), selectedFile.getName());
                Files.copy(selectedFile.toPath(), dest);

                this.assetModel.addAsset(new Asset(dest.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
