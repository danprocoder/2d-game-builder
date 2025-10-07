package com.gamebuilder.canvasobject;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;

import com.gamebuilder.Point;
import com.gamebuilder.canvasobject.shape.BoundingRect;

public abstract class CanvasObject {
    private boolean selected = false;
    private String resizeDirection = null;

    public void setResizeDirection(String direction) {
        this.resizeDirection = direction;
    }

    public String getResizeDirection() {
        return this.resizeDirection;
    }

    public abstract String getName();
    
    public abstract BoundingRect getBoundingRect();

    public abstract void setSize(int width, int height);

    public abstract void draw(Graphics g);

    public abstract void translate(Point p);

    public HashMap<String, BoundingRect> getResizeHandles() {
        HashMap<String, BoundingRect> handles = new HashMap<String, BoundingRect>();
        BoundingRect r = this.getBoundingRect();

        handles.put("top_left", new BoundingRect(r.top - 15, r.left - 5, r.top - 5, r.left - 15));
        handles.put("top_right", new BoundingRect(r.top - 15, r.right + 15, r.top - 5, r.right + 5));
        handles.put("bottom_left", new BoundingRect(r.bottom + 5, r.left - 5, r.bottom + 15, r.left - 15));
        handles.put("bottom_right", new BoundingRect(r.bottom + 5, r.right + 15, r.bottom + 15, r.right + 5));

        return handles;
    }

    protected void drawResizeHandles(Graphics g) {
        HashMap<String, BoundingRect> handles = this.getResizeHandles();
        g.setColor(Color.BLACK);

        BoundingRect topLeft = handles.get("top_left");
        if (this.resizeDirection == "top_left") {
            g.fillRect(topLeft.left, topLeft.top, topLeft.right - topLeft.left, topLeft.bottom - topLeft.top);
        } else {
            g.drawRect(topLeft.left, topLeft.top, topLeft.right - topLeft.left, topLeft.bottom - topLeft.top);
        }

        BoundingRect topRight = handles.get("top_right");
        if (this.resizeDirection == "top_right") {
            g.fillRect(topRight.left, topRight.top, topRight.right - topRight.left, topRight.bottom - topRight.top);
        } else {
            g.drawRect(topRight.left, topRight.top, topRight.right - topRight.left, topRight.bottom - topRight.top);
        }

        BoundingRect bottomLeft = handles.get("bottom_left");
        if (this.resizeDirection == "bottom_left") {
            g.fillRect(bottomLeft.left, bottomLeft.top, bottomLeft.right - bottomLeft.left, bottomLeft.bottom - bottomLeft.top);
        } else {
            g.drawRect(bottomLeft.left, bottomLeft.top, bottomLeft.right - bottomLeft.left, bottomLeft.bottom - bottomLeft.top);
        }

        BoundingRect bottomRight = handles.get("bottom_right");
        if (this.resizeDirection == "bottom_right") {
            g.fillRect(bottomRight.left, bottomRight.top, bottomRight.right - bottomRight.left, bottomRight.bottom - bottomRight.top);
        } else {
            g.drawRect(bottomRight.left, bottomRight.top, bottomRight.right - bottomRight.left, bottomRight.bottom - bottomRight.top);
        }

        BoundingRect r = this.getBoundingRect();
        // top line
        g.drawLine(r.left - 10, r.top - 10, r.right + 10, r.top - 10);
        // right line
        g.drawLine(r.right + 10, r.top - 10, r.right + 10, r.bottom + 10);
        // bottom line
        g.drawLine(r.left - 10, r.bottom + 10, r.right + 10, r.bottom + 10);
        // left line
        g.drawLine(r.left - 10, r.top - 10, r.left - 10, r.bottom + 10);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;

        if (selected == false) {
            this.resizeDirection = null;
        }
    }

    public boolean isSelected() {
        return this.selected;
    }
}
