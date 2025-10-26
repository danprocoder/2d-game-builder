package com.gamebuilder.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;

import com.gamebuilder.canvasobject.sprite.AddSpriteDialog;
import com.gamebuilder.service.CanvasService;

public class NewSpriteAction implements ActionListener {
    private CanvasService canvasService;

    public NewSpriteAction(CanvasService canvasService) {
        this.canvasService = canvasService;
    }

    @Override()
    public void actionPerformed(ActionEvent e) {
        JDialog dialog = new AddSpriteDialog(this.canvasService);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
