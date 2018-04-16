package com.platform.States;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.platform.Camera;
import com.platform.Elements.Element;
import com.platform.GameStateManager;
import com.platform.Messages.Message;

import java.util.Stack;

public class State {

    public GameStateManager gsm;
    public Camera camera;
    public Texture bg;
    public Stack<Message> messages;

    public State(GameStateManager gsm) {
        this.gsm = gsm;
        messages = new Stack<Message>();
    }

    public void update(float dt) {

    }

    public void render(SpriteBatch sb) {

    }

    public void  dispose() {

    }

    // добавление элемента // ЭТО КОСТЫЛЬ
    public void add(Element element) {

    }

    //добавляем соединение // ЭТО ВТОРОЙ КОСТЫЛЬ
    public void addConnection() {

    }
}
