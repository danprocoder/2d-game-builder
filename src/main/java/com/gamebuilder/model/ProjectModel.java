package com.gamebuilder.model;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.gamebuilder.GameBuilder;
import com.gamebuilder.scene.Scene;

public class ProjectModel {
  GameBuilder gameBuilder;
  Project project = null;
  ArrayList<ProjectModelUpdateListener> updateListeners = new ArrayList<ProjectModelUpdateListener>();

  ArrayList<Scene> scenes = new ArrayList<>();
  int currentSceneIndex = -1;

  public ProjectModel(GameBuilder gameBuilder) {
    this.gameBuilder = gameBuilder;
  }

  public ArrayList<Scene> getScenes() {
    return this.scenes;
  }

  public int addScene(Scene scene) {
    this.scenes.add(scene);
    return this.scenes.size() - 1;
  }

  public void setCurrentSceneIndex(int index) {
    this.currentSceneIndex = index;
  }

  public Scene getCurrentScene() {
    if (this.currentSceneIndex < 0 || this.currentSceneIndex >= this.scenes.size()) {
      return null;
    }
    return this.scenes.get(this.currentSceneIndex);
  }

  /**
   * Load a project from file
   * 
   * @param path The project folder path
   */
  public Project loadProject(String path)
      throws IOException, ParserConfigurationException, SAXException {
    FileInputStream fis = new FileInputStream(
      Paths.get(path, ProjectTemplate.getProjectFile()).toFile()
    );
    ByteArrayInputStream in = new ByteArrayInputStream(fis.readAllBytes());

    DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    Document doc = docBuilder.parse(in);
    String projectName = doc.getElementsByTagName("name").item(0).getTextContent();
    String version = doc.getElementsByTagName("version").item(0).getTextContent();

    fis.close();
    in.close();

    if (version == null || !version.equals("1.0")) {
      throw new IOException("Unsupported project version: " + version);
    }

    Project project = new Project(projectName, path);
    this.setProject(project);

    return project;
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
