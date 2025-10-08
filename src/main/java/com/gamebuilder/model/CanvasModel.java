package com.gamebuilder.model;

import java.awt.Color;
import java.util.ArrayList;

import com.gamebuilder.canvasobject.CanvasObject;

interface CanvasModelDeleteListener {
    public void onDelete(CanvasModel model);
}

public class CanvasModel {
    private ArrayList<CanvasObject> shapes = new ArrayList<CanvasObject>();

    private ArrayList<CanvasModelDeleteListener> deleteListener = new ArrayList<CanvasModelDeleteListener>();
    private ArrayList<CanvasModelAddListener> addListener = new ArrayList<CanvasModelAddListener>();
    private ArrayList<CanvasModelUpdateListener> updateListener = new ArrayList<CanvasModelUpdateListener>();

    private Color color = Color.RED;

    private String selectedTool = null;

    public String getSelectedTool() {
        return this.selectedTool;
    }

    public void setSelectedTool(String tool) {
        this.selectedTool = tool;
        this.notifyUpdate();
    }

    public void addObject(CanvasObject object) {
        this.shapes.add(object);

        for (CanvasModelAddListener listener: this.addListener) {
            listener.onAdd(object, this);
        }
    }

    public void deleteObject(int index) {
        this.shapes.remove(index);

        for (CanvasModelDeleteListener listener: this.deleteListener) {
            listener.onDelete(this);
        }
    }

    public ArrayList<CanvasObject> getObjects() {
        return this.shapes;
    }

    public CanvasObject getObjectAt(int index) {
        return this.shapes.get(index);
    }

    public void deselectAll() {
        for (CanvasObject shape: this.shapes) {
            shape.setSelected(false);
        }
        this.notifyUpdate();
    }

    public CanvasObject getSelectedObject() {
        for (CanvasObject shape: this.shapes) {
            if (shape.isSelected()) {
                return shape;
            }
        }

        return null;
    }

    public int getNumberOfObjects() {
        return this.shapes.size();
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
        this.notifyUpdate();
    }

    public void notifyUpdate() {
        for (CanvasModelUpdateListener listener: this.updateListener) {
            listener.onCanvasModelUpdated(this);
        }
    }

    public void addUpdateListener(CanvasModelUpdateListener listener) {
        this.updateListener.add(listener);
    }

    public void addOnAddListener(CanvasModelAddListener listener) {
        this.addListener.add(listener);
    }

    public void addDeleteListener(CanvasModelDeleteListener listener) {
        this.deleteListener.add(listener);
    }
}
