package com.gamebuilder;

public class Point {
    public float x;
    public float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float distanceFrom(Point p) {
        return (float) Math.sqrt(
            Math.pow(p.x - this.x, 2)
            + Math.pow(p.y - this.y, 2)
        );
    }

    public int getXInt() {
        return (int) this.x;
    }

    public int getYInt() {
        return (int) this.y;
    }

    @Override()
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    @Override()
    public boolean equals(Object obj) {
        Point p = (Point) obj;
        return p != null && p.x == this.x && p.y == this.y;
    }

    @Override()
    public Point clone() {
        return new Point(this.x, this.y);
    }
}
