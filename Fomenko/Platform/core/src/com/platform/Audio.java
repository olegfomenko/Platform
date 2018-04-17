package com.platform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Audio {

    private Sound tick, knock;

    public Audio() {
        tick = Gdx.audio.newSound(Gdx.files.internal("tick.ogg"));
        knock = Gdx.audio.newSound(Gdx.files.internal("knock.mp3"));
    }

    public  void playTick() {
        tick.play(1.0f);
    }

    public  void playKnock() {
        knock.play(1.0f);
    }

    public void dispose() {
        tick.dispose();
    }
}
