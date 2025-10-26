package com.gameengine.scene;

import com.gamebuilder.util.Log;
import com.gameengine.obj.Game2dObject;
import com.jogamp.opengl.GLAutoDrawable;

public class Game2dSceneObject {
    private Game2dObject object;
    private float x;
    private float y;

    public Game2dSceneObject(Game2dObject object, float initialX, float initialY) {
        this.object = object;
        this.x = initialX;
        this.y = initialY;
    }

    public void drawObject(GLAutoDrawable drawable) {
        Log.d("Game2dSceneObject.drawObject()", "Called to draw object.");
        object.draw(drawable, this.x, this.y);
    }
}
