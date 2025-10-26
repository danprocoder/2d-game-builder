package com.gamebuilder.scene;

import com.gamebuilder.model.Asset;
import com.gamebuilder.model.CanvasModel;
import com.gamebuilder.service.CanvasService;

public class Scene {
    private String id;
    private String name;
    private CanvasService canvasService;
    private Asset backgroundMusic;
    private boolean isActive;

    public Scene(String name) {
        this(java.util.UUID.randomUUID().toString(), name);
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

    public String toXml() {
        StringBuilder sb = new StringBuilder();
        sb.append(
            String.format(
                "\n<Scene id=\"%s\" name=\"%s\" backgroundMusicId=\"%s\">",
                this.id,
                this.name,
                this.backgroundMusic != null ? this.backgroundMusic.getId() : ""
            )
        );
        // TODO: serialize scene objects
        sb.append("\n</Scene>");
        return sb.toString();
    }
}
