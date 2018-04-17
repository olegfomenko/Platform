package com.platform.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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
import com.platform.Buttons.DeleteButton;
import com.platform.Buttons.GoButton;
import com.platform.Buttons.OkButton;
import com.platform.Buttons.RedDeleteButton;
import com.platform.Camera;
import com.platform.GameStateManager;
import com.platform.Parametr.Parametr;
import com.platform.Project;

import java.io.File;
import java.io.FilenameFilter;

import static java.lang.Math.*;

public class ListState extends State {

    public boolean buttonPress;

    public Rectangle input;
    public Vector3 vector3;

    public Audio audio;

    public String path;
    public Menu menu;

    public BackButton backButton;

    public float scroll;
    public float last_y;
    public float a;
    public boolean pressed;

    public Array<Project> list;
    public float width, height;

    public BitmapFont bf;

    public Texture square, donesquare;

    public class Filter implements FilenameFilter {

        private String ext;

        public Filter(String ext) {
            this.ext = ext;
        }

        @Override
        public boolean accept(File file, String s) {
            return s.toLowerCase().endsWith(ext);
        }
    }


    public ListState(GameStateManager gsm, String path, Menu menu) {
        super(gsm);
        this.path = path;
        this.menu = menu;

        bg = new Texture("white.png");
        camera = new Camera(400, 720, bg);

        buttonPress = false;

        input = new Rectangle(0, 0, 1, 1);
        vector3 = new Vector3();

        audio = new Audio();

        backButton = new BackButton();
        backButton.seteSize(64);
        backButton.reposition(camera.position.x - camera.viewportWidth / 2 + 10, camera.position.y - camera.viewportHeight / 2 + 10);


        pressed = false;
        a = 100;

        width = 350;
        height = 50;

        list = new Array<Project>();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("tahoma.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) (height / 2);
        bf = generator.generateFont(parameter);
        bf.setColor(Color.WHITE);
        generator.dispose();

        square = new Texture("square.png");
        donesquare = new Texture("donesquare.png");
    }

    public void setList() {

        for(Project p : list) p.dispose();
        list.clear();

        File files[] = new File(path).listFiles(new Filter(".txt"));

        if(files.length > 0)
            list.add(new Project(new FileHandle(files[0]), camera.position.x - width / 2,
                    camera.position.y + camera.viewportHeight / 2 - 10 - height, width, height));

        Project p;
        for(int i = 1; i < files.length; ++i) {
            p = list.get(i - 1);
            list.add(new Project(new FileHandle(files[i]), p.x, p.y - 10 - height, width, height));
        }
    }

    public void addToList(FileHandle fileHandle) {
        if(list.size == 0)
            list.add(new Project(fileHandle, camera.position.x - width / 2,
                    camera.position.y + camera.viewportHeight / 2 - 10 - height, width, height));
        else {
            Project p = list.peek();
            list.add(new Project(fileHandle, p.x, p.y - 10 - height, width, height));
        }
    }

    public void checkButtons() {
        if(input.overlaps(backButton.getRectangle())) {
            backButton.press();
            buttonPress = true;
            return;
        } else backButton.notPress();

        for(Project p : list) {
            RedDeleteButton redDeleteButton = p.getDeleteButton();
            if(input.overlaps(redDeleteButton.getRectangle())) {
                redDeleteButton.press();
                buttonPress = true;
                return;
            } else  redDeleteButton.notPress();

            GoButton goButton = p.getGoButton();
            if(input.overlaps(goButton.getRectangle())) {
                goButton.press();
                buttonPress = true;
                return;
            } else goButton.notPress();
        }

        buttonPress = false;
    }

    public void checkPressed() {

        if(!buttonPress) return;

        audio.playTick();

        if(backButton.isPress()) {
            backButton.notPress();
            gsm.pop();
        }

        for(int i = 0; i < list.size; ++i) {
            GoButton goButton = list.get(i).getGoButton();
            if(goButton.isPress()) {
                goButton.notPress();

                menu.setProject(list.get(i).getFileHandle(), false);
                gsm.push(menu);
            }


            RedDeleteButton redDeleteButton = list.get(i).getDeleteButton();
            if(redDeleteButton.isPress()) {
                redDeleteButton.notPress();

                FileHandle fileHandle = list.get(i).getFileHandle();
                fileHandle.delete();

                list.get(i).dispose();
                list.removeIndex(i);

                if(i == 0 && i < list.size)
                    list.get(0).reposition(camera.position.x - width / 2, camera.position.y + camera.viewportHeight / 2 - 10 - height);
                for(int j = max(i, 1); j < list.size; ++j) {
                    Project p = list.get(j - 1);
                    list.get(j).reposition(p.x, p.y - 10 - height);
                }
            }
        }

        buttonPress = false;
    }

    public void input(float dt) {

        if(Gdx.input.isTouched()) {
            vector3.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(vector3);
            input.setPosition(vector3.x, vector3.y);

            checkButtons();

            if(!buttonPress && pressed && list.size != 0) {
                scroll = last_y - input.y;
                float mx = camera.position.y + camera.viewportHeight / 2 - height - 10;
                float mn = max(mx - (10 + height) * (list.size - 1), camera.position.y - camera.viewportHeight / 2 + 10);
                scroll = min(scroll, list.get(0).y - mx);
                scroll = max(scroll, list.peek().y - mn);

                for(Project p : list)
                    p.reposition(p.x, p.y - scroll);
            }

            pressed = true;
            last_y = input.y;
        } else {
            pressed = false;
            checkPressed();

            if(scroll > 0) scroll = max(0, scroll - a * dt);
            if(scroll < 0) scroll = min(0, scroll + a * dt);

            if(scroll != 0) {

                float mx = camera.position.y + camera.viewportHeight / 2 - height - 10;
                float mn = max(mx - (10 + height) * (list.size - 1), camera.position.y - camera.viewportHeight / 2 + 10);
                scroll = min(scroll, list.get(0).y - mx);
                scroll = max(scroll, list.peek().y - mn);

                for(Project p : list)
                    p.reposition(p.x, p.y - scroll);

            }
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

        for(Project p : list) {
            if(p.done) sb.draw(donesquare, p.x, p.y, width, height);
            else sb.draw(square, p.x, p.y, width, height);
            p.render(sb, bf);
        }

        if(!messages.isEmpty()) messages.peek().render(sb);
        backButton.render(sb);

        sb.end();
    }

    public void dispose() {
        backButton.dispose();
        bg.dispose();
        square.dispose();
        donesquare.dispose();
        bf.dispose();
        for(Project p : list) p.dispose();

        audio.dispose();
    }
}
