package com.platform.Elements;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import static com.platform.Elements.Element.*;
import static java.lang.Math.min;

public class Connection {

    private boolean press = false, pressed = false;
    public Array<Point> points = new Array<Point>();
    public  Array<Rectangle> rectangles = new Array<Rectangle>();
    private Link link = new Link();

    public void press() {
        press = true;
    }

    public void notPress() {
        press = false;
    }

    public void pressed() {
        pressed = !pressed;
    }

    public boolean isPress() {
        return press;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void createRectangles() {
        Point p1, p2;
        Rectangle r;

        rectangles.clear();

        for(int i = 1; i < points.size; ++i) {
            p1 = points.get(i - 1);
            p2 = points.get(i);

            if(p1.compare(p2)) continue;

            if(p1.getX() != p2.getX()) {
                r = new Rectangle(min(p1.getX(), p2.getX()), p1.getY() - HEIGHT / 2, WIDTH, HEIGHT);
            }
            else {
                r = new Rectangle(p1.getX() - ROTATEWIDTH / 2, min(p1.getY(), p2.getY()), ROTATEWIDTH, ROTATEHEIGHT);
            }

            rectangles.add(r);
        }

        /*for(Point p : points)
            rectangles.add(p.getRectangle());*/

    }

    public void render(SpriteBatch sb) {
        if(press)        for(int i = 1; i < points.size; ++i) link.drawPressLink(points.get(i - 1), points.get(i), sb);

        else if(pressed) for(int i = 1; i < points.size; ++i) link.drawPressedLink(points.get(i - 1), points.get(i), sb);

        else             for(int i = 1; i < points.size; ++i) link.drawLink(points.get(i - 1), points.get(i), sb);

        if(points.size > 0) points.get(0).render(sb);
        if(points.size > 1) points.get(points.size - 1).render(sb);
    }

    public int size() {
        return points.size;
    }

    public void dispose() {
        link.dispose();
        for(Point p : points) p.dispose();
    }
}
