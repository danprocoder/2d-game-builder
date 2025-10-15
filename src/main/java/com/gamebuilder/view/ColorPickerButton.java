package com.gamebuilder.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JColorChooser;

public class ColorPickerButton extends JButton {
    private Color initialColor;
    private ColorSelectedListener listener;
    
    public ColorPickerButton(Color color, ColorSelectedListener listener) {
        this.initialColor = color;
        this.listener = listener;

        this.setPreferredSize(new Dimension(35, 35));
        this.setBackground(color);

        this.addActionListener(e -> {
            showColorPicker();
        });
    }

    private void showColorPicker() {
        JColorChooser chooser = new JColorChooser(initialColor);
        chooser.getSelectionModel().addChangeListener(ev -> {
            Color newColor = chooser.getColor();
            this.setBackground(newColor);
            listener.colorSelected(newColor);
        });

        JColorChooser.createDialog(
            this,
            "Select Color",
            true,
            chooser,
            okEv -> {
                this.initialColor = chooser.getColor();
            },
            cancelEv -> {
                this.setBackground(this.initialColor);
                listener.colorSelected(this.initialColor);
            }
        ).setVisible(true);
    }

    @Override()
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(getBackground());
        g2.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 3, 3);
        g2.dispose();
    }
}
