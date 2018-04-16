package com.platform;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.platform.Buttons.GoButton;
import com.platform.Buttons.RedDeleteButton;

public class Project {

    public FileHandle fileHandle;
    public RedDeleteButton redDeleteButton;
    public GoButton goButton;
    public float x, y, width, height;
    public boolean done;

    public Project(FileHandle fileHandle, float x, float y, float width, float height) {
        this.fileHandle = fileHandle;
        this.width = width;
        this.height = height;

        redDeleteButton = new RedDeleteButton();
        redDeleteButton.seteSize((int)height);

        goButton = new GoButton();
        goButton.seteSize((int)height);

        done = false;

        reposition(x, y);
    }

    public Project(FileHandle fileHandle, float x, float y, float width, float height, boolean done) {
        this.fileHandle = fileHandle;
        this.width = width;
        this.height = height;

        redDeleteButton = new RedDeleteButton();
        redDeleteButton.seteSize((int)height);

        goButton = new GoButton();
        goButton.seteSize((int)height);

        this.done = done;

        reposition(x, y);
    }

    public RedDeleteButton getDeleteButton() {
        return redDeleteButton;
    }

    public GoButton getGoButton() {
        return goButton;
    }

    public FileHandle getFileHandle() {
        return fileHandle;
    }

    public void reposition(float x, float y) {
        this.x = x;
        this.y = y;
        redDeleteButton.reposition(x + width - redDeleteButton.getSize(), y);
        goButton.reposition(redDeleteButton.getX() - goButton.getSize() - 5, y);
    }

    public void render(SpriteBatch sb, BitmapFont bf) {
        bf.draw(sb, fileHandle.nameWithoutExtension(), x + 10, y + height / 4 * 3);
        goButton.render(sb);
        redDeleteButton.render(sb);
    }

    public void dispose() {
        redDeleteButton.dispose();
        goButton.dispose();
    }
}
