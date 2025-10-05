package com.gamebuilder.model;

import java.util.ArrayList;

import com.gamebuilder.canvasobject.CanvasObject;

interface CanvasModelDeleteListener {
    public void onDelete(CanvasModel model);
}

public class CanvasModel {
    private ArrayList<CanvasObject> shapes = new ArrayList<CanvasObject>();

    private ArrayList<CanvasModelDeleteListener> deleteListener = new ArrayList<CanvasModelDeleteListener>();
    private ArrayList<CanvasModelAddListener> addListener = new ArrayList<CanvasModelAddListener>();

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

    public int getNumberOfObjects() {
        return this.shapes.size();
    }

    public void addOnAddListener(CanvasModelAddListener listener) {
        this.addListener.add(listener);
    }

    public void addDeleteListener(CanvasModelDeleteListener listener) {
        this.deleteListener.add(listener);
    }
}
