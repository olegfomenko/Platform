package com.platform;

import com.platform.Elements.Element;
import com.platform.Elements.Lamp;
import com.platform.Elements.Point;
import com.platform.Elements.Resistor;

public class Pair {
    public Point start, finish;
    public boolean used;
    public int index;
    public float resistance;
    public float voltage;
    public float strength;

    public Pair(Point start, Point finish, int index, Element e) {
        this.start = start;
        this.finish = finish;
        this.index = index;

        if(e instanceof Lamp) {
            resistance = e.parametres.peek().getParametr() * e.parametres.peek().getParametr() / e.parametres.get(0).getParametr();
        }

        if(e instanceof Resistor) {
            resistance = e.parametres.peek().getParametr();
        }

        resistance = (int)(resistance * 100) / 100.0f;

        used = false;
    }

    public Pair(Point first, Point second) {
        this.start = first;
        this.finish = second;
        resistance = 0;
        index = -1;
        used = false;
    }

    public Pair(Point first, Point second, int index) {
        this.start = first;
        this.finish = second;
        resistance = 0;
        this.index = index;
        used = false;
    }

    public void swap() {
        Point point = start;
        start = finish;
        finish = point;
    }

    public boolean compare(Pair p) {
        return (p.start == start && p.finish == finish) || (p.finish == start && p.start == finish);
    }
}
