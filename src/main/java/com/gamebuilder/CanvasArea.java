package com.gamebuilder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.canvasobject.shape.Polygon;
import com.gamebuilder.canvasobject.shape.BoundingRect;
import com.gamebuilder.canvasobject.shape.Circle;
import com.gamebuilder.canvasobject.shape.Rectangle;
import com.gamebuilder.model.CanvasModel;
import com.gamebuilder.model.CanvasModelUpdateListener;

public class CanvasArea extends JPanel implements MouseListener, MouseMotionListener, CanvasModelUpdateListener {

    private String selectedTool;
    private float currentMouseX;
    private float currentMouseY;
    private float lastMouseDragX = 0;
    private float lastMouseDragY = 0;
    private CanvasModel model;

    public CanvasArea(GameBuilder gameBuilder, CanvasModel model) {
        addMouseListener(this);
        addMouseMotionListener(this);

        this.model = model;
        model.addUpdateListener(this);
    }

    @Override()
    public void onCanvasModelUpdated(CanvasModel model) {
        this.repaint();
    }

    @Override()
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        String tool = this.model.getSelectedTool();
        if (tool != null) {
            g.drawString("Selected Tool: " + tool, 20, 20);
        }
    
        for (CanvasObject s: this.model.getObjects()) {
            s.draw(g);
        }

        if (this.selectedTool == "drawing_polygon") {
            g.setColor(Color.BLACK);

            // TODO: Check to be sure that the selected object is a polygon
            ArrayList<Point> pts = ((Polygon) this.model.getSelectedObject()).getPoints();

            // Show polygon lines
            for (int i = 0; i < pts.size(); i++) {
                if (i + 1 < pts.size()) {
                    g.drawLine(
                        (int) pts.get(i).x,
                        (int) pts.get(i).y,
                        (int) pts.get(i + 1).x,
                        (int) pts.get(i + 1).y
                    );
                }

                g.drawRect((int) pts.get(i).x - 2, (int) pts.get(i).y - 2, 4, 4);
            }
            Point lastPoint = pts.get(pts.size() - 1);
            g.drawLine((int) lastPoint.x, (int) lastPoint.y, (int) this.currentMouseX, (int) this.currentMouseY);
        }

    }

    @Override()
    public void mousePressed(MouseEvent event) {
        this.lastMouseDragX = event.getX();
        this.lastMouseDragY = event.getY();

        if (this.selectedTool == "move") {
            CanvasObject selectedObject = this.model.getSelectedObject();
            if (selectedObject != null) {
                String resizeDir = this.isOverResizeHandle(selectedObject, event.getX(), event.getY());
                int transformHandleIndex = -1;
                if (selectedObject instanceof Polygon) {
                    transformHandleIndex = this.isOverPolygonTransformHandle((Polygon) selectedObject, event.getX(), event.getY());
                }
                boolean clickInsideBox = selectedObject.getBoundingRect().hit(event.getX(), event.getY());

                if (resizeDir != null) {
                    selectedObject.setResizeDirection(resizeDir);
                } else if (selectedObject instanceof Polygon && transformHandleIndex != -1) {
                    ((Polygon) selectedObject).setTransformPointIndex(transformHandleIndex);
                } else if (clickInsideBox) {
                    this.handleClickInsideShape(selectedObject);
                } else {
                    this.findAndSelectShape(event.getX(), event.getY());
                }
            } else {
                this.findAndSelectShape(event.getX(), event.getY());
            }
        } else if (this.selectedTool == "drawing_polygon") {
            Polygon p = (Polygon) this.model.getSelectedObject();

            Point nextPoint = new Point(event.getX(), event.getY());

            if (Math.abs(nextPoint.distanceFrom(p.getPoints().get(0))) < 5) {
                // This completes the polygon because setting the tool back to "polygon" will
                // cause a new polygon to be started on the next click
                this.selectedTool = "polygon";
            } else {
                p.addPoint(nextPoint);
            }
        } else if (this.selectedTool != null) {
            CanvasObject object = null;
            switch (this.selectedTool) {
                case "polygon":
                    object = new Polygon(new Point(event.getX(), event.getY()), this.model.getColor(), "Polygon " + (this.model.getNumberOfObjects() + 1));
                    this.selectedTool = "drawing_polygon";
                    break;
                case "circle":
                    object = new Circle(new Point(event.getX(), event.getY()), 1, 1, this.model.getColor(), "Circle " + (this.model.getNumberOfObjects() + 1));
                    break;
                case "rect":
                    object = new Rectangle(new Point(event.getX(), event.getY()), 1, 1, this.model.getColor(), "Rectangle " + (this.model.getNumberOfObjects() + 1));
                    break;
            }
            if (object != null) {
                // New objects are selected by default
                this.model.deselectAll();
                object.setSelected(true);
                this.model.addObject(object);
            }
        }

        repaint();
    }

    private void handleClickInsideShape(CanvasObject selectedObject) {
        selectedObject.setResizeDirection(null);

        if (selectedObject instanceof Polygon) {
            Polygon p = (Polygon) selectedObject;
            p.setTransform(!p.getTransform());
        }
    }

    private CanvasObject getClickedShape(int mx, int my) {
        CanvasObject match = null;
        for (CanvasObject s: this.model.getObjects()) {
            if (s instanceof Polygon) {
                Polygon p = (Polygon) s;
                ArrayList<Point> pts = p.getPoints();
                int x[] = new int[p.getPoints().size()];
                int y[] = new int[p.getPoints().size()];
                for (int i = 0; i < pts.size(); i++) {
                    x[i] = (int) pts.get(i).x;
                    y[i] = (int) pts.get(i).y;
                }
                if (new java.awt.Polygon(x, y, x.length).contains(mx, my)) {
                    match = s;
                }
            } else if (s.getBoundingRect().hit(mx, my)) {
                match = s;
            }
        }
        return match;
    }

    public void findAndSelectShape(int mx, int my) {
        CanvasObject shape = this.getClickedShape(mx, my);
        if (shape != null) {
            this.model.deselectAll();
            shape.setSelected(true);
            this.model.notifyUpdate();
        } else {
            this.model.deselectAll();
        }
    }

    private String isOverResizeHandle(CanvasObject shape, int mouseX, int mouseY) {
        HashMap<String, BoundingRect> handles = shape.getResizeHandles();
        for (String key: handles.keySet()) {
            if (handles.get(key).hit(mouseX, mouseY)) {
                return key;
            }
        }

        return null;
    }

    private int isOverPolygonTransformHandle(Polygon shape, int mouseX, int mouseY) {
        ArrayList<BoundingRect> boxes = shape.getTransformBoxes();
        for (int i = 0; i < boxes.size(); i++) {
            if (boxes.get(i).hit(mouseX, mouseY)) {
                return i;
            }
        }
        return -1;
    }

    public void increaseSizeBy(int dx, int dy) {
        CanvasObject selectedObject = this.model.getSelectedObject();

        BoundingRect rect = selectedObject.getBoundingRect();
        if (selectedObject.getResizeDirection() == "bottom_right") {
            selectedObject.setSize(rect.right - rect.left + dx, rect.bottom - rect.top + dy);
        } else if (selectedObject.getResizeDirection() == "bottom_left") {
            selectedObject.setSize(rect.right - rect.left - dx, rect.bottom - rect.top + dy);
            selectedObject.translate(new Point(rect.left + dx, rect.top));
        } else if (selectedObject.getResizeDirection() == "top_right") {
            selectedObject.setSize(rect.right - rect.left + dx, rect.bottom - rect.top - dy);
            selectedObject.translate(new Point(rect.left, rect.top + dy));
        } else if (selectedObject.getResizeDirection() == "top_left") {
            selectedObject.setSize(rect.right - rect.left - dx, rect.bottom - rect.top - dy);
            selectedObject.translate(new Point(rect.left + dx, rect.top + dy));
        }
    }

    @Override()
    public void mouseReleased(MouseEvent event) {
        repaint();
    }

    @Override()
    public void mouseDragged(MouseEvent event) {
        int mx = event.getX();
        int my = event.getY();

        CanvasObject selectedObject = this.model.getSelectedObject();
        if (selectedObject == null) return;

        if (this.selectedTool == "circle" || this.selectedTool == "rect") {
            BoundingRect r = selectedObject.getBoundingRect();
            selectedObject.setSize(Math.abs(mx - r.left), Math.abs(my - r.top));
        } else if (this.selectedTool == "move") {
            BoundingRect r = selectedObject.getBoundingRect();
            if (r != null) {
                int dx = (int) (mx - this.lastMouseDragX);
                int dy = (int) (my - this.lastMouseDragY);

                if (selectedObject.getResizeDirection() != null) {
                    this.increaseSizeBy(dx, dy);
                } else if (selectedObject instanceof Polygon && ((Polygon) selectedObject).getTransformPointIndex() != -1) {
                    ((Polygon) selectedObject).moveTransformPoint(dx, dy);
                } else {
                    selectedObject.translate(new Point(r.left + dx, r.top + dy));
                }
            }
        }

        this.lastMouseDragX = mx;
        this.lastMouseDragY = my;

        repaint();
    }

    @Override()
    public void mouseMoved(MouseEvent event) {
        this.currentMouseX = event.getX();
        this.currentMouseY = event.getY();

        repaint();
    }

    @Override()
    public void mouseEntered(MouseEvent event) {}

    @Override()
    public void mouseClicked(MouseEvent event) {}

    @Override()
    public void mouseExited(MouseEvent event) {}

    public void setTool(String tool) {
        this.selectedTool = tool;
    }
}
