package com.platform.Elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.platform.Parametr.Parametr;
import com.platform.Parametr.Resistance;
import com.platform.Parametr.Strength;
import com.platform.Parametr.Voltage;

public class Element {

    public Texture element, element_pressed, element_press, elementRotate, elementRotate_pressed, elementRotate_press;
    public boolean press, pressed, rotate;
    public float x, y;

    public Point p1, p2;

    public String uri = "";

    public static final float WIDTH = 128, HEIGHT = 32, ROTATEWIDTH = 32, ROTATEHEIGHT = 128, ROTATION = 128;

    public Array<Parametr> parametres; // параметры
    public boolean parametrs_seted;
    public boolean block;
    public static final float ESIZE = 30;

    public Texture text; //246x105
    public static final float TWIDTH = 246, THEIGHT = 105;

    public String name, write;

    public int count = 0;

    public Resistance resistance;
    public Strength strength;
    public Voltage voltage;

    public Element() {
        pressed = false;
        press = false;
        rotate = false;

        p1 = new Point();
        p2 = new Point();

        parametres = new Array<Parametr>();
        parametrs_seted = false;
        write = "";

        block = false;

        resistance = new Resistance();
        resistance.set(false);

        strength = new Strength();
        strength.set(false);

        voltage = new Voltage();
        voltage.set(false);
    }

    public Element getNewElement() {
        return new Element();
    }

    public void setBlocking(boolean block) {
        this.block = block;
    }

    public boolean isBlock() {
        return block;
    }

    ////////////////////////////////////////////////

    public String getName() {
        return name;
    }

    public Texture getElement() {
        if(rotate) {

            if(press) return elementRotate_press;
            if(pressed) return elementRotate_pressed; else return elementRotate;

        } else {

            if(press) return element_press;
            if(pressed) return element_pressed; else return element;
        }
    }

    public Rectangle getRectangle() {
        if(rotate)
            return new Rectangle(x - ROTATEWIDTH / 2, y, ROTATEWIDTH, ROTATEHEIGHT);
        else
            return new Rectangle(x, y - HEIGHT / 2, WIDTH, HEIGHT);
    }

    public void reposition(float x, float y) {
        if(rotate) {
            x = Math.round(x / ROTATION) * ROTATION;
            y = Math.round(y / ROTATION) * ROTATION;
        } else {
            x = Math.round(x / ROTATION) * ROTATION;
            y = Math.round(y / ROTATION) * ROTATION;
        }

        this.x = x;
        this.y = y;


        p1.reposition(this.x, this.y);
        if(rotate) p2.reposition(this.x, this.y + ROTATION); else p2.reposition(this.x + ROTATION, this.y);
    }

    public void scrolling(float x, float y) {
        this.x -= x;
        this.y -= y;

        p1.reposition(this.x, this.y);
        if(rotate) p2.reposition(this.x, this.y + ROTATION); else p2.reposition(this.x + ROTATION, this.y);
    }

    ////////////////////////////////////////////////

    public void pressed() {
        pressed = !pressed;
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

    public void rotation() {
        rotate = !rotate;
        reposition(getX(), getY());
    }

    public void setRotate(boolean rotate) {
        this.rotate = rotate;
        reposition(this.x, this.y);
    }

    public boolean isRotate() {
        return rotate;
    }

    ////////////////////////////////////////////////

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    ////////////////////////////////////////////////

    public void update(float dt) {
        for (Parametr p : parametres)
            if(!p.isSeted()) {
                parametrs_seted = false;
                return;
            }

        parametrs_seted = true;
    }

    public void render(SpriteBatch sb) {
        Rectangle r = getRectangle();
        sb.draw(getElement(), r.x, r.y, r.width, r.height);
        p1.render(sb);
        p2.render(sb);
    }

    ////////////////////////////////////////////////

    public void openWiki() {
        Gdx.net.openURI(uri);
    }

    ////////////////////////////////////////////////

    public boolean isParametrs_seted() {
        return parametrs_seted;
    }

    public Texture getText() {
        return text;
    }

    public boolean compare(Element e) {
        return true;
    }

    public void dispose() {
        element.dispose();
        element_press.dispose();
        element_pressed.dispose();

        elementRotate.dispose();
        elementRotate_press.dispose();
        elementRotate_pressed.dispose();
    }
}
