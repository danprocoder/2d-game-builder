package com.gamebuilder;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.gamebuilder.GameBuilder;

public class ShapePanel extends JPanel {
    final static String MOVE_TOOL = "move";
    final static String HAND_TOOL = "hand";
    final static String POLYGON_TOOL = "polygon";
    final static String CIRCLE_TOOL = "circle";
    final static String RECT_TOOL = "rect";

    private String toolEnabled = null;
    private GameBuilder gameBuilder;

    public ShapePanel(GameBuilder gameBuilder) {
        this.gameBuilder = gameBuilder;

        setPreferredSize(new Dimension(100, 400));
        addButtons();
    }

    private void addButtons() {
        JButton moveTool = new JButton("V");
        JButton handTool = new JButton("H");
        JButton polygonTool = new JButton("P");
        JButton circleTool = new JButton("C");
        JButton rectTool = new JButton("R");

        moveTool.addActionListener(new ActionListener() {
            @Override()
            public void actionPerformed(ActionEvent e) {
                toolEnabled = MOVE_TOOL;
                gameBuilder.onToolChanged(toolEnabled);
            }
        });

        handTool.addActionListener(new ActionListener() {
            @Override()
            public void actionPerformed(ActionEvent e) {
                toolEnabled = HAND_TOOL;
                gameBuilder.onToolChanged(toolEnabled);
            }
        });

        polygonTool.addActionListener(new ActionListener() {
            @Override()
            public void actionPerformed(ActionEvent e) {
                toolEnabled = POLYGON_TOOL;
                gameBuilder.onToolChanged(toolEnabled);
            }
        });

        circleTool.addActionListener(new ActionListener() {
            @Override()
            public void actionPerformed(ActionEvent e) {
                toolEnabled = CIRCLE_TOOL;
                gameBuilder.onToolChanged(toolEnabled);
            }
        });

        rectTool.addActionListener(new ActionListener() {
            @Override()
            public void actionPerformed(ActionEvent e) {
                toolEnabled = RECT_TOOL;
                gameBuilder.onToolChanged(toolEnabled);
            }
        });
        
        add(moveTool);
        add(handTool);
        add(polygonTool);
        add(circleTool);
        add(rectTool);
    }
}
