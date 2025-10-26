package com.gamebuilder.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import com.gamebuilder.canvasobject.shape.Polygon;
import com.gamebuilder.util.ColorHelper;
import com.gamebuilder.util.GridHelper;

public class PolygonSettingView extends JPanel {
    private Polygon polygon;

    private JSpinner opacityInput;
    
    public PolygonSettingView(Polygon polygon) {
        this.polygon = polygon;

        JPanel grid = new JPanel();
        grid.setLayout(new GridBagLayout());

        grid.add(getColorRow(), GridHelper.getConstraints(0, 0, 4, 1, GridBagConstraints.WEST, GridBagConstraints.NONE));

        for (int i = 0; i < polygon.getPoints().size(); i++) {
            grid.add(
                new JLabel("P" + (i + 1)),
                GridHelper.getConstraints(0, i + 1)
            );

            grid.add(
                new JLabel("X:"),
                GridHelper.getConstraints(1, i + 1)
            );
            JSpinner xSpinner = new SimpleNumberSpinner((int) polygon.getPoints().get(i).x, e -> {});
            grid.add(xSpinner, GridHelper.getConstraints(2, i + 1));

            grid.add(
                new JLabel("Y:"),
                GridHelper.getConstraints(3, i + 1)
            );
            JSpinner ySpinner = new SimpleNumberSpinner((int) polygon.getPoints().get(i).y, e -> {});
            grid.add(ySpinner, GridHelper.getConstraints(4, i + 1));
        }

        add(grid);
    }

    private JPanel getColorRow() {
        JPanel grid = new JPanel();
        grid.setLayout(new GridBagLayout());

        grid.add(new JLabel("Color:"), GridHelper.getConstraints(0, 0));
        grid.add(
            new ColorPickerButton(polygon.getColor(), c -> {
                int opacity = (int) this.opacityInput.getValue();
                polygon.setColor(ColorHelper.setOpacity(c, opacity));
            }),
            GridHelper.getConstraints(1, 0)
        );

        grid.add(new JLabel("Opacity:"), GridHelper.getConstraints(0, 1));
        this.opacityInput = new SimpleNumberSpinner(
            ColorHelper.getOpacity(polygon.getColor()), 100, e -> {
                int opacity = (int) ((JSpinner) e.getSource()).getValue();
                Color newColor = ColorHelper.setOpacity(this.polygon.getColor(), opacity);
                this.polygon.setColor(newColor);
            });
        grid.add(this.opacityInput, GridHelper.getConstraints(1, 1));

        return grid;
    }
}
