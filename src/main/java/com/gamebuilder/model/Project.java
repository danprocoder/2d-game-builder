package com.gamebuilder.model;

class ProjectLanguage {
    public static final String JAVA = "Java";
}

public class Project {
    private String name = null;
    private String directory = null;
    private boolean dirty = false;
    private ProjectModel projectModel;
    private String language = ProjectLanguage.JAVA;
    private int resolutionWidth = 800;
    private int resolutionHeight = 600;

    public Project(String name) {
        this.name = name;
        this.dirty = true;
    }

    public Project(String name, String directory) {
        this.name = name;
        this.directory = directory;
        this.dirty = false;
    }

    protected void setProjectModel(ProjectModel projectModel) {
        this.projectModel = projectModel;
    }

    public String getName() {
        return this.name;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        // TODO: move this from here. Don't call this in the project file
        this.projectModel.notifyChange();
    }

    public void setDirectory(String directory) {
        this.directory = directory;
        // TODO: move this from here. Don't call this in the project file
        this.projectModel.notifyChange();
    }

    public String getDirectory() {
        return this.directory;
    }

    public boolean isDirty() {
        return this.dirty;
    }
}
