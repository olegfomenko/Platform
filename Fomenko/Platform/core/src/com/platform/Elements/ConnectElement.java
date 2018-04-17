package com.platform.Elements;


import com.badlogic.gdx.graphics.Texture;

public class ConnectElement extends Element{

    public ConnectElement(float x, float y) {
        element = new Texture("link.png");
        element_press = new Texture("link_press.png");
        element_pressed = new Texture("link.png");

        elementRotate = new Texture("link9.png");
        elementRotate_press = new Texture("link9.png");
        elementRotate_pressed = new Texture("link9.png");

        this.x = x;
        this.y = y;

        p1.reposition(this.x, this.y);
        if(rotate) p2.reposition(this.x, this.y + ROTATION); else p2.reposition(this.x + ROTATION, this.y);

        uri = "https://ru.wikipedia.org/wiki/Провод";

        parametrs_seted = true; // нет параметров

        text = new Texture("link_text.png");
    }

    public boolean compare(Element e) {
        return e instanceof ConnectElement;
    }
}
