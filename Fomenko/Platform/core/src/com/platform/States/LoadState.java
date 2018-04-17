package com.platform.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.platform.Algorithm;
import com.platform.Audio;
import com.platform.Buttons.BackButton;
import com.platform.Buttons.MoveButton;
import com.platform.Camera;
import com.platform.Elements.Batery;
import com.platform.Elements.Connection;
import com.platform.Elements.Element;
import com.platform.Elements.Lamp;
import com.platform.Elements.Point;
import com.platform.Elements.Resistor;
import com.platform.GameStateManager;
import com.platform.Messages.DoneMessage;
import com.platform.Messages.NotDoneMessage;
import com.platform.Pair;
import com.platform.Table;

import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;


public class LoadState extends State {

    private BackButton backButton;
    private MoveButton moveButton;

    private boolean pressed, buttonPress, elementPressed, connectionPress, tablePress;

    private Rectangle input, check;
    private Vector3 vector3;

    private Array<Element> elements;
    private Array<Connection> connections;

    private float last_x, last_y;
    private float dx, dy;

    private Audio audio;

    private Algorithm algo = new Algorithm();

    private BitmapFont bf;

    public Array<Pair> animation;

    private Texture electron;
    private Array<Rectangle> an;
    private double speed = 200;

    private DoneMessage doneMessage;
    private NotDoneMessage notDoneMessage;

    private Array<Table> tables;
    private Texture tab;
    private Texture line;
    private Sprite s;

    public LoadState(GameStateManager gsm) {
        super(gsm);

        bg = new Texture("white.png");
        camera = new Camera(520, 936, bg);

        backButton = new BackButton();
        moveButton = new MoveButton();

        backButton.seteSize(83);
        moveButton.seteSize(83);

        input = new Rectangle(0, 0, 1, 1);
        check = new Rectangle(0, 0, 1, 1);
        vector3 = new Vector3(0, 0, 0);

        pressed = false;
        elementPressed = false;
        buttonPress = false;
        tablePress = false;

        elements = new Array<Element>();
        connections = new Array<Connection>();

        connectionPress = false;

        audio = new Audio();

        animation = new Array<Pair>();
        electron = new Texture("electron.png");
        an = new Array<Rectangle>();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("courier.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        bf = generator.generateFont(parameter);
        bf.setColor(Color.BLACK);
        generator.dispose();

        doneMessage = new DoneMessage();
        notDoneMessage = new NotDoneMessage();

        tables = new Array<Table>();
        tab = new Texture("table.png");
        line = new Texture("line.png");
        s = new Sprite(line);

        updateButtons();
    }

    public void setElements(float x, float y, Array<Element> elements, Array<Connection> connections) {
        camera.position.x = x;
        camera.position.y = y;
        camera.update();
        updateButtons();
        this.elements = elements;
        this.connections = connections;

        an.clear();
        animation.clear();
        tables.clear();

        algo.algorithm(this, elements, connections);

        for(Pair p : animation) {
            Point p1 = p.start;
            an.add(new Rectangle(p1.getX() - 5, p1.getY() - 5, 10, 10));
        }
    }

    public void setElements(float x, float y, Array<Element> elements, Array<Connection> connections, FileHandle checkfile) {
        camera.position.x = x;
        camera.position.y = y;
        camera.update();
        updateButtons();
        this.elements = elements;
        this.connections = connections;

        an.clear();
        animation.clear();
        tables.clear();

        if(algo.algorithm(this, elements, connections, checkfile)) {
            if(messages.size() < 10) {
                doneMessage.reset();
                messages.push(doneMessage);
            }
        } else {
            if(messages.size() < 10) {
                notDoneMessage.reset();
                messages.push(notDoneMessage);
            }
        }

        for(Pair p : animation) {
            Point p1 = p.start;
            an.add(new Rectangle(p1.getX() - 5, p1.getY() - 5, 10, 10));
        }
    }

    private void updateButtons() {
        backButton.reposition(camera.position.x - camera.viewportWidth / 2 + 10,
                camera.position.y - camera.viewportHeight / 2 + 10);

        moveButton.reposition(backButton.getX(), backButton.getY() + moveButton.getSize() + 10);

        doneMessage.setPosition(camera.position.x - doneMessage.width / 2, camera.position.y - camera.viewportHeight / 2 + 100);
        notDoneMessage.setPosition(doneMessage.x, doneMessage.y);
    }

    private void checkButtons() {

        if(!tablePress && input.overlaps(moveButton.getRectangle())) {
            moveButton.press();
            buttonPress = true;
            return;
        } else moveButton.notPress();

        if(moveButton.isPresed()) return;

        if(!pressed || tablePress)
        for(int i = 0; i < tables.size; ++i) {
            Table t = tables.get(i);
            if(input.overlaps(t.getRectangle())) {
                tablePress = true;
                tables.removeIndex(i);
                tables.add(t);
                return;
            }
        }

        tablePress = false;

        if(input.overlaps(backButton.getRectangle())) {
            backButton.press();
            buttonPress = true;
            return;
        } else backButton.notPress();

        buttonPress = false;

        for(Element e : elements)
            if(input.overlaps(e.getRectangle())) {
                e.press();
                elementPressed = true;
                return;
            } else e.notPress();

        elementPressed = false;

        for(Connection c : connections)
            for(Rectangle r : c.rectangles)
                if(input.overlaps(r)) {
                    c.press();
                    connectionPress = true;
                    return;
                }

        connectionPress = false;
    }

    private void checkPressed() {

        if(buttonPress) {

            audio.playTick();

            if (moveButton.isPress()) {
                moveButton.notPress();
                moveButton.pressed();
            }

            if (backButton.isPress()) {
                backButton.notPress();
                gsm.pop();
            }
        }

        if(elementPressed) {
            audio.playTick();

            for(Element e : elements) if (e.isPress()) {
                e.notPress();

                boolean flag = true;
                Table t = new Table(camera.position.x, camera.position.y, e, bf);
                for (int j = 0; j < tables.size; ++j) if (tables.get(j).compare(t)) {
                    flag = false;
                    tables.removeIndex(j);
                    break;
                }

                if(flag) tables.add(t);
                break;
            }
        }


        if(connectionPress) {
            audio.playTick();
            for(Connection c : connections) if(c.isPress()) {
                c.notPress();
            }
        }


        connectionPress = false;
        elementPressed = false;
        buttonPress = false;
    }

    private void input() {
        if(Gdx.input.isTouched()) {
            check.setPosition(Gdx.input.getX(), Gdx.input.getY());
            vector3.set(check.x, check.y, 0);
            camera.unproject(vector3);
            input.setPosition(vector3.x, vector3.y);

            if(!tablePress) checkButtons();

            if(tablePress && pressed) {
                tables.peek().reposition(last_x - check.x, check.y - last_y);
            }

            if(moveButton.isPresed() && !buttonPress && pressed) {
                camera.reposition(last_x - check.x, check.y - last_y);
                updateButtons();
            }

            pressed = true;
            last_x = check.x;
            last_y = check.y;
        } else {
            check.setPosition(0, 0);
            input.setPosition(0, 0);
            pressed = false;
            tablePress = false;
            checkPressed();
        }

    }

    public void update(float dt) {
        input();
        for(int i = 0; i < animation.size; ++i) {
            Point p1 = animation.get(i).start, p2 = animation.get(i).finish;
            Rectangle r = an.get(i);

            if(p1.getX() > p2.getX()) {
                r.x -= speed * dt;
                if(r.x < p2.getX()) r.x = p1.getX();
            }

            if(p1.getX() < p2.getX()) {
                r.x += speed * dt;
                if(r.x > p2.getX()) r.x = p1.getX();
            }

            if(p1.getY() > p2.getY()) {
                r.y -= speed * dt;
                if(r.y < p2.getY()) r.y = p1.getY();
            }

            if(p1.getY() < p2.getY()) {
                r.y += speed * dt;
                if(r.y > p2.getY()) r.y = p1.getY();
            }
        }

        if(!messages.isEmpty()) {
            messages.peek().update(dt);
            if(!messages.peek().isExist()) messages.pop();
        }
    }

    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);

        sb.begin();

        sb.draw(bg, 0, 0);

        for(Element e : elements) e.render(sb);
        for(Connection c : connections) c.render(sb);

        for(int i = 0; i < animation.size; ++i) {
            Rectangle r = an.get(i);
            sb.draw(electron, r.x, r.y, r.width, r.height);
        }

        for(Table t : tables) {
            float x1, y1, x2, y2, rotaion;

            Rectangle r = t.element.getRectangle();
            float elx = r.getX() + r.getWidth() / 2;
            float ely = r.getY() + r.getHeight() / 2;

            r = t.getRectangle();
            if(r.getX() < elx) {
                x1 = r.getX();
                y1 = r.getY();

                x2 = elx;
                y2 = ely;
            } else {
                x1 = elx;
                y1 = ely;

                x2 = r.getX();
                y2 = r.getY();
            }

            rotaion = (float)toDegrees(atan(abs(y1 - y2) / abs(x1 - x2)));
            if(y2 < y1) rotaion = -rotaion;

            float len = (float)sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

            s.setPosition(x1, y1);
            s.setOrigin(0, 0);
            s.setRotation(rotaion);
            s.setSize(len, line.getHeight());
            s.draw(sb);

            sb.draw(tab, r.getX(), r.getY(), r.getWidth(), r.getHeight());
            t.render(bf, sb);
        }

        if(!messages.isEmpty()) messages.peek().render(sb);

        backButton.render(sb);
        moveButton.render(sb);

        sb.end();

    }

    public void dispose() {
        backButton.dispose();
        moveButton.dispose();
        bg.dispose();
        bf.dispose();

        audio.dispose();

        electron.dispose();

        tab.dispose();
        line.dispose();

        doneMessage.dispose();
        notDoneMessage.dispose();
    }
}
