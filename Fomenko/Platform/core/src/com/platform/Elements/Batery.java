package com.platform.Elements;

import com.badlogic.gdx.graphics.Texture;
import com.platform.Parametr.Strength;
import com.platform.Parametr.Voltage;

public class Batery extends Element{

    public Batery(float x, float y) {
        element = new Texture("batery.png");
        element_press = new Texture("batery_press.png");
        element_pressed = new Texture("batery_pressed.png");

        elementRotate = new Texture("batery9.png");
        elementRotate_press = new Texture("batery9_press.png");
        elementRotate_pressed = new Texture("batery9_pressed.png");

        this.x = x;
        this.y = y;

        p1.reposition(this.x, this.y);
        if(rotate) p2.reposition(this.x, this.y + ROTATION); else p2.reposition(this.x + ROTATION, this.y);

        uri = "https://ru.wikipedia.org/wiki/Источник_ЭДС";

        parametres.add(new Voltage());

        text = new Texture("batery_text.png");

        name = "Battery";
    }

    public Element getNewElement() {
        return new Batery(0, 0);
    }

    public boolean compare(Element e) {
        return e instanceof Batery;
    }

}
