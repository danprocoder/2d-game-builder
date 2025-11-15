package com.gameengine.obj.img;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

public class Game2dObjectImage {
    private String path;
    private int width = 0;
    private int height = 0;
    private ByteBuffer bytes;
    private ByteBuffer byteFlippedX;
    private boolean flippedX = false;

    public Game2dObjectImage(String path, int width, int height) throws IOException {
        this.path = path;
        this.width = width;
        this.height = height;

        BufferedImage original = ImageIO.read(new File(this.path));
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.drawImage(original, 0, 0, width, height, null);
        g2d.dispose();
        BufferedImage image = scaled;
        System.out.println("Original image size: " + original.getWidth() + "x" + original.getHeight());
        System.out.println("Loaded image: " + this.path + " (w: " + image.getWidth() + ", h: " + image.getHeight() + ")");

        this.loadBytes(image);
    }

    public void setFlippedX(boolean flipped) {
        this.flippedX = flipped;
    }

    private void loadBytes(BufferedImage src) {
        BufferedImage abgr = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = abgr.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();

        byte[] srcBytes = ((DataBufferByte) abgr.getRaster().getDataBuffer()).getData();

        // flip vertically
        byte[] flipped = ImageUtil.flipVertically(srcBytes, width, height);
        this.bytes = this.getByteBuffer(srcBytes);

        // flip horizontally
        byte[] flippedX = ImageUtil.flipHorizontally(srcBytes, width, height);
        this.byteFlippedX = this.getByteBuffer(flippedX);
    }

    private ByteBuffer getByteBuffer(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length).order(ByteOrder.nativeOrder());
        buffer.put(bytes).flip();   // IMPORTANT
        return buffer;
    }

    public ByteBuffer getBytes() {
        return this.flippedX ? this.byteFlippedX : this.bytes;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
