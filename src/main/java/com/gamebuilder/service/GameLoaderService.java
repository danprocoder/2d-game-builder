package com.gamebuilder.service;

import java.util.ArrayList;

import com.gamebuilder.Point;
import com.gamebuilder.canvasobject.AssetObject;
import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.canvasobject.shape.Circle;
import com.gamebuilder.canvasobject.shape.Polygon;
import com.gamebuilder.canvasobject.shape.Rectangle;
import com.gamebuilder.canvasobject.shape.Shape;
import com.gamebuilder.canvasobject.sprite.Sprite;
import com.gamebuilder.model.Event;
import com.gamebuilder.scene.Scene;
import com.gamebuilder.service.script.ScriptParser;
import com.gamebuilder.service.script.expression.FuncCallExpression;
import com.gamebuilder.service.script.resolver.ValueResolver;
import com.gamebuilder.util.Log;
import com.gameengine.Game;
import com.gameengine.audio.Audio;
import com.gameengine.obj.Color;
import com.gameengine.obj.Game2dCircleObject;
import com.gameengine.obj.Game2dImageObject;
import com.gameengine.obj.Game2dObjectState;
import com.gameengine.obj.Game2dPolygonObject;
import com.gameengine.obj.Game2dRectObject;
import com.gameengine.obj.Point2D;
import com.gameengine.obj.event.EventAction;
import com.gameengine.obj.event.EventContext;
import com.gameengine.obj.event.ObjectEvent;
import com.gameengine.obj.event.actions.FlipXChangeEvent;
import com.gameengine.obj.event.actions.ObjectViewChangeEvent;
import com.gameengine.obj.event.actions.SpeedChangeEvent;
import com.gameengine.obj.event.actions.VelocityChangeEvent;
import com.gameengine.obj.event.conditions.Condition;
import com.gameengine.obj.img.Game2dObjectAnimatedImage;
import com.gameengine.scene.Game2dScene;
import com.gameengine.scene.Game2dSceneObject;

public class GameLoaderService {

    private static GameLoaderService instance;

    private GameLoaderService() {}

    public static GameLoaderService getInstance() {
        if (instance == null) {
            instance = new GameLoaderService();
        }
        return instance;
    }

    public Game loadGame(SceneService sceneService) {
        try {
            ArrayList<Game2dScene> scenes = this.getGameScenes(sceneService);

            return new Game("1.0", "Test Game", new int[] { 640, 480 }, scenes);
        } catch (Exception e) {
            System.out.println("Failed to load game");
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<Game2dScene> getGameScenes(SceneService sceneService) throws Exception {
        ArrayList<Game2dScene> gameScenes = new ArrayList<>();

        for (Scene s: sceneService.getScenes()) {
            Game2dScene gameScene = new Game2dScene();
            if (s.getBackgroundMusic() != null) {
                gameScene.setBackgroundAudio(
                        new Audio(s.getBackgroundMusic().getFullPath()));
            }

            gameScene.setObjects(this.getGameObjects(s.getCanvasService()));
            
            gameScenes.add(gameScene);
        }

        return gameScenes;
    }

    private ArrayList<Game2dSceneObject> getGameObjects(CanvasService canvasService) throws Exception {
        ArrayList<Game2dSceneObject> objects = new ArrayList<Game2dSceneObject>();

        canvasService.getObjects().forEach(canvasObject -> {
            try {
                if (canvasObject instanceof AssetObject) {
                    objects.add(canvasObjectToGameSceneObject(((AssetObject) canvasObject).getObject()));
                } else {
                    objects.add(canvasObjectToGameSceneObject(canvasObject));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return objects;
    }

    private Game2dSceneObject canvasObjectToGameSceneObject(CanvasObject canvasObject) throws Exception {
        Game2dSceneObject obj = null;

        if (canvasObject instanceof Sprite) {
            ArrayList<Game2dObjectState> states = new ArrayList<>();
            ((Sprite) canvasObject).getStates().forEach((state) -> {
                Game2dObjectAnimatedImage image = new Game2dObjectAnimatedImage();
                state.getImage().getFrames().forEach((frame) -> {
                    try {
                        image.addFrame(frame.getAsset(), frame.getWidth(), frame.getHeight());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                states.add(new Game2dObjectState(new ArrayList<>(), image, state.getName()));
            });
            obj = new Game2dSceneObject(
                new Game2dImageObject(states, "default"),
                this.getOpenGlCoords(canvasObject.getPosition())
            );
        } else if (canvasObject instanceof Polygon) {
            Polygon polygon = (Polygon) canvasObject;
            ArrayList<Point2D> pts = this.getOpenGlPolygonPoints(polygon.getPoints());
            Game2dPolygonObject gPoly = new Game2dPolygonObject(
                    pts, getColor((Shape) canvasObject));
            obj = new Game2dSceneObject(gPoly, this.getPolygonInitialPosition(pts));
        } else if (canvasObject instanceof Circle) {
            obj = new Game2dSceneObject(
                new Game2dCircleObject(
                    ((Circle) canvasObject).getRadius(),
                    getColor((Shape) canvasObject)
                ),
                this.getOpenGlCoords(canvasObject.getPosition())
            );
        } else if (canvasObject instanceof Rectangle) {
            Point2D position = this.getOpenGlCoords(canvasObject.getPosition())
                                   .subtract(new Point2D(0, canvasObject.getHeight()));
            obj = new Game2dSceneObject(
                new Game2dRectObject(
                    canvasObject.getWidth(),
                    canvasObject.getHeight(),
                    getColor((Shape) canvasObject)
                ),
                position
            );
        }

        if (obj != null) {
            ArrayList<ObjectEvent> events = this.getEvents(canvasObject, obj);
            obj.setEvents(events);
            Log.v("canvasObjectToGameSceneObject()", "Events parsed: " + events.size());
        }

        return obj;
    }

    private ArrayList<ObjectEvent> getEvents(CanvasObject canvasObject, Game2dSceneObject gObject) throws Exception {
        ArrayList<ObjectEvent> events = new ArrayList<ObjectEvent>();

        for (Event<Object> ev: canvasObject.getEvents()) {
            if (ev.value == null) {
                continue;
            }

            // Convert value to Character - handle both String and Character types
            Character keyChar = null;
            if (ev.getValue() instanceof Character) {
                keyChar = (Character) ev.getValue();
            } else if (ev.getValue() instanceof String) {
                String str = (String) ev.getValue();
                if (!str.isEmpty()) {
                    keyChar = str.charAt(0);
                }
            } else {
                Log.e("getEvents()", "Unexpected event value type: " + ev.getValue().getClass().getName());
                continue;
            }

            if (keyChar == null) {
                Log.e("getEvents()", "Could not convert event value to Character");
                continue;
            }

            switch (ev.eventType) {
                case "On Key Press":
                    events.add(
                        new ObjectEvent(
                            new EventContext("key_down", keyChar),
                            gObject,
                            this.parseCondition(gObject, ev.getPrecondition()),
                            this.parseAction(gObject, ev.getAction())
                        )
                    );
                    break;

                case "On Key Release":
                    events.add(
                        new ObjectEvent(
                            new EventContext("key_up", keyChar),
                            gObject,
                            this.parseCondition(gObject, ev.getPrecondition()),
                            this.parseAction(gObject, ev.getAction())
                        )
                    );
                    break;

                case "On Click":
                    events.add(
                        new ObjectEvent(
                            new EventContext("mouse_down", keyChar),
                            gObject,
                            this.parseCondition(gObject, ev.getPrecondition()),
                            this.parseAction(gObject, ev.getAction())
                        )
                    );
                    break;
                
                case "On Mouse Up":
                    events.add(
                        new ObjectEvent(
                            new EventContext("mouse_up", keyChar),
                            gObject,
                            this.parseCondition(gObject, ev.getPrecondition()),
                            this.parseAction(gObject, ev.getAction())
                        )
                    );
                    break;
            }
        };

        return events;
    }

    private ArrayList<EventAction> parseAction(Game2dSceneObject object, String action) throws Exception {
        ArrayList<EventAction> actions = new ArrayList<EventAction>();

        for (String script : action.split(";")) {
            ScriptParser parser = new ScriptParser(script);

            parser.parse().getAst().forEach(exp -> {
                if (exp instanceof FuncCallExpression) {
                    FuncCallExpression fn = (FuncCallExpression) exp;

                    switch (fn.getName()) {
                        case "SetVelocity":
                            try {
                                // TODO: address this unchecked cast later
                                VelocityChangeEvent velocityEvent = new VelocityChangeEvent(object,
                                    (ValueResolver<Float>) fn.getArgs().get(0).getResolver(),
                                    (ValueResolver<Float>) fn.getArgs().get(1).getResolver()
                                );
                                actions.add(velocityEvent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case "SetView":
                            ObjectViewChangeEvent objectEvent = new ObjectViewChangeEvent(object,
                                (ValueResolver<String>) fn.getArgs().get(0).getResolver());
                            actions.add(objectEvent);
                            break;
                        case "flipX":
                            FlipXChangeEvent flipX = new FlipXChangeEvent(object, (ValueResolver<Boolean>) fn.getArgs().get(0).getResolver());
                            actions.add(flipX);
                            break;
                        case "SetSpeed":
                            SpeedChangeEvent speedEvent = new SpeedChangeEvent(object, (ValueResolver<Integer>) fn.getArgs().get(0).getResolver());
                            actions.add(speedEvent);
                            break;
                    }
                }
            });
        }

        return actions;
    }

    private Condition parseCondition(Game2dSceneObject object, String action) {
        try {
            ScriptParser statement = new ScriptParser(action);
            statement.parse();
        } catch (Exception e) {}
        Log.v("GameLoaderService.parseCondition()", "Parsing condition " + action);
        return null;
    }

    private Point2D getOpenGlCoords(Point swingPos) {
        return new Point2D(swingPos.x, 480 - swingPos.y);
    }

    private ArrayList<Point2D> getOpenGlPolygonPoints(ArrayList<Point> pts) {
        ArrayList<Point2D> openGlVerteces = new ArrayList<Point2D>();

        pts.forEach(pt -> openGlVerteces.add(this.getOpenGlCoords(pt)));

        return openGlVerteces;
    }

    private Point2D getPolygonInitialPosition(ArrayList<Point2D> position) {
        float x = Float.MAX_VALUE;
        float y = Float.MAX_VALUE;

        for (Point2D p: position) {
            if (p.getX() < x) x = p.getX();
            if (p.getY() < y) y = p.getY();
        }

        return new Point2D(x, y);
    }

    private Color getColor(Shape canvasObject) {
        return new Color(
            canvasObject.getColor().getRed(),
            canvasObject.getColor().getGreen(),
            canvasObject.getColor().getBlue(),
            canvasObject.getColor().getAlpha()
        );
    }
}
