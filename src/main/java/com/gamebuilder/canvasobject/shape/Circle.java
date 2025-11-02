package com.gamebuilder.canvasobject.shape;

import java.awt.Color;
import java.awt.Graphics;

import com.gamebuilder.Point;
import com.gamebuilder.model.Event;
import com.gamebuilder.util.ColorHelper;

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

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getX() {
        return (int) this.p.x;
    }

    public int getY() {
        return (int) this.p.y;
    }

    public void setX(int x) {
        this.p = new Point(x, this.p.y);
    }

    public void setY(int y) {
        this.p = new Point(this.p.x, y);
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override()
    public BoundingRect getBoundingRect() {
        BoundingRect rect = new BoundingRect(
            getOffsetY() + this.p.getYInt(),
            getOffsetX() + this.p.getXInt() + this.width,
            getOffsetY() + this.p.getYInt() + this.height,
            getOffsetX() + this.p.getXInt()
        );

        return rect;
    }

    @Override()
    public boolean mouseHit(int mx, int my) {
        return this.getBoundingRect().hit(mx, my);
    }

    @Override()
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override()
    public void translate(int dx, int dy) {
        this.p = new Point(this.p.x + dx, this.p.y + dy);
    }

    @Override()
    public void draw(Graphics g) {
        g.setColor(this.color);
        g.fillOval(
            getOffsetX() + this.p.getXInt(),
            getOffsetY() + this.p.getYInt(),
            this.width,
            this.height
        );
    }

    @Override()
    public String getTooltipText() {
        return String.format(
            "%s (x: %d, y: %d, w: %d, h: %d)",
            this.name,
            (int) this.p.x,
            (int) this.p.y,
            this.width,
            this.height
        );
    }

    @Override()
    public String toXml() {
        StringBuilder xml = new StringBuilder();
        xml.append(String.format(
            "\n<Circle name=\"%s\" x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" color=\"%s\">",
            this.getName(),
            (int) this.p.x,
            (int) this.p.y,
            this.width,
            this.height,
            ColorHelper.getHexFromColor(this.color)
        ));

        if (this.getEvents().size() > 0) {
            xml.append("\n<Events>");
            for (Event event: this.getEvents()) {
                xml.append(event.toXml());
            }
            xml.append("\n</Events>");
        }

        xml.append("\n</Circle>");

        return xml.toString();
    }
}
