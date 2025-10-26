package com.gamebuilder.util;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GridHelper {
    public static GridBagConstraints getConstraints(int x, int y) {
        return getConstraints(x, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    }

    public static GridBagConstraints getConstraints(int x, int y, int xSpan, int ySpan) {
        return getConstraints(x, y, xSpan, ySpan, GridBagConstraints.WEST, GridBagConstraints.NONE);
    }

    public static GridBagConstraints getConstraints(int x, int y, int xSpan, int ySpan, int anchor, int fill) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = xSpan;
        gbc.gridheight = ySpan;
        gbc.weightx = 1.0;
        gbc.anchor = anchor;
        gbc.insets = new Insets(4, 2, 4, 2);
        gbc.fill = fill;

        return gbc;
    }
}
