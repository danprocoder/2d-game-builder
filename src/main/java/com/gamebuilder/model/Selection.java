package com.gamebuilder.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.gamebuilder.canvasobject.shape.BoundingRect;
import com.gamebuilder.service.CanvasService;

/**
 * Class representing all the objects currently selected on the canvas and provides methods
 * to manipulate them as a group.
 */
public class Selection {
    private ArrayList<Selectable> selected;
    CanvasService service;

    public Selection(ArrayList<Selectable> selected, CanvasService service) {
        this.selected = selected;
        this.service = service;
    }

    public boolean isEmpty() {
        return this.selected.size() == 0;
    }

    public BoundingRect getBoundingRect() {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Selectable s: this.selected) {
            BoundingRect r = s.getBoundingRect();
            if (r == null) return null;
            
            if (r.left < minX) {
                minX = r.left;
            }
            if (r.right > maxX) {
                maxX = r.right;
            }
            if (r.top < minY) {
                minY = r.top;
            }
            if (r.bottom > maxY) {
                maxY = r.bottom;
            }
        }

        return new BoundingRect(minY, maxX, maxY, minX);
    }

    public HashMap<String, BoundingRect> getResizeHandles() {
        HashMap<String, BoundingRect> handles = new HashMap<String, BoundingRect>();
        BoundingRect r = this.getBoundingRect();

        handles.put("top_left", new BoundingRect(r.top, r.left, r.top, r.left).grow(4));
        handles.put("top_right", new BoundingRect(r.top, r.right, r.top, r.right).grow(4));
        handles.put("bottom_left", new BoundingRect(r.bottom, r.left, r.bottom, r.left).grow(4));
        handles.put("bottom_right", new BoundingRect(r.bottom, r.right, r.bottom, r.right).grow(4));

        return handles;
    }

    public String getResizeDirection(int mx, int my) {
        HashMap<String, BoundingRect> handles = this.getResizeHandles();
        for (String direction: handles.keySet()) {
            if (handles.get(direction).hit(mx, my)) {
                return direction;
            }
        }

        return null;
    }

    public void translate(int dx, int dy) {
        for (Selectable s: this.selected) {
            s.translate(dx, dy);
        }
    }

    public void increaseSizeBy(String direction, int dx, int dy) {
        for (Selectable s: this.selected) {
            BoundingRect rect = s.getBoundingRect();
            if (direction == "bottom_right") {
                s.setSize(rect.right - rect.left + dx, rect.bottom - rect.top + dy);
            } else if (direction == "bottom_left") {
                s.setSize(rect.right - rect.left - dx, rect.bottom - rect.top + dy);
                s.translate(dx, 0);
            } else if (direction == "top_right") {
                s.setSize(rect.right - rect.left + dx, rect.bottom - rect.top - dy);
                s.translate(0, dy);
            } else if (direction == "top_left") {
                s.setSize(rect.right - rect.left - dx, rect.bottom - rect.top - dy);
                s.translate(dx, dy);
            }
        }
    }

    public int getNumberSelected() {
        return this.selected.size();
    }

    public ArrayList<Selectable> getSelectedObjects() {
        return this.selected;
    }
}
