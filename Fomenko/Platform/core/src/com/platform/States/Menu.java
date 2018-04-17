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

    private MoveButton moveButton;
    private AddButton addButton;
    private OkButton okButton;
    private RotateButton rotateButton;
    private DeleteButton deleteButton;
    private EditButton editButton;
    private BackButton backButton;
    private PlayButton playButton;

    private float last_x, last_y;

    private boolean pressed, buttonPress, elementPressed, connectionPress, connectionPressed, createConection;

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

        moveButton = new MoveButton();
        addButton = new AddButton();
        okButton = new OkButton();
        rotateButton = new RotateButton();
        deleteButton = new DeleteButton();
        editButton = new EditButton();
        backButton = new BackButton();
        playButton = new PlayButton();

        moveButton.seteSize(83);
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
        moveButton.notPressed();

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

    //проверка наведения на кнопку
    private void checkButtons() {

        // получаем нормальные координаты
        getCoordinates();

        if(!elementPressed && !connectionPressed)
        {
            if (check.overlaps(moveButton.getRectangle())) {
                moveButton.press();
                buttonPress = true;
                return;
            } else moveButton.notPress();

            if (moveButton.isPresed())
                return; // если идет передвижение экрана - невозможно ни с чем взаимодействовать

            if (check.overlaps(addButton.getRectangle())) {
                addButton.press();
                buttonPress = true;
                return;
            } else addButton.notPress();

            if(check.overlaps(backButton.getRectangle())) {
                backButton.press();
                buttonPress = true;
                return;
            } else backButton.notPress();

            if(seted && check.overlaps(playButton.getRectangle())) {
                playButton.press();
                buttonPress = true;
                return;
            } else playButton.notPress();


            /* на элемент наживаем ТОЛЬКО ОДИН РАЗ при выборе для перемещения.
            для отмены используем кнопки OK или CANCLE которые видны только если элемент нажат */

            for (int i = elements.size - 1; i >= 0; --i) {
                element = elements.get(i);

                if (check.overlaps(element.getRectangle())) {
                    element.press();
                    buttonPress = true;
                    return;
                } else element.notPress();
            }

            Connection con;
            for(int i = connections.size - 1; i >= 0; --i) {
                con = connections.get(i);

                for(Rectangle r : con.rectangles)
                    if(check.overlaps(r)) {
                        con.press();
                        connectionPress = true;
                        return;
                    } else con.notPress();
            }

        }

        if(elementPressed)
        {
            if (check.overlaps(okButton.getRectangle())) {
                okButton.press();
                buttonPress = true;
                return;
            } else okButton.notPress();

            if(check.overlaps(rotateButton.getRectangle())) {
                rotateButton.press();
                buttonPress = true;
                return;
            } else rotateButton.notPress();

            if(check.overlaps(deleteButton.getRectangle())) {
                deleteButton.press();
                buttonPress = true;
                return;
            } else deleteButton.notPress();

            if(check.overlaps(editButton.getRectangle())) {
                editButton.press();
                buttonPress = true;
                return;
            } else editButton.notPress();
        }

        if(connectionPressed)
        {
            if(check.overlaps(deleteButton.getRectangle())) {
                deleteButton.press();
                buttonPress = true;
                return;
            } else deleteButton.notPress();

            if (check.overlaps(okButton.getRectangle())) {
                okButton.press();
                buttonPress = true;
                return;
            } else okButton.notPress();
        }

        buttonPress = false;
        connectionPress = false;
    }

    //проверка нажатия на кнопку
    private void checkPressed() {
        if(buttonPress) //если нажата кнопка
        {
            audio.playTick();

            if (moveButton.isPress()) {
                moveButton.notPress();
                moveButton.pressed();
            }

            if (addButton.isPress()) {
                addButton.notPress();
                gsm.push(addState);
            }

            if(backButton.isPress()) {
                backButton.notPress();
                save();
                gsm.pop();
            }

            if(playButton.isPress()) {
                playButton.notPress();

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


            for (int i = elements.size - 1; i >= 0; --i) {
                element = elements.get(i);

                if (element.isPress()) {
                    element.notPress();
                    element.pressed();
                    elementPressed = true;

                    elements.removeIndex(i);
                    elements.add(element);

                    break;
                }
            }

            buttonPress = false;
        }

        if(connectionPress) // если нажато соединение
        {
            audio.playTick();

            Connection con;
            for (int i = 0; i < connections.size; ++i) {
                con = connections.get(i);

                if(con.isPress()) {
                    con.notPress();
                    con.pressed();
                    connectionPressed = true;

                    connections.removeIndex(i);
                    connections.add(con);

                    break;
                }
            }

            connectionPress = false;
        }

    }

    //перемещение кнопок вслед за камерой
    private void updateButtons() {

        backButton.reposition(camera.position.x - camera.viewportWidth / 2 + 10,
                camera.position.y - camera.viewportHeight / 2 + 10);

        moveButton.reposition(backButton.getX(), backButton.getY() + moveButton.getSize() + 10);

        addButton.reposition(moveButton.getX(), moveButton.getY() + moveButton.getSize() + 10);

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

        getCoordinates();

        // не передвигаем если нажата кнопка
        if(check.overlaps(okButton.getRectangle()) || check.overlaps(rotateButton.getRectangle())
                || check.overlaps(deleteButton.getRectangle()) || check.overlaps(editButton.getRectangle())
                || check.overlaps(backButton.getRectangle()) || check.overlaps(playButton.getRectangle())) return;

        element = elements.peek();
        element.reposition(check.x - element.getRectangle().width / 2,
                check.y - element.getRectangle().height / 2);
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

        getCoordinates();

        Connection con = connections.get(connections.size - 1);

        float x = Math.round(check.x / ROTATION) * ROTATION, y = Math.round(check.y / ROTATION) * ROTATION;
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

    private void getCoordinates() {
        // получаем нормальные координаты
        vector3.set(input.x, input.y, 0);
        camera.unproject(vector3);
        check.setPosition(vector3.x, vector3.y);
    }

    //обработка нажатия на экран
    private void input() {
        if (Gdx.input.isTouched()) {
            input.setPosition(Gdx.input.getX(), Gdx.input.getY());

            if(createConection) {
                drawConnection();
            } else {

                checkButtons(); // проверка на нажатие кнопки

                if (elementPressed)
                    elementReposition();
                else if (pressed && !buttonPress && moveButton.isPresed()) {
                    camera.reposition(last_x - input.x, input.y - last_y);
                    updateButtons(); // перемещение кнопок вслед за камерой
                }

                pressed = true;
            }

        } else {
            pressed = false;
            input.setPosition(0, 0);
            checkPressed(); // проверим было ли нажатие

            if(created && createConection) {
                createConection = false;
                connections.get(connections.size - 1).createRectangles(); // создадим rectangles в первый раз
                if(connections.peek().points.size == 1) connections.pop();
            }
        }

        last_x = input.x;
        last_y = input.y;
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

        moveButton.render(sb);
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
        moveButton.dispose();
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
