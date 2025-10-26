package com.gameengine.obj;

import java.util.ArrayList;

public class Game2dObjectState {
    private ArrayList<Object> collisionBoxes = new ArrayList<Object>();
    private Game2dObjectAnimatedImage image;
    private String name;

    public Game2dObjectState(ArrayList<Object> collisionBoxes, Game2dObjectAnimatedImage image, String name) {
        this.collisionBoxes = collisionBoxes;
        this.image = image;
        this.name = name;
    }

    public ArrayList<Object> getCollisionBoxes() {
        return this.collisionBoxes;
    }

    public String getName() {
        return this.name;
    }

    public Game2dObjectAnimatedImage getImage() {
        return this.image;
    }
}
