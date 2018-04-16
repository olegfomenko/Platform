package com.platform;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

import  static java.lang.Math.*;

public class Camera extends OrthographicCamera{

    Texture bg;

    public Camera(float viewportWidth, float viewportHeight, Texture bg) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;

        this.bg = bg;

        position.x = bg.getWidth() / 2;
        position.y = bg.getHeight() / 2;

        update();
    }

    public void reposition(float x, float y) {
        position.x += x;
        position.y += y;

        position.x = max(position.x, viewportWidth / 2);
        position.x = min(position.x, bg.getWidth() - viewportWidth / 2);

        position.y = max(position.y, viewportHeight / 2);
        position.y = min(position.y, bg.getHeight() - viewportHeight / 2);

        update();
    }
}
