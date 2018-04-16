package com.platform.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.platform.Audio;
import com.platform.Buttons.*;
import com.platform.Camera;
import com.platform.Elements.Batery;
import com.platform.Elements.Connection;
import com.platform.Elements.Element;
import com.platform.Elements.Lamp;
import com.platform.Elements.Point;
import com.platform.Elements.Resistor;
import com.platform.GameStateManager;
import com.platform.Messages.Message;
import com.platform.Messages.RequiredElementMessage;
import com.platform.Messages.SchemeErrorMessage;
import com.platform.Parametr.Parametr;

import java.io.BufferedReader;

import static com.platform.Elements.Element.*;

public class Menu extends State{

    private AddButton addButton;
    private OkButton okButton;
    private RotateButton rotateButton;
    private DeleteButton deleteButton;
    private EditButton editButton;
    private BackButton backButton;
    private PlayButton playButton;

    private float last_x, last_y;

    private boolean pressed, buttonPress, elementPressed, elementPress, connectionPress, connectionPressed, createConection, reposition;

    private Rectangle input, check;
    private Vector3 vector3;

    private AddState addState;
    private LoadState loadState;

    private Array<Element> elements;
    private Element element;
    private Texture error;

    private Array<Connection> connections;
    private boolean created;

    private Audio audio;

    private FileHandle project;

    private int index;
    private StringBuffer buf;

    private boolean seted;
    private boolean learningmode;
    private FileHandle checkFile;

    private SchemeErrorMessage schemeErrorMessage;
    private RequiredElementMessage requiredElementMessage;

    public Menu(GameStateManager gsm) {
        super(gsm);
        bg = new Texture("bg.jpg");

        camera = new Camera(520, 936, bg);

        addButton = new AddButton();
        okButton = new OkButton();
        rotateButton = new RotateButton();
        deleteButton = new DeleteButton();
        editButton = new EditButton();
        backButton = new BackButton();
        playButton = new PlayButton();

        addButton.seteSize(83);
        okButton.seteSize(83);
        rotateButton.seteSize(83);
        deleteButton.seteSize(83);
        editButton.seteSize(83);
        backButton.seteSize(83);
        playButton.seteSize(75);

        input = new Rectangle(0, 0, 1, 1);
        check = new Rectangle(0, 0, 1, 1);
        vector3 = new Vector3(0, 0, 0);

        pressed = false;
        elementPressed = false;
        buttonPress = false;
        seted = false;

        addState = new AddState(gsm);
        loadState = new LoadState(gsm);

        elements = new Array<Element>();
        connections = new Array<Connection>();

        createConection = false;
        created = true;
        connectionPress = false;
        connectionPressed = false;

        audio = new Audio();

        error = new Texture("error.png");

        schemeErrorMessage = new SchemeErrorMessage();
        requiredElementMessage = new RequiredElementMessage();

        updateButtons();
    }

    ///////////////////////////////////////////////////////

    public void setProject(FileHandle project, boolean learningmode) {
        this.project = project;
        this.learningmode = learningmode;

        if(learningmode) checkFile = new FileHandle(project.pathWithoutExtension() + "_learn.txt");

        for(Element e : elements) e.dispose();
        for(Connection con : connections) con.dispose();

        elements.clear();
        connections.clear();

        buttonPress = false;
        elementPressed = false;
        connectionPressed = false;

        BufferedReader bufferedReader = new BufferedReader(project.reader());
        String s;

        try {
            buf = new StringBuffer(bufferedReader.readLine());
            index = 0;
            camera.position.x = Float.valueOf(getNextWord());
            camera.position.y = Float.valueOf(getNextWord());
            camera.update();
            updateButtons();

            for(Element e : addState.elements) e.count = Integer.valueOf(getNextWord());

        }
        catch(Exception e) {
            System.out.println("OOPS ERROR HAS BEEN FOUND");
        }

        while (true)
        try {
            buf = new StringBuffer(bufferedReader.readLine());
            index = 0;

            if(buf.charAt(index) == 'e') {
                ++index;
                getElement(getNextWord());
            } else if(buf.charAt(index) == 'c') {
                ++index;
                getConnection(Integer.valueOf(getNextWord()));
            }

        } catch (Exception e) {
            break;
        }

        try { bufferedReader.close(); } catch(Exception e) {}
    }

    private String getNextWord() {
        String ans = "";
        while(index < buf.length() && buf.charAt(index) == ' ') ++index;

        while (index < buf.length() && buf.charAt(index) != ' ') {
            ans += buf.charAt(index);
            ++index;
        }

        return ans;
    }

    //Дбавляем элементы
    private void getElement(String name) {
        if(name.compareTo("Battery") == 0) {
            float x = Float.valueOf(getNextWord()), y = Float.valueOf(getNextWord());
            boolean rotate = Boolean.valueOf(getNextWord());
            boolean block = Boolean.valueOf(getNextWord());
            boolean seted = Boolean.valueOf(getNextWord());

            Batery b = new Batery(x, y);
            b.setRotate(rotate);
            b.setBlocking(block);

            if(seted) for(Parametr p : b.parametres) {
                p.setParametr(Float.valueOf(getNextWord()));
                p.set(true);
            }
            b.update(0);
            elements.add(b);
            return;
        }

        if(name.compareTo("Lamp") == 0) {
            float x = Float.valueOf(getNextWord()), y = Float.valueOf(getNextWord());
            boolean rotate = Boolean.valueOf(getNextWord());
            boolean block = Boolean.valueOf(getNextWord());
            boolean seted = Boolean.valueOf(getNextWord());

            Lamp l = new Lamp(x, y);
            l.setRotate(rotate);
            l.setBlocking(block);

            if(seted) for(Parametr p : l.parametres) {
                p.setParametr(Float.valueOf(getNextWord()));
                p.set(true);
            }

            l.update(0);
            elements.add(l);

            return;
        }

        if(name.compareTo("Resistor") == 0) {
            float x = Float.valueOf(getNextWord()), y = Float.valueOf(getNextWord());
            boolean rotate = Boolean.valueOf(getNextWord());
            boolean block = Boolean.valueOf(getNextWord());
            boolean seted = Boolean.valueOf(getNextWord());

            Resistor r = new Resistor(x, y);
            r.setRotate(rotate);
            r.setBlocking(block);

            if(seted) for(Parametr p : r.parametres) {
                p.setParametr(Float.valueOf(getNextWord()));
                p.set(true);
            }

            r.update(0);
            elements.add(r);
            return;
        }
    }

    private void getConnection(int count) {
        float x, y;
        Connection con = new Connection();
        for(int i = 0; i < count; ++i) {
            x = Float.valueOf(getNextWord());
            y = Float.valueOf(getNextWord());
            con.points.add(new Point(x, y));
        }

        con.createRectangles();
        connections.add(con);
    }

    private void save() {
        project.writeString(camera.position.x + " " + camera.position.y + " ", false);
        for(Element e : addState.elements) project.writeString(e.count + " ", true);
        project.writeString("\n", true);
        for (Element e : elements) {
            project.writeString("e " + e.getName() + " " + e.getX() + " " + e.getY()
                    + " " + e.isRotate() + " " + e.isBlock() + " " + e.isParametrs_seted() + " ", true);

            if(e.isParametrs_seted()) for(Parametr p : e.parametres) project.writeString(p.getParametr() + " ", true);

            project.writeString("\n", true);
        }

        for(Connection con : connections) {
            project.writeString("c " + con.points.size + " ", true);

            for(Point p : con.points) project.writeString(p.getX() + " " + p.getY() + " ", true);

            project.writeString("\n", true);
        }
    }

    /////////////////*BUTTONS*/////////////////////////////

    private void unactive() {
        if(elementPressed) elements.peek().pressed();
        if(connectionPressed) connections.peek().pressed();
        elementPress = false;
        elementPressed = false;
        connectionPress = false;
        connectionPressed = false;
    }

    //проверка наведения на кнопку
    private void checkButtons() {
        if(elementPress || reposition) return;

        if(elementPressed)
        {
            if(input.overlaps(okButton.getRectangle())) {
                okButton.press();
                buttonPress = true;
                return;
            } else okButton.notPress();

            if(input.overlaps(rotateButton.getRectangle())) {
                rotateButton.press();
                buttonPress = true;
                return;
            } else rotateButton.notPress();

            if(input.overlaps(deleteButton.getRectangle())) {
                deleteButton.press();
                buttonPress = true;
                return;
            } else deleteButton.notPress();

            if(input.overlaps(editButton.getRectangle())) {
                editButton.press();
                buttonPress = true;
                return;
            } else editButton.notPress();
        }

        if(connectionPressed)
        {
            if(input.overlaps(deleteButton.getRectangle())) {
                deleteButton.press();
                buttonPress = true;
                return;
            } else deleteButton.notPress();

            if(input.overlaps(okButton.getRectangle())) {
                okButton.press();
                buttonPress = true;
                return;
            } else okButton.notPress();
        }

        if(input.overlaps(addButton.getRectangle())) {
            addButton.press();
            buttonPress = true;
            return;
        } else addButton.notPress();

        if(input.overlaps(backButton.getRectangle())) {
            backButton.press();
            buttonPress = true;
            return;
        } else backButton.notPress();

        if(seted && input.overlaps(playButton.getRectangle())) {
            playButton.press();
            buttonPress = true;
            return;
        } else playButton.notPress();


        for(int i = elements.size - 1; (i >= 0 && !pressed && !elementPress); --i) {
            element = elements.get(i);

            if (input.overlaps(element.getRectangle())) {
                unactive();

                element.press();
                elementPress = true;

                elements.removeIndex(i);
                elements.add(element);

                return;
            } else element.notPress();
        }

        Connection con;
        for(int i = connections.size - 1; i >= 0; --i) {
            con = connections.get(i);

            for (Rectangle r : con.rectangles)
                if (input.overlaps(r)) {
                    unactive();

                    con.press();
                    connectionPress = true;

                    connections.removeIndex(i);
                    connections.add(con);
                    return;
                } else con.notPress();
        }

        buttonPress = false;
        elementPress = false;
        connectionPress = false;
        reposition = true;
        unactive();
    }

    //проверка нажатия на кнопку
    private void checkPressed() {
        if(buttonPress) //если нажата кнопка
        {
            audio.playTick();

            if (addButton.isPress()) {
                addButton.notPress();
                unactive();
                gsm.push(addState);
            }

            if(backButton.isPress()) {
                backButton.notPress();
                unactive();
                save();
                gsm.pop();
            }

            if(playButton.isPress()) {
                playButton.notPress();
                unactive();

                gsm.push(loadState);
                try {
                    if (learningmode)
                        loadState.setElements(camera.position.x, camera.position.y, elements, connections, checkFile);
                    else
                        loadState.setElements(camera.position.x, camera.position.y, elements, connections);

                } catch (Exception e) {
                    gsm.pop();
                    System.out.println(e.getMessage());
                    if(messages.size() < 10) {
                        schemeErrorMessage.reset();
                        messages.push(schemeErrorMessage);
                    }
                }
            }

            if (okButton.isPress() && elementPressed) {
                okButton.notPress();
                elementPressed = false;
                elements.peek().pressed();
            }

            if (okButton.isPress() && connectionPressed) {
                okButton.notPress();
                connectionPressed = false;
                connections.peek().pressed();
            }

            if (rotateButton.isPress()) {
                rotateButton.notPress();
                elements.peek().rotation();
            }

            if (deleteButton.isPress() && elementPressed) {
                deleteButton.notPress();
                if(!elements.peek().isBlock()) {
                    elementPressed = false;
                    addState.elementComeBack(elements.peek());
                    elements.peek().dispose();
                    elements.pop();
                } else if(messages.size() < 10) {
                    requiredElementMessage.reset();
                    messages.push(requiredElementMessage);
                }
            }

            if (deleteButton.isPress() && connectionPressed) {
                deleteButton.notPress();
                connectionPressed = false;
                connections.peek().dispose();
                connections.pop();
            }

            if(editButton.isPress()) {
                editButton.notPress();

                for(int i = 0; i < elements.peek().parametres.size; ++i) {

                    final Element e = elements.peek();
                    final Parametr p = e.parametres.get(i);

                    Gdx.input.getTextInput(new Input.TextInputListener() {
                        @Override
                        public void input(String text) {
                            if(!e.isBlock()) {
                                p.setParametr(Float.parseFloat(text));
                                p.set(true);
                            }
                        }

                        @Override
                        public void canceled() {

                        }

                    }, "Set " + p.getName(), p.isSeted()? Float.toString(p.getParametr()) : "", "");
                }

            }

            buttonPress = false;
        }

        if(elementPress) {
            audio.playTick();

            elements.peek().notPress();
            elements.peek().pressed();
            elementPressed = elements.peek().isPresed();
            elementPress = false;
        }

        if(connectionPress) // если нажато соединение
        {
            audio.playTick();

            connections.peek().notPress();
            connections.peek().pressed();

            connectionPressed = connections.peek().isPressed();
            connectionPress = false;
        }

    }

    //перемещение кнопок вслед за камерой
    private void updateButtons() {

        backButton.reposition(camera.position.x - camera.viewportWidth / 2 + 10,
                camera.position.y - camera.viewportHeight / 2 + 10);

        addButton.reposition(backButton.getX(), backButton.getY() + backButton.getSize() + 10);

        deleteButton.reposition(addButton.getX(), addButton.getY() + addButton.getSize() + 10);

        okButton.reposition(deleteButton.getX(), deleteButton.getY() + deleteButton.getSize() + 10);

        rotateButton.reposition(okButton.getX(), okButton.getY() + rotateButton.getSize() + 10);

        editButton.reposition(rotateButton.getX(), rotateButton.getY() + editButton.getSize() + 10);

        playButton.reposition(camera.position.x - camera.viewportWidth / 2 + 10,
                camera.position.y + camera.viewportHeight / 2 - playButton.getSize() - 10);

        schemeErrorMessage.setPosition(camera.position.x - schemeErrorMessage.width / 2, camera.position.y - camera.viewportHeight / 2 + 100);
        requiredElementMessage.setPosition(schemeErrorMessage.x, schemeErrorMessage.y);
    }

    ///////////////////////////////////////////////////////

    public void add(Element element) {
        element.reposition(camera.position.x, camera.position.y);
        element.pressed();

        elements.add(element);

        elementPressed = true;
    }

    private void elementReposition() {
        // не передвигаем если нажата кнопка
        if(input.overlaps(okButton.getRectangle()) || input.overlaps(rotateButton.getRectangle())
                || input.overlaps(deleteButton.getRectangle()) || input.overlaps(editButton.getRectangle())
                || input.overlaps(backButton.getRectangle()) || input.overlaps(playButton.getRectangle())) return;

        element = elements.peek();
        element.reposition(input.x - element.getRectangle().width / 2,
                input.y - element.getRectangle().height / 2);
    }

    ///////////////////////////////////////////////////////

    public void addConnection() {
        created = false;
        createConection = true;
    }

    private void drawConnection() {
        if(!created) {
            created = true;
            connections.add(new Connection());
        }

        Connection con = connections.get(connections.size - 1);

        float x = Math.round(input.x / ROTATION) * ROTATION, y = Math.round(input.y / ROTATION) * ROTATION;
        Point p = new Point(x, y);
        if( con.points.size == 0 ||  ( !con.points.get(con.size() - 1).compare(p) &&  (p.getX() == con.points.get(con.size() - 1).getX() || p.getY() == con.points.get(con.size() - 1).getY()) ) ) {
            con.points.add(p);
                if(con.size() >= 3 && p.compare(con.points.get(con.size() - 3))) {

                    con.points.get(con.size() - 1).dispose();
                    con.points.get(con.size() - 2).dispose();
                    con.points.removeIndex(con.size() - 1);
                    con.points.removeIndex(con.size() - 1);

                    return;
                }
        }
    }

    ///////////////////////////////////////////////////////

    //обработка нажатия на экран
    private void input() {
        if (Gdx.input.isTouched()) {
            check.setPosition(Gdx.input.getX(), Gdx.input.getY());
            // получаем нормальные координаты
            vector3.set(check.x, check.y, 0);
            camera.unproject(vector3);
            input.setPosition(vector3.x, vector3.y);

            if(createConection)
            {
                drawConnection();
                return;
            }

            checkButtons(); // проверка на нажатие кнопки

            if(reposition && pressed) {
                camera.reposition(last_x - check.x, check.y - last_y);
                updateButtons(); // перемещение кнопок вслед за камерой
            }
            else
            if (elementPress) elementReposition();

            pressed = true;

        } else {
            pressed = false;
            checkPressed(); // проверим было ли нажатие
            input.setPosition(0, 0);
            check.setPosition(0, 0);

            if(created && createConection) {
                createConection = false;
                connections.peek().createRectangles(); // создадим rectangles в первый раз
                if(connections.peek().points.size <= 1) connections.pop();
            }

            reposition = false;
        }

        last_x = check.x;
        last_y = check.y;
    }

    public void update(float dt) {
        for(Element e : elements) e.update(dt);
        seted = true;
        for(Element e : elements) if(!e.isParametrs_seted()) {
            seted = false;
            break;
        }

        input();

        if(!messages.isEmpty()) {
            messages.peek().update(dt);
            if(!messages.peek().isExist()) messages.pop();
        }
    }

    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();

        sb.draw(bg, 0, 0);

        for(Connection con : connections)
            con.render(sb);

        for(Element element : elements)
            element.render(sb);

        for (Element element : elements)
            if(!element.isParametrs_seted()) {
                Rectangle r = element.getRectangle();
                sb.draw(error, r.x - ESIZE * 1.5f, r.y, ESIZE, ESIZE);
            }

        if(!messages.isEmpty()) messages.peek().render(sb);

        addButton.render(sb);
        backButton.render(sb);
        if(seted) playButton.render(sb);

        if(elementPressed) {
            okButton.render(sb);
            rotateButton.render(sb);
            deleteButton.render(sb);
            editButton.render(sb);
        }

        if(connectionPressed) {
            deleteButton.render(sb);
            okButton.render(sb);
        }

        sb.end();
    }

    public void  dispose() {
        addButton.dispose();
        okButton.dispose();
        rotateButton.dispose();
        deleteButton.dispose();
        editButton.dispose();
        backButton.dispose();
        playButton.dispose();

        for(Element e : elements) e.dispose();

        for (Connection c : connections) c.dispose();

        addState.dispose();
        loadState.dispose();

        bg.dispose();

        System.out.println("MENU DISPOSED!");
        audio.dispose();

        schemeErrorMessage.dispose();
        requiredElementMessage.dispose();
    }
}
