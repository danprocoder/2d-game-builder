package com.gameengine.obj;

import java.io.IOException;
import java.util.ArrayList;

import com.gamebuilder.model.Asset;

public class Game2dObjectAnimatedImage {
    private ArrayList<Game2dObjectImage> frames = new ArrayList<Game2dObjectImage>();
    private int currentFramePos = 0;
    private boolean continuous = true;
    private long lastFrameTime = 0l;
    private long lastFinishedAt = 0l;
    private int delay = 2000;
    private int fps = 10;
    private boolean endReached = false;
    private float opacity = 100.0f;

    public void addFrame(Asset asset, int w, int h) throws IOException {
        this.frames.add(new Game2dObjectImage(asset.getFullPath(), w, h));
    }

    public Game2dObjectImage getFrameImage() {
        if (!this.continuous && this.endReached) {
           return this.frames.get(0);
        }

        long now = System.currentTimeMillis();
        if (frames.isEmpty()) return null;

        if (this.endReached && (now - this.lastFinishedAt) > this.delay) {
            this.endReached = false;
            this.currentFramePos = 0;
            return this.frames.get(0);
        }

        long diffMillis = now - this.lastFrameTime;
        Game2dObjectImage image = this.frames.get(this.currentFramePos);

        if (diffMillis > (1000 / this.fps)) {
            this.lastFrameTime = System.currentTimeMillis();

            this.currentFramePos++;
            if (this.currentFramePos >= this.frames.size()) {
                this.currentFramePos = 0;
                this.lastFinishedAt = now;
                this.endReached = true;
            }
        }

        return image;
    }
}
