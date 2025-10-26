package com.gameengine;

import com.gameengine.obj.Game2dObject;

public class GameCamera {
    private int x;
    private int y;
    private int width;
    private int height;
    private Game2dObject objectToTrack;

    public GameCamera(int width, int height) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
    }

    public void track(Game2dObject object) {
        this.objectToTrack = object;
    }
}
