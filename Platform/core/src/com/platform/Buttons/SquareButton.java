package com.platform.Buttons;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class SquareButton extends Button{
    public float width, height;

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public Rectangle getRectangle() {
        return new Rectangle(getX(), getY(), width, height);
    }

    public void render(SpriteBatch sb) {
        sb.draw(getButton(), getX(), getY(), width, height);
    }
}
