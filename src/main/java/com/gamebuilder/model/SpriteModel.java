package com.gamebuilder.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gamebuilder.Point;
import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.canvasobject.shape.Circle;
import com.gamebuilder.canvasobject.shape.Polygon;
import com.gamebuilder.canvasobject.shape.Rectangle;
import com.gamebuilder.canvasobject.sprite.AnimatedSpriteImage;
import com.gamebuilder.canvasobject.sprite.Sprite;
import com.gamebuilder.canvasobject.sprite.SpriteState;
import com.gamebuilder.util.ColorHelper;


public class SpriteModel {
    private ArrayList<Sprite> sprites = new ArrayList<Sprite>();
    private ArrayList<SpriteModelUpdateListener> updateListeners = new ArrayList<SpriteModelUpdateListener>();

    private static SpriteModel instance = null;
    private static AssetModel assetModel = AssetModel.getInstance();

    private Sprite selectedSprite = null;

    public static SpriteModel getInstance() {
        if (instance == null) {
            instance = new SpriteModel();
        }
        return instance;
    }

    /** Parses the object xml from the object.xml file */
    public void loadFromFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new Exception("Sprite file does not exists: " + path);
            }

            if (!file.isFile()) {
                throw new Exception("Sprite path is not a file: " + path);
            }

            ByteArrayInputStream in = new ByteArrayInputStream(Files.readAllBytes(file.toPath()));

            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            Document doc = docBuilder.parse(in);

            NodeList spriteNodes = doc.getElementsByTagName("Sprite");
            for (int i = 0; i < spriteNodes.getLength(); i++) {
                Node spriteTag = spriteNodes.item(i);
                NamedNodeMap attrs = spriteTag.getAttributes();

                Sprite sprite = new Sprite(
                    attrs.getNamedItem("id").getNodeValue(),
                    attrs.getNamedItem("name").getNodeValue()
                );
                ArrayList<SpriteState> states = parseSpriteStates(spriteTag, sprite);
                sprite.setStates(states);

                this.sprites.add(sprite);
            }
            this.notifyUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<SpriteState> parseSpriteStates(Node spriteTag, Sprite sprite) {
        ArrayList<SpriteState> states = new ArrayList<SpriteState>();
        NodeList childNodes = spriteTag.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node child = childNodes.item(j);
            if (child.getNodeName().equals("State")) {
                NamedNodeMap attrs = child.getAttributes();
                SpriteState state = new SpriteState(attrs.getNamedItem("name").getNodeValue());

                state.setImage(this.parseSpriteStateImage(child, sprite));
                state.setCollisionBox(this.parseSpriteStateCollisionBox(child));

                states.add(state);
            }
        }
        return states;
    }

    // TODO: refactor out the CanvasService singleton usage here
    private AnimatedSpriteImage parseSpriteStateImage(Node spriteStateTag, Sprite sprite) {
        AnimatedSpriteImage image = null;

        NodeList imageNode = spriteStateTag.getChildNodes();
        for (int i = 0; i < imageNode.getLength(); i++) {
            Node node = imageNode.item(i);
            if (node.getNodeName().equals("AnimatedImage")) {
                image = new AnimatedSpriteImage();

                NamedNodeMap attrs = node.getAttributes();
                image.setX(attrs.getNamedItem("x") != null ? Integer.parseInt(attrs.getNamedItem("x").getNodeValue()) : 0);
                image.setY(attrs.getNamedItem("y") != null ? Integer.parseInt(attrs.getNamedItem("y").getNodeValue()) : 0);
                
                // Build each frame with the <Frame />
                parseAnimatedImageFrames(image, node);

                break;
            }
        }

        return image;
    }

    private void parseAnimatedImageFrames(AnimatedSpriteImage image, Node animatedImageTag) {
        NodeList frames = animatedImageTag.getChildNodes();
        for (int j = 0; j < frames.getLength(); j++) {
            Node frameNode = frames.item(j);
            if (frameNode.getNodeName().equals("Frame")) {
                String assetId = frameNode.getAttributes().getNamedItem("assetId").getNodeValue();
                try {
                    image.addFrame(assetModel.getById(assetId));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // TODO: refactor out the CanvasService singleton usage here
    private CanvasObject parseSpriteStateCollisionBox(Node spriteStateTag) {
        CanvasObject object = null;

        NodeList children = spriteStateTag.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeName().equals("CollisionBox")) {
                NodeList boxChildren = node.getChildNodes();
                for (int j = 0; j < boxChildren.getLength(); j++) {
                    Node boxNode = boxChildren.item(j);
                    if (boxNode.getNodeName().equals("Rectangle")) {
                        NamedNodeMap attrs = boxNode.getAttributes();
                        object = new Rectangle(
                            new Point(
                                Integer.parseInt(attrs.getNamedItem("x").getNodeValue()),
                                Integer.parseInt(attrs.getNamedItem("y").getNodeValue())
                            ),
                            Integer.parseInt(attrs.getNamedItem("width").getNodeValue()),
                            Integer.parseInt(attrs.getNamedItem("height").getNodeValue()),
                            ColorHelper.getColorFromHex(attrs.getNamedItem("color").getNodeValue()),
                            attrs.getNamedItem("name").getNodeValue()
                        );
                    } else if (boxNode.getNodeName().equals("Circle")) {
                        NamedNodeMap attrs = boxNode.getAttributes();
                        object = new Circle(
                            new Point(
                                Integer.parseInt(attrs.getNamedItem("x").getNodeValue()),
                                Integer.parseInt(attrs.getNamedItem("y").getNodeValue())
                            ),
                            Integer.parseInt(attrs.getNamedItem("width").getNodeValue()),
                            Integer.parseInt(attrs.getNamedItem("height").getNodeValue()),
                            ColorHelper.getColorFromHex(attrs.getNamedItem("color").getNodeValue()),
                            attrs.getNamedItem("name").getNodeValue()
                        );
                    } else if (boxNode.getNodeName().equals("Polygon")) {
                        ArrayList<Point> pts = new ArrayList<Point>();

                        NodeList pointNodes = boxNode.getChildNodes();
                        for (int k = 0; k < pointNodes.getLength(); k++) {
                            Node pointNode = pointNodes.item(k);
                            if (pointNode.getNodeName().equals("Point")) {
                                NamedNodeMap pointAttrs = pointNode.getAttributes();
                                pts.add(new Point(
                                    Integer.parseInt(pointAttrs.getNamedItem("x").getNodeValue()),
                                    Integer.parseInt(pointAttrs.getNamedItem("y").getNodeValue())
                                ));
                            }
                        }

                        NamedNodeMap attrs = boxNode.getAttributes();
                        object = new Polygon(
                            pts,
                            ColorHelper.getColorFromHex(attrs.getNamedItem("color").getNodeValue()),
                            attrs.getNamedItem("name").getNodeValue()
                        );
                    }
                }
                break;
            }
        }

        return object;
    }

    public void addSprite(Sprite sprite) {
        this.sprites.add(sprite);
        this.notifyUpdate();
    }

    public ArrayList<Sprite> getSprites() {
        return this.sprites;
    }

    public Sprite getSelectedSprite() {
        return this.selectedSprite;
    }

    public void setSelectedSprite(Sprite sprite) {
        this.selectedSprite = sprite;
        this.notifyUpdate();
    }

    public void addUpdateListener(SpriteModelUpdateListener listener) {
        this.updateListeners.add(listener);
    }

    public void notifyUpdate() {
        for (SpriteModelUpdateListener listener: this.updateListeners) {
            listener.onSpriteModelUpdated();
        }
    }
}
