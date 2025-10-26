package com.gamebuilder;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.gamebuilder.model.CanvasModel;
import com.gamebuilder.service.CanvasService;
import com.gamebuilder.util.GridHelper;
import com.gamebuilder.view.ColorPickerButton;

public class ShapePanel extends JPanel {
    private CanvasService canvasService;

    public ShapePanel(CanvasService canvasService) {
        this.canvasService = canvasService;

        setPreferredSize(new Dimension(150, 400));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        addButtons();
    }

    private void addButtons() {
        CanvasModel canvasModel = canvasService.getModel();
        try {
            JButton moveTool = new JButton(getIcon("/icons/move.png"));
            JButton handTool = new JButton(getIcon("/icons/hold.png"));
            JButton polygonTool = new JButton(getIcon("/icons/polygon.png"));
            JButton circleTool = new JButton(getIcon("/icons/oval.png"));
            JButton rectTool = new JButton(getIcon("/icons/square.png"));

            moveTool.addActionListener(new ActionListener() {
                @Override()
                public void actionPerformed(ActionEvent e) {
                    canvasModel.setSelectedTool(CanvasTool.MOVE_TOOL);
                }
            });

            handTool.addActionListener(new ActionListener() {
                @Override()
                public void actionPerformed(ActionEvent e) {
                    canvasModel.setSelectedTool(CanvasTool.HAND_TOOL);
                }
            });

            polygonTool.addActionListener(new ActionListener() {
                @Override()
                public void actionPerformed(ActionEvent e) {
                    canvasModel.setSelectedTool(CanvasTool.POLYGON_TOOL);
                }
            });

            circleTool.addActionListener(new ActionListener() {
                @Override()
                public void actionPerformed(ActionEvent e) {
                    canvasModel.setSelectedTool(CanvasTool.CIRCLE_TOOL);
                }
            });

            rectTool.addActionListener(new ActionListener() {
                @Override()
                public void actionPerformed(ActionEvent e) {
                    canvasModel.setSelectedTool(CanvasTool.RECT_TOOL);
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

            Dimension pref = grid.getPreferredSize();
            grid.setMaximumSize(new Dimension(pref.width, pref.height));
            add(grid);

            add(Box.createVerticalStrut(30));

            ColorPickerButton colorPickerBtn = new ColorPickerButton(canvasModel.getColor(), c -> {
                canvasModel.setColor(c);
            });
            add(colorPickerBtn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ImageIcon getIcon(String path) throws IOException {
        BufferedImage img = ImageIO.read(getClass().getResourceAsStream(path));
        Image scaledImg = img.getScaledInstance(24, 24,  java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
}
