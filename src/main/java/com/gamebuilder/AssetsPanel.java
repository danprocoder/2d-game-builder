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

    @Override()
    public void onAssetModelUpdated() {
        this.showView();
    }

    private void showView() {
        this.removeAll();

        JButton importBtn = new JButton("Import File");
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
        System.out.println("Creating list item for asset: " + asset.getPath());
        File path = new File(asset.getPath());
        JPanel listItem = new JPanel();
        listItem.setLayout(new BoxLayout(listItem, BoxLayout.X_AXIS));

        listItem.setTransferHandler(new TransferHandler() {
            @Override public int getSourceActions(JComponent c) { return COPY; }

            @Override
            public Transferable createTransferable(JComponent c) {
                return new StringSelection(asset.getId());
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

        if (asset.getType().equals("image")) {
            BufferedImage img = ImageIO.read(path);
            JLabel thumbnail = new JLabel(new ImageIcon(img.getScaledInstance(32, 32, 0)));
            listItem.add(thumbnail);
        } else if (asset.getType().equals("audio")) {
            // Create an audio thumbnail
            JLabel audioLabel = new JLabel("[AUDIO]");
            listItem.add(audioLabel);
        }

        JLabel nameLabel = new JLabel(path.getName());
        listItem.add(nameLabel);

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
            // TODO: asset should save maybe in RAM until project is saved.
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
