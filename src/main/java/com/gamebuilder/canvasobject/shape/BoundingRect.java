package com.gamebuilder.canvasobject.shape;

public class BoundingRect {
    public int left;
    public int top;
    public int right;
    public int bottom;

    public boolean hit(int x, int y) {
        return x >= this.left && x <= this.right && y >= this.top && y <= this.bottom;
    }
}
