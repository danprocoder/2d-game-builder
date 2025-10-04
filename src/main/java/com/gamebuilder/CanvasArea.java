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

public class CanvasArea extends JPanel implements MouseListener, MouseMotionListener {
    public ArrayList<CanvasObject> shapes = new ArrayList<CanvasObject>();

    private CanvasObject focusedShape;
    private BoundingRect rect;
    private String selectedTool;
    private float mouseStartX;
    private float mouseStartY;

    public CanvasArea(GameBuilder gameBuilder) {
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void drawFocusedIndicator(Graphics g) {
        if (this.focusedShape != null) {
            BoundingRect r = this.focusedShape.getBoundingRect();

            g.setColor(new Color(0, 0, 0));

            // top line
            g.drawLine(r.left - 10, r.top - 10, r.right + 10, r.top - 10);
            // right line
            g.drawLine(r.right + 10, r.top - 10, r.right + 10, r.bottom + 10);
            // bottom line
            g.drawLine(r.left - 10, r.bottom + 10, r.right + 10, r.bottom + 10);
            // left line
            g.drawLine(r.left - 10, r.top - 10, r.left - 10, r.bottom + 10);

            g.drawRect(r.left - 15, r.top - 15, 10, 10);
            g.drawRect(r.right + 5, r.top - 15, 10, 10);
            g.drawRect(r.left - 15, r.bottom + 5, 10, 10);
            g.drawRect(r.right + 5, r.bottom + 5, 10, 10);
        }
    }

    @Override()
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this.selectedTool != null) {
            g.drawString(this.selectedTool, 20, 20);
        }
    
        for (CanvasObject s: this.shapes) {
            s.draw(g);
        }

        this.drawFocusedIndicator(g);
    }

    @Override()
    public void mousePressed(MouseEvent event) {
        this.mouseStartX = event.getX();
        this.mouseStartY = event.getY();

        if (this.selectedTool != null) {
            if (this.selectedTool == "move") {
                CanvasObject shape = this.getClickedShape();
                this.focusedShape = shape;
                this.rect = shape.getBoundingRect();
            } else {
                Random rand = new Random();
                Color clr = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));

                CanvasObject object = null;
                switch (this.selectedTool) {
                    case "polygon":
                        object = new Polygon(new Point(event.getX(), event.getY()), clr);
                        break;
                    case "circle":
                        object = new Circle(new Point(event.getX(), event.getY()), 1, 1, clr);
                        break;
                    case "rect":
                        object = new Rectangle(new Point(event.getX(), event.getY()), 1, 1, clr);
                        break;
                }
                if (object != null) {
                    this.shapes.add(object);
                    this.focusedShape = object;
                }
            }
        }

        repaint();
    }

    private CanvasObject getClickedShape() {
        return this.shapes.get(0);
    }

    @Override()
    public void mouseReleased(MouseEvent event) {
        repaint();
    }

    @Override()
    public void mouseDragged(MouseEvent event) {
        if (this.selectedTool == "circle" || this.selectedTool == "rect" || this.selectedTool == "polygon") {
            BoundingRect r = this.focusedShape.getBoundingRect();
            this.focusedShape.setSize(Math.abs(event.getX() - r.left), Math.abs(event.getY() - r.top));
        } else if (this.selectedTool == "move" && this.focusedShape != null) {
            int dx = (int) (event.getX() - this.mouseStartX);
            int dy = (int) (event.getY() - this.mouseStartY);
            this.focusedShape.translate(new Point(this.rect.left + dx, this.rect.top + dy));
        }

        repaint();
    }

    @Override()
    public void mouseMoved(MouseEvent event) {

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
