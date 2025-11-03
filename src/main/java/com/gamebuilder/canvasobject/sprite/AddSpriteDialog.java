package com.gamebuilder.canvasobject.sprite;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.gamebuilder.model.SpriteModel;
import com.gamebuilder.service.CanvasService;

public class AddSpriteDialog extends JDialog {
    private JTextField nameField;
    private CanvasService canvasService;

    /** Adds all the components of the dialog. */
    public AddSpriteDialog(CanvasService canvasService) {
        this.canvasService = canvasService;

        this.setTitle("Create Sprite");
        this.setPreferredSize(new Dimension(300, 400));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("Name"));
        nameField = new JTextField();
        panel.add(nameField);

        JPanel buttonArea = new JPanel();
        buttonArea.add(new JButton("Cancel"));
        JButton createBtn = new JButton("Create Sprite");
        createBtn.addActionListener(e -> createNewSprite());
        buttonArea.add(createBtn);

        panel.add(buttonArea);

        this.add(panel);
        this.pack();
    }

    /** Called when you click the create sprite button */
    private void createNewSprite() {
        String spriteName = nameField.getText();

        // TODO: Remove use of singleton here
        Sprite sprite = new Sprite(spriteName);
        sprite.addState(new SpriteState("default"));
        sprite.setState("default");
        SpriteModel model = SpriteModel.getInstance();
        model.addSprite(sprite);
        model.setSelectedSprite(sprite);

        canvasService.addNewObject(sprite);

        this.setVisible(false);
        this.dispose();
    }
}
