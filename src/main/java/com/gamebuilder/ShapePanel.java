package com.gamebuilder;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.gamebuilder.scene.Scene;
import com.gamebuilder.service.CanvasService;
import com.gamebuilder.service.IconService;
import com.gamebuilder.service.SceneService;
import com.gamebuilder.util.GridHelper;
import com.gamebuilder.view.ColorPickerButton;

public class ShapePanel extends JPanel {
    private CanvasService canvasService;
    private SceneService sceneService;

    public ShapePanel(CanvasService canvasService, SceneService sceneService) {
        this.canvasService = canvasService;
        this.sceneService = sceneService;

        setPreferredSize(new Dimension(150, 400));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        addButtons();
    }

    private void addButtons() {
        try {
            JButton moveTool = new JButton(IconService.getIcon("/draw tools/move.png"));
            JButton handTool = new JButton(IconService.getIcon("/draw tools/hold.png"));
            JButton polygonTool = new JButton(IconService.getIcon("/draw tools/polygon.png"));
            JButton circleTool = new JButton(IconService.getIcon("/draw tools/oval.png"));
            JButton rectTool = new JButton(IconService.getIcon("/draw tools/square.png"));
            JButton textTool = new JButton(IconService.getIcon("/draw tools/text.png"));

            moveTool.addActionListener(new ActionListener() {
                @Override()
                public void actionPerformed(ActionEvent e) {
                    getService().setSelectedTool(CanvasTool.MOVE_TOOL);
                }
            });

            handTool.addActionListener(new ActionListener() {
                @Override()
                public void actionPerformed(ActionEvent e) {
                    getService().setSelectedTool(CanvasTool.HAND_TOOL);
                }
            });

            polygonTool.addActionListener(new ActionListener() {
                @Override()
                public void actionPerformed(ActionEvent e) {
                    getService().setSelectedTool(CanvasTool.POLYGON_TOOL);
                }
            });

            circleTool.addActionListener(new ActionListener() {
                @Override()
                public void actionPerformed(ActionEvent e) {
                    getService().setSelectedTool(CanvasTool.CIRCLE_TOOL);
                }
            });

            rectTool.addActionListener(new ActionListener() {
                @Override()
                public void actionPerformed(ActionEvent e) {
                    getService().setSelectedTool(CanvasTool.RECT_TOOL);
                }
            });

            textTool.addActionListener(new ActionListener() {
                @Override()
                public void actionPerformed(ActionEvent e) {
                    getService().setSelectedTool(CanvasTool.TEXT_TOOL);
                }
            });
            
            JPanel grid = new JPanel();
            grid.setAlignmentX(CENTER_ALIGNMENT);
            grid.setLayout(new GridBagLayout());
            grid.add(moveTool, GridHelper.getConstraints(0, 0));
            grid.add(handTool, GridHelper.getConstraints(1, 0));
            grid.add(polygonTool, GridHelper.getConstraints(0, 1));
            grid.add(circleTool, GridHelper.getConstraints(1, 1));
            grid.add(rectTool, GridHelper.getConstraints(0, 2));
            grid.add(textTool, GridHelper.getConstraints(1, 2));

            Dimension pref = grid.getPreferredSize();
            grid.setMaximumSize(new Dimension(pref.width, pref.height));
            add(grid);

            add(Box.createVerticalStrut(30));

            ColorPickerButton colorPickerBtn = new ColorPickerButton(getService().getSelectedBgColor(), c -> {
                getService().setSelectedBgColor(c);
            });
            add(colorPickerBtn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: refactor to avoid code duplication with other views
    private CanvasService getService() {
        Scene scene = this.sceneService.getActiveScene();
        if (scene != null) {
            return scene.getCanvasService();
        }
        return this.canvasService;
    }
}
