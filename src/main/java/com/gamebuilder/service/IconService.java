package com.gamebuilder.service;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class IconService {
    static HashMap<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>();

    public static ImageIcon getIcon(String path, int w, int h) throws IOException {
        path = "/icons" + path;
        if (iconCache.containsKey(path)) {
            return iconCache.get(path);
        }

        BufferedImage img = ImageIO.read(IconService.class.getResourceAsStream(path));
        Image scaledImg = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImg);
        iconCache.put(path, icon);
        return icon;
    }

    public static Image getImage(String path, int w, int h) throws IOException {
        path = "/icons" + path;

        BufferedImage img = ImageIO.read(IconService.class.getResourceAsStream(path));
        return img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }
}
