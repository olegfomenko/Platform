package com.platform.Elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Point {

    private Texture point;
    private float x, y;

    public Point(float x, float y) {
        point = new Texture("point.png");

        this.x = x;
        this.y = y;
    }

    public Point() {
        point = new Texture("point.png");
    }

    public void reposition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getRectangle() {
        return new Rectangle(x - point.getWidth() / 2 - 3, y - point.getHeight() / 2 - 3, point.getWidth() + 6, point.getHeight() + 6);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void render(SpriteBatch sb) {
        sb.draw(point, x - point.getWidth() / 2, y - point.getHeight() / 2);
    }

    public boolean compare(Point p) {
        return p.getX() == x && p.getY() == y;
    }

    public void dispose() {
        point.dispose();
    }
}
