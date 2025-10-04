package com.gamebuilder.canvasobject.shape;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;

import com.gamebuilder.Point;

public class Polygon extends Shape {
    private ArrayList<Point> pts = new ArrayList<Point>();
    private Color color;

    public Polygon(Point startingPoint, Color color) {
        this.pts.add(startingPoint);
        this.color = color;
    }

    @Override()
    public BoundingRect getBoundingRect() {
        BoundingRect rect = new BoundingRect();

        return rect;
    }

    @Override()
    public void setSize(int width, int height) {

    }

    @Override()
    public void translate(Point p) {
    }

    @Override()
    public void draw(Graphics g) {
        
    }
}
