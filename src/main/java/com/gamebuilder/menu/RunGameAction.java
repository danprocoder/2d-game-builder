package com.gamebuilder.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.canvasobject.shape.Polygon;
import com.gamebuilder.canvasobject.sprite.Sprite;
import com.gamebuilder.scene.Scene;
import com.gamebuilder.service.CanvasService;
import com.gamebuilder.service.SceneService;
import com.gamebuilder.util.Log;
import com.gameengine.Game;
import com.gameengine.GameEngine;
import com.gameengine.obj.Game2dObject;
import com.gameengine.obj.Game2dObjectAnimatedImage;
import com.gameengine.obj.Game2dObjectState;
import com.gameengine.scene.Game2dScene;
import com.gameengine.scene.Game2dSceneObject;
import com.gameengine.sound.Audio;

class GameRunnerView extends JFrame {
    private GameEngine engine;

    public GameRunnerView() {
        setSize(750, 450);
        setTitle("Game Runner");
        setLocationRelativeTo(null);

        this.showLoadingScreen();
    }

    public void showLoadingScreen() {
        Log.v("GameRunnerView.showLoadingScreen()", "Showing loading screen");
        JPanel loadingPanel = new JPanel();
        loadingPanel.add(new JLabel("Loading..."));
        this.getContentPane().add(loadingPanel);
    }

    public void startGame(Game game) {
        Log.v("GameRunnerView.startGame()", "Starting game " + game);
        this.engine = new GameEngine(game);
        this.getContentPane().removeAll();
        this.getContentPane().add(engine.getView());

        SwingUtilities.invokeLater(() -> {
            this.revalidate();
            this.repaint();
        });
    }
}

// TODO: This call might need to be refactored to move the loadGame() into a service
// and maybe read from an xml. I've not decided yet.
public class RunGameAction implements ActionListener {
    private CanvasService canvasService;
    private SceneService sceneService;
    private GameRunnerView gameRunnerView;

    public RunGameAction(CanvasService canvasService, SceneService sceneService) {
        this.canvasService = canvasService;
        this.sceneService = sceneService;
        this.gameRunnerView = new GameRunnerView();
    }

    @Override()
    public void actionPerformed(ActionEvent event) {
        this.gameRunnerView.setVisible(true);

        Thread loadingThread = new Thread(() -> {
            Log.v("RunGameAction.actionPerformed()", "Thread started to load game");
            Game game = this.loadGame();
            this.gameRunnerView.startGame(game);
        });
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    private Game loadGame() {
        try {
            ArrayList<Game2dScene> scenes = this.getGameScenes();

            // This is for testing purposes only. Later, the objects will be added from 
            // the getGameScenes() method based on the SceneModel data
            // scenes.get(0).addSceneObject(new Game2dSceneObject(objects.get(0), 10, 100));

            return new Game("1.0", "Test Game", new int[] { 800, 600 }, scenes);
        } catch (Exception e) {
            System.out.println("Failed to load game");
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<Game2dScene> getGameScenes() throws Exception {
        ArrayList<Game2dScene> gameScenes = new ArrayList<>();

        for (Scene s: this.sceneService.getScenes()) {
            Game2dScene gameScene = new Game2dScene();
            if (s.getBackgroundMusic() != null) {
                gameScene.setBackgroundAudio(
                    new Audio(s.getBackgroundMusic().getFullPath())
                );

                gameScene.setObjects(this.getGameObjects(s.getCanvasService()));
            }
            gameScenes.add(gameScene);
        }

        return gameScenes;
    }  

    private ArrayList<Game2dSceneObject> getGameObjects(CanvasService canvasService) {
        ArrayList<Game2dSceneObject> objects = new ArrayList<Game2dSceneObject>();

        for (int i = 0; i < canvasService.getObjects().size(); i++) {
            CanvasObject canvasObject = canvasService.getObjects().get(i);

            if (canvasObject instanceof Sprite) {
                ArrayList<Game2dObjectState> states = new ArrayList<>();
                ((Sprite) canvasObject).getStates().forEach((state) -> {
                    Game2dObjectAnimatedImage image = new Game2dObjectAnimatedImage();
                    state.getImage().getFrames().forEach((frame) -> {
                        try {
                            image.addFrame(frame.getAsset(), frame.getWidth(), frame.getHeight());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    states.add(
                        new Game2dObjectState(new ArrayList<>(), image, "default")
                    );
                });
                new Game2dSceneObject(
                    new Game2dObject(states, "default"),
                    canvasObject.getPosition().x,
                    canvasObject.getPosition().y);
                // canvasObject.
                // gameObjects.add();
            } else if (canvasObject instanceof Polygon) {

            } else {

            }
        }

        return objects;
    }
}
