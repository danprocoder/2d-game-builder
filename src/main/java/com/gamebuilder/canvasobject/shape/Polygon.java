package com.gamebuilder.canvasobject.shape;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;

import com.gamebuilder.Point;

public class Polygon extends Shape {
    private ArrayList<Point> pts = new ArrayList<Point>();
    private Color color;
    private String name;

    public Polygon(Point startingPoint, Color color, String name) {
        this.pts.add(startingPoint);
        this.color = color;
        this.name = name;
    }

    public void addPoint(Point p) {
        this.pts.add(p);
    }

    public ArrayList<Point> getPoints() {
        return this.pts;
    }

    @Override()
    public String getName() {
        return this.name;
    }

    @Override()
    public BoundingRect getBoundingRect() {
        BoundingRect rect = new BoundingRect();

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point pt: this.pts) {
            if (pt.x < minX) minX = (int) pt.x;
            if (pt.x > maxX) maxX = (int) pt.x;
            if (pt.y < minY) minY = (int) pt.y;
            if (pt.y > maxY) maxY = (int) pt.y;
        }

        rect.left = minX;
        rect.top = minY;
        rect.right = maxX;
        rect.bottom = maxY;

        return rect;
    }

    @Override()
    public void setSize(int width, int height) {

    }

    @Override()
    public void translate(Point p) {
        BoundingRect r = this.getBoundingRect();
        for (Point pt: this.pts) {
            int dx = (int) (p.x - r.left);
            int dy = (int) (p.y - r.top);
            pt.x += dx;
            pt.y += dy;
        }
    }

    @Override()
    public void draw(Graphics g) {
        g.setColor(this.color);
        
        int[] x = new int[this.pts.size()];
        int[] y = new int[this.pts.size()];
        for (int i = 0; i < this.pts.size(); i++) {
            x[i] = (int) this.pts.get(i).x;
            y[i] = (int) this.pts.get(i).y;
        }
        g.fillPolygon(x, y, this.pts.size());
    }
}
