package com.gamebuilder.model;

public class Project {
    private String name = null;
    private String directory = null;
    private boolean dirty = false;
    private ProjectModel projectModel;

    public Project(String name) {
        this.name = name;
        this.dirty = true;
    }

    protected void setProjectModel(ProjectModel projectModel) {
        this.projectModel = projectModel;
    }

    public String getName() {
        return this.name;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        this.projectModel.notifyChange();
    }

    public void setDirectory(String directory) {
        this.directory = directory;
        this.projectModel.notifyChange();
    }

    public String getDirectory() {
        return this.directory;
    }

    public boolean isDirty() {
        return this.dirty;
    }
}
