package com.gamebuilder.model.asset;

import java.nio.file.Paths;

import com.gamebuilder.util.Log;

public class Asset extends AbstractAsset {
    String path;
    String fullPath;
    String type;

    public Asset(String path, String fullPath) {
        super();

        this.path = path;
        this.fullPath = fullPath;
        this.name = Paths.get(path).getFileName().toString();
        this.type = this.getAssetType(path);
    }

    public Asset(String id, String path, String fullPath, String name) {
        super(id);

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

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public String getPath() {
        return path;
    }

    public String getFullPath() {
        return this.fullPath;
    }

    @Override()
    public String toXml() {
        return String.format(
            "\n  <AssetItem id=\"%s\" name=\"%s\" path=\"%s\" />",
            this.id,
            this.name,
            this.path
        );
    }
}
