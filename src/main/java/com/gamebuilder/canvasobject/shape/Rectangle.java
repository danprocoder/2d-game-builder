package com.gamebuilder.canvasobject.shape;

import java.awt.Color;
import java.awt.Graphics;

import com.gamebuilder.Point;

public class Rectangle extends Shape {
    private Point point;
    private int width;
    private int height;
    private Color color;
    private String name;
    private boolean selected;

    public Rectangle(Point point, int width, int height, Color color, String name) {
        this.point = point;
        this.width = width;
        this.height = height;
        this.color = color;
        this.name = name;
    }

    @Override()
    public String getName() {
        return this.name;
    }

    @Override()
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override()
    public BoundingRect getBoundingRect() {
        BoundingRect rect = new BoundingRect();

        rect.left = (int) this.point.x;
        rect.top = (int) this.point.y;
        rect.right = (int) this.point.x + this.width;
        rect.bottom = (int) this.point.y + this.height;

        return rect;
    }

    @Override()
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override()
    public void translate(Point p) {
        this.point = new Point(p.x, p.y);
    }

    @Override()
    public void draw(Graphics g) {
        g.setColor(this.color);
        g.fillRect((int) this.point.x, (int) this.point.y, this.width, this.height);
    }
}
