package com.platform.Parametr;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.platform.Buttons.Button;
import com.platform.Buttons.SetButton;

public class Parametr {

    private float parametr;
    private boolean seted;
    private float x, y;
    private String name;

    public Parametr() {
        parametr = 0;
        seted = false;
        name = "";
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setParametr(float parametr) {
        this.parametr = parametr;
    }

    public float getParametr() {
        return parametr;
    }

    public void set(boolean seted) {
        this.seted = seted;
    }

    public boolean isSeted() {
        return seted;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
