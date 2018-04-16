package com.platform.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.platform.Audio;
import com.platform.Buttons.BackButton;
import com.platform.Buttons.InfoButton;
import com.platform.Camera;
import com.platform.Elements.Batery;
import com.platform.Elements.ConnectElement;
import com.platform.Elements.Element;
import com.platform.Elements.Lamp;
import com.platform.Elements.Resistor;
import com.platform.GameStateManager;
import com.platform.Messages.OutOfStockMessage;

import static com.platform.Elements.Element.HEIGHT;
import static com.platform.Elements.Element.THEIGHT;
import static com.platform.Elements.Element.TWIDTH;
import static java.lang.Math.*;

public class AddState extends State {

    private BackButton backButton;
    private InfoButton infoButton;

    private boolean button_pressed;

    private Rectangle input;
    private Vector3 vector3;

    public Array<Element> elements;

    private float scroll, last_x, a;

    private boolean pressed;

    private Rectangle center;

    private Audio audio;

    private int index;

    private BitmapFont bf;

    private OutOfStockMessage outOfStockMessage;

    public AddState(GameStateManager gsm) {
        super(gsm);

        bg = new Texture("bg7.png");
        camera = new Camera(400, 720, bg);

        backButton = new BackButton();
        backButton.seteSize(64);

        infoButton = new InfoButton();
        infoButton.seteSize(64);

        vector3 = new Vector3();
        input = new Rectangle(0, 0, 1, 1);

        button_pressed = false;

        backButton.reposition(camera.position.x - camera.viewportWidth / 2 + 10,
                camera.position.y - camera.viewportHeight / 2 + 10);

        infoButton.reposition(backButton.getX(), backButton.getY() + backButton.size + 10);

        elements = new Array<Element>();
        setElements();
        index = 0;

        scroll = 0;

        center = new Rectangle(camera.position.x - 30, camera.position.y, 60, 1);

        audio = new Audio();

        a = 150;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("tahoma.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        bf = generator.generateFont(parameter);
        bf.setColor(Color.WHITE);
        generator.dispose();

        outOfStockMessage = new OutOfStockMessage();
        outOfStockMessage.setPosition(camera.position.x - outOfStockMessage.width / 2, camera.position.y - camera.viewportHeight / 2 + 100);
    }

    // заполнение масива элементов
    private void setElements() {
        elements.add(new ConnectElement(camera.position.x - Element.WIDTH / 2, camera.position.y));
        elements.peek().count = 999;
        elements.add(new Lamp(elements.get(elements.size - 1).getX() + Element.WIDTH / 2 + camera.viewportWidth / 2, camera.position.y));
        elements.peek().count = 999;
        elements.add(new Resistor(elements.get(elements.size - 1).getX() + Element.WIDTH / 2 + camera.viewportWidth / 2, camera.position.y));
        elements.peek().count = 999;
        elements.add(new Batery(elements.get(elements.size - 1).getX() + Element.WIDTH / 2 + camera.viewportWidth / 2, camera.position.y));
        elements.peek().count = 1;

        for (Element el : elements) el.parametrs_seted = true; // в AddState нет параметров
    }

    public void elementComeBack(Element e) {
        for(Element element : elements) if(e.compare(element)) {
            ++element.count;
        }
    }

    /////////////////*BUTTONS*/////////////////////////////

    //проверка наведения на кнопку
    private void checkButtons() {
        if(input.overlaps(backButton.getRectangle())) {
            backButton.press();
            button_pressed = true;
            return;
        } else backButton.notPress();

        if(input.overlaps(infoButton.getRectangle())) {
            infoButton.press();
            button_pressed = true;
            return;
        } else infoButton.notPress();

        for(Element el : elements)
            if(input.overlaps(el.getRectangle())) {
                el.press();
                button_pressed = true;
                return;
            } else el.notPress();

        button_pressed = false;
    }

    //проверка нажатия на кнопку
    private void checkPressed() {
        if(!button_pressed) return;

        audio.playTick();

        if(backButton.isPress()) {
            backButton.notPress();
            button_pressed = false;
            gsm.pop();
        }

        if(infoButton.isPress()) {
            infoButton.notPress();
            button_pressed = false;
            elements.get(index).openWiki();
        }

        for(int i = 1; i < elements.size; ++i) {
            Element el = elements.get(i);
            if(el.isPress()) {
                el.notPress();
                button_pressed = false;
                if(el.count > 0) {
                    gsm.pop_and_add(el);
                    --el.count;
                } else if(messages.size() < 10) {
                    outOfStockMessage.reset();
                    messages.push(outOfStockMessage);
                }
            }
        }

        Element el = elements.get(0);
        if(el.isPress()) {
            el.notPress();
            button_pressed = false;
            gsm.pop_and_add_connection();
        }

        button_pressed = false;
    }

    private void updateElements() {
        for(Element el : elements)
            el.scrolling(scroll, 0);

        Element el;
        for(int i = 0; i < elements.size; ++i) {
            el = elements.get(i);
            if(center.overlaps(el.getRectangle()) && index != i) {
                index = i;
                audio.playKnock();
            }
        }
    }

    ///////////////////////////////////////////////////////

    private void input(float dt) {

        if (Gdx.input.isTouched()) {
            vector3.set(Gdx.input.getX(), Gdx.input.getY(), 0);

            if(pressed) {
                // пролистывание кнопок
                scroll = last_x - vector3.x;
                scroll = max(scroll, elements.get(0).getX() + Element.WIDTH / 2 - camera.position.x);
                scroll = min(scroll, elements.peek().getX() + Element.WIDTH / 2 - camera.position.x);

                updateElements();
            } else scroll = 0;

            last_x = vector3.x;

            camera.unproject(vector3);
            input.setPosition(vector3.x, vector3.y);

            checkButtons(); // проверка на нажатие кнопки
            pressed = true;

        } else {

            if(scroll > 0) scroll = max(0, scroll - a * dt);
            if(scroll < 0) scroll = min(0, scroll + a * dt);

            if(scroll != 0) {
                scroll = max(scroll, elements.get(0).getX() + Element.WIDTH / 2 - camera.position.x);
                scroll = min(scroll, elements.peek().getX() + Element.WIDTH / 2 - camera.position.x);

                updateElements();
            }

            checkPressed(); // проверим было ли нажатие
            input.setPosition(0, 0);
            pressed = false;
        }
    }

    public void update(float dt) {
        input(dt);
        if(!messages.isEmpty()) {
            messages.peek().update(dt);
            if(!messages.peek().isExist()) messages.pop();
        }
    }

    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();

        sb.draw(bg, 0, 0);

        String s;
        if(index != 0) s = "Avialable: " + Integer.toString(elements.get(index).count); else s = "";
        sb.draw(elements.get(index).getText(), camera.position.x - TWIDTH / 2, camera.position.y + camera.viewportHeight / 4, TWIDTH, THEIGHT);
        bf.draw(sb, s, camera.position.x - s.length() / 2 * 14, camera.position.y + camera.viewportHeight / 4 - 15);

        for(Element el : elements)
            el.render(sb);

        if(!messages.isEmpty()) messages.peek().render(sb);

        backButton.render(sb);
        infoButton.render(sb);
        sb.end();
    }

    public void  dispose() {
        backButton.dispose();
        infoButton.dispose();

        for(int i = 0; i < elements.size; ++i) elements.get(i).dispose();

        bg.dispose();

        audio.dispose();

        bf.dispose();

        outOfStockMessage.dispose();
    }
}
