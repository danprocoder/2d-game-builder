package com.gamebuilder.service;

import java.nio.file.Paths;
import java.util.ArrayList;

import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.model.AssetModel;
import com.gamebuilder.model.ProjectModel;
import com.gamebuilder.model.ProjectTemplate;

public class OpenProjectService {
    private ProjectModel projectModel;
    private CanvasService canvasService;
    private SceneService sceneService;

    public OpenProjectService(CanvasService canvasService,
            ProjectModel projectModel, SceneService sceneService) {
        this.canvasService = canvasService;
        this.projectModel = projectModel;
        this.sceneService = sceneService;
    }

    public void openProject(String directory) {
        try {
            this.projectModel.loadProject(directory);

            this.loadAssets(directory);

            this.loadObjects(directory);

            this.loadScenes(directory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadObjects(String projectDir) throws Exception {
        ArrayList<CanvasObject> objects = XmlParserService.loadFromFile(
                Paths.get(projectDir, ProjectTemplate.getObjectFile()).toString());

        for (CanvasObject obj: objects) {
            this.canvasService.addNewObject(obj);
        }
    }

    private void loadAssets(String projectDir) throws Exception {
        AssetModel assetModel = AssetModel.getInstance();
        assetModel.loadFromFile(
                projectDir, Paths.get(projectDir, ProjectTemplate.getAssetFile()).toString());
    }

    private void loadScenes(String projectDir) throws Exception {
        this.sceneService.loadScenesFromFile(
                Paths.get(projectDir, ProjectTemplate.getSceneFile()).toString());
    }
}
