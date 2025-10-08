package com.gamebuilder.model;

import java.util.ArrayList;

import com.gamebuilder.GameBuilder;

public class ProjectModel {
  GameBuilder gameBuilder;
  Project project = null;
  ArrayList<ProjectModelUpdateListener> updateListeners = new ArrayList<ProjectModelUpdateListener>();

  public ProjectModel(GameBuilder gameBuilder) {
    this.gameBuilder = gameBuilder;
  }

  public void setProject(Project project) {
    this.project = project;
    project.setProjectModel(this);
    this.notifyChange();
  }

  public Project getProject() {
    return this.project;
  }

  public void addUpdateListener(ProjectModelUpdateListener listener) {
    this.updateListeners.add(listener);
  }

  protected void notifyChange() {
    for (ProjectModelUpdateListener listener: this.updateListeners) {
      listener.onProjectModelUpdate();
    }
  }
}
