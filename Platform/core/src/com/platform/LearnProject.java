package com.platform;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LearnProject extends Project {

    public LearnProject(FileHandle fileHandle, float x, float y, float width, float height, boolean done) {
        super(fileHandle, x, y, width, height, done);

    }

    public void reposition(float x, float y) {
        this.x = x;
        this.y = y;
        goButton.reposition(x + width - redDeleteButton.getSize(), y);
    }

    public void render(SpriteBatch sb, BitmapFont bf) {
        bf.draw(sb, fileHandle.nameWithoutExtension(), x + 10, y + height / 4 * 3);
        goButton.render(sb);
    }
}
