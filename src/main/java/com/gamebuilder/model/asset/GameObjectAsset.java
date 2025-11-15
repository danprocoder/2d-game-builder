package com.gamebuilder.model.asset;

import com.gamebuilder.canvasobject.CanvasObject;

public class GameObjectAsset extends AbstractAsset {
    private CanvasObject object;

    public GameObjectAsset(CanvasObject object) {
        super();

        this.object = object;
        this.name = object.getName();
    }

    public GameObjectAsset(String id, CanvasObject object) {
        super(id);

        this.object = object;
        this.name = object.getName();
    }

    public CanvasObject getObject() {
        return this.object;
    }

    @Override()
    public String toXml() {
        return "";
    }
}
