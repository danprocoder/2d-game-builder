package com.gamebuilder.service;

import java.awt.Color;
import java.util.ArrayList;

import com.gamebuilder.CanvasTool;
import com.gamebuilder.Point;
import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.canvasobject.shape.BoundingRect;
import com.gamebuilder.canvasobject.shape.Polygon;
import com.gamebuilder.canvasobject.shape.Text;
import com.gamebuilder.canvasobject.sprite.Sprite;
import com.gamebuilder.canvasobject.sprite.SpriteState;
import com.gamebuilder.model.CanvasModel;
import com.gamebuilder.model.CanvasModelUpdateListener;
import com.gamebuilder.model.Selectable;
import com.gamebuilder.model.Selection;
import com.gamebuilder.util.Log;

public class CanvasService {
    private CanvasModel model;
    private CanvasObject lastItem;
    private ArrayList<CanvasModelUpdateListener> updateListeners = new ArrayList<CanvasModelUpdateListener>();

    public CanvasService(CanvasModel model) {
        this.model = model;
    }

    public CanvasModel getModel() {
        return this.model;
    }

    public int getNumberOfObjects() {
        return this.model.getObjects().size();
    }

    public void setSelectedTool(CanvasTool tool) {
        this.model.setSelectedTool(tool);
        this.notifyUpdate("select_tool");
    }

    public CanvasTool getSelectedTool() {
        return this.model.getSelectedTool();
    }

    public Color getSelectedBgColor() {
        return this.model.getColor();
    }

    public void setSelectedBgColor(Color color) {
        this.model.setColor(color);
        this.notifyUpdate("set_bg_color");
    }

    public ArrayList<Selectable> getSelectableObjects() {
        return this.model.getSelectableObjects();
    }

    public void addNewObject(CanvasObject shape) {
        CanvasObject active = this.model.getActiveObject();

        // TODO: refactor
        if (shape instanceof Sprite || active == null || !(active instanceof Sprite)) {
            this.model.addObject(shape);
            this.setActive(shape);
        } else if (active instanceof Sprite) {
            Sprite sprite = (Sprite) active;
            SpriteState state = sprite.getAt(sprite.getCurrentState());
            if (state.getCollisionBox() == null) {
                state.setCollisionBox(shape);
            } else {
                this.model.addObject(shape);
                this.setActive(shape);
            }
        }
        this.selectObject(shape, false);
        this.lastItem = shape;

        this.notifyUpdate("add_object");
    }

    public ArrayList<CanvasObject> getObjects() {
        return this.model.getObjects();
    }

    public CanvasObject getLastAddedObject() {
        return this.lastItem;
    }

    public void setActive(CanvasObject object) {
        for (CanvasObject co: this.model.getObjects()) {
            co.setActive(false);
        }

        object.setActive(true);

        this.notifyUpdate("set_active_object");
    }

    public CanvasObject getActiveObject() {
        return this.model.getActiveObject();
    }

    public void unfocusAllTextInputs() {
        for (CanvasObject c: this.getObjects()) {
            if (c instanceof Text) ((Text) c).setFocus(false);
        }
    }

    public void resizeObjectBy(CanvasObject object, int dx, int dy) {
        object.setSize(object.getWidth() + dx, object.getHeight() + dy);
        this.notifyUpdate("resize_object_by");
    }

    public CanvasObject getClickedObject(int mx, int my) {
        ArrayList<CanvasObject> objects = this.model.getObjects();
        for (int i = objects.size() - 1; i >= 0; i--) {
            CanvasObject obj = objects.get(i);
            if (obj instanceof Selectable) {
                if (((Selectable) obj).mouseHit(mx, my)) return obj;
            }
        }

        return null;
    }

    public Selectable findAndSelectShape(int x, int y, boolean addToSelection) {
        if (!addToSelection) {
            this.model.deselectAll();
        }

        Selectable object = (Selectable) this.getClickedObject(x, y);
        if (object != null) {
            object.setSelected(true);
            this.notifyUpdate("shape_selected");
            return object;
        }

        return null;
    }

    public void handleClickOnPolygon(Polygon poly) {
        if (poly.getSelected()) {
            poly.setTransform(!poly.getTransform());

            if (poly.getTransform() == false) {
                this.model.setTransformingPolygon(null);
            } else {
                this.model.setTransformingPolygon(poly);
            }
        }
    }

    public Polygon getTransformingPolygon() {
        return this.model.getTransformingPolygon();
    }

    public int getClickedTransformPointIndex(int mx, int my) {
        Polygon poly = this.model.getTransformingPolygon();
        if (poly != null && poly.getTransform()) {
            ArrayList<BoundingRect> tBoxes = poly.getTransformBoxes();
            for (int i = 0; i < tBoxes.size(); i++) {
                if (tBoxes.get(i).hit(mx, my)) return i;
            }
        }
        return -1;
    }

    /** Checks if the mouse is on the line of the transforming polygon */
    public Point[] isOnPolygonLine(int mx, int my) {
        Point mp = new Point(mx, my);

        Polygon poly = this.model.getTransformingPolygon();
        if (poly != null && poly.getTransform()) {
            ArrayList<Point> points = poly.getPoints();
            for (int i = 0; i < points.size(); i++) {
                Point a = points.get(i);
                Point b = points.get(i + 1 == points.size() ? 0 : i + 1);

                Log.d("CanvasService.isOnPolygonLine()",
                        "Distance: c: ca + bc " + (mp.distanceFrom(a) + b.distanceFrom(mp))
                        + ", ba: " + b.distanceFrom(a));
                float sumMouseToPoints = mp.distanceFrom(a) + b.distanceFrom(mp);
                float distBetweenPoints = b.distanceFrom(a);

                if (sumMouseToPoints - distBetweenPoints < 0.5f) {
                    Log.d("CanvasService.isOnPolygonLine()",
                            "Mouse is on line between " + a + " and " + b);
                    return new Point[] {a, b};
                }
            }
        }

        return null;
    }

    /**
     * Adds a new point between two points to a polygon
     *
     * @return the index of the new point, or -1 if the polygon is not in transform mode
     */
    public int addPointToPolygon(Point p1, Point p2, Point newPoint) {
        Polygon poly = this.model.getTransformingPolygon();
        // Make sure polygon is in transform mode
        if (poly != null && poly.getTransform()) {
            for (int i = 0; i < poly.getPoints().size(); i++) {
                if (poly.getPointAt(i).equals(p1)) {
                    poly.insertPointAt(i + 1, newPoint);
                    return i + 1;
                }
            }
        }

        return -1;
    }

    public void removePointFromPolygon(int index) {
        Polygon poly = this.model.getTransformingPolygon();
        // Make sure polygon is in transform mode
        if (poly != null && poly.getTransform()) {
            // TODO: add this check to other places that modify points
            if (poly.getPoints().size() > 3) {
                poly.getPoints().remove(index);
            }
        }
    }

    public void selectObject(CanvasObject object, boolean addToSelection) {
        if (!addToSelection) {
            this.model.deselectAll();
        }
        if (object instanceof Selectable) {
            ((Selectable) object).setSelected(true);
        } else if (object instanceof Sprite) {
            ((Sprite) object).setSelected(true);
        }
    }

    /**
     * Finds all objects currently selected on the canvas and deletes them
     * from the canvas model.
     */
    public void findAndDeleteSelectedObjects() {
        try {
            Selection selection = this.getSelection();
            ArrayList<Selectable> selected = selection.getSelectedObjects();
            Log.d("findAndDeleteSelectedObjects()", "Deleting " + selected.size() + " objects");
            for (Selectable s: selected) {
                this.model.deleteObject((CanvasObject) s);
            }
            this.notifyUpdate("delete_selected_objects");
        } catch (Exception e) {
            Log.e("findAndDeleteSelectedObjects()", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes a canvas object at the provided position
     * @param index the index of the object to delete
     */
    public void deleteObjectAt(int index) {
        try {
            this.model.deleteObject(index);
            this.notifyUpdate("delete_object_at");
        } catch (Exception e) {
            Log.e("deleteObjectAt(" + index + ")", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * TODO: write docs
     */
    public void moveObjectToPosition(int draggedIndex, int dropIndex) {
        Log.d("moveObjectToPosition()", "Moving object from=" + draggedIndex + " to=" + dropIndex);
        CanvasObject removed = this.model.getObjects().remove(draggedIndex);
        this.model.getObjects().add(dropIndex, removed);
        this.notifyUpdate("move_object_to_position");
    }

    /**
     * Returns a Selection object representing all currently selected objects
     */
    public Selection getSelection() {
        return new Selection(this.model.getSelectedObjects(), this);
    }

    /**
     * Moves all objects on the canvas by the provided offset. It adds dx, dy to the x and y
     * coordinates of all objects on the canvas.
     *
     * @param dx how much to move in the x direction
     * @param dy how much to move in the y direction
     */
    public void adjustOffset(int dx, int dy) {
        Point offset = this.model.getOffset();
        this.model.setOffset(new Point(offset.x + dx, offset.y + dy));

        for (CanvasObject obj: this.model.getObjects()) {
            obj.adjustOffset(dx, dy);
        }

        this.notifyUpdate("adjust_offset");
    }

    public int getScreenX() {
        return (int) (this.model.getScreenOffset().x + this.model.getOffset().x);
    }

    public int getScreenY() {
        return (int) (this.model.getScreenOffset().y + this.model.getOffset().y);
    }

    public int getRelativeX(int x) {
        return x - ((int) this.model.getScreenOffset().x + (int) this.model.getOffset().x);
    }

    public int getRelativeY(int y) {
        return y - ((int) this.model.getScreenOffset().y + (int) this.model.getOffset().y);
    }

    public void addUpdateListener(CanvasModelUpdateListener listener) {
        this.updateListeners.add(listener);
    }

    public void removeUpdateListener(CanvasModelUpdateListener listener) {
        this.updateListeners.remove(listener);
    }
    
    public void notifyUpdate(String update) {
        for (CanvasModelUpdateListener listener: this.updateListeners) {
            listener.onCanvasModelUpdated(this.model, update);
        }
    }
}
