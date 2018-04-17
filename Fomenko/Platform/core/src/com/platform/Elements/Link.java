package com.platform.Elements;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static java.lang.Math.*;
import static com.platform.Elements.Element.*;

public class Link {
    public  Texture horisontal = new Texture("link.png"), vertical = new Texture("link9.png");
    public  Texture horisontal_press = new Texture("link_press.png"), vertical_press = new Texture("link9_press.png");
    public  Texture horisontal_pressed = new Texture("link_pressed.png"), vertical_pressed = new Texture("link9_pressed.png");

    public  void drawLink(Point p1, Point p2, SpriteBatch sb) {
        if(p1.compare(p2)) return;
        if(p1.getX() != p2.getX()) drawHorisontal(sb, horisontal, p1, p2); else drawVertical(sb, vertical, p1, p2);
    }

    public  void drawPressLink(Point p1, Point p2, SpriteBatch sb) {
        if(p1.compare(p2)) return;
        if(p1.getX() != p2.getX()) drawHorisontal(sb, horisontal_press, p1, p2); else drawVertical(sb, vertical_press, p1, p2);
    }

    public  void drawPressedLink(Point p1, Point p2, SpriteBatch sb) {
        if(p1.compare(p2)) return;
        if(p1.getX() != p2.getX()) drawHorisontal(sb, horisontal_pressed, p1, p2); else drawVertical(sb, vertical_pressed, p1, p2);
    }

    private void drawHorisontal(SpriteBatch sb, Texture t, Point p1, Point p2) {
        sb.draw(t, min(p1.getX(), p2.getX()), p1.getY() - HEIGHT / 2, WIDTH, HEIGHT);
    }

    private void drawVertical(SpriteBatch sb, Texture t, Point p1, Point p2) {
        sb.draw(t, p1.getX() - ROTATEWIDTH / 2, min(p1.getY(), p2.getY()), ROTATEWIDTH, ROTATEHEIGHT);
    }

    public void dispose() {
        horisontal.dispose();
        vertical.dispose();

        horisontal_press.dispose();
        vertical_press.dispose();

        horisontal_pressed.dispose();
        vertical_pressed.dispose();
    }
}
