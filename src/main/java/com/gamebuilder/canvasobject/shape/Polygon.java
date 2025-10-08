package com.gamebuilder.canvasobject.shape;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;

import com.gamebuilder.Point;

public class Polygon extends Shape {
    private ArrayList<Point> pts = new ArrayList<Point>();
    private Color color;
    private boolean transform = false;
    private int transformPointIndex = -1;

    public Polygon(Point startingPoint, Color color, String name) {
        this.pts.add(startingPoint);
        this.color = color;
        setName(name);
    }

    public void setTransform(boolean transform) {
        this.transform = transform;

        if (transform == false) {
            this.transformPointIndex = -1;
        }
    }

    public boolean getTransform() {
        return this.transform;
    }

    public void setTransformPointIndex(int index) {
        this.transformPointIndex = index;
    }

    public int getTransformPointIndex() {
        return this.transformPointIndex;
    }

    public void moveTransformPoint(int dx, int dy) {
        if (this.transformPointIndex != -1 && this.transformPointIndex < this.pts.size()) {
            Point pt = this.pts.get(this.transformPointIndex);
            pt.x += dx;
            pt.y += dy;
        }
    }

    public void addPoint(Point p) {
        this.pts.add(p);
    }

    public ArrayList<Point> getPoints() {
        return this.pts;
    }

    @Override()
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        if (selected == false) {
            this.transform = false;
            this.transformPointIndex = -1;
        }
    }

    @Override()
    public BoundingRect getBoundingRect() {
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

        return new BoundingRect(minY, maxX, maxY, minX);
    }

    public ArrayList<BoundingRect> getTransformBoxes() {
        ArrayList<BoundingRect> boxes = new ArrayList<BoundingRect>();
        for (Point pt: this.pts) {
            boxes.add(
                new BoundingRect(
                    (int) (pt.y - 5),
                    (int) (pt.x + 5),
                    (int) (pt.y + 5),
                    (int) (pt.x - 5)
                )
            );
        }
        return boxes;
    }

    @Override()
    public void setSize(int width, int height) {
        BoundingRect r = this.getBoundingRect();
        float scaleX = (float) width / (r.right - r.left);
        float scaleY = (float) height / (r.bottom - r.top);

        // Resize around center
        float cx = (r.right - r.left) / 2;
        float cy = (r.bottom - r.top) / 2;

        for (Point pt: this.pts) {
            pt.x = cx + (scaleX * (pt.x - cx));
            pt.y = cy + (scaleY * (pt.y - cy));
        }
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

        if (this.transform) {
            ArrayList<BoundingRect> boxes = this.getTransformBoxes();
            g.setColor(Color.BLACK);
            for (int i = 0; i < boxes.size(); i++) {
                BoundingRect box = boxes.get(i);

                if (this.transformPointIndex != -1 && this.transformPointIndex == i) {
                    g.fillRect(box.left, box.top, box.right - box.left, box.bottom - box.top);
                } else {
                    g.drawRect(box.left, box.top, box.right - box.left, box.bottom - box.top);
                }

                g.drawLine(
                    box.left + 5,
                    box.top + 5,
                    boxes.get(i + 1 < boxes.size() ? i + 1 : 0).left + 5,
                    boxes.get(i + 1 < boxes.size() ? i + 1 : 0).top + 5
                );
            }
        } else if (this.isSelected()) {
            this.drawResizeHandles(g);
        }
    }
}
