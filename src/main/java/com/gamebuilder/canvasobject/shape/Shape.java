package com.gamebuilder.canvasobject.shape;

import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.model.Selectable;

public abstract class Shape extends CanvasObject implements Selectable {
    private boolean selected = false;

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean getSelected() {
        return this.selected;
    }
}
