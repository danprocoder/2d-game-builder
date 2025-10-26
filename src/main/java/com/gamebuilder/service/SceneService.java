package com.gamebuilder.service;

import java.io.File;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.gamebuilder.model.AssetModel;
import com.gamebuilder.model.SceneModel;
import com.gamebuilder.model.SceneUpdateListener;
import com.gamebuilder.scene.Scene;

class XmlLoadException extends Exception {
    public XmlLoadException(String message) {
        super(message);
    }
}

public class SceneService {
    private SceneModel sceneModel;
    private ArrayList<SceneUpdateListener> updateListeners = new ArrayList<SceneUpdateListener>();
    
    public SceneService(SceneModel sceneModel) {
        this.sceneModel = sceneModel;
    }

    public void addScene(Scene scene) {
        this.sceneModel.addScene(scene);
        this.setActive(scene);
        this.notifyChange();
    }

    public ArrayList<Scene> getScenes() {
        return this.sceneModel.getScenes();
    }

    public int getSceneCount() {
        return this.sceneModel.getScenes().size();
    }

    public Scene getCurrentScene() {
        for (Scene scene: this.sceneModel.getScenes()) {
            if (scene.isActive()) {
                return scene;
            }
        }
        return null;
    }

    public void setActive(Scene scene) {
        for (Scene s: this.sceneModel.getScenes()) {
            s.setActive(false);
        }
        scene.setActive(true);

        this.notifyChange();
    }

    public void addUpdateListener(SceneUpdateListener listener) {
        this.updateListeners.add(listener);
    }

    public void notifyChange() {
        for (SceneUpdateListener listener: this.updateListeners) {
            listener.onSceneUpdate();
        }
    }

    public void loadScenesFromFile(String filePath) throws Exception {
        ArrayList<Scene> scenes = new ArrayList<Scene>();

        File file = new File(filePath);
        if (!file.exists()) {
            throw new XmlLoadException("File not found: " + filePath);
        } else if (!file.isFile()) {
            throw new XmlLoadException("Path is not a file: " + filePath);
        }

        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = docBuilder.parse(file);

        NodeList sceneNodes = doc.getElementsByTagName("Scenes").item(0).getChildNodes();

        for (int i = 0; i < sceneNodes.getLength(); i++) {
            if (sceneNodes.item(i).getNodeName().equals("Scene")) {
                NamedNodeMap attributes = sceneNodes.item(i).getAttributes();
                String id = attributes.getNamedItem("id").getNodeValue();
                String name = attributes.getNamedItem("name").getNodeValue();
                String bgMusicId = attributes.getNamedItem("backgroundMusicId").getNodeValue();

                Scene scene = new Scene(id, name);
                scene.setBackgroundMusic(AssetModel.getInstance().getById(bgMusicId));
                scenes.add(scene);
            }
        }

        this.sceneModel.setScenes(scenes);
        this.notifyChange();
    }
}
