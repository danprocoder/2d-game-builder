package com.gamebuilder.model;

import java.nio.file.Paths;
import java.util.UUID;

import com.gamebuilder.util.Log;

public class Asset {
    String id;
    String name;
    String path;
    String fullPath;
    String type;

    public Asset(String path, String fullPath) {
        this.id = UUID.randomUUID().toString();
        this.path = path;
        this.fullPath = fullPath;
        this.name = Paths.get(path).getFileName().toString();
        this.type = this.getAssetType(path);
    }

    public Asset(String id, String path, String fullPath, String name) {
        this.id = id;
        this.path = path;
        this.fullPath = fullPath;
        this.name = name;
        this.type = this.getAssetType(path);
    }

    private String getAssetType(String path) {
        String ext = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
        Log.v("Asset.getAssetType()", "Extension extracted: " + ext);
        if (ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("gif")) {
            return "image";
        } else if (ext.equals("mp3") || ext.equals("wav") || ext.equals("ogg")) {
            return "audio";
        }

        return "unknown";
    }

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getPath() {
        return path;
    }

    public String getFullPath() {
        return this.fullPath;
    }

    public String toXml() {
        return String.format(
            "\n  <AssetItem id=\"%s\" name=\"%s\" path=\"%s\" />",
            this.id,
            this.name,
            this.path
        );
    }
}
