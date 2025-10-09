package com.gamebuilder.model;

import java.io.File;

public class Asset {
    String path;

    public Asset(String path) {
        this.path = path;
    }

    public File getPath() {
        return new File(path);
    }
}
