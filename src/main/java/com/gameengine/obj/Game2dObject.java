package com.gameengine.obj;

import java.util.ArrayList;

import com.gamebuilder.util.Log;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

public class Game2dObject {
    private ArrayList<Game2dObjectState> states = new ArrayList<Game2dObjectState>();
    private String currentStateName;

    public Game2dObject(ArrayList<Game2dObjectState> states, String initialStateName) {
        this.states = states;
        this.currentStateName = initialStateName;
    }

    public Game2dObjectState getObjectState() {
        for (Game2dObjectState state : this.states) {
            if (state.getName().equals(this.currentStateName)) {
                return state;
            }
        }
        return null;
    }

    public void draw(GLAutoDrawable drawable, float x, float y) {
        Game2dObjectState currentState = this.getObjectState();
        if (currentState != null) {
            Game2dObjectAnimatedImage image = currentState.getImage();
            
            Log.d("Game2dObject(d, x, y)", "Called to draw image at x: " + x + ", y: " + y + ", bytes: " + image.getFrameImage().getBytes().capacity());

            GL2 gl = drawable.getGL().getGL2();
            if (image.getFrameImage() != null) {
                Log.d("Game2dObject.draw(d, x, y)", "Frames exists");
                
                gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
                
                // Set raster position for where to draw
                gl.glRasterPos2f(x, y);
                
                // Use glDrawPixels for colored images instead of glBitmap
                // GL_RGB or GL_RGBA depending on your image format
                gl.glDrawPixels(
                    // TODO: change name from getBytes to getImage or similar
                    image.getFrameImage().getWidth(), 
                    image.getFrameImage().getHeight(), 
                    GL2.GL_ABGR_EXT,
                    GL2.GL_UNSIGNED_BYTE, 
                    image.getFrameImage().getBytes()
                );
            }
        }
    }
}
