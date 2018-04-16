package com.platform.Buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Button
{
    public Texture button, button_pressed, button_press;
    private boolean press, pressed;
    private float x, y;

    public int size;

    public Button() {
        pressed = false;
        press = false;
    }

    public void seteSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    ////////////////////////////////////////////////

    public Texture getButton() {
            if(press) return button_press;
            if(pressed) return button_pressed; else return button;
    }

    public Rectangle getRectangle() {
        return new Rectangle(x, y, size, size);
    }

    public void reposition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    ////////////////////////////////////////////////

    public void pressed() {
        pressed = !pressed;
    }

    public void notPressed() {
        pressed = false;
    }

    public void press() {
        press = true;
    }

    public void notPress() {
        press = false;
    }

    ////////////////////////////////////////////////

    public boolean isPresed() {
        return pressed;
    }

    public boolean isPress() {
        return press;
    }


    ////////////////////////////////////////////////

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    ////////////////////////////////////////////////

    public void render(SpriteBatch sb) {
        /*Sprite s = new Sprite(getButton());
        s.setPosition(x, y);
        s.setOrigin(0, 0);
        s.setRotation(10);
        s.setSize(size, size);
        s.draw(sb);*/
        sb.draw(getButton(), x, y, size, size);
    }

    ////////////////////////////////////////////////

    public void dispose() {
        button.dispose();
        button_pressed.dispose();
        button_press.dispose();
    }
}
