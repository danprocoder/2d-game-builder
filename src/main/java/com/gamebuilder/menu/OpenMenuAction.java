package com.gamebuilder.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import com.gamebuilder.model.ProjectModel;
import com.gamebuilder.service.CanvasService;
import com.gamebuilder.service.OpenProjectService;
import com.gamebuilder.service.SceneService;

public class OpenMenuAction implements ActionListener {
    private ProjectModel projectModel;
    private CanvasService canvasService;
    private SceneService sceneService;

    public OpenMenuAction(CanvasService canvasService, ProjectModel projectModel, SceneService sceneService) {
        this.canvasService = canvasService;
        this.projectModel = projectModel;
        this.sceneService = sceneService;
    }

    @Override()
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            OpenProjectService service = new OpenProjectService(this.canvasService, this.projectModel, this.sceneService);

            String projectDir = fileChooser.getSelectedFile().getPath();
            service.openProject(projectDir);
        }
    }
}
