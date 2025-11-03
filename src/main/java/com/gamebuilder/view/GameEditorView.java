package com.gamebuilder.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import com.gamebuilder.ShapePanel;
import com.gamebuilder.model.AssetModel;
import com.gamebuilder.model.ProjectModel;
import com.gamebuilder.service.CanvasService;
import com.gamebuilder.service.SceneService;

public class GameEditorView extends JPanel {
    private CanvasService canvasService;
    private ProjectModel projectModel;
    private AssetModel assetModel;
    private SceneService sceneService;

    public GameEditorView(ProjectModel projectModel, AssetModel assetModel, SceneService sceneService, CanvasService canvasService) {
        this.projectModel = projectModel;
        this.assetModel = assetModel;
        this.sceneService = sceneService;
        this.canvasService = canvasService;
        createUI();
    }

    private void createUI() {
        setLayout(new BorderLayout()); // Set layout before adding components
        
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        mainSplitPane.add(createLeftPanel());

        mainSplitPane.add(createCenterPanel());

        add(mainSplitPane, BorderLayout.CENTER);
    }

    private JSplitPane createLeftPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Shapes", new ShapePanel(this.canvasService, this.sceneService));
        tabbedPane.addTab("Assets", new AssetsPanelView(this.projectModel, this.assetModel));

        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplitPane.add(tabbedPane);
        leftSplitPane.add(new SceneListView(this.sceneService));
        leftSplitPane.setDividerLocation(300);

        return leftSplitPane;
    }

    private JSplitPane createCenterPanel() {
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplitPane.setDividerLocation(600); // Negative value positions from the right (200px from right edge)
        centerSplitPane.add(new CanvasView(this.canvasService, this.sceneService));
        centerSplitPane.add(createRightPanel());
        return centerSplitPane;
    }

    private JTabbedPane createRightPanel() {
        JTabbedPane rightTabbedPane = new JTabbedPane();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        ObjectListView layersPanel = new ObjectListView(this.canvasService, this.sceneService);
        this.canvasService.addUpdateListener(layersPanel);
        splitPane.add(layersPanel);

        ObjectSettingsView objectSettingsView = new ObjectSettingsView(this.canvasService, this.sceneService);
        splitPane.add(objectSettingsView);

        rightTabbedPane.addTab("Objects", splitPane);
        
        rightTabbedPane.addTab("Scene Properties", new ScenePropertyView(this.sceneService));

        return rightTabbedPane;
    }
}
