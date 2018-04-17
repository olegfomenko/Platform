package com.platform.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.platform.Audio;
import com.platform.Buttons.BackButton;
import com.platform.Buttons.CreateButton;
import com.platform.Buttons.LearnButton;
import com.platform.Buttons.OpenButton;
import com.platform.Camera;
import com.platform.GameStateManager;
import com.platform.Messages.Message;
import com.platform.Messages.WrongFileNameMessage;

import java.io.File;
import java.util.Stack;

public class StartState extends State {

    private boolean buttonPress;

    private Rectangle input;
    private Vector3 vector3;

    private Audio audio;

    private CreateButton createButton;
    private OpenButton openButton;
    private BackButton backButton;
    private LearnButton learnButton;

    private String path;

    private Menu menu;
    private ListState listState;
    private LearnListState learnListState;

    private WrongFileNameMessage wrongFileNameMessage;

    public StartState(GameStateManager gsm, String path) {
        super(gsm);

        this.path = path;

        buttonPress = false;

        bg = new Texture("white.png");
        camera = new Camera(400, 720, bg);

        vector3 = new Vector3();
        input = new Rectangle(0, 0, 1, 1);

        audio = new Audio();

        openButton = new OpenButton();
        openButton.setSize(250, 80);
        openButton.reposition(camera.position.x - openButton.width / 2, camera.position.y - openButton.height / 2);

        createButton = new CreateButton();
        createButton.setSize(250, 80);
        createButton.reposition(camera.position.x - createButton.width / 2, openButton.getY() + openButton.height + 30);

        learnButton = new LearnButton();
        learnButton.setSize(250, 80);
        learnButton.reposition(camera.position.x - learnButton.width / 2, openButton.getY() - learnButton.height - 30);


        backButton = new BackButton();
        backButton.seteSize(64);
        backButton.reposition(camera.position.x - camera.viewportWidth / 2 + 10, camera.position.y - camera.viewportHeight / 2 + 10);

        menu = new Menu(gsm);
        listState = new ListState(gsm, path, menu);
        learnListState = new LearnListState(gsm, path + "/Learn", menu);

        listState.setList();
        learnListState.setList();

        wrongFileNameMessage = new WrongFileNameMessage();
        wrongFileNameMessage.setPosition(camera.position.x - wrongFileNameMessage.width / 2, learnButton.getY() - wrongFileNameMessage.height - 60);
    }

    private void checkButtons() {
        if(input.overlaps(createButton.getRectangle())) {
            buttonPress = true;
            createButton.press();
            return;
        } else createButton.notPress();

        if(input.overlaps(openButton.getRectangle())) {
            buttonPress = true;
            openButton.press();
            return;
        } else openButton.notPress();

        if(input.overlaps(backButton.getRectangle())) {
            buttonPress = true;
            backButton.press();
            return;
        } else backButton.notPress();

        if(input.overlaps(learnButton.getRectangle())) {
            buttonPress = true;
            learnButton.press();
            return;
        } else learnButton.notPress();

        buttonPress = false;
    }

    private void checkPressed() {

        if(!buttonPress) return;

        audio.playTick();

        if(createButton.isPress()) {
            createButton.notPress();

            Gdx.input.getTextInput(new Input.TextInputListener() {
                @Override
                public void input(String name) {
                    File project = new File(path, name + ".txt");

                    try {
                        for(int i = 0; i < name.length(); ++i)
                            if(!(name.charAt(i) >= 'A' && name.charAt(i) <= 'Z'
                            || name.charAt(i) >= 'a' && name.charAt(i) <= 'z'
                            || name.charAt(i) >= '0' && name.charAt(i) <= '9'
                            || name.charAt(i) == '_' || name.charAt(i) == '-'
                            || name.charAt(i) == '(' || name.charAt(i) == ')' || name.charAt(i) == ' ')) throw new Exception();

                        if(!project.exists()) project.createNewFile(); else throw new Exception();

                    } catch (Exception e) {
                        wrongFileNameMessage.reset();
                        messages.push(wrongFileNameMessage);
                        return;
                    }

                    FileHandle fileHandle = new FileHandle(project);
                    fileHandle.writeString(camera.position.x + " " + camera.position.y + " 256 256 256 1\n", false);
                    menu.setProject(fileHandle, false);
                    listState.addToList(fileHandle);
                    gsm.push(menu);
                }

                @Override
                public void canceled() {

                }

            }, "Enter new project's name", "", "");

        }

        if(openButton.isPress()) {
            openButton.notPress();
            gsm.push(listState);
        }

        if(learnButton.isPress()) {
            learnButton.notPress();
            gsm.push(learnListState);
        }

        if(backButton.isPress()) {
            backButton.notPress();
            Gdx.app.exit();
        }

        buttonPress = false;
    }

    private void input() {

        if(Gdx.input.isTouched()) {
            vector3.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(vector3);
            input.setPosition(vector3.x, vector3.y);

            checkButtons();
        } else {
            checkPressed();
            input.setPosition(0, 0);
        }

    }

    public void update(float dt) {
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

        if(!messages.isEmpty()) messages.peek().render(sb);

        createButton.render(sb);
        openButton.render(sb);
        backButton.render(sb);
        learnButton.render(sb);

        sb.end();

    }

    public void dispose() {
        bg.dispose();
        openButton.dispose();
        createButton.dispose();
        backButton.dispose();
        learnButton.dispose();

        listState.dispose();
        learnListState.dispose();
        menu.dispose();

        audio.dispose();

        wrongFileNameMessage.dispose();
    }

}
