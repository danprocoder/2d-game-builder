package com.gamebuilder.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import com.gamebuilder.canvasobject.shape.Circle;
import com.gamebuilder.util.ColorHelper;
import com.gamebuilder.util.GridHelper;

public class CircleSettingView extends JPanel {
    private Circle circle;
    private JSpinner xInput;
    private JSpinner yInput;
    private JSpinner wInput;
    private JSpinner hInput;
    private JSpinner opacityInput;

    public CircleSettingView(Circle circle) {
        this.circle = circle;

        JPanel grid = new JPanel();
        grid.setLayout(new GridBagLayout());

        grid.add(getColorRow(), GridHelper.getConstraints(0, 0, 4, 1, GridBagConstraints.WEST, GridBagConstraints.NONE));

        // X input
        grid.add(new JLabel("X:"), GridHelper.getConstraints(0, 1));

        xInput = new SimpleNumberSpinner(circle.getX(), e -> circle.setX((Integer) xInput.getValue()));
        grid.add(xInput, GridHelper.getConstraints(1, 1));

        // Y input
        grid.add(new JLabel("Y:"), GridHelper.getConstraints(2, 1));

        yInput = new SimpleNumberSpinner(circle.getY(), e -> circle.setY((Integer) yInput.getValue()));
        grid.add(yInput, GridHelper.getConstraints(3, 1));

        // Width input
        grid.add(new JLabel("W:"), GridHelper.getConstraints(0, 2));

        wInput = new SimpleNumberSpinner(circle.getWidth(), e -> circle.setWidth((Integer) wInput.getValue()));
        grid.add(wInput, GridHelper.getConstraints(1, 2));

        // Height input
        grid.add(new JLabel("H:"), GridHelper.getConstraints(2, 2));

        hInput = new SimpleNumberSpinner(circle.getHeight(), e -> circle.setHeight((Integer) hInput.getValue()));
        grid.add(hInput, GridHelper.getConstraints(3, 2));

        add(grid);
    }

    private JPanel getColorRow() {
        JPanel grid = new JPanel();
        grid.setLayout(new GridBagLayout());

        grid.add(new JLabel("Color:"), GridHelper.getConstraints(0, 0));
        grid.add(
            new ColorPickerButton(circle.getColor(), c -> {
                int opacity = (int) this.opacityInput.getValue();
                circle.setColor(ColorHelper.setOpacity(c, opacity));
            }),
            GridHelper.getConstraints(1, 0)
        );

        grid.add(new JLabel("Opacity:"), GridHelper.getConstraints(0, 1));
        this.opacityInput = new SimpleNumberSpinner(
            ColorHelper.getOpacity(circle.getColor()), 100, e -> {
                int opacity = (int) ((JSpinner) e.getSource()).getValue();
                Color newColor = ColorHelper.setOpacity(this.circle.getColor(), opacity);
                this.circle.setColor(newColor);
            });
        grid.add(this.opacityInput, GridHelper.getConstraints(1, 1));

        return grid;
    }
}
