package com.platform;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.platform.Elements.Batery;
import com.platform.Elements.Element;

import static java.lang.Math.max;
import static java.lang.Math.signum;

public class Table {

    private String text;
    private float x, y, size;
    public int index;
    public Element element;
    private float width, height;
    private BitmapFont bf;

    public Table(float x, float y, Element element, BitmapFont bf) {
        setPosition(x, y);
        this.element = element;
        this.bf = bf;
        setText();
    }

    public String getText() {
        return text;
    }

    public void reposition(float dx, float dy) {
        x -= dx;
        y -= dy;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getRectangle() {
        return new Rectangle(x - width / 2 - 5, y - height / 2 - 5, width + 10, height + 10);
    }

    public void render(BitmapFont bf, SpriteBatch sb) {
        bf.draw(sb, text, x - width / 2, y + height / 2);
    }

    public void setText() {
        if(element instanceof Batery) {
            String u = "U = " + element.voltage.getParametr(), i = "I = " + element.strength.getParametr();
            text = u + "\n" + i;
            width = max(u.length(), i.length()) * bf.getSpaceWidth();
            height = bf.getLineHeight() * 2;
        } else {
            String u = "U = " + element.voltage.getParametr(), i = "I = " + element.strength.getParametr(), r = "R = " + element.resistance.getParametr();
            text = u + "\n" + i + "\n" + r;
            width = max(u.length(), max(i.length(), r.length())) * bf.getSpaceWidth();
            height = bf.getLineHeight() * 3;
        }

        System.out.println("WIDTH = " + width);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean compare(Table t) {
        return this.element.equals(t.element);
    }

}
