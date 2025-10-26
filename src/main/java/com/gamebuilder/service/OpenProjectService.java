package com.gamebuilder.service;

import java.nio.file.Paths;

import com.gamebuilder.canvasobject.sprite.Sprite;
import com.gamebuilder.model.AssetModel;
import com.gamebuilder.model.ProjectModel;
import com.gamebuilder.model.ProjectTemplate;
import com.gamebuilder.model.SpriteModel;

public class OpenProjectService {
    private ProjectModel projectModel;
    private CanvasService canvasService;
    private SceneService sceneService;

    public OpenProjectService(
        CanvasService canvasService,
        ProjectModel projectModel,
        SceneService sceneService
    ) {
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

    private void loadObjects(String directory) throws Exception {
        SpriteModel spriteModel = SpriteModel.getInstance();
        spriteModel.loadFromFile(
            Paths.get(directory, ProjectTemplate.getObjectFile()).toString()
        );
        for (Sprite s: spriteModel.getSprites()) {
            this.canvasService.addNewObject(s);
        }
    }

    private void loadAssets(String directory) throws Exception {
        AssetModel assetModel = AssetModel.getInstance();
        assetModel.loadFromFile(
            Paths.get(directory, ProjectTemplate.getAssetFile()).toString()
        );
    }

    private void loadScenes(String directory) throws Exception {
        this.sceneService.loadScenesFromFile(
            Paths.get(directory, ProjectTemplate.getSceneFile()).toString()
        );
    }
}
