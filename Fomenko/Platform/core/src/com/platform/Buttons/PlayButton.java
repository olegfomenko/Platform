package com.platform.Buttons;

import com.badlogic.gdx.graphics.Texture;

public class PlayButton extends Button {

    public PlayButton() {
        button = new Texture("play.png");
        button_press = new Texture("play_press.png");
        button_pressed = new Texture("play_pressed.png");
    }
}
