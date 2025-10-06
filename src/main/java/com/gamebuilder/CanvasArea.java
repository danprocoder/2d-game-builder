package com.gamebuilder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
    private Color color;
    private CanvasModel model;

    public CanvasArea(GameBuilder gameBuilder, CanvasModel model) {
        addMouseListener(this);
        addMouseMotionListener(this);

        Random rand = new Random();
        this.color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));

        this.model = model;
    }

    // public void drawFocusedIndicator(Graphics g) {
    //     CanvasObject selectedObject = this.model.getSelectedObject();
    //     if (selectedObject != null) {
    //         BoundingRect r = selectedObject.getBoundingRect();

    //         g.setColor(new Color(0, 0, 0));

    //         // top line
    //         g.drawLine(r.left - 10, r.top - 10, r.right + 10, r.top - 10);
    //         // right line
    //         g.drawLine(r.right + 10, r.top - 10, r.right + 10, r.bottom + 10);
    //         // bottom line
    //         g.drawLine(r.left - 10, r.bottom + 10, r.right + 10, r.bottom + 10);
    //         // left line
    //         g.drawLine(r.left - 10, r.top - 10, r.left - 10, r.bottom + 10);

    //         g.drawRect(r.left - 15, r.top - 15, 10, 10);
    //         g.drawRect(r.right + 5, r.top - 15, 10, 10);
    //         g.drawRect(r.left - 15, r.bottom + 5, 10, 10);
    //         g.drawRect(r.right + 5, r.bottom + 5, 10, 10);
    //     }
    // }

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
            // This will make sure that if a shape is currently selected, it
            // will be deselected if the user clicks outside of the shape
            this.model.deselectAll();

            CanvasObject shape = this.getClickedShape();
            if (shape != null) {
                shape.setSelected(true);
                this.rect = shape.getBoundingRect();
            }
        } else if (this.selectedTool == "drawing_polygon") {
            Polygon p = (Polygon) this.model.getSelectedObject();

            Point nextPoint = new Point(event.getX(), event.getY());

            if (Math.abs(nextPoint.distanceFrom(p.getPoints().get(0))) < 3) {
                // This completes the polygon because setting the tool back to "polygon" will
                // cause a new polygon to be started on the next click
                this.selectedTool = "polygon";
            } else {
                p.addPoint(nextPoint);
            }
        } else {
            CanvasObject object = null;
            switch (this.selectedTool) {
                case "polygon":
                    object = new Polygon(new Point(event.getX(), event.getY()), this.color, "Polygon " + (this.model.getNumberOfObjects() + 1));
                    this.selectedTool = "drawing_polygon";
                    break;
                case "circle":
                    object = new Circle(new Point(event.getX(), event.getY()), 1, 1, this.color, "Circle " + (this.model.getNumberOfObjects() + 1));
                    break;
                case "rect":
                    object = new Rectangle(new Point(event.getX(), event.getY()), 1, 1, this.color, "Rectangle " + (this.model.getNumberOfObjects() + 1));
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

    private CanvasObject getClickedShape() {
        CanvasObject match = null;
        for (CanvasObject s: this.model.getObjects()) {
            if (s.getBoundingRect().hit((int) this.mouseStartX, (int) this.mouseStartY)) {
                match = s;
            }
        }
        return match;
    }

    @Override()
    public void mouseReleased(MouseEvent event) {
        repaint();
    }

    @Override()
    public void mouseDragged(MouseEvent event) {
        CanvasObject selectedObject = this.model.getSelectedObject();
        if (selectedObject == null) return;

        if (this.selectedTool == "circle" || this.selectedTool == "rect" || this.selectedTool == "polygon") {
            BoundingRect r = selectedObject.getBoundingRect();
            selectedObject.setSize(Math.abs(event.getX() - r.left), Math.abs(event.getY() - r.top));
        } else if (this.selectedTool == "move") {
            if (this.rect != null) {
                int dx = (int) (event.getX() - this.mouseStartX);
                int dy = (int) (event.getY() - this.mouseStartY);
                selectedObject.translate(new Point(this.rect.left + dx, this.rect.top + dy));
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
