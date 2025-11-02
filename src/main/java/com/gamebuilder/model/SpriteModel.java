package com.gamebuilder.model;

import java.util.ArrayList;

import com.gamebuilder.canvasobject.sprite.Sprite;

public class SpriteModel {
    private ArrayList<Sprite> sprites = new ArrayList<Sprite>();
    private ArrayList<SpriteModelUpdateListener> updateListeners = new ArrayList<SpriteModelUpdateListener>();

    private static SpriteModel instance = null;

    private Sprite selectedSprite = null;

    public static SpriteModel getInstance() {
        if (instance == null) {
            instance = new SpriteModel();
        }
        return instance;
    }

    public void addSprite(Sprite sprite) {
        this.sprites.add(sprite);
        this.notifyUpdate();
    }

    public ArrayList<Sprite> getSprites() {
        return this.sprites;
    }

    public Sprite getSelectedSprite() {
        return this.selectedSprite;
    }

    public void setSelectedSprite(Sprite sprite) {
        this.selectedSprite = sprite;
        this.notifyUpdate();
    }

    public void addUpdateListener(SpriteModelUpdateListener listener) {
        this.updateListeners.add(listener);
    }

    public void notifyUpdate() {
        for (SpriteModelUpdateListener listener: this.updateListeners) {
            listener.onSpriteModelUpdated();
        }
    }
}
