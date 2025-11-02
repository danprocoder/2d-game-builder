package com.gamebuilder.canvasobject.shape;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;

import com.gamebuilder.Point;
import com.gamebuilder.model.Event;
import com.gamebuilder.util.ColorHelper;

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

    public Polygon(ArrayList<Point> points, Color color, String name) {
        this.pts = points;
        this.color = color;
        setName(name);
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void addPoint(Point p) {
        this.pts.add(p);
    }

    /**
     * Adds a new point to the index provided, add points currently at index and above will be shifted
     * to the right.
     *
     * @param index the index to insert at
     * @param newPoint the point to insert
     */
    public void insertPointAt(int index, Point newPoint) {
        this.pts.add(index, newPoint);
    }

    public ArrayList<Point> getPoints() {
        return this.pts;
    }

    /** Returns the point at the provided index */
    public Point getPointAt(int index) {
        return this.pts.get(index);
    }

    /**
     * Sets the polygon state to selected. If polygon is currently in transform mode and
     * 
     */
    @Override()
    public void setSelected(boolean selected) {
        if (selected == false) {
            this.transform = false;
            this.transformPointIndex = -1;
        }

        super.setSelected(selected);
    }

    @Override()
    public boolean mouseHit(int mx, int my) {
        int numPoints = this.pts.size();
        int x[] = new int[numPoints];
        int y[] = new int[numPoints];
        for (int i = 0; i < numPoints; i++) {
            x[i] = getOffsetX() + pts.get(i).getXInt();
            y[i] = getOffsetY() + pts.get(i).getYInt();
        }
        return new java.awt.Polygon(x, y, x.length).contains(mx, my);
    }

    @Override()
    public int getWidth() {
        BoundingRect r = this.getBoundingRect();
        return r.right - r.left;
    }

    @Override()
    public int getHeight() {
        BoundingRect r = this.getBoundingRect();
        return r.bottom - r.top;
    }

    @Override()
    public BoundingRect getBoundingRect() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point pt: this.pts) {
            if (pt.x < minX) minX = pt.getXInt();
            if (pt.x > maxX) maxX = pt.getXInt();
            if (pt.y < minY) minY = pt.getYInt();
            if (pt.y > maxY) maxY = pt.getYInt();
        }

        return new BoundingRect(
            getOffsetY() + minY,
            getOffsetX() + maxX,
            getOffsetY() + maxY,
            getOffsetX() + minX
        );
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
    public void translate(int dx, int dy) {
        for (Point pt: this.pts) {
            pt.x += dx;
            pt.y += dy;
        }
    }

    @Override()
    public void draw(Graphics g) {
        // TODO: optimize this
        g.setColor(this.color);

        int numPoints = this.pts.size();
        int[] x = new int[numPoints];
        int[] y = new int[numPoints];
        for (int i = 0; i < numPoints; i++) {
            Point pt = this.pts.get(i);
            x[i] = getOffsetX() + pt.getXInt();
            y[i] = getOffsetY() + pt.getYInt();
        }
        g.fillPolygon(x, y, numPoints);

        if (this.transform) {
            ArrayList<BoundingRect> boxes = this.getTransformBoxes();
            for (int i = 0; i < boxes.size(); i++) {
                BoundingRect box = boxes.get(i);

                g.setColor(Color.BLACK);
                g.drawLine(
                    box.left + 5,
                    box.top + 5,
                    boxes.get(i + 1 < boxes.size() ? i + 1 : 0).left + 5,
                    boxes.get(i + 1 < boxes.size() ? i + 1 : 0).top + 5
                );

                if (1 != 0) {
                    drawTransformBoxes(g, i);
                }
            }
            drawTransformBoxes(g, 0);
        }
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

    public void moveTransformPoint(int index, int dx, int dy) {
        if (index >= 0 && index < this.pts.size()) {
            Point pt = this.pts.get(index);
            pt.x += dx;
            pt.y += dy;
        }
    }

    private void drawTransformBoxes(Graphics g, int index) {
        BoundingRect box = this.getTransformBoxes().get(index);
        if (this.transformPointIndex == index) {
            g.setColor(Color.BLACK);
            g.fillRect(box.left, box.top, box.right - box.left, box.bottom - box.top);
        } else {
            g.setColor(Color.WHITE);
            g.fillOval(box.left, box.top, box.right - box.left, box.bottom - box.top);

            g.setColor(Color.BLACK);
            g.drawOval(box.left, box.top, box.right - box.left, box.bottom - box.top);
        }
    }

    public ArrayList<BoundingRect> getTransformBoxes() {
        ArrayList<BoundingRect> boxes = new ArrayList<BoundingRect>();
        for (Point pt: this.pts) {
            boxes.add(
                new BoundingRect(
                    getOffsetY() + pt.getYInt() - 5,
                    getOffsetX() + pt.getXInt() + 5,
                    getOffsetY() + pt.getYInt() + 5,
                    getOffsetX() + pt.getXInt() - 5
                )
            );
        }
        return boxes;
    }

    // TODO: optimise this
    @Override()
    public String getTooltipText() {
        BoundingRect box = this.getBoundingRect();
        return String.format(
            "%s (x: %d, y: %d, w: %d, h: %d)",
            this.name,
            box.left,
            box.top,
            box.right - box.left,
            box.bottom - box.top
        );
    }

    @Override()
    public String toXml() {
        StringBuilder xml = new StringBuilder();
        xml.append(
            String.format(
                "\n<Polygon name=\"%s\" color=\"%s\">",
                this.getName(),
                ColorHelper.getHexFromColor(this.color)
            )
        );
        for (Point pt: this.pts) {
            xml.append(
                String.format(
                    "\n  <Point x=\"%d\" y=\"%d\" />",
                    pt.getXInt(),
                    pt.getYInt()
                )
            );
        }

        if (this.getEvents().size() > 0) {
            xml.append("\n<Events>");
            for (Event event: this.getEvents()) {
                xml.append(event.toXml());
            }
            xml.append("\n</Events>");
        }

        xml.append("\n</Polygon>");
        return xml.toString();
    }
}