package com.gamebuilder.model;

import java.util.ArrayList;

import com.gamebuilder.scene.Scene;


public class SceneModel {
    private ArrayList<Scene> scenes;

    public SceneModel() {
        this.scenes = new ArrayList<>();
    }

    public ArrayList<Scene> getScenes() {
        return scenes;
    }

    public void addScene(Scene scene) {
        this.scenes.add(scene);
    }

    public void setScenes(ArrayList<Scene> scenes) {
        this.scenes = scenes;
    }
}
