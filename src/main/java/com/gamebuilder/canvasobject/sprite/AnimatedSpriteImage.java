package com.gamebuilder.canvasobject.sprite;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;

import com.gamebuilder.Point;
import com.gamebuilder.canvasobject.shape.BoundingRect;
import com.gamebuilder.model.asset.Asset;
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
        // TODO: refactor to improve readability
        Point p = this.sprite.getPosition();
        return new BoundingRect(
            this.offsetY + p.getYInt(),
            this.offsetX + p.getXInt() + maxWidth,
            this.offsetY + p.getYInt() + maxHeight,
            this.offsetX + p.getXInt()
        );
    }

    @Override()
    public String getTooltipText() {
        return "Image";
    }

    @Override()
    public void translate(int dx, int dy) {
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
        return this.sprite.getPosition().getXInt();
    }

    public void setX(int x) {
        this.sprite.setPosition(new Point(x, this.sprite.getPosition().getYInt()));
    }

    public int getY() {
        return this.sprite.getPosition().getYInt();
    }

    public void setY(int y) {
        this.sprite.setPosition(new Point(this.sprite.getPosition().getXInt(), y));
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
        Point p = this.sprite.getPosition();
        float imgX = this.offsetX + p.getXInt();
        float imgY = this.offsetY + p.getYInt();

        this.draw(g, new Point(imgX, imgY));
    }

    public void draw(Graphics g, Point position) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.opacity / 100f));

        if (!this.continuous && this.endReached) {
           this.frames.get(0).draw(g2d, position.getXInt(), position.getYInt());
           return;
        }

        long now = System.currentTimeMillis();
        if (frames.isEmpty()) return;

        if (this.endReached && (now - this.lastFinishedAt) > this.delay) {
            this.endReached = false;
            this.currentFramePos = 0;
            this.frames.get(0).draw(g2d, position.getXInt(), position.getYInt());
        }

        long diffMillis = now - this.lastFrameTime;
        this.frames.get(this.currentFramePos).draw(g2d, position.getXInt(), position.getYInt());

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
        Point p = this.sprite.getPosition();
        StringBuilder sb = new StringBuilder();
        sb.append(
            String.format(
                "\n<AnimatedImage x=\"%d\" y=\"%d\" fps=\"%d\" continuous=\"%b\" delay=\"%d\">",
                (int) p.x,
                (int) p.y,
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
