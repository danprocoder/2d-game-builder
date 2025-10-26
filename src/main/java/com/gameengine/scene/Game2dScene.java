package com.gameengine.scene;

import java.util.ArrayList;

import com.gamebuilder.util.Log;
import com.gameengine.sound.Audio;
import com.jogamp.opengl.GLAutoDrawable;

public class Game2dScene {
    private ArrayList<Game2dSceneObject> sceneObjects = new ArrayList<Game2dSceneObject>();
    private Audio backgroundAudio;

    public Game2dScene() {}

    public void addSceneObject(Game2dSceneObject sceneObject) {
        this.sceneObjects.add(sceneObject);
    }

    public void setBackgroundAudio(Audio backgroundAudio) {
        this.backgroundAudio = backgroundAudio;
    }

    public void drawScene(GLAutoDrawable drawable) {
        if (this.backgroundAudio != null) {
            try {
                this.backgroundAudio.play();
            } catch (Exception e) {
                Log.e("Game2dScene.drawScene(d)", "Error playing background audio: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        for (Game2dSceneObject sceneObject : this.sceneObjects) {
            sceneObject.drawObject(drawable);
        }
    }
}
