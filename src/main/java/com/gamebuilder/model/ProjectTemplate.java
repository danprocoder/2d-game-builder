package com.gamebuilder.model;

public class ProjectTemplate {
    public static String[] getDefaultDirectories() {
        return new String[] {
            "main/java",
            "main/resources/assets/images",
            "main/resources/assets/sounds",
        };
    }

    public static String[] getDefaultFiles() {
        return new String[] {
            "main/java/App.java",
            "main/resources/objects.xml",
        };
    }

    public static String getProjectFile() {
        return "project.xml";
    }

    public static String getAssetDirectory() {
        return "main/resources/assets";
    }

    public static String getImageDirectory() {
        return "main/resources/assets/images";
    }

    public static String getSoundDirectory() {
        return "main/resources/assets/sounds";
    }

    public static String getSceneFile() {
        return "main/resources/scenes.xml";
    }

    public static String getAssetFile() {
        return "main/resources/assets.xml";
    }

    public static String getObjectFile() {
        return "main/resources/objects.xml";
    }
}
