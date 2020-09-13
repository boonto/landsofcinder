package de.loc.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.Ray;

import de.loc.core.LandsOfCinderScreen;
import de.loc.graphics.CameraManager;
import de.loc.input.userinterface.BaseScreen;
import de.loc.tools.Position;

public abstract class InputHandler implements InputProcessor, GestureDetector.GestureListener {

    protected Vector2 lastDragPos;
    private final InputMultiplexer inputMultiplexer;

    public IInput inputSystem;
    public BaseScreen screen;

    public InputHandler() {
        this.inputMultiplexer = new InputMultiplexer();

        Gdx.input.setInputProcessor(this.inputMultiplexer);
    }

    public InputHandler(BaseScreen screen) {
        this();
        this.screen = screen;
    }

    public InputHandler(LandsOfCinderScreen screen, IInput inputSystem) {
        this(screen);
        this.inputSystem = inputSystem;

        this.lastDragPos = new Vector2(0.0f, 0.0f);
    }

    public void resume() {
        Gdx.input.setInputProcessor(this.inputMultiplexer);
    }

    public abstract boolean handle(String handleID);

    public void addProcessor(InputProcessor processor) {

        this.inputMultiplexer.addProcessor(processor);
        switch ( Gdx.app.getType() ) {
            case Android:
                this.inputMultiplexer.addProcessor(new GestureDetector(this));
                break;
            case Desktop:
                this.inputMultiplexer.addProcessor(this);
                break;
            default:
                break;
        }
    }

    // Desktop
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.lastDragPos.x = (float) screenX;
        this.lastDragPos.y = (float) screenY;

        //setPosition(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        int distX = (int) (this.lastDragPos.x - (float) screenX);
        int distY = (int) (this.lastDragPos.y - (float) screenY);

        // müsste eigentlich sein: moveX = ViewportWidth * (PixelWidth / dist),
        // und für moveY das gleiche.

        CameraManager.moveCamera(new Vector2((float) distY / 1100.0f, (float) distX / 1100.0f));

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        float scaleFactor = 1.0f;
        if ( amount == 1 ) {
            scaleFactor = 1.1f;
        } else if ( amount == -1 ) {
            scaleFactor = 0.9f;
        }

        CameraManager.scaleCamera(scaleFactor);

        return true;
    }

    // Android
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        this.lastDragPos.x = x;
        this.lastDragPos.y = y;

        this.setPosition((int) x, (int) y);
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        CameraManager.moveCamera(new Vector2(-deltaY / 250.0f, -deltaX / 250.0f));
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if ( distance > initialDistance ) {
            CameraManager.scaleCamera(0.98f);
        } else if ( distance < initialDistance ) {
            CameraManager.scaleCamera(1.02f);
        }
        return false;
    }

    @Override
    public boolean pinch(
        Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    private void setPosition(int screenX, int screenY) {
        Ray pickRay = CameraManager.getPickRay(screenX, screenY);
        Position position = CameraManager.getGridFromScreenCoords(pickRay);
        this.inputSystem.clicked(pickRay, position);
    }

    public void pinchStop() {
    }
}
