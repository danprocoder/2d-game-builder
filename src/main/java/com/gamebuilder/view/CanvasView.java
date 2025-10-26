package com.gamebuilder.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import com.gamebuilder.CanvasTool;
import com.gamebuilder.Point;
import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.canvasobject.shape.Polygon;
import com.gamebuilder.canvasobject.shape.BoundingRect;
import com.gamebuilder.canvasobject.shape.Circle;
import com.gamebuilder.canvasobject.shape.Rectangle;
import com.gamebuilder.model.CanvasModel;
import com.gamebuilder.model.CanvasModelUpdateListener;
import com.gamebuilder.model.Selectable;
import com.gamebuilder.model.Selection;
import com.gamebuilder.model.SpriteModel;
import com.gamebuilder.model.SpriteModelUpdateListener;
import com.gamebuilder.service.CanvasService;

public class CanvasView extends JPanel
        implements MouseListener, MouseMotionListener, CanvasModelUpdateListener,
            SpriteModelUpdateListener {

    private CanvasModel model;
    private SpriteModel spriteModel = SpriteModel.getInstance();
    private CanvasService service;

    private int currentMouseX;
    private int currentMouseY;
    private int lastMouseDragX = 0;
    private int lastMouseDragY = 0;

    private boolean drawing = false;
    private boolean mouseInCanvas = false;

    private Selection selection;
    private String selectAction = null;
    private String resizeDirection = null;

    private int transformPointIndex = -1;
    private Point[] polyEdgeMouseIsTouching = null;

    private int width = 640;
    private int height = 480;

    private boolean dPressed = false;

    public CanvasView(CanvasService service) {
        this.service = service;
        this.model = service.getModel();
        this.service.addUpdateListener(this);
        this.spriteModel.addUpdateListener(this);

        addMouseListener(this);
        addMouseMotionListener(this);

        this.setUpShortcutKeys();
        
        setFocusable(true);
        setFocusable(true);
        // requestFocusInWindow();

        Timer t = new Timer(1000 / 24, e -> repaint());
        t.start();
    }

    @Override()
    public void onCanvasModelUpdated(CanvasModel model, String update) {
        this.repaint();
    }

    @Override()
    public void onSpriteModelUpdated() {
        this.repaint();
    }

    @Override()
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawBackground(g);

        CanvasTool tool = this.model.getSelectedTool();
        if (tool != null) {
            g.setColor(Color.BLACK);
            g.drawString("Selected Tool: " + tool, 20, 20);

            if (tool == CanvasTool.MOVE_TOOL && this.selectAction != null) {
                int x = g.getFontMetrics().stringWidth("Selected Tool: " + tool) + 40;
                g.drawString("Action: " + this.selectAction, x, 20);
            }
        }

        for (CanvasObject object: this.model.getObjects()) {
            object.draw(g);
        }

        // Show line represent the polygon path as it's being drawn.
        if (tool == CanvasTool.POLYGON_TOOL && this.drawing) {
            ArrayList<Point> pts = ((Polygon) this.service.getLastAddedObject()).getPoints();

            // Line from the last point to the current mouse position
            // Draw the line before because we want the circles to overlap it.
            Point lastPoint = pts.get(pts.size() - 1);
            g.setColor(Color.BLACK);
            g.drawLine((int) lastPoint.x, (int) lastPoint.y, this.currentMouseX, this.currentMouseY);

            for (int i = 0; i < pts.size(); i++) {
                if (i + 1 < pts.size()) {
                    g.setColor(Color.BLACK);
                    g.drawLine(
                        (int) pts.get(i).x, (int) pts.get(i).y, (int) pts.get(i + 1).x, (int) pts.get(i + 1).y
                    );
                }

                int s = 4;
                // Draw box at each point
                if (i == 0 && Math.abs(new Point(this.currentMouseX, this.currentMouseY).distanceFrom(pts.get(0))) < 5) {
                    s = 12;
                }
                g.setColor(Color.WHITE);
                g.fillOval((int) pts.get(i).x - (s / 2), (int) pts.get(i).y - (s / 2), s, s);

                g.setColor(Color.BLACK);
                g.drawOval((int) pts.get(i).x - (s / 2), (int) pts.get(i).y - (s / 2), s, s);
            }
        }

        this.drawSelectionBox(g);
        this.drawTooltip(g);

        // Draw rect to indicate the current screen resolution
        g.setColor(Color.BLACK);
        g.drawRect(service.getScreenX(), service.getScreenY(), this.width, this.height);
    }

    @Override()
    public void mousePressed(MouseEvent event) {
        // requestFocusInWindow();

        int mx = event.getX();
        int my = event.getY();
        this.lastMouseDragX = mx;
        this.lastMouseDragY = my;
        CanvasTool tool = this.model.getSelectedTool();

        if (tool == CanvasTool.RECT_TOOL && !this.drawing) {
            Rectangle object = new Rectangle(
                new Point(mx, my),
                1,
                1,
                this.model.getColor(),
                "Rectangle " + (this.model.getNumberOfObjects() + 1)
            );
            this.service.addNewObject(object);
            this.drawing = true;
        } else if (tool == CanvasTool.CIRCLE_TOOL && !this.drawing) {
            Circle object = new Circle(
                new Point(mx, my),
                1,
                1,
                this.model.getColor(),
                "Circle " + (this.model.getNumberOfObjects() + 1)
            );
            this.service.addNewObject(object);
            this.drawing = true;
        } else if (tool == CanvasTool.POLYGON_TOOL && !this.drawing) {
            Polygon object = new Polygon(
                new Point(mx, my),
                this.model.getColor(),
                "Polygon " + (this.model.getNumberOfObjects() + 1)
            );
            this.service.addNewObject(object);
            this.drawing = true;
        } else if (tool == CanvasTool.POLYGON_TOOL && this.drawing) {
            Polygon p = (Polygon) this.service.getLastAddedObject();

            Point nextPoint = new Point(mx, my);

            if (Math.abs(nextPoint.distanceFrom(p.getPoints().get(0))) < 5) {
                // This completes the polygon because setting the tool back to "polygon" will
                // cause a new polygon to be started on the next click
                this.drawing = false;
            } else {
                p.addPoint(nextPoint);
            }
            this.service.notifyUpdate("add_point_to_polygon");
        } else if (tool == CanvasTool.MOVE_TOOL) {
            this.selection = this.service.getSelection();

            boolean clickInsideBox = selection.getBoundingRect().hit(mx, my);
            this.resizeDirection = selection.getResizeDirection(mx, my);
            this.transformPointIndex = this.service.getClickedTransformPointIndex(mx, my);

            if (this.transformPointIndex != -1) {
                if (this.dPressed) {
                    this.service.removePointFromPolygon(this.transformPointIndex);
                    this.transformPointIndex = -1;
                } else {
                    this.selectAction = "transform_polygon";
                }
            } else if (this.polyEdgeMouseIsTouching != null) {
                this.service.addPointToPolygon(
                    this.polyEdgeMouseIsTouching[0],
                    this.polyEdgeMouseIsTouching[1],
                    new Point(mx, my)
                );
            } else if (this.resizeDirection != null) {
                this.selectAction = "resize";
            } else if (clickInsideBox) {
                this.selectAction = "move";

                Selectable selected = this.service.findAndSelectShape(mx, my, false);
                if (selected != null && selected instanceof Polygon) {
                    Polygon poly = (Polygon) selected;
                    service.handleClickOnPolygon(poly);
                    if (poly.getTransform()) {
                        this.selectAction = "transform_polygon";
                    }
                }
            } else {
                this.selectAction = "move";
                this.service.findAndSelectShape(mx, my, false);
            }
        }

        repaint();
    }

    @Override()
    public void mouseDragged(MouseEvent event) {
        int mx = event.getX();
        int my = event.getY();
        this.currentMouseX = mx;
        this.currentMouseY = my;
        CanvasTool tool = this.model.getSelectedTool();

        int dx = mx - this.lastMouseDragX;
        int dy = my - this.lastMouseDragY;

        if (this.drawing && (tool == CanvasTool.RECT_TOOL || tool == CanvasTool.CIRCLE_TOOL)) {
            service.resizeObjectBy(service.getLastAddedObject(), dx, dy);
        } else if (tool == CanvasTool.MOVE_TOOL) {
            // TODO: you can only move if the mouse is inside
            if (this.selectAction == "move") {
                this.selection.translate(dx, dy);
            } else if (this.selectAction == "resize" && this.resizeDirection != null) {
                this.selection.increaseSizeBy(this.resizeDirection, dx, dy);
            } else if (this.selectAction == "transform_polygon") {
                Polygon poly = this.model.getTransformingPolygon();
                if (poly != null && poly.getTransform()) {
                    poly.moveTransformPoint(this.transformPointIndex, dx, dy);
                }
            }
        } else if (tool == CanvasTool.HAND_TOOL) {
            this.service.adjustOffset(dx, dy);
        }

        this.lastMouseDragX = mx;
        this.lastMouseDragY = my;

        repaint();
    }

    @Override()
    public void mouseReleased(MouseEvent event) {
        CanvasTool tool = this.model.getSelectedTool();
        if (this.drawing
                && (tool == CanvasTool.RECT_TOOL || tool == CanvasTool.CIRCLE_TOOL)) {
            this.drawing = false;
        }
        repaint();
    }

    @Override()
    public void mouseMoved(MouseEvent event) {
        this.currentMouseX = event.getX();
        this.currentMouseY = event.getY();

        this.polyEdgeMouseIsTouching = this.service.isOnPolygonLine(this.currentMouseX, this.currentMouseY);
        this.transformPointIndex = this.service.getClickedTransformPointIndex(this.currentMouseX, this.currentMouseY);

        repaint();
    }

    @Override()
    public void mouseEntered(MouseEvent event) {
        this.mouseInCanvas = true;

        switch (this.model.getSelectedTool()) {
            case CanvasTool.HAND_TOOL:
            case CanvasTool.MOVE_TOOL:
                this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                break;
            case CanvasTool.POLYGON_TOOL:
            case CanvasTool.CIRCLE_TOOL:
            case CanvasTool.RECT_TOOL:
                this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                break;
            default:
                this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                break;
        }
    }

    @Override()
    public void mouseClicked(MouseEvent event) {}

    @Override()
    public void mouseExited(MouseEvent event) {
        this.mouseInCanvas = false;
    }

    private void drawSelectionBox(Graphics g) {
        CanvasTool tool = this.model.getSelectedTool();
        if (tool != CanvasTool.MOVE_TOOL) {
            return;
        }

        // Draw selection/expansion box
        Selection selection = this.service.getSelection();
        if (this.shouldShowSelectionBox(selection)) {
            BoundingRect r = selection.getBoundingRect();

            g.setColor(Color.BLACK);
            g.drawRect(r.left, r.top, r.right - r.left, r.bottom - r.top);

            HashMap<String, BoundingRect> resizeHandles = selection.getResizeHandles();
            for (String dir: resizeHandles.keySet()) {
                BoundingRect r2 = resizeHandles.get(dir);

                if (r2.hit(this.currentMouseX, this.currentMouseY) && tool == CanvasTool.MOVE_TOOL) {
                    g.setColor(Color.BLACK);
                    g.fillRect(r2.left, r2.top, r2.right - r2.left, r2.bottom - r2.top);
                } else {
                    g.setColor(Color.BLACK);
                    g.drawRect(r2.left, r2.top, r2.right - r2.left, r2.bottom - r2.top);

                    g.setColor(Color.WHITE);
                    g.fillRect(r2.left + 1, r2.top + 1, r2.right - r2.left - 1, r2.bottom - r2.top - 1);
                }
            }
        }
    }

    private void drawBackground(Graphics g) {
        int x = this.service.getScreenX();
        int y = this.service.getScreenY();
        
        g.setColor(Color.WHITE);
        g.fillRect(x, y, this.width, this.height);

        for (int i = 0; i < this.width; i += 20) {
            for (int j = 0; j < this.height; j += 20) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillOval(x + i - 1, y + j - 1, 2, 2);
            }
        }
    }

    private void drawTooltip(Graphics g) {
        if (this.mouseInCanvas) {
            g.setColor(Color.BLACK);
            g.drawString(
                "(x: " + this.service.getRelativeX(this.currentMouseX)
                    + ", y: " + this.service.getRelativeY(this.currentMouseY) + ")",
                this.currentMouseX + 10,
                this.currentMouseY - 10
            );

            if (this.model.getSelectedTool() == CanvasTool.MOVE_TOOL) {
                for (Selectable s: this.model.getSelectableObjects()) {
                    if (s.mouseHit(this.currentMouseX, this.currentMouseY)) {
                        g.drawString(s.getTooltipText(), this.currentMouseX + 10, this.currentMouseY + 5);
                    }
                }
            }

            if (this.polyEdgeMouseIsTouching != null) {
                g.drawString("Add Point to Polygon", this.currentMouseX + 10, this.currentMouseY + 15);
            }

            if (this.transformPointIndex != -1) {
                if (this.dPressed) {
                    g.drawString("Click to delete point", this.currentMouseX + 10, this.currentMouseY + 15);
                } else {
                    g.drawString("Drag to move point, D + Click to delete", this.currentMouseX + 10, this.currentMouseY + 15);
                }
            }
        }
    }

    /**
     * Returns whether if the selection/box should be shown of not based on the Selection object.
     *
     * @param selection the Selection object representing the current objects selected on the canvas.
     * @return true if the selection box should be shown, false otherwise.
     */
    private boolean shouldShowSelectionBox(Selection selection) {
        if (selection.isEmpty() || this.drawing) {
            return false;
        }

        ArrayList<Selectable> selectedObjects = selection.getSelectedObjects();
        if (selectedObjects.size() == 1 && selectedObjects.get(0) instanceof Polygon) {
            Polygon p = (Polygon) selectedObjects.get(0);

            if (p.getTransform()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Sets up all the shortcut key action events for the canvas.
     */
    private void setUpShortcutKeys() {
        // Use WHEN_IN_FOCUSED_WINDOW to catch keys even if component doesn't have direct focus
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "dPressed");
        getActionMap().put("dPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CanvasView.this.dPressed = true;
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "dReleased");
        getActionMap().put("dReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CanvasView.this.dPressed = false;
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0, false), "backspacePressed");
        getActionMap().put("backspacePressed", new AbstractAction() {
            @Override()
            public void actionPerformed(ActionEvent e) {
                CanvasView.this.service.findAndDeleteSelectedObjects();
            }
        });
    }
}
