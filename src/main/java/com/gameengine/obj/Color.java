package com.gameengine.obj;

public class Color {
  private int r;
  private int g;
  private int b;
  private int a;

  public Color(int r, int g, int b, int a) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }

  public float getRed() {
    return this.r;
  }

  public float getGreen() {
    return this.g;
  }

  public float getBlue() {
    return this.b;
  }

  public float getAlpha() {
    return this.a;
  }
}
