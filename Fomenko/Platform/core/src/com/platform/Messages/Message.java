package com.platform.Messages;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Message {
    public Texture message;
    public float x, y, width, height;

    public static final float ALIVE = 2;
    public float time;

    public Message(float x, float y) {
        width = 100;
        height = 40;

        this.x = x;
        this.y = y;
    }

    public Message() {
        width = 200;
        height = 80;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void reset() {
        time = 0;
    }

    public boolean isExist() {
        return time <= ALIVE;
    }

    public void update(float dt) {
        time += dt;
    }

    public void render(SpriteBatch sb) {
        sb.draw(message, x, y, width, height);
    }

    public void dispose() {
        message.dispose();
    }
}
