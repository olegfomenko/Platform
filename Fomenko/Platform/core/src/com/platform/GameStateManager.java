package com.platform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.platform.Elements.Element;
import com.platform.States.*;
import java.util.Stack;

public class GameStateManager {

    private Stack<State> states;

    public GameStateManager() {
        states = new Stack<State>();
    }

    public void pop() {
        states.pop();
    }

    public void disposedPop() {
        states.peek().dispose();
        states.pop();
    }

    public void pop_and_add(Element element) {
        states.pop();
        states.peek().add(element.getNewElement());
    }

    public void pop_and_add_connection() {
        states.pop();
        states.peek().addConnection();
    }

    public void push(State state) {
        state.messages.clear();
        states.push(state);
    }

    public void update(float dt) {
        if(!states.isEmpty()) states.peek().update(dt);
    }

    public void render(SpriteBatch sb) {
        if(!states.isEmpty()) states.peek().render(sb);
    }

    public void dispose() {
        while(states.size() > 0) {
            states.peek().dispose();
            states.pop();
        }
    }
}
