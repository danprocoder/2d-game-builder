package com.gamebuilder.service;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.event.ChangeListener;

import com.gamebuilder.util.EventEmitter;
import com.gamebuilder.util.Log;
import com.gamebuilder.view.GameRunnerWindow;
import com.gameengine.Game;

public class RunProjectService {
    private static RunProjectService instance = null;

    private boolean isRunning = false;
    private boolean isPaused = false;
    private GameRunnerWindow gameRunnerView;

    private EventEmitter<ChangeListener> changeListener = new EventEmitter<>();

    private RunProjectService() {}

    public static RunProjectService getInstance() {
        if (instance == null) {
            instance = new RunProjectService();
        }

        return instance;
    }

    public void start(SceneService sceneService) {
        this.isRunning = true;
        this.changeListener.emit(e -> e.stateChanged(null));

        this.gameRunnerView = new GameRunnerWindow();
        this.gameRunnerView.addWindowListener(new WindowAdapter() {
            @Override()
            public void windowClosing(WindowEvent e) {
                gameRunnerView = null;
                handleGameStopped();
            }
        });
        this.gameRunnerView.setVisible(true);

        Thread loadingThread = new Thread(() -> {
            Log.v("RunGameAction.actionPerformed()", "Thread started to load game");
            Game game = GameLoaderService.getInstance().loadGame(sceneService);
            gameRunnerView.setSize(game.getResolution()[0], game.getResolution()[1]);
            gameRunnerView.startGame(game);
        });
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public void pause() {
        this.isPaused = true;
        this.gameRunnerView.pause();
        this.changeListener.emit(ev -> ev.stateChanged(null));
    }

    public void resume() {
        this.isPaused = false;
        this.gameRunnerView.resume();
        this.changeListener.emit(ev -> ev.stateChanged(null));
    }

    public void stop() {
        this.gameRunnerView.close();

        this.handleGameStopped();
    }

    public void handleGameStopped() {
        this.isRunning = false;
        this.isPaused = false;
        changeListener.emit(ev -> ev.stateChanged(null));
    }

    public void addListener(ChangeListener listener) {
        this.changeListener.subscribe(listener);
    }
}
