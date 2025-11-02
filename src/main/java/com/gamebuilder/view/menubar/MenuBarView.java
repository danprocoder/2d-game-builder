package com.gamebuilder.view.menubar;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.gamebuilder.menu.NewMenuAction;
import com.gamebuilder.menu.NewSceneMenuAction;
import com.gamebuilder.menu.NewSpriteAction;
import com.gamebuilder.menu.OpenMenuAction;
import com.gamebuilder.menu.RunGameAction;
import com.gamebuilder.menu.SaveMenuAction;
import com.gamebuilder.model.ProjectModel;
import com.gamebuilder.service.CanvasService;
import com.gamebuilder.service.SceneService;
import com.gamebuilder.view.events.EventMapView;

public class MenuBarView extends JMenuBar {
    private JFrame frame;
    private CanvasService canvasService;
    private SceneService sceneService;
    private ProjectModel projectModel;

    public MenuBarView(JFrame frame, CanvasService canvasService,
            SceneService sceneService, ProjectModel projectModel) {
        this.frame = frame;
        this.canvasService = canvasService;
        this.sceneService = sceneService;
        this.projectModel = projectModel;

        this.initialize();
    }

    public void initialize() {
        JMenu projectMenu = new JMenu("Project");

        JMenuItem newProject = new JMenuItem("New");
        newProject.addActionListener(new NewMenuAction(this.projectModel));
        projectMenu.add(newProject);

        JMenuItem openProject = new JMenuItem("Open");
        openProject.addActionListener(
            new OpenMenuAction(this.canvasService, this.projectModel, this.sceneService)
        );
        projectMenu.add(openProject);

        JMenuItem saveProject = new JMenuItem("Save");
        saveProject.addActionListener(
            new SaveMenuAction(this.projectModel, this.canvasService, this.sceneService)
        );
        projectMenu.add(saveProject);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        projectMenu.add(exit);
        this.add(projectMenu);

        JMenu editMenu = new JMenu("Edit");
        editMenu.add(new JMenuItem("Undo"));
        editMenu.add(new JMenuItem("Redo"));
        this.add(editMenu);

        JMenu viewMenu = new JMenu("View");
        JMenuItem fabs = new JMenuItem("Fabs");
        fabs.addActionListener(e -> {
            sceneService.showFabsView();
        });
        viewMenu.add(fabs);

        JMenu sceneMenu = new JMenu("Scenes");
        JMenuItem newScene = new JMenuItem("New Scene");
        newScene.addActionListener(new NewSceneMenuAction(this.sceneService));
        sceneMenu.add(newScene);
        viewMenu.add(sceneMenu);
        this.add(viewMenu);

        JMenu spriteMenu = new JMenu("Object");
        JMenuItem newSprite = new JMenuItem("New Object");
        newSprite.addActionListener(new NewSpriteAction(this.canvasService));
        spriteMenu.add(newSprite);
        this.add(spriteMenu);

        JMenu eventMenu = new JMenu("Events");
        JMenuItem newEvent = new JMenuItem("New Event");
        newEvent.addActionListener(e -> {
            JDialog dialog = new JDialog(this.frame, "Event Mapper", true);
            dialog.setSize(850, 400);
            dialog.setContentPane(new EventMapView(dialog, canvasService, sceneService));
            dialog.setLocationRelativeTo(this.frame);
            dialog.setVisible(true);
        });
        eventMenu.add(newEvent);
        this.add(eventMenu);

        JMenu toolsMenu = new JMenu("Tools");

        JMenuItem runMenu = new JMenuItem("Run");
        runMenu.addActionListener(new RunGameAction(this.canvasService, this.sceneService));
        toolsMenu.add(runMenu);

        JMenuItem viewLogs = new JMenuItem("View Logs");
        toolsMenu.add(viewLogs);

        this.add(toolsMenu);
    }
}
