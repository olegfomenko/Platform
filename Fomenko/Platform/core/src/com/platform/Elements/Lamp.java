package com.platform.Elements;

import com.badlogic.gdx.graphics.Texture;
import com.platform.Parametr.Power;
import com.platform.Parametr.Voltage;

public class Lamp extends Element {

    public Lamp(float x, float y) {
        element = new Texture("lamp.png");
        element_press = new Texture("lamp_press.png");
        element_pressed = new Texture("lamp_pressed.png");

        elementRotate = new Texture("lamp9.png");
        elementRotate_press = new Texture("lamp9_press.png");
        elementRotate_pressed = new Texture("lamp9_pressed.png");

        this.x = x;
        this.y = y;

        p1.reposition(this.x, this.y);
        if(rotate) p2.reposition(this.x, this.y + ROTATION); else p2.reposition(this.x + ROTATION, this.y);

        uri = "https://ru.wikipedia.org/wiki/Лампа_накаливания";

        parametres.add(new Power());
        parametres.add(new Voltage());

        text = new Texture("lamp_text.png");

        name = "Lamp";
    }

    public Element getNewElement() {
        return new Lamp(0, 0);
    }

    public boolean compare(Element e) {
        return e instanceof Lamp;
    }
}
