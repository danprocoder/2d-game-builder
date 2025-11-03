package com.gamebuilder.service;

import java.io.FileInputStream;
import java.io.IOException;
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
import com.gamebuilder.model.AssetModel;
import com.gamebuilder.model.Event;
import com.gamebuilder.util.ColorHelper;

public class XmlParserService {
    public static ArrayList<CanvasObject> loadFromFile(String filePath) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(new FileInputStream(filePath));

        Node root = document.getElementsByTagName("Object").item(0);
        if (root == null) {
            throw new Exception("Invalid object XML: Missing root Object element");
        }

        return parseObjects(root.getChildNodes());
    }
    
    public static ArrayList<CanvasObject> parseObjects(NodeList objectNodeList) {
        ArrayList<CanvasObject> objects = new ArrayList<CanvasObject>();

        for (int i = 0; i < objectNodeList.getLength(); i++) {
            Node node = objectNodeList.item(i);
            String nodeName = node.getNodeName();
            if (nodeName.equals("Rectangle")) {
                objects.add(parseRectangle(node));
            } else if (nodeName.equals("Circle")) {
                objects.add(parseCircle(node));
            } else if (nodeName.equals("Polygon")) {
                objects.add(parsePolygon(node));
            } else if (nodeName.equals("Sprite")) {
                objects.add(parseSprite(node));
            }
        }

        return objects;
    }

    public static ArrayList<Event> parseEvents(Node eventParentNode) {
        ArrayList<Event> events = new ArrayList<Event>();

        NodeList childNodes = eventParentNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeName().equals("Events")) {
                NodeList eventNodes = child.getChildNodes();
                for (int j = 0; j < eventNodes.getLength(); j++) {
                    Node eventNode = eventNodes.item(j);
                    if (eventNode.getNodeName().equals("Event")) {
                        NamedNodeMap attrs = eventNode.getAttributes();
                        events.add(new Event(
                            attrs.getNamedItem("type").getNodeValue()
                        ));
                    }
                }
            }
        }

        return events;
    }

    private static Sprite parseSprite(Node node) {
        NamedNodeMap attrs = node.getAttributes();

        Sprite sprite = new Sprite(
            attrs.getNamedItem("id").getNodeValue(),
            attrs.getNamedItem("name").getNodeValue()
        );
        ArrayList<SpriteState> states = parseSpriteStates(node, sprite);

        ArrayList<Event> events = parseEvents(node);

        sprite.setStates(states);
        sprite.setEvents(events);

        return sprite;
    }

    private static ArrayList<SpriteState> parseSpriteStates(Node spriteTag, Sprite sprite) {
        ArrayList<SpriteState> states = new ArrayList<SpriteState>();
        NodeList childNodes = spriteTag.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node child = childNodes.item(j);
            if (child.getNodeName().equals("State")) {
                NamedNodeMap attrs = child.getAttributes();
                SpriteState state = new SpriteState(attrs.getNamedItem("name").getNodeValue());

                state.setImage(parseSpriteStateImage(child, sprite));
                state.setCollisionBox(parseSpriteStateCollisionBox(child));

                states.add(state);
            }
        }
        return states;
    }

    private static AnimatedSpriteImage parseSpriteStateImage(Node spriteStateTag, Sprite sprite) {
        AnimatedSpriteImage image = null;

        NodeList imageNode = spriteStateTag.getChildNodes();
        for (int i = 0; i < imageNode.getLength(); i++) {
            Node node = imageNode.item(i);
            if (node.getNodeName().equals("AnimatedImage")) {
                image = new AnimatedSpriteImage(sprite);

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

    private static void parseAnimatedImageFrames(AnimatedSpriteImage image, Node animatedImageTag) {
        NodeList frames = animatedImageTag.getChildNodes();
        for (int j = 0; j < frames.getLength(); j++) {
            Node frameNode = frames.item(j);
            if (frameNode.getNodeName().equals("Frame")) {
                String assetId = frameNode.getAttributes().getNamedItem("assetId").getNodeValue();
                try {
                    image.addFrame(AssetModel.getInstance().getById(assetId));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static CanvasObject parseSpriteStateCollisionBox(Node spriteStateTag) {
        ArrayList<CanvasObject> objects = null;

        NodeList children = spriteStateTag.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeName().equals("CollisionBox")) {
                NodeList boxChildren = node.getChildNodes();
                objects = parseObjects(boxChildren);
            }
        }

        return objects.size() > 0 ? objects.get(0) : null;
    }
    
    private static Rectangle parseRectangle(Node node) {
        NamedNodeMap attrs = node.getAttributes();
        Rectangle rect = new Rectangle(
            new Point(
                Integer.parseInt(attrs.getNamedItem("x").getNodeValue()),
                Integer.parseInt(attrs.getNamedItem("y").getNodeValue())
            ),
            Integer.parseInt(attrs.getNamedItem("width").getNodeValue()),
            Integer.parseInt(attrs.getNamedItem("height").getNodeValue()),
            ColorHelper.getColorFromHex(attrs.getNamedItem("color").getNodeValue()),
            attrs.getNamedItem("name").getNodeValue()
        );

        ArrayList<Event> events = parseEvents(node);
        rect.setEvents(events);

        return rect;
    }

    private static Circle parseCircle(Node node) {
        NamedNodeMap attrs = node.getAttributes();
        Circle circle = new Circle(
            new Point(
                Integer.parseInt(attrs.getNamedItem("x").getNodeValue()),
                Integer.parseInt(attrs.getNamedItem("y").getNodeValue())
            ),
            Integer.parseInt(attrs.getNamedItem("width").getNodeValue()),
            Integer.parseInt(attrs.getNamedItem("height").getNodeValue()),
            ColorHelper.getColorFromHex(attrs.getNamedItem("color").getNodeValue()),
            attrs.getNamedItem("name").getNodeValue()
        );

        ArrayList<Event> events = parseEvents(node);
        circle.setEvents(events);

        return circle;
    }

    private static Polygon parsePolygon(Node node) {
        ArrayList<Point> pts = new ArrayList<Point>();

        NodeList pointNodes = node.getChildNodes();
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

        NamedNodeMap attrs = node.getAttributes();
        Polygon polygon = new Polygon(
            pts,
            ColorHelper.getColorFromHex(attrs.getNamedItem("color").getNodeValue()),
            attrs.getNamedItem("name").getNodeValue()
        );

        ArrayList<Event> events = parseEvents(node);
        polygon.setEvents(events);

        return polygon;
    }
}
