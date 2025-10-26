package com.gameengine;

import java.util.ArrayList;

import com.gameengine.obj.Game2dObject;
import com.gameengine.scene.Game2dScene;


public class Game {
    private String version = "1.0";
    private String name = "Name of game";
    private int width = 640;
    private int height = 450;

    private ArrayList<Game2dObject> objects;
    private ArrayList<Game2dScene> scenes;
    private int currentSceneIndex = 0;

    public Game(
        String version,
        String name,
        int[] resolution,
        ArrayList<Game2dObject> object,
        ArrayList<Game2dScene> scenes
    ) {
        this.version = version;
        this.name = name;
        this.width = resolution[0];
        this.height = resolution[1];
        this.objects = object;
        this.scenes = scenes;
    }

    public int[] getResolution() {
        return new int[] { this.width, this.height };
    }

    public String getName() {
        return this.name;
    }

    public Game2dScene getCurrentScene() {
        return this.scenes.get(this.currentSceneIndex);
    }

    public ArrayList<Game2dObject> get2dObjects() {
        return this.objects;
    }

    public ArrayList<Game2dScene> get2dScenes() {
        return this.scenes;
    }

    @Override()
    public String toString() {
        return String.format("%s %s (%d x %d)", this.name, this.version, this.width, this.height);
    }
}
