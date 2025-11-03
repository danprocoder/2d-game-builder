package com.gamebuilder.canvasobject.sprite;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;

import com.gamebuilder.Point;
import com.gamebuilder.canvasobject.shape.BoundingRect;
import com.gamebuilder.model.Asset;
import com.gamebuilder.model.Selectable;

public class AnimatedSpriteImage implements Selectable {
    private Sprite sprite;
    private ArrayList<SpriteImage> frames = new ArrayList<SpriteImage>();
    private int currentFramePos = 0;
    private boolean continuous = true;
    private long lastFrameTime = 0l;
    private long lastFinishedAt = 0l;
    private int delay = 2000;
    private int fps = 10;
    private boolean endReached = false;
    private Point point = new Point(0, 0);
    private float opacity = 100.0f;
    private int offsetX = 0;
    private int offsetY = 0;
    private boolean isSelected = false;

    public AnimatedSpriteImage(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override()
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    @Override()
    public boolean getSelected() {
        return this.isSelected;
    }

    @Override()
    public boolean mouseHit(int mx, int my) {
        return this.getBoundingRect().hit(mx, my);
    }

    @Override()
    public BoundingRect getBoundingRect() {
        int maxWidth = 0;
        int maxHeight = 0;
        for (SpriteImage frame: this.frames) {
            if (frame.getWidth() > maxWidth) {
                maxWidth = frame.getWidth();
            }
            if (frame.getHeight() > maxHeight) {
                maxHeight = frame.getHeight();
            }
        }
        return new BoundingRect(
            this.offsetY + this.point.getYInt(),
            this.offsetX + this.point.getXInt() + maxWidth,
            this.offsetY + this.point.getYInt() + maxHeight,
            this.offsetX + this.point.getXInt()
        );
    }

    @Override()
    public String getTooltipText() {
        return "Image";
    }

    @Override()
    public void translate(int dx, int dy) {
        this.point = new Point(this.point.getXInt() + dx, this.point.getYInt() + dy);
        this.sprite.translate(dx, dy);
    }

    @Override()
    public void setSize(int w, int h) {
        BoundingRect rect = this.getBoundingRect();
        int currentWidth = rect.right - rect.left;
        int currentHeight = rect.bottom - rect.top;
        int dw = w - currentWidth;
        int dh = h - currentHeight;

        for (SpriteImage frame: this.frames) {
            frame.setSize(frame.getWidth() + dw, frame.getHeight() + dh);
        }
    }

    public int getX() {
        return this.point.getXInt();
    }

    public void setX(int x) {
        this.point = new Point(x, this.point.getYInt());
    }

    public int getY() {
        return this.point.getYInt();
    }

    public void setY(int y) {
        this.point = new Point(this.point.getXInt(), y);
    }

    public void adjustOffset(int dx, int dy) {
        this.offsetX += dx;
        this.offsetY += dy;
    }

    public void addFrame(Asset asset) throws IOException {
        this.frames.add(new SpriteImage(asset));
    }

    public void removeFrameAt(int index) {
        this.frames.remove(index);
    }

    public void setFPS(int fps) {
        this.fps = fps;
    }

    public int getFPS() {
        return this.fps;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return this.delay;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public float getOpacity() {
        return this.opacity;
    }

    public boolean getContinuous() {
        return this.continuous;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.opacity / 100f));

        int imgX = this.offsetX + this.point.getXInt();
        int imgY = this.offsetY + this.point.getYInt();

        if (!this.continuous && this.endReached) {
           this.frames.get(0).draw(g2d, imgX, imgY);
           return;
        }

        long now = System.currentTimeMillis();
        if (frames.isEmpty()) return;

        if (this.endReached && (now - this.lastFinishedAt) > this.delay) {
            this.endReached = false;
            this.currentFramePos = 0;
            this.frames.get(0).draw(g2d, imgX, imgY);
        }

        long diffMillis = now - this.lastFrameTime;
        this.frames.get(this.currentFramePos).draw(g2d, imgX, imgY);

        if (diffMillis > (1000 / this.fps)) {
            this.lastFrameTime = System.currentTimeMillis();

            this.currentFramePos++;
            if (this.currentFramePos >= this.frames.size()) {
                this.currentFramePos = 0;
                this.lastFinishedAt = now;
                this.endReached = true;
            }
        }

        g2d.dispose();
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    public ArrayList<SpriteImage> getFrames() {
        return this.frames;
    }

    public int getNumberOfFrames() {
        return this.frames.size();
    }

    public String toXml() {
        StringBuilder sb = new StringBuilder();
        sb.append(
            String.format(
                "\n<AnimatedImage x=\"%d\" y=\"%d\" fps=\"%d\" continuous=\"%b\" delay=\"%d\">",
                (int) this.point.x,
                (int) this.point.y,
                this.fps,
                this.continuous,
                this.delay
            )
        );
        for (SpriteImage frame: this.frames) {
            sb.append(
                String.format(
                    "\n  <Frame assetId=\"%s\" width=\"%d\" height=\"%d\" />",
                    frame.getAsset().getId(),
                    frame.getWidth(),
                    frame.getHeight()
                )
            );
        }
        sb.append("\n</AnimatedImage>");
        return sb.toString();
    }
}
