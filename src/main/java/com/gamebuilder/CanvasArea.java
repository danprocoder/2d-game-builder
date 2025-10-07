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

public class CanvasArea extends JPanel implements MouseListener, MouseMotionListener {

    private BoundingRect rect;
    private String selectedTool;
    private float mouseStartX;
    private float mouseStartY;
    private float currentMouseX;
    private float currentMouseY;
    private CanvasModel model;

    public CanvasArea(GameBuilder gameBuilder, CanvasModel model) {
        addMouseListener(this);
        addMouseMotionListener(this);

        this.model = model;
    }

    @Override()
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.selectedTool != null) {
            g.drawString(this.selectedTool, 20, 20);
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
        this.mouseStartX = event.getX();
        this.mouseStartY = event.getY();

        if (this.selectedTool == "move") {
            CanvasObject selectedObject = this.model.getSelectedObject();
            if (selectedObject != null) {
                String dir = this.isOverResizeHandle(selectedObject, event.getX(), event.getY());
                if (dir != null) {
                    selectedObject.setResizeDirection(dir);
                    this.rect = selectedObject.getBoundingRect();
                } else if (selectedObject.getBoundingRect().hit(event.getX(), event.getY())) {
                    if (selectedObject instanceof Polygon) {
                        Polygon p = (Polygon) selectedObject;
                        p.setTransform(!p.getTransform());
                    } else {
                        selectedObject.setResizeDirection(null);
                        this.rect = selectedObject.getBoundingRect();
                    }
                } else {
                    this.findAndSelectShape();
                }
            } else {
                this.findAndSelectShape();
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

                this.rect = p.getBoundingRect();
            }
        } else {
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

                this.rect = object.getBoundingRect();
            }
        }

        repaint();
    }

    private CanvasObject getClickedShape() {
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
                if (new java.awt.Polygon(x, y, x.length).contains(this.mouseStartX, this.mouseStartY)) {
                    match = s;
                }
            } else if (s.getBoundingRect().hit((int) this.mouseStartX, (int) this.mouseStartY)) {
                match = s;
            }
        }
        return match;
    }

    public void findAndSelectShape() {
        CanvasObject shape = this.getClickedShape();
        if (shape != null) {
            this.model.deselectAll();
            shape.setSelected(true);
            this.rect = shape.getBoundingRect();
        } else {
            this.model.deselectAll();
            this.rect = null;
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

    public void increaseSizeBy(int dx, int dy) {
        CanvasObject selectedObject = this.model.getSelectedObject();
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
        CanvasObject selectedObject = this.model.getSelectedObject();
        if (selectedObject == null) return;

        if (this.selectedTool == "circle" || this.selectedTool == "rect") {
            BoundingRect r = selectedObject.getBoundingRect();
            selectedObject.setSize(Math.abs(event.getX() - r.left), Math.abs(event.getY() - r.top));
        } else if (this.selectedTool == "move") {
            if (this.rect != null) {
                int dx = (int) (event.getX() - this.mouseStartX);
                int dy = (int) (event.getY() - this.mouseStartY);
                
                String resizeDir = selectedObject.getResizeDirection();
                if (resizeDir != null) {
                    this.increaseSizeBy(dx, dy);
                } else {
                    selectedObject.translate(new Point(this.rect.left + dx, this.rect.top + dy));
                }
            }
        }

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
