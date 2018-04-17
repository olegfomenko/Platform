package com.platform.Elements;

import com.badlogic.gdx.graphics.Texture;
import com.platform.Parametr.Power;
import com.platform.Parametr.Resistance;
import com.platform.Parametr.Strength;
import com.platform.Parametr.Voltage;
import com.sun.org.apache.regexp.internal.RE;

public class Resistor extends Element {

    public Resistor(float x, float y) {
        element = new Texture("resistor.png");
        element_press = new Texture("resistor_press.png");
        element_pressed = new Texture("resistor_pressed.png");

        elementRotate = new Texture("resistor9.png");
        elementRotate_press = new Texture("resistor9_press.png");
        elementRotate_pressed = new Texture("resistor9_pressed.png");

        this.x = x;
        this.y = y;

        p1.reposition(this.x, this.y);
        if(rotate) p2.reposition(this.x, this.y + ROTATION); else p2.reposition(this.x + ROTATION, this.y);

        uri = "https://ru.wikipedia.org/wiki/Резистор";

        parametres.add(new Resistance());

        text = new Texture("resistor_text.png");

        name = "Resistor";
    }

    public Element getNewElement() {
        return new Resistor(0, 0);
    }

    public boolean compare(Element e) {
        return e instanceof Resistor;
    }
}
