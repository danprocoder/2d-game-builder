package com.gameengine;

import com.gameengine.view.GameView;

public class GameEngine {
    private Game game;
    private GameView view;

    public GameEngine(Game game) {
        this.game = game;
        view = new GameView(this.game);
    }

    public GameView getView() {
        return this.view;
    }
}
