package com.gamebuilder.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.gamebuilder.service.CanvasService;
import com.gamebuilder.service.RunProjectService;
import com.gamebuilder.service.SceneService;

public class RunGameAction implements ActionListener {
    private SceneService sceneService;

    public RunGameAction(CanvasService canvasService, SceneService sceneService) {
        this.sceneService = sceneService;
    }

    @Override()
    public void actionPerformed(ActionEvent event) {
        RunProjectService.getInstance().start(this.sceneService);
    }
}
