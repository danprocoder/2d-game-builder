package com.gamebuilder.canvasobject.shape;

import java.awt.Color;
import java.awt.Graphics;

import com.gamebuilder.Point;
import com.gamebuilder.model.Event;
import com.gamebuilder.util.ColorHelper;

public class Rectangle extends Shape {
    private Point point;
    private int width;
    private int height;
    private Color color;

    public Rectangle(Point point, int width, int height, Color color, String name) {
        this.point = point;
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
        return (int) this.point.x;
    }

    public int getY() {
        return (int) this.point.y;
    }

    public void setX(int x) {
        this.point = new Point(x, this.point.y);
    }

    public void setY(int y) {
        this.point = new Point(this.point.x, y);
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
    public boolean mouseHit(int mx, int my) {
        return this.getBoundingRect().hit(mx, my);
    }

    @Override()
    public BoundingRect getBoundingRect() {
        return new BoundingRect(
            getOffsetY() + this.point.getYInt(),
            getOffsetX() + this.point.getXInt() + this.width,
            getOffsetY() + this.point.getYInt() + this.height,
            getOffsetX() + this.point.getXInt()
        );
    }

    @Override()
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override()
    public void translate(int dx, int dy) {
        this.point = new Point(this.point.x + dx, this.point.y + dy);
    }

    @Override()
    public void draw(Graphics g) {
        g.setColor(this.color);
        g.fillRect(
            getOffsetX() + this.point.getXInt(),
            getOffsetY() + this.point.getYInt(),
            this.width,
            this.height
        );
    }

    @Override()
    public String getTooltipText() {
        return String.format(
            "%s (x: %d, y: %d, w: %d, h: %d)",
            this.name,
            (int) this.point.getXInt(),
            (int) this.point.getYInt(),
            this.width,
            this.height
        );
    }

    @Override()
    public String toXml() {
        StringBuilder xml = new StringBuilder();
        xml.append(
            String.format(
                "\n<Rectangle name=\"%s\" x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" color=\"%s\">",
                this.getName(),
                (int) this.point.x,
                (int) this.point.y,
                this.width,
                this.height,
                ColorHelper.getHexFromColor(this.color)
            )
        );

        if (this.getEvents().size() > 0) {
            xml.append("\n<Events>");
            for (Event event: this.getEvents()) {
                xml.append(event.toXml());
            }
            xml.append("\n</Events>");
        }

        xml.append("\n</Rectangle>");

        return xml.toString();
    }
}
