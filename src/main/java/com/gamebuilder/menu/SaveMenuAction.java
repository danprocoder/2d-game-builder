package com.gamebuilder.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

import javax.swing.JFileChooser;

import com.gamebuilder.model.Project;
import com.gamebuilder.model.ProjectModel;
import com.gamebuilder.model.ProjectTemplate;


public class SaveMenuAction implements ActionListener {
    ProjectModel projectModel;

    public SaveMenuAction(ProjectModel projectModel) {
        this.projectModel = projectModel;
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
