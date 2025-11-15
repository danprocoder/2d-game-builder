package com.gamebuilder.scene;

import java.util.ArrayList;
import java.util.UUID;

import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.model.asset.Asset;
import com.gamebuilder.model.CanvasModel;
import com.gamebuilder.model.Event;
import com.gamebuilder.service.CanvasService;

public class Scene {
    private String id;
    private String name;
    private CanvasService canvasService;
    private Asset backgroundMusic;
    private ArrayList<Event> events = new ArrayList<Event>();
    private boolean isActive;

    public Scene(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    public Scene(String id, String name) {
        this.id = id;
        this.name = name;
        this.canvasService = new CanvasService(new CanvasModel());
        this.isActive = false;
    }

    public String getName() {
        return name;
    }

    public void setBackgroundMusic(Asset backgroundMusic) {
        this.backgroundMusic = backgroundMusic;
    }

    public Asset getBackgroundMusic() {
        return backgroundMusic;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public ArrayList<Event> getEvents() {
        return this.events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public CanvasService getCanvasService() {
        return this.canvasService;
    }

    public String toXml() {
        StringBuilder sb = new StringBuilder();
        sb.append(
            String.format(
                "\n  <Scene id=\"%s\" name=\"%s\" backgroundMusicId=\"%s\">",
                this.id,
                this.name,
                this.backgroundMusic != null ? this.backgroundMusic.getId() : ""
            )
        );

        sb.append("\n    <Objects>");
        for (CanvasObject object: this.canvasService.getObjects()) {
            sb.append(object.toXml());
        }
        sb.append("\n    </Objects>");

        if (this.events.size() > 0) {
            sb.append("\n    <Events>");
            for (var event: this.events) {
                sb.append(event.toXml());
            }
            sb.append("\n    </Events>");
        }

        sb.append("\n  </Scene>");
        return sb.toString();
    }
}
