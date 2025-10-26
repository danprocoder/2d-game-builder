package com.gamebuilder.view;

import java.awt.datatransfer.DataFlavor;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import com.gamebuilder.model.Asset;
import com.gamebuilder.model.AssetModel;
import com.gamebuilder.model.SceneUpdateListener;
import com.gamebuilder.scene.Scene;
import com.gamebuilder.service.SceneService;

public class ScenePropertyView extends JPanel implements SceneUpdateListener {
    private SceneService sceneService;

    public ScenePropertyView(SceneService sceneService) {
        this.sceneService = sceneService;
        this.sceneService.addUpdateListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.createUI();
    }
    
    @Override()
    public void onSceneUpdate() {
        this.removeAll();
        this.createUI();
        this.revalidate();
        this.repaint();
    }

    private void createUI() {
        Scene scene = this.sceneService.getCurrentScene();
        if (scene == null) {
            this.add(new JLabel("No scene selected"));
            return;
        }
        
        this.add(getBackgroundMusicPanel(scene));
    }

    private JPanel getBackgroundMusicPanel(Scene scene) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("Background Music");
        panel.add(nameLabel);

        Asset bgMusic = scene.getBackgroundMusic();
        if (bgMusic == null) {
            panel.add(new JLabel("No background music set"));
        } else {
            panel.add(new JLabel(bgMusic.getName()));
        }

        panel.setTransferHandler(new TransferHandler() {
            @Override()
            public boolean canImport(TransferSupport support) {
                // TODO: can only import audio assets
                return true;
            }

            @Override()
            public boolean importData(TransferSupport support) {
                try {
                    String assetId = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);

                    Asset asset = AssetModel.getInstance().getById(assetId);
                    if (asset != null) {
                        scene.setBackgroundMusic(asset);
                        ScenePropertyView.this.sceneService.notifyChange();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        return panel;
    }
}
