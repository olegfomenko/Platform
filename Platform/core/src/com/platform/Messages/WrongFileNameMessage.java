package com.platform.Messages;

import com.badlogic.gdx.graphics.Texture;

public class WrongFileNameMessage extends Message{

    public WrongFileNameMessage() {
        message = new Texture("WrongName.png");
    }
}
