package com.platform;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.platform.States.StartState;

import java.io.File;

public class Main extends ApplicationAdapter {
	private SpriteBatch sb;
	private GameStateManager gsm;

	public String storage, directory, learndirectory;


	@Override
	public void create() {
		storage = Gdx.files.getExternalStoragePath();
		directory = "Platform";
		learndirectory = "Learn";


		File dir = new File(storage + directory);
		dir.mkdir();

		File learndir = new File(storage + directory + "/" + learndirectory);
		if(!learndir.exists()) {
			learndir.mkdir();
			FileHandle ld = new FileHandle(learndir);

			addTask("Task 1", ld);
			addTask("Task 2", ld);
			addTask("Task 3", ld);
			addTask("Task 4", ld);
		}

		sb = new SpriteBatch();
		gsm = new GameStateManager();
		gsm.push(new StartState(gsm, storage + directory));

		Gdx.input.setCatchBackKey(true);
	}

	private void addTask(String name, FileHandle dir) {
		FileHandle f = Gdx.files.internal(name + ".txt");
		f.copyTo(dir);

		f = Gdx.files.internal(name + "_learn.txt");
		f.copyTo(dir);

		f = Gdx.files.internal(name + "_text.png");
		f.copyTo(dir);
	}

	@Override
	public void render() {
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(sb);
	}

	@Override
	public void dispose() {
		gsm.dispose();
		sb.dispose();
	}
}
