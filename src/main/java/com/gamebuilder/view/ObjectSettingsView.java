package com.gamebuilder.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.TransferHandler;

import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.canvasobject.shape.Circle;
import com.gamebuilder.canvasobject.shape.Polygon;
import com.gamebuilder.canvasobject.shape.Rectangle;
import com.gamebuilder.canvasobject.sprite.AnimatedSpriteImage;
import com.gamebuilder.canvasobject.sprite.Sprite;
import com.gamebuilder.canvasobject.sprite.SpriteState;
import com.gamebuilder.model.Asset;
import com.gamebuilder.model.CanvasModel;
import com.gamebuilder.model.CanvasModelUpdateListener;
import com.gamebuilder.model.SceneUpdateListener;
import com.gamebuilder.scene.Scene;
import com.gamebuilder.service.CanvasService;
import com.gamebuilder.service.SceneService;
import com.gamebuilder.util.Log;
import com.gamebuilder.view.dnd.ImageAssetFlavor;

public class ObjectSettingsView extends JPanel implements CanvasModelUpdateListener, SceneUpdateListener {
    private CanvasService canvasService;
    private SceneService sceneService;

    public ObjectSettingsView(CanvasService canvasService, SceneService sceneService) {
        this.canvasService = canvasService;
        this.sceneService = sceneService;

        this.canvasService.addUpdateListener(this);
        this.sceneService.addUpdateListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.showSettingsForSprite();
    }

    public void showSettingsForSprite() {
        removeAll();

        JLabel setting = new JLabel("Settings");
        setting.setAlignmentX(Component.LEFT_ALIGNMENT);
        Font settingFont = setting.getFont();
        setting.setFont(settingFont.deriveFont(settingFont.getStyle() | Font.BOLD));
        add(setting);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);

        JPanel scrollPanel = new JPanel();
        scrollPanel.setAlignmentX(LEFT_ALIGNMENT);
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));

        CanvasObject activeObject = this.getService().getActiveObject();
        if (activeObject == null) {
            return;
        }

        if (activeObject instanceof Sprite) {
            Sprite sprite = (Sprite) activeObject;
            scrollPanel.add(getSpriteSettingView((Sprite) activeObject));

            JButton addStateBtn = new JButton("Add State");
            addStateBtn.addActionListener(e -> this.onAddSpriteState(sprite));
            add(addStateBtn);
        } else if (activeObject instanceof Rectangle) {
            scrollPanel.add(new RectSettingView((Rectangle) activeObject));
        } else if (activeObject instanceof Circle) {
            scrollPanel.add(new CircleSettingView((Circle) activeObject));
        } else if (activeObject instanceof Polygon) {
            scrollPanel.add(new PolygonSettingView((Polygon) activeObject));
        }

        scrollPanel.add(new JSeparator());

        // Add physics settings
        Box physicsBox = new Box(BoxLayout.X_AXIS);
        physicsBox.add(new JLabel("Mass:"));
        physicsBox.add(new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1)));
        physicsBox.setMaximumSize(new Dimension(1000, physicsBox.getPreferredSize().height));
        scrollPanel.add(physicsBox);

        scrollPane.setViewportView(scrollPanel);
        add(scrollPane);
    }

    private JPanel getSpriteSettingView(Sprite sprite) {
        JPanel spriteSettingPanel = new JPanel();
        spriteSettingPanel.setLayout(new BoxLayout(spriteSettingPanel, BoxLayout.Y_AXIS));

        ArrayList<SpriteState> states = sprite.getStates();
        for (int i = 0; i < states.size(); i++) {
            if (i > 0) {
                spriteSettingPanel.add(new JSeparator());
            }

            SpriteState state = states.get(i);

            JPanel stateSettingPanel = new JPanel();

            stateSettingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            stateSettingPanel.setLayout(new BoxLayout(stateSettingPanel, BoxLayout.Y_AXIS));

            JLabel stateLabel = new JLabel("State: " + state.getName());
            stateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            stateSettingPanel.add(stateLabel);

            JLabel collisionBoxLabel = new JLabel();
            collisionBoxLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            stateSettingPanel.add(collisionBoxLabel);

            CanvasObject collisionBox = state.getCollisionBox();
            if (collisionBox instanceof Rectangle) {
                collisionBoxLabel.setText("Collision Box: " + collisionBox.getName());
                stateSettingPanel.add(new RectSettingView((Rectangle) collisionBox));
            } else if (collisionBox instanceof Circle) {
                collisionBoxLabel.setText("Collision Box: " + collisionBox.getName());
                stateSettingPanel.add(new CircleSettingView((Circle) collisionBox));
            } else if (collisionBox instanceof Polygon) {
                collisionBoxLabel.setText("Collision Box: " + collisionBox.getName());
                stateSettingPanel.add(new PolygonSettingView((Polygon) collisionBox));
            } else {
                collisionBoxLabel.setText("No Collision Box!");
            }

            stateSettingPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        sprite.setState(state.getName());
                        ObjectSettingsView.this.getService().notifyUpdate("sprite_state_changed");
                    }
                }
            });

            stateSettingPanel.setTransferHandler(new TransferHandler() {
                ImageAssetFlavor imgFlavor = new ImageAssetFlavor();

                @Override()
                public boolean canImport(TransferSupport support) {
                    return support.isDataFlavorSupported(imgFlavor);
                }

                @Override()
                public boolean importData(TransferSupport support) {
                    try {
                        Asset asset = (Asset) support.getTransferable().getTransferData(imgFlavor);

                        AnimatedSpriteImage image = (AnimatedSpriteImage) state.getImage();
                        if (image == null) {
                            image = new AnimatedSpriteImage(sprite);
                            state.setImage(image);
                        }
                        image.addFrame(asset);
                        ObjectSettingsView.this.getService().notifyUpdate("sprite_image_frame_added");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });

            if (state.getName().equals(sprite.getCurrentState())) {
                stateSettingPanel.setBackground(Color.BLUE);

                stateLabel.setForeground(Color.WHITE);
                collisionBoxLabel.setForeground(Color.WHITE);
            }

            stateSettingPanel.add(new SpriteImageSettingView(state));
            spriteSettingPanel.add(stateSettingPanel);
        }

        return spriteSettingPanel;
    }

    private void onAddSpriteState(Sprite sprite) {
        sprite.addState(new SpriteState("New State" + (sprite.getStates().size() + 1)));
        this.getService().notifyUpdate("sprite_state_added");
    }

    @Override
    public void onCanvasModelUpdated(CanvasModel model, String update) {
        this.showSettingsForSprite();
    }

    @Override()
    public void onSceneUpdate() {
        this.canvasService.removeUpdateListener(this);
        for (Scene scene : this.sceneService.getScenes()) {
            scene.getCanvasService().removeUpdateListener(this);
        }
        Log.v("ObjectListView.setupUpdateListeners()", "Adding update listener to canvas service");
        this.getService().addUpdateListener(this);

        this.showSettingsForSprite();
    }

    // TODO: refactor to avoid code duplication with other views
    private CanvasService getService() {
        Scene scene = this.sceneService.getActiveScene();
        if (scene != null) {
            return scene.getCanvasService();
        }
        return this.canvasService;
    }
}
