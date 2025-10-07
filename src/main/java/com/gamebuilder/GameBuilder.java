package com.gamebuilder;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.gamebuilder.model.CanvasModel;

class Assets extends JPanel {

}

public class GameBuilder {
    CanvasArea canvas;
    private CanvasModel model;

    public GameBuilder() {
        this.model = new CanvasModel();

        JFrame frame = new JFrame("Game Builder");
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
        frame.add(layersPanel, BorderLayout.EAST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JMenuBar getMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem("New"));
        fileMenu.add(new JMenuItem("Open"));
        fileMenu.add(new JMenuItem("Save"));

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> this.exit());
        fileMenu.add(exit);
        menuBar.add(fileMenu);

        // JMenu editMenu = new JMenu("Edit");
        // editMenu.add(new JMenuItem("Undo"));
        // editMenu.add(new JMenuItem("Redo"));
        // menuBar.add(editMenu);

        return menuBar;
    }

    private void exit() {
        System.exit(0);
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
