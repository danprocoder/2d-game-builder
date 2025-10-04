package com.gamebuilder;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameBuilder {
    CanvasArea canvas;

    public GameBuilder() {
        JFrame frame = new JFrame("Game Builder");
        frame.setSize(850, 500);

        frame.setLayout(new BorderLayout());

        frame.add(new ShapePanel(this), BorderLayout.WEST);

        this.canvas = new CanvasArea(this);
        frame.add(this.canvas, BorderLayout.CENTER);

        frame.add(new LayersPanel(this), BorderLayout.EAST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void onToolChanged(String tool) {
        this.canvas.setTool(tool);
    }

    public static void main(String[] args) {
        GameBuilder gameBuilder = new GameBuilder();
    }
}
