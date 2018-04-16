package com.platform;


import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.platform.Elements.Connection;
import com.platform.Elements.Element;
import com.platform.Elements.Point;
import com.platform.States.LoadState;

import java.io.BufferedReader;

public class Algorithm {

    Array<Pair> pairs;
    Array<Pair> parallelPairs;
    int bateryIndex = -1;
    Pair bat;

    public void algorithm(LoadState ls, Array<Element> elements, Array<Connection> connections) {
        pairs = new Array<Pair>();
        parallelPairs = new Array<Pair>();

        bateryIndex = -1;

        for(int i = 0; i < elements.size; ++i)
            if(elements.get(i).getName().compareTo("Battery") != 0)
                pairs.add(new Pair(elements.get(i).p1, elements.get(i).p2, i, elements.get(i)));
            else
                bateryIndex = i;

        for(int i = 0; i < connections.size; ++i)
            pairs.add(new Pair(connections.get(i).points.get(0), connections.get(i).points.peek(), -1));

        bat = new Pair(elements.get(bateryIndex).p2, elements.get(bateryIndex).p1, bateryIndex);
        create(new Array<Point>(), bat.start, bat.finish);

        //for(Pair p : pairs) System.out.println(p.start.getX() + " " + p.start.getY() + "   " + p.finish.getX() + " " + p.finish.getY());

        for(Pair p : pairs)
            if(p.index == -1)
                for(Connection c : connections) {
                    if(c.points.peek().compare(p.start) && c.points.get(0).compare(p.finish)) c.points.reverse();
                    if(c.points.get(0).compare(p.start) && c.points.peek().compare(p.finish))
                        for(int i = 1; i < c.points.size; ++i) ls.animation.add(new Pair(c.points.get(i - 1), c.points.get(i)));
                }
            else ls.animation.add(p);


        bat.voltage = elements.get(bateryIndex).parametres.peek().getParametr();
        bat.resistance = getResistence(bat.start, bat.start, bat.finish);
        bat.strength = bat.voltage / bat.resistance;

        bat.strength = (int)(bat.strength * 100) / 100.0f;

        Element b = elements.get(bateryIndex);

        b.voltage.setParametr(bat.voltage); b.voltage.set(true);
        b.strength.setParametr(bat.strength); b.strength.set(true);

        //ВЫЗОВ getResistance обязателен до вызова set()

        set(bat.start, bat.finish, elements.get(bateryIndex).parametres.peek().getParametr(), new Array<Pair>());
        for(Pair p : pairs) if(p.index != -1) System.out.println("I = " + p.strength + " U = " + p.voltage + " R = " + p.resistance);

        for(Pair p : pairs) if(p.index != -1) {
            Element e = elements.get(p.index);
            e.voltage.setParametr(p.voltage); e.voltage.set(true);
            e.resistance.setParametr(p.resistance); e.resistance.set(true);
            e.strength.setParametr(p.strength); e.strength.set(true);
        }
    }

    int index;
    StringBuffer buf;
    public boolean algorithm(LoadState ls, Array<Element> elements, Array<Connection> connections, FileHandle checkFile) {
        algorithm(ls, elements, connections);

        Array<String> strings = new Array<String>();

        BufferedReader bufferedReader = new BufferedReader(checkFile.reader());
        try { bufferedReader.readLine();} catch (Exception e) {}

        boolean result = true;

        while(true)
            try {
                strings.add(bufferedReader.readLine());
                buf = new StringBuffer(strings.peek());
                index = 0;

                if(getNextWord().compareTo("o") == 0) {

                    String s = getNextWord();
                    if(s.compareTo("r") == 0 && Float.valueOf(getNextWord()) != bat.resistance) {
                        result = false;
                        break;
                    }

                    if(s.compareTo("i") == 0 && Float.valueOf(getNextWord()) != bat.strength) {
                        result = false;
                        break;
                    }

                    if(s.compareTo("u") == 0 && Float.valueOf(getNextWord()) != bat.voltage) {
                        result = false;
                        break;
                    }
                }
            }
            catch (Exception e) {
                break;
            }

        checkFile.writeString(result + "\n", false);
        for(String s : strings) checkFile.writeString(s + "\n", true);

        return result;
    }

    private String getNextWord() {
        String ans = "";
        while(index != buf.length() && buf.charAt(index) == ' ') ++index;

        while (index != buf.length() && buf.charAt(index) != ' ') {
            ans += buf.charAt(index);
            ++index;
        }

        return ans;
    }

    private Point flag;
    private boolean parallel;
    private float getResistence(Point start, Point last, Point finish) {
        float resistance = 0;
        Array<Pair> neighbors = new Array<Pair>();

        while(true) {

            if(start.compare(finish)) {
                flag = finish;
                return resistance;
            }

            if(parallel)
            for(Pair p : pairs) if(!p.start.compare(last) && p.finish.compare(start)) {
                flag = start;
                return resistance;
            }

            neighbors.clear();

            for(int i = 0; i < pairs.size; ++i) {
                Pair p = pairs.get(i);
                if(p.start.compare(start))
                    neighbors.add(p);
            }

            if(neighbors.size == 1) {
                resistance += neighbors.peek().resistance;
                last = start;
                start = neighbors.peek().finish;
            } else {

                float r = 0;
                parallel = true;

                for(int i = 0; i < neighbors.size; ++i) {
                    Pair p = neighbors.get(i);
                    float rr = p.resistance + getResistence(p.finish, start, finish);
                    if(rr != 0) r += 1 / rr;
                }

                Pair prl = new Pair(start, flag, -2);
                prl.resistance = 1 / r;
                parallelPairs.add(prl);

                resistance += 1 / r;

                start = flag;
                parallel = false;
            }
        }
    }

    private boolean create(Array<Point> reletives, Point current, Point finish) {
        if(current.compare(finish)) return true;
        for(Point p : reletives) if(p.compare(current)) return false;

        boolean check = false;
        reletives.add(current);

        for(int i = 0; i < pairs.size; ++i) {
            Pair pair = pairs.get(i);
            if(pair.start.compare(current)) {
                if(pair.used) {
                    check = true;
                    continue;
                }
                pair.used = true;
                if(!create(reletives, pair.finish, finish)) pair.used = false; else check = true;
            }

            if(pair.finish.compare(current)) {
                if(pair.used) continue;
                pair.swap();
                pair.used = true;
                if(!create(reletives, pair.finish, finish)) pair.used = false; else check = true;
            }
        }
        reletives.pop();

        return check;
    }

    private void set(Point start, Point finish, float voltage, Array<Pair> resistances) {

        while(!start.compare(finish))
        {
            boolean flag = false;
            //System.out.println("STOP POINT " + finish.getX() + finish.getY());
            //System.out.println("START POINT " + start.getX() + start.getY());
            for(Pair p : parallelPairs) if(p.start.compare(start)) {
                start = p.finish;
                resistances.add(p);
                flag = true;
                break;
            }

            if(flag) continue;

            for(Pair p : pairs) if(start.compare(p.start)) {
                start = p.finish;
                if(p.index != -1) resistances.add(p);
                break;
            }
        }

        for(int i = 0; i < resistances.size; ++i) {
            float sum = 1;
            Pair pair = resistances.get(i);
            for(int j = 0; j < resistances.size; ++j) {
                if(j == i) continue;
                    sum += resistances.get(j).resistance / pair.resistance;
            }

            pair.voltage = (int)(voltage / sum * 1000) / 1000.0f;
            pair.strength = (int)(resistances.get(i).voltage / resistances.get(i).resistance * 1000) / 1000.0f;

            if(pair.index == -2)//-1 connection тоже подходит
                for (int j = 0; j < pairs.size; ++j) {
                    Pair p = pairs.get(j);

                    if(p.start.compare(pair.start)) {
                        Array<Pair> arr = new Array<Pair>();
                        if(p.index != -1) arr.add(p);
                        set(p.finish, pair.finish, pair.voltage, arr);
                    }
                }
        }
    }

}
