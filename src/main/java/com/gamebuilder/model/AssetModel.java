package com.gamebuilder.model;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class AssetModel {
    private ArrayList<Asset> assets = new ArrayList<>();
    private ArrayList<AssetModelUpdateListener> updateListeners = new ArrayList<>();

    private AssetModel() {}

    private static AssetModel instance = null;

    public static AssetModel getInstance() {
        if (instance == null) {
            instance = new AssetModel();
        }
        return instance;
    }

    /**
     * Parses the asset xml from the asset.xml file.
     * 
     * @param projectDir The basepath of the project
     * @param assetXmlPath The relative path of the asset xml file
     */
    public void loadFromFile(String projectDir, String assetXmlPath) throws Exception {
        File file = new File(assetXmlPath);
        if (!file.exists()) {
            throw new Exception("Asset file does not exist: " + assetXmlPath);
        } else if (!file.isFile()) {
            throw new Exception("Asset path is not a file: " + assetXmlPath);
        }

        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = docBuilder.parse(file);

        NodeList assetNodes = doc.getElementsByTagName("Assets").item(0).getChildNodes();
        for (int i = 0; i < assetNodes.getLength(); i++) {
            if (assetNodes.item(i).getNodeName().equals("AssetItem")) {
                NamedNodeMap attributes = assetNodes.item(i).getAttributes();
                String id = attributes.getNamedItem("id").getNodeValue();
                String path = attributes.getNamedItem("path").getNodeValue();
                String name = attributes.getNamedItem("name").getNodeValue();

                String fullPath = Paths.get(projectDir, path).toString();
                Asset asset = new Asset(id, path, fullPath, name);
                assets.add(asset);
            }
        }
        this.notifyUpdate();
    }

    public Asset getById(String id) {
        for (Asset a: assets) {
            if (a.getId().equals(id)) {
                return a;
            }
        }
        return null;
    }

    public void addAsset(Asset asset) {
        this.assets.add(asset);
        this.notifyUpdate();
    }

    public ArrayList<Asset> getAssets() {
        return assets;
    }

    public void addUpdateListener(AssetModelUpdateListener listener) {
        this.updateListeners.add(listener);
    }

    public void notifyUpdate() {
        for (AssetModelUpdateListener listener : this.updateListeners) {
            listener.onAssetModelUpdated();
        }
    }
}
