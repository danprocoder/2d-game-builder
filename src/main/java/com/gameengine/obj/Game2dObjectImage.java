package com.gameengine.obj;

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
        // this.width = image.getWidth();
        // this.height = image.getHeight();

        this.bytes = loadBytes(image);
    }

    private ByteBuffer loadBytes(BufferedImage src) {
        BufferedImage abgr = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = abgr.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();

        byte[] srcBytes = ((DataBufferByte) abgr.getRaster().getDataBuffer()).getData();

        // flip vertically, 4 bytes per pixel
        int stride = width * 4;
        byte[] flipped = new byte[srcBytes.length];
        for (int y = 0; y < height; y++) {
            int srcRow = y * stride;
            int dstRow = (height - 1 - y) * stride;
            System.arraycopy(srcBytes, srcRow, flipped, dstRow, stride);
        }

        ByteBuffer buffer = ByteBuffer.allocateDirect(flipped.length).order(ByteOrder.nativeOrder());
        buffer.put(flipped).flip();   // IMPORTANT
        return buffer;
    }

    public ByteBuffer getBytes() {
        return this.bytes;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
