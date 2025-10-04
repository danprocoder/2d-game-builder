package com.gamebuilder;

import java.awt.Dimension;
import javax.swing.JPanel;

import com.gamebuilder.model.CanvasModel;

public class LayersPanel extends JPanel {
    private CanvasModel model;

    public LayersPanel(GameBuilder gameBuilder, CanvasModel model) {
        this.model = model;
        setPreferredSize(new Dimension(150, 600));
    }
}
