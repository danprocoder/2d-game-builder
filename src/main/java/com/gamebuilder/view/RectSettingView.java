package com.gamebuilder.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import com.gamebuilder.canvasobject.shape.Rectangle;
import com.gamebuilder.util.ColorHelper;
import com.gamebuilder.util.GridHelper;

public class RectSettingView extends JPanel {
    private Rectangle rect;
    private JSpinner xInput;
    private JSpinner yInput;
    private JSpinner wInput;
    private JSpinner hInput;
    private JSpinner opacityInput;

    public RectSettingView(Rectangle rect) {
        this.rect = rect;

        JPanel grid = new JPanel();
        grid.setLayout(new GridBagLayout());

        grid.add(getColorRow(), GridHelper.getConstraints(0, 0, 4, 1, GridBagConstraints.WEST, GridBagConstraints.NONE));

        // X input
        grid.add(new JLabel("X:"), GridHelper.getConstraints(0, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE));

        this.xInput = new SimpleNumberSpinner(rect.getX(), Integer.MAX_VALUE, e -> rect.setX((Integer) xInput.getValue()));
        grid.add(xInput, GridHelper.getConstraints(1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL));

        // Y input
        grid.add(new JLabel("Y:"), GridHelper.getConstraints(2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE));

        this.yInput = new SimpleNumberSpinner(rect.getY(), Integer.MAX_VALUE, e -> rect.setY((Integer) yInput.getValue()));
        grid.add(yInput, GridHelper.getConstraints(3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL));

        // Width input
        grid.add(new JLabel("W:"), GridHelper.getConstraints(0, 2, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE));

        this.wInput = new SimpleNumberSpinner(rect.getWidth(), Integer.MAX_VALUE, e -> rect.setWidth((Integer) wInput.getValue()));
        grid.add(wInput, GridHelper.getConstraints(1, 2, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL));

        // Height input
        grid.add(new JLabel("H:"), GridHelper.getConstraints(2, 2, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE));

        this.hInput = new SimpleNumberSpinner(rect.getHeight(), Integer.MAX_VALUE, e -> rect.setHeight((Integer) hInput.getValue()));
        grid.add(hInput, GridHelper.getConstraints(3, 2, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL));

        add(grid);
    }

    private JPanel getColorRow() {
        JPanel grid = new JPanel();
        grid.setLayout(new GridBagLayout());

        grid.add(new JLabel("Color:"), GridHelper.getConstraints(0, 0));
        grid.add(
            new ColorPickerButton(rect.getColor(), c -> {
                int opacity = (int) this.opacityInput.getValue();
                rect.setColor(ColorHelper.setOpacity(c, opacity));
            }),
            GridHelper.getConstraints(1, 0)
        );

        grid.add(new JLabel("Opacity:"), GridHelper.getConstraints(0, 1));
        this.opacityInput = new SimpleNumberSpinner(
            ColorHelper.getOpacity(rect.getColor()), 100, e -> {
                int opacity = (int) ((JSpinner) e.getSource()).getValue();
                Color newColor = ColorHelper.setOpacity(this.rect.getColor(), opacity);
                this.rect.setColor(newColor);
            });
        grid.add(this.opacityInput, GridHelper.getConstraints(1, 1));

        return grid;
    }
}
