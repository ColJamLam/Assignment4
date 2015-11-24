package com.badlogic.gdx;



import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MainMenuScreen implements Screen {
    final DonutRoll game;
    OrthographicCamera camera;

    public MainMenuScreen(final DonutRoll gam) {
        game = gam;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 900, 700);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Donut get hit by the Hands Beta 1.0" +
                "\n\nUse Direction Keys to move:" +
                "\nLEFT/RIGHT move respectively" +
                "\nUP to Jump, can double jump" +
                "\nHOLD DOWN to stop movement" +
                "\nPress 'P' to pause/unpause" +
                "\n\nObjective: Dodge the Hands, save the Munckins!" +
                "\ncurrent update has no munchkins or collisions with hands" +
                "\n\nTap anywhere to begin!", 200, 450);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen((Screen) new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}