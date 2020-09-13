package de.loc.input.userinterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.loc.input.MenuInputHandler;
import de.loc.main.LandsOfCinder;

public class MenuScreen extends BaseScreen {

    private Sprite sprite;
    private Texture spriteTexture;
    private SpriteBatch spriteBatch;

    public MenuScreen(LandsOfCinder game) {
        super(game);

        this.setupScrollingBackground();

        this.inputHandler = new MenuInputHandler(this);
        this.ui = new UserInterface(this.inputHandler);
        this.ui.setScreenSize(30, Color.BLACK);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.renderScrollingBackground(delta);

        super.render(delta);
    }

    private void setupScrollingBackground() {
        this.spriteBatch = new SpriteBatch();
        this.spriteTexture = new Texture(Gdx.files.internal("ui/mainmenubackground_bridge_5000x1100.png"));
        this.spriteTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        this.sprite = new Sprite(this.spriteTexture, 0, 0, this.spriteTexture.getWidth(), this.spriteTexture.getHeight());
    }

    private void renderScrollingBackground(float delta) {
        this.sprite.scroll(delta * 0.01f, 0);
        this.spriteBatch.begin();
        this.sprite.draw(this.spriteBatch);
        this.spriteBatch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        this.spriteTexture.dispose();
        this.spriteBatch.dispose();
    }
}
