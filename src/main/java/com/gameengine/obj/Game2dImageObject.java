package com.gameengine.obj;

import java.util.ArrayList;

import com.gameengine.obj.img.Game2dObjectAnimatedImage;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

public class Game2dImageObject extends Game2dObject {
    private ArrayList<Game2dObjectState> states = new ArrayList<Game2dObjectState>();
    private String currentViewName;
    private boolean flipX = false;

    public Game2dImageObject(ArrayList<Game2dObjectState> states, String initialStateName) {
        this.states = states;
        this.currentViewName = initialStateName;
    }

    public Game2dObjectState getView() {
        for (Game2dObjectState state : this.states) {
            if (state.getName().equals(this.currentViewName)) {
                return state;
            }
        }
        return null;
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public void setView(String viewName) {
        this.currentViewName = viewName;
    }

    @Override()
    public void draw(GLAutoDrawable drawable, Point2D position) {
        try {
            float x = position.getX();
            float y = position.getY();

            Game2dObjectState currentState = this.getView();
            if (currentState != null) {
                Game2dObjectAnimatedImage image = currentState.getImage();
                image.setFlipX(this.flipX);

                GL2 gl = drawable.getGL().getGL2();
                if (image.getFrameImage() != null) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
