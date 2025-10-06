package com.gamebuilder.canvasobject;

import java.awt.Graphics;

import com.gamebuilder.Point;
import com.gamebuilder.canvasobject.shape.BoundingRect;

public abstract class CanvasObject {
    private boolean selected = false;

    public abstract String getName();
    
    public abstract BoundingRect getBoundingRect();

    public abstract void setSize(int width, int height);

    public abstract void draw(Graphics g);

    public abstract void translate(Point p);

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }
}
