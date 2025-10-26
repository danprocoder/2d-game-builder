package com.gamebuilder.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.gamebuilder.canvasobject.sprite.AnimatedSpriteImage;
import com.gamebuilder.canvasobject.sprite.SpriteImage;
import com.gamebuilder.canvasobject.sprite.SpriteState;
import com.gamebuilder.util.GridHelper;

abstract class TextChangedListener implements DocumentListener {
    public abstract void onChanged();

    @Override
    public void insertUpdate(DocumentEvent e) {
        onChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        onChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        onChanged();
    }
}

class FrameView extends JPanel {
    private AnimatedSpriteImage image;

    public FrameView(AnimatedSpriteImage image) {
        this.image = image;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        if (image.getFrames().size() == 0) {
            JLabel noFramesLabel = new JLabel("Drop frames here");
            add(noFramesLabel);
            return;
        } else {
            for (SpriteImage frame : image.getFrames()) {
                add(getThumbnail(frame));
            }
        }
    }

    private JLabel getThumbnail(SpriteImage frame) {
        JLabel thumbnail = new JLabel(
            new ImageIcon(
                frame.getImage().getScaledInstance(32, 32, 0)
            )
        );
        thumbnail.addMouseListener(new MouseAdapter() {
            @Override()
            public void mouseClicked(MouseEvent e) {
                if (e.isMetaDown()) {
                    JPopupMenu menu = new JPopupMenu();

                    JMenuItem removeItem = new JMenuItem("Remove Frame");
                    removeItem.addActionListener(ev -> FrameView.this.deleteFrame(frame));
                    menu.add(removeItem);
                    menu.show(thumbnail, e.getX(), e.getY());
                }
            }
        });
        return thumbnail;
    }

    private void deleteFrame(SpriteImage frame) {
        int index = this.image.getFrames().indexOf(frame);
        image.removeFrameAt(index);
        remove(index);
        revalidate();
        repaint();
    }
}

public class SpriteImageSettingView extends JPanel {
    private SpriteState spriteState;

    private JSpinner xInput;
    private JSpinner yInput;
    private JSpinner fpsInput;
    private JSpinner delayInput;
    private JSpinner opacityInput;

    public SpriteImageSettingView(SpriteState spriteState) {
        this.spriteState = spriteState;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        AnimatedSpriteImage image = this.spriteState.getImage();
        if (image != null) {
            add(getFramesGrid(image));
            add(getDimensionSettingView(image));
            add(getAnimationSettingView(image));
        }
    }

    private JPanel getDimensionSettingView(AnimatedSpriteImage image) {
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridBagLayout());

        // X Input
        gridPanel.add(new JLabel("X:"), GridHelper.getConstraints(0, 0));
        xInput = new SimpleNumberSpinner(image.getX(), e -> image.setX((Integer) xInput.getValue()));
        gridPanel.add(xInput, GridHelper.getConstraints(1, 0));

        // Y Input
        gridPanel.add(new JLabel("Y:"), GridHelper.getConstraints(2, 0));
        yInput = new SimpleNumberSpinner(image.getY(), e -> image.setY((Integer) yInput.getValue()));
        gridPanel.add(yInput, GridHelper.getConstraints(3, 0));

        // Opacity Input
        Box opacityBox = Box.createHorizontalBox();
        opacityBox.add(
            new JLabel("Opacity:"),
            GridHelper.getConstraints(0, 1)
        );
        this.opacityInput = new SimpleNumberSpinner(
            (int) image.getOpacity(),
            100,
            e -> image.setOpacity(((Integer) opacityInput.getValue()))
        );
        opacityBox.add(this.opacityInput);
        gridPanel.add(opacityBox, GridHelper.getConstraints(1, 1, 4, 1));

        return gridPanel;
    }

    private JPanel getAnimationSettingView(AnimatedSpriteImage image) {
        JPanel gridPanel2 = new JPanel();
        gridPanel2.setLayout(new GridBagLayout());

        // Frames Per Seconds field
        gridPanel2.add(
            new JLabel("Frames Per Seconds:"),
            GridHelper.getConstraints(0, 1)
        );
        fpsInput = new SimpleNumberSpinner(image.getFPS(), e -> image.setFPS((Integer) fpsInput.getValue()));
        gridPanel2.add(fpsInput, GridHelper.getConstraints(1, 1));

        // Delay field
        gridPanel2.add(
            new JLabel("Delay:"),
            GridHelper.getConstraints(0, 2, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE)
        );
        delayInput = new SimpleNumberSpinner(image.getDelay(), e -> image.setDelay((Integer) delayInput.getValue()));
        gridPanel2.add(delayInput, GridHelper.getConstraints(1, 2));

        // Continuous checkbox
        gridPanel2.add(
            new JCheckBox("Continuous", image.getContinuous()),
            GridHelper.getConstraints(0, 3)
        );

        return gridPanel2;
    }

    private JPanel getFramesGrid(AnimatedSpriteImage image) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Frames:");
        label.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(label);

        panel.add(new FrameView(image));

        return panel;
    }
}
