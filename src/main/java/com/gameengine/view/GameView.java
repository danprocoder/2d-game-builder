package com.gameengine.view;

import java.awt.Dimension;

import com.gameengine.Game;
import com.jogamp.opengl.awt.GLCanvas;

public class GameView extends GLCanvas {
    public GameView(Game game) {
        int[] resolution = game.getResolution();
        setPreferredSize(new Dimension(resolution[0], resolution[1]));
        addGLEventListener(new GameCanvas(game));
    }
}
