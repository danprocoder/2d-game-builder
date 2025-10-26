package com.gamebuilder.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.gamebuilder.scene.Scene;
import com.gamebuilder.service.SceneService;

public class NewSceneMenuAction implements ActionListener {
    private SceneService sceneService;

    public NewSceneMenuAction(SceneService sceneService) {
        this.sceneService = sceneService;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = "Scene " + (this.sceneService.getSceneCount() + 1);
        this.sceneService.addScene(new Scene(name));
    }
}
