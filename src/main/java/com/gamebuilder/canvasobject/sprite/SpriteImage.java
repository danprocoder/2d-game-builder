package com.gamebuilder.canvasobject.sprite;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.gamebuilder.model.Asset;

public class SpriteImage {
    private Asset asset;
    private String path;
    private BufferedImage image = null;
    private int width = 0;
    private int height = 0;

    public SpriteImage(Asset asset) throws IOException {
        this.asset = asset;
        this.path = asset.getFullPath();
        this.image = ImageIO.read(new File(this.path));
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public Asset getAsset() {
        return this.asset;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g) {
        if (this.image != null) {
            g.drawImage(this.image.getScaledInstance(this.width, this.height, Image.SCALE_SMOOTH), 0, 0, null);
        }
    }

    public void draw(Graphics g, int x, int y) {
        if (this.image != null) {
            g.drawImage(this.image.getScaledInstance(this.width, this.height, Image.SCALE_SMOOTH), x, y, null);
        }
    }
}