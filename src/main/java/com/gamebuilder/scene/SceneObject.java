package com.gamebuilder.scene;

import com.gamebuilder.canvasobject.CanvasObject;

public class SceneObject {
    private int x;
    private int y;
    private CanvasObject object;

    public SceneObject(int x, int y, CanvasObject object) {
        this.x = x;
        this.y = y;
        this.object = object;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public CanvasObject getObject() {
        return object;
    }
}
