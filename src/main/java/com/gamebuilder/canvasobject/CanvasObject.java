package com.gamebuilder.canvasobject;

import java.awt.Graphics;

import com.gamebuilder.Point;
import com.gamebuilder.canvasobject.shape.BoundingRect;

public abstract class CanvasObject {
    public abstract String getName();
    
    public abstract BoundingRect getBoundingRect();

    public abstract void setSize(int width, int height);

    public abstract void draw(Graphics g);

    public abstract void translate(Point p);
}
