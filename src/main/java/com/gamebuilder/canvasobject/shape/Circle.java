package com.gamebuilder.canvasobject.shape;

import java.awt.Color;
import java.awt.Graphics;

import com.gamebuilder.Point;

public class Circle extends Shape {
    private Point p;
    private int width;
    private int height;
    private Color color;

    public Circle(Point p, int width, int height, Color color, String name) {
        this.p = p;
        this.width = width;
        this.height = height;
        this.color = color;
        setName(name);
    }

    @Override()
    public BoundingRect getBoundingRect() {
        BoundingRect rect = new BoundingRect(
            (int) this.p.y,
            (int) this.p.x + this.width,
            (int) this.p.y + this.height,
            (int) this.p.x
        );

        return rect;
    }

    @Override()
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override()
    public void translate(Point p) {
        this.p = new Point(p.x, p.y);
    }

    @Override()
    public void draw(Graphics g) {
        g.setColor(this.color);
        g.fillOval((int) this.p.x, (int) this.p.y, this.width, this.height);

        if (this.isSelected()) {
            this.drawResizeHandles(g);
        }
    }
}
