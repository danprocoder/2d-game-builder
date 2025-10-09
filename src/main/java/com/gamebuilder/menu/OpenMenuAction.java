package com.gamebuilder.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;

import javax.swing.JFileChooser;

import com.gamebuilder.model.AssetModel;
import com.gamebuilder.model.ProjectModel;
import com.gamebuilder.model.ProjectTemplate;

public class OpenMenuAction implements ActionListener {
    public ProjectModel projectModel;

    public OpenMenuAction(ProjectModel projectModel) {
        this.projectModel = projectModel;
    }

    @Override()
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String directory = fileChooser.getSelectedFile().getPath();
            this.openProject(directory);
        }
    }

    private void openProject(String directory) {
        try {
            this.projectModel.loadProject(directory);

            this.loadAssets(directory);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadAssets(String directory) throws Exception {
        AssetModel assetModel = AssetModel.getInstance();
        assetModel.loadFromDirectory(
            Paths.get(directory, ProjectTemplate.getImageDirectory()).toString()
        );
        assetModel.loadFromDirectory(
            Paths.get(directory, ProjectTemplate.getSoundDirectory()).toString()
        );
    }
}
