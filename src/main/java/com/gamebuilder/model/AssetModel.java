package com.gamebuilder.model;

import java.io.File;
import java.util.ArrayList;

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

    public void loadFromDirectory(String directory) throws Exception {
        File dir = new File(directory);
        if (!dir.exists()) {
            throw new Exception("Asset directory does not exists: " + directory);
        }

        if (dir.isDirectory()) {
            throw new Exception("Asset path is not a directory: " + directory);
        }

        for (File file: dir.listFiles()) {
            if (file.isFile()) {
                Asset asset = new Asset(file.getAbsolutePath());
                this.assets.add(asset);
            }
        }
        this.notifyUpdate();
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
