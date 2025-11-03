package com.gamebuilder.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyListener;
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
import com.gamebuilder.canvasobject.shape.Text;
import com.gamebuilder.model.CanvasModel;
import com.gamebuilder.model.CanvasModelUpdateListener;
import com.gamebuilder.model.SceneUpdateListener;
import com.gamebuilder.model.Selectable;
import com.gamebuilder.model.Selection;
import com.gamebuilder.model.SpriteModel;
import com.gamebuilder.model.SpriteModelUpdateListener;
import com.gamebuilder.scene.Scene;
import com.gamebuilder.service.CanvasService;
import com.gamebuilder.service.IconService;
import com.gamebuilder.service.SceneService;
import com.gamebuilder.util.Log;

public class CanvasView extends JPanel
        implements MouseListener, MouseMotionListener, KeyListener, CanvasModelUpdateListener,
            SpriteModelUpdateListener, SceneUpdateListener, FocusListener {

    private SpriteModel spriteModel = SpriteModel.getInstance();
    private CanvasService service;
    private SceneService sceneService;

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

    public CanvasView(CanvasService service, SceneService sceneService) {
        this.service = service;
        this.sceneService = sceneService;
        this.sceneService.addUpdateListener(this);
        this.service.addUpdateListener(this);
        this.spriteModel.addUpdateListener(this);

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);

        this.setUpShortcutKeys();
        
        setFocusable(true);
        setFocusable(true);
        // requestFocusInWindow();

        Timer t = new Timer(1000 / 24, e -> repaint());
        t.start();
    }

    @Override()
    public void onCanvasModelUpdated(CanvasModel model, String update) {
        if (update.equals("select_tool")) {
            this.getService().unfocusAllTextInputs();
            this.drawing = false;
        }
        this.repaint();
    }

    @Override()
    public void onSpriteModelUpdated() {
        this.repaint();
    }

    @Override()
    public void onSceneUpdate() {
        this.repaint();
    }

    @Override()
    public void focusLost(FocusEvent event) {
        this.getService().unfocusAllTextInputs();
    }

    @Override()
    public void focusGained(FocusEvent event) {}

    @Override()
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        CanvasService service = this.getService();

        drawBackground(g);

        CanvasTool tool = service.getSelectedTool();
        if (tool != null) {
            g.setColor(Color.BLACK);
            g.drawString("Selected Tool: " + tool, 20, 20);

            if (tool == CanvasTool.MOVE_TOOL && this.selectAction != null) {
                int x = g.getFontMetrics().stringWidth("Selected Tool: " + tool) + 40;
                g.drawString("Action: " + this.selectAction, x, 20);
            }
        }

        for (CanvasObject object: service.getObjects()) {
            object.draw(g);
        }

        // Show line represent the polygon path as it's being drawn.
        if (tool == CanvasTool.POLYGON_TOOL && this.drawing) {
            ArrayList<Point> pts = ((Polygon) service.getLastAddedObject()).getPoints();

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

        //TODO: drawGrid()
    }

    @Override()
    public void mousePressed(MouseEvent event) {
        Log.d("CanvasView.mousePressed()", "Mouse pressed at (" + event.getX() + ", " + event.getY() + ")");
        CanvasService service = this.getService();

        int mx = event.getX();
        int my = event.getY();
        this.lastMouseDragX = mx;
        this.lastMouseDragY = my;
        CanvasTool tool = service.getSelectedTool();

        if (tool == CanvasTool.RECT_TOOL && !this.drawing) {
            Log.d("CanvasView.mousePressed()", "Starting new rectangle at (" + mx + ", " + my + ")");
            Rectangle object = new Rectangle(
                new Point(mx, my),
                1,
                1,
                service.getSelectedBgColor(),
                "Rectangle " + (service.getNumberOfObjects() + 1)
            );
            service.addNewObject(object);
            this.drawing = true;
        } else if (tool == CanvasTool.CIRCLE_TOOL && !this.drawing) {
            Circle object = new Circle(
                new Point(mx, my),
                1,
                1,
                service.getSelectedBgColor(),
                "Circle " + (service.getNumberOfObjects() + 1)
            );
            service.addNewObject(object);
            this.drawing = true;
        } else if (tool == CanvasTool.POLYGON_TOOL && !this.drawing) {
            Polygon object = new Polygon(
                new Point(mx, my),
                service.getSelectedBgColor(),
                "Polygon " + (service.getNumberOfObjects() + 1)
            );
            service.addNewObject(object);
            this.drawing = true;
        } else if (tool == CanvasTool.POLYGON_TOOL && this.drawing) {
            Polygon p = (Polygon) service.getLastAddedObject();

            Point nextPoint = new Point(mx, my);

            if (Math.abs(nextPoint.distanceFrom(p.getPoints().get(0))) < 5) {
                // This completes the polygon because setting the tool back to "polygon" will
                // cause a new polygon to be started on the next click
                this.drawing = false;
            } else {
                p.addPoint(nextPoint);
            }
            service.notifyUpdate("add_point_to_polygon");
        } else if (tool == CanvasTool.TEXT_TOOL) {
            Text text;
            CanvasObject clicked = service.getClickedObject(mx, my);
            if (clicked == null || !(clicked instanceof Text)) {
                service.unfocusAllTextInputs();
                
                text = new Text("Text " + (service.getNumberOfObjects() + 1), new Point(mx, my - 6));
                service.addNewObject(text);
                this.drawing = true;
                this.requestFocus();
            } else if (clicked instanceof Text) {
                service.unfocusAllTextInputs();

                text = (Text) clicked;
                text.setFocus(true);
                text.onClick(new Point(mx, my));
                this.requestFocus();
                this.drawing = true;
            }
        } else if (tool == CanvasTool.MOVE_TOOL) {
            this.selection = service.getSelection();

            service.unfocusAllTextInputs();
            this.drawing = false;

            boolean clickInsideBox = selection.getBoundingRect().hit(mx, my);
            this.resizeDirection = selection.getResizeDirection(mx, my);
            this.transformPointIndex = service.getClickedTransformPointIndex(mx, my);

            if (this.transformPointIndex != -1) {
                if (this.dPressed) {
                    service.removePointFromPolygon(this.transformPointIndex);
                    this.transformPointIndex = -1;
                } else {
                    this.selectAction = "transform_polygon";
                }
            } else if (this.polyEdgeMouseIsTouching != null) {
                service.addPointToPolygon(
                    this.polyEdgeMouseIsTouching[0],
                    this.polyEdgeMouseIsTouching[1],
                    new Point(mx, my)
                );
            } else if (this.resizeDirection != null) {
                this.selectAction = "resize";
            } else if (clickInsideBox) {
                this.selectAction = "move";

                Selectable selected = service.findAndSelectShape(mx, my, false);
                if (selected != null && selected instanceof Polygon) {
                    Polygon poly = (Polygon) selected;
                    service.handleClickOnPolygon(poly);
                    if (poly.getTransform()) {
                        this.selectAction = "transform_polygon";
                    }
                }
            } else {
                this.selectAction = "move";
                service.findAndSelectShape(mx, my, false);
            }
        }

        repaint();
    }

    @Override()
    public void mouseDragged(MouseEvent event) {
        CanvasService service = this.getService();

        int mx = event.getX();
        int my = event.getY();
        this.currentMouseX = mx;
        this.currentMouseY = my;
        CanvasTool tool = service.getSelectedTool();

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
                Polygon poly = service.getTransformingPolygon();
                if (poly != null && poly.getTransform()) {
                    poly.moveTransformPoint(this.transformPointIndex, dx, dy);
                }
            }
        } else if (tool == CanvasTool.HAND_TOOL) {
            service.adjustOffset(dx, dy);
        }

        this.lastMouseDragX = mx;
        this.lastMouseDragY = my;

        repaint();
    }

    @Override()
    public void mouseReleased(MouseEvent event) {
        CanvasService service = this.getService();
        CanvasTool tool = service.getSelectedTool();
        if (this.drawing
                && (tool == CanvasTool.RECT_TOOL || tool == CanvasTool.CIRCLE_TOOL)) {
            this.drawing = false;
        }
        repaint();
    }

    @Override()
    public void mouseMoved(MouseEvent event) {
        CanvasService service = this.getService();

        this.currentMouseX = event.getX();
        this.currentMouseY = event.getY();

        this.polyEdgeMouseIsTouching = service.isOnPolygonLine(this.currentMouseX, this.currentMouseY);
        this.transformPointIndex = service.getClickedTransformPointIndex(this.currentMouseX, this.currentMouseY);

        repaint();
    }

    @Override()
    public void mouseEntered(MouseEvent event) {
        this.mouseInCanvas = true;

        switch (service.getSelectedTool()) {
            case CanvasTool.HAND_TOOL:
                try {
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    Dimension dimension = toolkit.getBestCursorSize(10, 10);
                    this.setCursor(toolkit.createCustomCursor(
                            IconService.getImage("/cursors/hand.png", dimension.width, dimension.height), 
                            new java.awt.Point(0, 0),
                            "Move Tool"));
                } catch (Exception e) {
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
                break;
            case CanvasTool.MOVE_TOOL:
                try {
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    Dimension dimension = toolkit.getBestCursorSize(10, 10);
                    this.setCursor(toolkit.createCustomCursor(
                            IconService.getImage("/cursors/cursor.png", dimension.width, dimension.height), 
                            new java.awt.Point(0, 0),
                            "Move Tool"));
                } catch (Exception e) {
                    // Fallback to default cursor if custom cursor fails
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
                break;
            case CanvasTool.POLYGON_TOOL:
            case CanvasTool.CIRCLE_TOOL:
            case CanvasTool.RECT_TOOL:
                this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                break;
            case CanvasTool.TEXT_TOOL:
                this.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
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

    @Override()
    public void keyTyped(KeyEvent e) {
        if (isTyping()) {
            char c = e.getKeyChar();
            Log.d("CanvasView.keyTyped()", "Typed character: " + c);
            // Add the character to the current text object
            // You'll need to implement this based on your Text class

            CanvasObject object = this.getService().getLastAddedObject();
            if (object instanceof Text) {
                ((Text) object).addCharacter(c);
            }
        }
    }

    @Override()
    public void keyPressed(KeyEvent e) {
        if (!isTyping()) {
            return;
        }

        Text text = (Text) this.getService().getLastAddedObject();

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                text.moveCursorLeft();
                break;
            case KeyEvent.VK_RIGHT:
                text.moveCursorRight();
                break;
            case KeyEvent.VK_UP:
                text.moveCursorUp();
                break;
            case KeyEvent.VK_DOWN:
                text.moveCursorDown();
                break;
        }
    }

    @Override()
    public void keyReleased(KeyEvent e) {
        // Handle key releases if needed
    }

    private void drawSelectionBox(Graphics g) {
        CanvasService service = this.getService();

        CanvasTool tool = service.getSelectedTool();
        if (tool != CanvasTool.MOVE_TOOL) {
            return;
        }

        // Draw selection/expansion box
        Selection selection = service.getSelection();
        if (this.shouldShowSelectionBox(selection)) {
            BoundingRect r = selection.getBoundingRect();
            if (r == null) return;

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

    private boolean isTyping() {
        return this.getService().getSelectedTool() == CanvasTool.TEXT_TOOL && this.drawing;
    }

    private void drawBackground(Graphics g) {
        CanvasService service = this.getService();
        int x = service.getScreenX();
        int y = service.getScreenY();

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
        CanvasService service = this.getService();

        if (this.mouseInCanvas) {
            g.setColor(Color.BLACK);
            g.drawString(
                "(x: " + service.getRelativeX(this.currentMouseX)
                    + ", y: " + service.getRelativeY(this.currentMouseY) + ")",
                this.currentMouseX + 10,
                this.currentMouseY - 10
            );

            if (service.getSelectedTool() == CanvasTool.MOVE_TOOL) {
                for (Selectable s: service.getSelectableObjects()) {
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
                if (CanvasView.this.isTyping()) {
                } else {
                    CanvasView.this.getService().findAndDeleteSelectedObjects();
                }
            }
        });
    }

    // TODO: refactor to avoid code duplication with other views
    private CanvasService getService() {
        Scene scene = this.sceneService.getActiveScene();
        if (scene != null) {
            return scene.getCanvasService();
        }
        return this.service;
    }
}
