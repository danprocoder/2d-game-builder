package com.gamebuilder.model;

import com.gamebuilder.canvasobject.shape.BoundingRect;

/**
 * Interface for items that can be clicked to select on the canvas, offering functionality to resize and move
 */
public interface Selectable {
    /** Checks if the mouse coordinates hit the selectable area */
    public boolean mouseHit(int mx, int my);

    /** Returns the bounding rectangle for the selected object */
    public abstract BoundingRect getBoundingRect();

    /** Set selected */
    public void setSelected(boolean selected);

    /** Get selected */
    public boolean getSelected();

    /** move the selected object by the given delta */
    public abstract void translate(int dx, int dy);

    /** set the new size of the selected object */
    public abstract void setSize(int w, int h);

    /** Returns a tooltip text to show when the mouse hovers on the object */
    public abstract String getTooltipText();
}
