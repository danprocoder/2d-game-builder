package com.gamebuilder;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.gamebuilder.model.AssetModel;
import com.gamebuilder.model.CanvasModel;
import com.gamebuilder.model.Project;
import com.gamebuilder.model.ProjectModel;
import com.gamebuilder.model.ProjectModelUpdateListener;
import com.gamebuilder.model.SceneModel;
import com.gamebuilder.service.CanvasService;
import com.gamebuilder.service.SceneService;
import com.gamebuilder.util.LogThread;
import com.gamebuilder.view.CanvasView;
import com.gamebuilder.view.GameEditorView;
import com.gamebuilder.view.ToolBarView;
import com.gamebuilder.view.menubar.MenuBarView;

public class GameBuilder implements ProjectModelUpdateListener {
    CanvasView canvas;
    private CanvasModel model;
    private CanvasService canvasService;
    private SceneService sceneService;
    private ProjectModel projectModel;
    private AssetModel assetModel;
    private JFrame frame;

    public GameBuilder() {
        LogThread.getInstance().start();
        
        this.model = new CanvasModel();
        this.canvasService = new CanvasService(this.model);
        this.sceneService = new SceneService(new SceneModel());

        this.assetModel = AssetModel.getInstance();

        this.projectModel = new ProjectModel();
        this.projectModel.addUpdateListener(this);

        this.frame = new JFrame("Game Builder");
        frame.setSize(1024, 700);
        frame.setLayout(new BorderLayout());

        frame.setJMenuBar(
            new MenuBarView(
                this.frame,
                this.canvasService,
                this.sceneService,
                this.projectModel
            )
        );

        frame.add(new ToolBarView(this.sceneService), BorderLayout.NORTH);

        frame.add(
            new GameEditorView(
                this.projectModel,
                this.assetModel,
                this.sceneService,
                this.canvasService
            ),
            BorderLayout.CENTER
        );

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override()
    public void onProjectModelUpdate() {
        Project project = this.projectModel.getProject();
        this.frame.setTitle(project.getName() + (project.isDirty() ? "*" : "") + " - Game Builder");
    }

    public CanvasModel getModel() {
        return this.model;
    }

    public static void main(String[] args) {
        new GameBuilder();
    }
}
