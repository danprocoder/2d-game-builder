package com.gamebuilder.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;

import com.gamebuilder.model.Asset;
import com.gamebuilder.model.AssetModel;
import com.gamebuilder.model.Project;
import com.gamebuilder.model.ProjectModel;
import com.gamebuilder.model.ProjectTemplate;
import com.gamebuilder.model.SpriteModel;
import com.gamebuilder.scene.Scene;
import com.gamebuilder.service.CanvasService;
import com.gamebuilder.service.SceneService;


public class SaveMenuAction implements ActionListener {
    ProjectModel projectModel;
    CanvasService canvasService;
    SceneService sceneService;

    public SaveMenuAction(ProjectModel projectModel, CanvasService canvasService, SceneService sceneService) {
        this.projectModel = projectModel;
        this.canvasService = canvasService;
        this.sceneService = sceneService;
    }

    @Override()
    public void actionPerformed(ActionEvent e) {
        Project project = this.projectModel.getProject();
        if (project != null) {
            this.saveProject(project);
        }
    }

    private void saveProject(Project project) {
        String directory = project.getDirectory();
        if (directory != null) {
            if (new File(directory).exists()) {
                try {
                    this.saveAssetFile(directory);
                    this.saveObjectFile(directory);
                    this.saveSceneFile(directory);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                project.setDirty(false);
            } else {
                // TODO: show unable to save alert
            }
        } else {
            this.showSaveDialog(project);
        }
    }

    private void showSaveDialog(Project project) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a folder to create the project in");
        fileChooser.setApproveButtonText("Use This Folder");
        fileChooser.setName(project.getName());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int option = fileChooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            String path = Paths.get(fileChooser.getSelectedFile().getAbsolutePath(), project.getName()).toString();
            if (new File(path).exists()) {
                // TODO: warn the user about possible file replacement
            } else {
                this.createFolderStructure(path);

                project.setDirectory(path);
                project.setDirty(false);
            }
        }
    }

    private void saveObjectFile(String projectDirectory) throws IOException {
        SpriteModel spriteModel = SpriteModel.getInstance();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        xml.append("\n<Object>");

        for (var object: canvasService.getObjects()) {
            xml.append(object.toXml());
        }

        for (var sprite: spriteModel.getSprites()) {
            xml.append(sprite.toXml());
        }

        xml.append("\n</Object>");

        Path objectFile = Paths.get(projectDirectory, ProjectTemplate.getObjectFile());
        PrintWriter out = new PrintWriter(objectFile.toFile(), "UTF-8");
        out.println(xml.toString());
        out.close();
    }

    private void saveAssetFile(String projectDirectory) throws IOException {
        AssetModel assetModel = AssetModel.getInstance();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        xml.append("\n<Assets>");
        for (Asset a: assetModel.getAssets()) {
            xml.append(a.toXml());
        }
        xml.append("\n</Assets>");

        Path assetFile = Paths.get(projectDirectory, ProjectTemplate.getAssetFile());
        PrintWriter out = new PrintWriter(assetFile.toFile(), "UTF-8");
        out.println(xml.toString());
        out.close();
    }
    
    private void saveSceneFile(String projectDirectory) throws IOException {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        xml.append("\n<Scenes>");
        for (Scene s: this.sceneService.getScenes()) {
            xml.append(s.toXml());
        }
        xml.append("\n</Scenes>");

        Path sceneFile = Paths.get(projectDirectory, ProjectTemplate.getSceneFile());
        PrintWriter out = new PrintWriter(sceneFile.toFile(), "UTF-8");
        out.println(xml.toString());
        out.close();
    }

    private void createFolderStructure(String path) {
        for (String dir: ProjectTemplate.getDefaultDirectories()) {
            Paths.get(path, dir).toFile().mkdirs();
        }

        for (String file: ProjectTemplate.getDefaultFiles()) {
            try {
                Paths.get(path, file).toFile().createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        try {
            File projectFile = Paths.get(path, ProjectTemplate.getProjectFile()).toFile();
            PrintWriter out = new PrintWriter(projectFile, "UTF-8");
            out.println("<project>");

            Project project = this.projectModel.getProject();
            out.println("    <name>" + project.getName() + "</name>");

            out.println("    <version>1.0</version>");
            out.println("</project>");
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
