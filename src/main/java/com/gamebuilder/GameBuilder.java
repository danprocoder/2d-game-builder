package com.gamebuilder;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.gamebuilder.menu.NewMenuAction;
import com.gamebuilder.menu.OpenMenuAction;
import com.gamebuilder.menu.SaveMenuAction;
import com.gamebuilder.model.CanvasModel;
import com.gamebuilder.model.Project;
import com.gamebuilder.model.ProjectModel;
import com.gamebuilder.model.ProjectModelUpdateListener;

class Assets extends JPanel {

}

public class GameBuilder implements ProjectModelUpdateListener {
    CanvasArea canvas;
    private CanvasModel model;
    private ProjectModel projectModel;
    private JFrame frame;

    public GameBuilder() {
        this.model = new CanvasModel();

        this.projectModel = new ProjectModel(this);
        this.projectModel.addUpdateListener(this);

        this.frame = new JFrame("Game Builder");
        frame.setSize(850, 500);
        frame.setLayout(new BorderLayout());

        frame.setJMenuBar(this.getMenuBar());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Shapes", new ShapePanel(this));
        tabbedPane.addTab("Assets", new Assets());

        frame.add(tabbedPane, BorderLayout.WEST);

        this.canvas = new CanvasArea(this, model);
        frame.add(this.canvas, BorderLayout.CENTER);

        LayersPanel layersPanel = new LayersPanel(this, model);
        model.addOnAddListener(layersPanel);
        model.addUpdateListener(layersPanel);
        frame.add(layersPanel, BorderLayout.EAST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JMenuBar getMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem newProject = new JMenuItem("New");
        newProject.addActionListener(new NewMenuAction(this.projectModel));
        fileMenu.add(newProject);

        JMenuItem openProject = new JMenuItem("Open");
        openProject.addActionListener(new OpenMenuAction());
        fileMenu.add(openProject);

        JMenuItem saveProject = new JMenuItem("Save");
        saveProject.addActionListener(new SaveMenuAction(this.projectModel));
        fileMenu.add(saveProject);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        fileMenu.add(exit);
        menuBar.add(fileMenu);

        // JMenu editMenu = new JMenu("Edit");
        // editMenu.add(new JMenuItem("Undo"));
        // editMenu.add(new JMenuItem("Redo"));
        // menuBar.add(editMenu);

        return menuBar;
    }

    @Override()
    public void onProjectModelUpdate() {
        Project project = this.projectModel.getProject();
        this.frame.setTitle(project.getName() + (project.isDirty() ? "*" : "") + " - Game Builder");
    }

    public void onToolChanged(String tool) {
        this.canvas.setTool(tool);
    }

    public CanvasModel getModel() {
        return this.model;
    }

    public static void main(String[] args) {
        new GameBuilder();
    }
}
