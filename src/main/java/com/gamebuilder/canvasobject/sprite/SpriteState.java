package com.gamebuilder.canvasobject.sprite;

import com.gamebuilder.canvasobject.CanvasObject;

public class SpriteState {
    private String name;
    private CanvasObject collisionBox;
    private AnimatedSpriteImage image = null;

    public SpriteState(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public CanvasObject getCollisionBox() {
        return this.collisionBox;
    }

    public AnimatedSpriteImage getImage() {
        return this.image;
    }

    public void setCollisionBox(CanvasObject box) {
        this.collisionBox = box;
    }

    public void setImage(AnimatedSpriteImage image) {
        this.image = image;
    }

    public String getXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<State name=\"" + this.name + "\">");
        if (this.collisionBox != null) {
            sb.append("\n  <CollisionBox>");
            sb.append(this.collisionBox.toXml());
            sb.append("\n  </CollisionBox>");
        }
        sb.append(this.image.toXml());
        sb.append("\n</State>");
        return sb.toString();
    }
}
