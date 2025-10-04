package com.gamebuilder;

public class Point {
    public float x, y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override()
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
