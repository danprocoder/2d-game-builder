package com.gamebuilder.canvasobject.sprite;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.UUID;

import com.gamebuilder.Point;
import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.model.Event;
import com.gamebuilder.model.Selectable;

public class Sprite extends CanvasObject {
    private String id;
    private String name;
    private ArrayList<SpriteState> states = new ArrayList<SpriteState>();
    private String currentState = null;
    private Point position = new Point(0, 0);

    public Sprite(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public Sprite(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(String state) {
        this.currentState = state;
    }

    public void addState(SpriteState state) {
        this.states.add(state);
    }

    public void setSize(int newWidth, int newHeight) {}

    public void setStates(ArrayList<SpriteState> states) {
        this.states = states;
        if (states.size() > 0) {
            this.currentState = states.get(0).getName();
        } else {
            this.currentState = null;
        }
    }

    public String getCurrentState() {
        return this.currentState;
    }

    public ArrayList<SpriteState> getStates() {
        return this.states;
    }

    public void setSelected(boolean selected) {
        if (this.currentState != null) {
            SpriteState state = this.getAt(this.currentState);
            if (state == null) {
                return;
            }

            AnimatedSpriteImage image = state.getImage();
            if (image != null) {
                image.setSelected(selected);
            }
            
            CanvasObject object = state.getCollisionBox();
            if (object != null) {
                ((Selectable) object).setSelected(selected);
            }
        }
    }

    public void translate(int dx, int dy) {
        this.position = new Point(this.position.getXInt() + dx, this.position.getYInt() + dy);
    }

    @Override()
    public Point getPosition() {
        return this.position;
    }

    @Override()
    public void adjustOffset(int dx, int dy) {
        super.adjustOffset(dx, dy);

        if (this.currentState != null) {
            SpriteState state = this.getAt(this.currentState);
            if (state == null) {
                return;
            }
            
            CanvasObject object = state.getCollisionBox();
            if (object != null) {
                object.adjustOffset(dx, dy);
            }

            AnimatedSpriteImage image = state.getImage();
            if (image != null) {
                image.adjustOffset(dx, dy);
            }
        }
    }

    @Override()
    public void draw(Graphics g) {
        if (this.currentState != null) {
            SpriteState state = this.getAt(this.currentState);
            if (state == null) {
                return;
            }

            AnimatedSpriteImage image = state.getImage();
            if (image != null) {
                image.draw(g);
            }
            
            CanvasObject object = state.getCollisionBox();
            if (object != null) {
                object.draw(g);
            }
        }
    }

    public SpriteState getAt(String state) {
        for (SpriteState s: this.states) {
            if (s.getName().equals(state)) {
                return s;
            }
        }

        return null;
    }

    @Override()
    public int getWidth() {
        // This should be the size of the current state
        return 100;
    }

    @Override()
    public int getHeight() {
        // This should be the size of the current state
        return 100;
    }

    public String toXml() {
        StringBuilder xml = new StringBuilder();

        xml.append(String.format("\n<Sprite id=\"%s\" name=\"%s\">", this.id, this.name));

        for (SpriteState state: this.states) {
            xml.append(state.getXml());
        }

        if (this.getEvents().size() > 0) {
            xml.append("\n<Events>");
            for (Event event: this.getEvents()) {
                xml.append(event.toXml());
            }
            xml.append("\n</Events>");
        }

        xml.append("\n</Sprite>");

        return xml.toString();
    }
}
