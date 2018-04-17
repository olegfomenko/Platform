package com.platform.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.platform.Buttons.GoButton;
import com.platform.Buttons.ListButton;
import com.platform.Buttons.RedDeleteButton;
import com.platform.GameStateManager;
import com.platform.LearnProject;
import com.platform.Project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class LearnListState extends ListState {

    private FileHandle f, cf;
    private boolean done;
    private Array<Texture> texts;
    private TextState textState;

    public class BigFilter implements FilenameFilter {

        private String ext;
        private final String learn = "_learn";

        public BigFilter(String ext) {
            this.ext = ext;
        }

        @Override
        public boolean accept(File file, String s) {
            return (s.toLowerCase().endsWith(ext)) && (!s.toLowerCase().endsWith(learn + ext));
        }
    }

    public LearnListState(GameStateManager gsm, String path, Menu menu) {
        super(gsm, path, menu);
        texts = new Array<Texture>();
        textState = new TextState(gsm);
    }

    public void setList() {

        for(Project p : list) p.dispose();
        list.clear();

        for(Texture t : texts) t.dispose();
        texts.clear();

        File files[] = new File(path).listFiles(new BigFilter(".txt"));


        if(files.length > 0) {
            f = new FileHandle(files[0]);
            cf = new FileHandle(f.pathWithoutExtension() + "_learn.txt");
            BufferedReader bufferedReader = new BufferedReader(cf.reader());

            try {
                done = Boolean.valueOf(bufferedReader.readLine());
                bufferedReader.close();
            }
            catch(Exception e) {
                done = false;
            }

            list.add(new LearnProject(f, camera.position.x - width / 2, camera.position.y + camera.viewportHeight / 2 - 10 - height,
                    width, height, done));
            texts.add(new Texture(new FileHandle(f.pathWithoutExtension() + "_text.png")));
        }

        Project p;
        for(int i = 1; i < files.length; ++i) {
            f = new FileHandle(files[i]);
            cf = new FileHandle(f.pathWithoutExtension() + "_learn.txt");
            BufferedReader bufferedReader = new BufferedReader(cf.reader());

            try {
                done = Boolean.valueOf(bufferedReader.readLine());
                bufferedReader.close();
            }
            catch(Exception e) {
                done = false;
            }

            p = list.get(i - 1);
            list.add(new LearnProject(new FileHandle(files[i]), p.x, p.y - 10 - height, width, height, done));
            texts.add(new Texture(new FileHandle(f.pathWithoutExtension() + "_text.png")));
        }

        System.out.println("SIZE = " + files.length);
    }

    public void addToList(FileHandle fileHandle) {
        cf = new FileHandle(fileHandle.nameWithoutExtension() + "_learn.txt");
        BufferedReader bufferedReader = new BufferedReader(cf.reader());

        try {
            done = Boolean.valueOf(bufferedReader.readLine());
            bufferedReader.close();
        }
        catch(Exception e) {
            done = false;
        }

        if(list.size == 0) {
            list.add(new LearnProject(fileHandle, camera.position.x - width / 2, camera.position.y + camera.viewportHeight / 2 - 10 - height,
                    width, height, done));
        } else {
            Project p = list.peek();
            list.add(new LearnProject(fileHandle, p.x, p.y - 10 - height, width, height, done));
        }
        texts.add(new Texture(new FileHandle(f.pathWithoutExtension() + "_text.png")));
    }


    int index = -1;
    public void checkAtList() {
        cf = new FileHandle(list.get(index).fileHandle.pathWithoutExtension() + "_learn.txt");
        BufferedReader bufferedReader = new BufferedReader(cf.reader());

        try {
            done = Boolean.valueOf(bufferedReader.readLine());
            bufferedReader.close();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            done = false;
        }

        list.get(index).done = done;
    }

    public void checkButtons() {
        if(input.overlaps(backButton.getRectangle())) {
            backButton.press();
            buttonPress = true;
            return;
        } else backButton.notPress();

        for(Project p : list) {
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
                menu.setProject(list.get(i).getFileHandle(), true);
                textState.set(menu, texts.get(i));
                index = i;
                gsm.push(textState);
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
        if(index != -1) {
            checkAtList();
            index = -1;
        }

        input(dt);

        if(!messages.isEmpty()) {
            messages.peek().update(dt);
            if(!messages.peek().isExist()) messages.pop();
        }
    }

    public void dispose() {
        super.dispose();
        textState.dispose();

        for(Texture t : texts) t.dispose();
        texts.clear();
    }
}
