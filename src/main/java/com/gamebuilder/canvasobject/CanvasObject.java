package com.gamebuilder.canvasobject;

import java.awt.Graphics;
import java.util.ArrayList;

import com.gamebuilder.Point;
import com.gamebuilder.model.Event;

public abstract class CanvasObject {
    protected String name;

    private String resizeDirection = null;
    private boolean isActive = false;
    private int offsetX = 0;
    private int offsetY = 0;

    ArrayList<Event> events = new ArrayList<Event>();

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public void setResizeDirection(String direction) {
        this.resizeDirection = direction;
    }

    public String getResizeDirection() {
        return this.resizeDirection;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public ArrayList<Event> getEvents() {
        return this.events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void adjustOffset(int dx, int dy) {
        this.offsetX += dx;
        this.offsetY += dy;
    }

    public void setOffset(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
    }

    public int getOffsetX() {
        return this.offsetX;
    }

    public int getOffsetY() {
        return this.offsetY;
    }

    public abstract void draw(Graphics g);

    public abstract void setSize(int newWidth, int newHeight);

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract Point getPosition();

    public abstract String toXml();
}
