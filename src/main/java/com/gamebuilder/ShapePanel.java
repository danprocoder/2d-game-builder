package com.gamebuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.gamebuilder.model.CanvasModel;
import com.gamebuilder.model.CanvasModelUpdateListener;

class ColorPickerDialog extends JDialog {
    private CanvasModel model;
    private Color color;
    
    ColorPickerDialog(CanvasModel model) {
        this.model = model;
        this.color = new Color(
            model.getColor().getRed(),
            model.getColor().getGreen(),
            model.getColor().getBlue(),
            model.getColor().getAlpha()
        );

        showForm();

        setTitle("Pick a color");
        pack();
        setLocationRelativeTo(null);
    }

    private void showForm() {
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        // The big panel showing the chosen color
        JPanel panel = new JPanel();
        panel.setBackground(this.color);
        panel.setPreferredSize(new Dimension(300, 300));

        add(panel);

        // Input fields
        JPanel rgba = new JPanel();
        rgba.setLayout(new BoxLayout(rgba, BoxLayout.X_AXIS));
        
        rgba.add(new JLabel("R"));
        JSpinner rField = new JSpinner(new SpinnerNumberModel(this.color.getRed(), 0, 255, 1));
        rField.addChangeListener(e -> {
            int r = (int) rField.getValue();
            color = new Color(r, color.getGreen(), color.getBlue(), color.getAlpha());
            panel.setBackground(color);
        });
        rgba.add(rField);

        rgba.add(new JLabel("G"));
        JSpinner gField = new JSpinner(new SpinnerNumberModel(this.color.getGreen(), 0, 255, 1));
        gField.addChangeListener(e -> {
            int g = (int) gField.getValue();
            color = new Color(color.getRed(), g, color.getBlue(), color.getAlpha());
            panel.setBackground(color);
        });
        rgba.add(gField);

        rgba.add(new JLabel("B"));
        JSpinner bField = new JSpinner(new SpinnerNumberModel(this.color.getBlue(), 0, 255, 1));
        bField.addChangeListener(e -> {
            int b = (int) bField.getValue();
            color = new Color(color.getRed(), color.getGreen(), b, color.getAlpha());
            panel.setBackground(color);
        });
        rgba.add(bField);

        rgba.add(new JLabel("A"));
        JSpinner aField = new JSpinner(new SpinnerNumberModel(this.color.getAlpha(), 0, 255, 1));
        aField.addChangeListener(e -> {
            int a = (int) aField.getValue();
            color = new Color(color.getRed(), color.getGreen(), color.getBlue(), a);
            panel.setBackground(color);
        });
        rgba.add(aField);

        add(rgba);

        // Bottom buttons
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        JButton cancelButton = new JButton("Cancel");
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            model.setColor(this.color);

            setVisible(false);
        });

        buttons.add(cancelButton);
        buttons.add(okButton);

        add(buttons);
    }

    
}

class ColorPicker extends JPanel implements ActionListener, CanvasModelUpdateListener {
    private CanvasModel model;

    private JPanel selectedColor;

    ColorPicker(CanvasModel model) {
        this.model = model;
        this.model.addUpdateListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        selectedColor = new JPanel();
        selectedColor.setPreferredSize(new Dimension(50, 50));
        selectedColor.setBackground(model.getColor());
        add(selectedColor);

        JButton button = new JButton("Change Color");
        button.addActionListener(this);
        add(button);
    }

    @Override
    public void onCanvasModelUpdated(CanvasModel model) {
        selectedColor.setBackground(model.getColor());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ColorPickerDialog dialog = new ColorPickerDialog(model);
        dialog.setVisible(true);
    }
}

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

        setPreferredSize(new Dimension(150, 400));
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
        add(new ColorPicker(gameBuilder.getModel()));
    }
}
