package com.gamebuilder.model;

import java.awt.Color;
import java.util.ArrayList;

import com.gamebuilder.CanvasTool;
import com.gamebuilder.Point;
import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.canvasobject.shape.Polygon;
import com.gamebuilder.canvasobject.sprite.AnimatedSpriteImage;
import com.gamebuilder.canvasobject.sprite.Sprite;


/**
 * Model representing the state of the canvas, including all objects on it,
 * the selected tool, colors, and offsets.
 */
public class CanvasModel {
    private ArrayList<CanvasObject> objects = new ArrayList<CanvasObject>();
    private Polygon transformingPolygon = null;

    private Color color = Color.RED;
    private CanvasTool selectedTool = CanvasTool.MOVE_TOOL;

    private int screenOffsetX = 50;
    private int screenOffsetY = 50;
    private int currentOffsetX = 0;
    private int currentOffsetY = 0;

    public Point getScreenOffset() {
        return new Point(this.screenOffsetX, this.screenOffsetY);
    }

    public Point getOffset() {
        return new Point(this.currentOffsetX, this.currentOffsetY); 
    }

    public void setOffset(Point newOffset) {
        this.currentOffsetX = (int) newOffset.x;
        this.currentOffsetY = (int) newOffset.y;
    }

    public CanvasObject getActiveObject() {
        for (CanvasObject co: this.objects) {
            if (co.isActive()) {
                return co;
            }
        }

        return null;
    }

    public CanvasTool getSelectedTool() {
        return this.selectedTool;
    }

    public ArrayList<Selectable> getSelectableObjects() {
        ArrayList<Selectable> selectables = new ArrayList<Selectable>();
        for (CanvasObject obj: this.objects) {
            if (obj instanceof Selectable) {
                selectables.add((Selectable) obj);
            } else if (obj instanceof Sprite) {
                Sprite sprite = (Sprite) obj;
                CanvasObject collisionBox = sprite.getAt(sprite.getCurrentState()).getCollisionBox();
                if (collisionBox != null) {
                    selectables.add((Selectable) collisionBox);
                }

                AnimatedSpriteImage image = sprite.getAt(sprite.getCurrentState()).getImage();
                if (image != null) {
                    selectables.add(image);
                }
            }
        }
        return selectables;
    }

    public boolean isObjectSelected(CanvasObject object) {
        if (object instanceof Sprite) {
            Sprite sprite = (Sprite) object;
            CanvasObject obj = sprite.getAt(sprite.getCurrentState()).getCollisionBox();
            if (obj != null) {
                return ((Selectable) obj).getSelected();
            }
            AnimatedSpriteImage image = sprite.getAt(sprite.getCurrentState()).getImage();
            if (image != null) {
                return image.getSelected();
            }
        } else if (object instanceof Selectable) {
            return ((Selectable) object).getSelected();
        }

        return false;
    }

    public void setSelectedTool(CanvasTool tool) {
        this.selectedTool = tool;
    }

    public ArrayList<Selectable> getSelectedObjects() {
        ArrayList<Selectable> selected = new ArrayList<Selectable>();
        for (Selectable s: this.getSelectableObjects()) {
            if (s.getSelected()) {
                selected.add(s);
            }
        }

        return selected;
    }

    public void deselectAll() {
        for (Selectable s: this.getSelectableObjects()) {
            s.setSelected(false);
        }
    }

    public void setTransformingPolygon(Polygon poly) {
        this.transformingPolygon = poly;
    }

    public Polygon getTransformingPolygon() {
        return this.transformingPolygon;
    }

    public void addObject(CanvasObject object) {
        this.objects.add(object);
    }

    public void deleteObject(CanvasObject object) {
        this.objects.remove(object);
    }

    public void deleteObject(int index) {
        this.objects.remove(index);
    }

    public ArrayList<CanvasObject> getObjects() {
        return this.objects;
    }

    public CanvasObject getObjectAt(int index) {
        return this.objects.get(index);
    }

    public int getNumberOfObjects() {
        return this.objects.size();
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
