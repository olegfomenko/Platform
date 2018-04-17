package com.platform.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.platform.Audio;
import com.platform.Buttons.BackButton;
import com.platform.Buttons.GoButton;
import com.platform.Camera;
import com.platform.GameStateManager;

public class TextState extends State {

    private boolean buttonPress;

    private Rectangle input;
    private Vector3 vector3;

    private Audio audio;

    private Menu menu;

    private GoButton goButton;
    private BackButton backButton;

    private Texture text;

    private float width, height;

    public TextState(GameStateManager gsm) {
        super(gsm);

        bg = new Texture("white.png");
        camera = new Camera(400, 720, bg);

        buttonPress = false;
        input = new Rectangle(0, 0, 1, 1);
        vector3 = new Vector3(0, 0, 0);
        audio = new Audio();

        goButton = new GoButton();
        goButton.seteSize(64);
        goButton.reposition(camera.position.x + camera.viewportWidth / 2 - 10 - goButton.getSize(), camera.position.y - camera.viewportHeight / 2 + 10);

        backButton = new BackButton();
        backButton.seteSize(64);
        backButton.reposition(camera.position.x - camera.viewportWidth / 2 + 10, camera.position.y - camera.viewportHeight / 2 + 10);

        width = 420;
        height = 560;
    }

    public void set(Menu menu, Texture text) {
        this.menu = menu;
        this.text = text;
    }

    public void checkButtons() {
        if(input.overlaps(backButton.getRectangle())) {
            backButton.press();
            buttonPress = true;
            return;
        } else backButton.notPress();

        if(input.overlaps(goButton.getRectangle())) {
            goButton.press();
            buttonPress = true;
            return;
        } else goButton.notPress();

        buttonPress = false;
    }

    public void checkPressed() {
        if(!buttonPress) return;

        audio.playTick();

        if(backButton.isPress()) {
            backButton.notPress();
            gsm.pop();
        }

        if(goButton.isPress()) {
            goButton.notPress();
            gsm.push(menu);
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
    }

    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();

        sb.draw(bg, 0, 0);

        sb.draw(text, camera.position.x - width / 2 - 5, camera.position.y + camera.viewportHeight / 2 - 10 - height, width, height);

        goButton.render(sb);
        backButton.render(sb);

        sb.end();
    }

    public void dispose() {
        backButton.dispose();
        goButton.dispose();
        audio.dispose();
    }
}
