package com.gameengine.obj;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

public class Game2dCircleObject extends Game2dObject {
    private float radius;
    private Color color;

    public Game2dCircleObject(float radius, Color color) {
        this.radius = radius;
        this.color = color;
    }

    @Override()
    public void draw(GLAutoDrawable drawable, Point2D position) {
        GL2 gl2 = drawable.getGL().getGL2();

        gl2.glColor4f(this.color.getRed(), this.color.getGreen(), this.color.getBlue(),
                      this.color.getAlpha());

        gl2.glBegin(GL2.GL_POLYGON);

        float cx = position.getX() + this.radius;
        float cy = position.getY() + this.radius;

        for (int i = 0; i < 360; i++) {
            float px = cx + (this.radius * (float) Math.cos(i));
            float py = cy + (this.radius * (float) Math.sin(i));

            // float px2 = x + (this.radius * (float) Math.cos(i + 1));
            // float py2 = y + (this.radius * (float) Math.sin(i + 1));

            gl2.glVertex2f(px, py);
        }

        gl2.glEnd();
    }
}
