package com.gamebuilder;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.gamebuilder.model.CanvasModel;

public class GameBuilder {
    CanvasArea canvas;

    public GameBuilder() {
        JFrame frame = new JFrame("Game Builder");
        frame.setSize(850, 500);

        frame.setLayout(new BorderLayout());

        frame.add(new ShapePanel(this), BorderLayout.WEST);

        CanvasModel model = new CanvasModel();

        this.canvas = new CanvasArea(this, model);
        frame.add(this.canvas, BorderLayout.CENTER);

        LayersPanel layersPanel = new LayersPanel(this, model);
        frame.add(layersPanel, BorderLayout.EAST);
        model.addOnAddListener(layersPanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void onToolChanged(String tool) {
        this.canvas.setTool(tool);
    }

    public static void main(String[] args) {
        GameBuilder gameBuilder = new GameBuilder();
    }
}
