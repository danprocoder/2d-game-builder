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
    public BoundingRect getBoundingRect() {
        return new BoundingRect(
            (int) this.point.y,
            (int) this.point.x + this.width,
            (int) this.point.y + this.height,
            (int) this.point.x
        );
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

        if (this.isSelected()) {
            this.drawResizeHandles(g);
        }
    }
}
