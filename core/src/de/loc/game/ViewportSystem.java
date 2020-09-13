package de.loc.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.loc.core.LandsOfCinderSystem;
import de.loc.core.LevelManager;
import de.loc.core.PositionComponent;
import de.loc.graphics.CameraManager;
import de.loc.tools.Constants;

public class ViewportSystem extends LandsOfCinderSystem {

    private Entity playerEntity;
    private final ComponentMapper<PositionComponent> positionMapper;

    private Vector3 upRight;
    private Vector3 downLeft;

    private Matrix4 worldToScreen;
    private Matrix4 screenToWorld;

    private Vector3 lastPos;

    public ViewportSystem() {
        this.positionMapper = ComponentMapper.getFor(PositionComponent.class);
    }

    public void addedToEngine(Engine engine) {
        this.playerEntity = LevelManager.getInstance().getPlayerEntity();
        this.upRight = new Vector3();
        this.downLeft = new Vector3();
    }

    public void update(float deltaTime) {
        PositionComponent pos = this.positionMapper.get(this.playerEntity);

        Vector3 playerPos = LevelManager.getInstance().getLevel().getWorldFromPosF(pos.positionF);
        //System.out.println("SUPER_TEST_LAST_POS:__" + lastPos);
        Vector3 moveWorld = LevelManager.getInstance().getLevel().getWorldFromPosF(pos.positionF).sub(this.lastPos);
        //System.out.println("MOVE WORLD:__" + moveWorld);
        Vector3 moveScreen = new Vector3(moveWorld).mul(this.worldToScreen);
        //System.out.println("MOVE SCREEN:_" + moveScreen);

        float width = (float) Gdx.graphics.getWidth();
        float height = (float) Gdx.graphics.getHeight();
        Vector3 playerScreen = CameraManager.getCamera().project(new Vector3(playerPos));
        Vector3 borderScreen = CameraManager.getCamera().project(new Vector3(this.downLeft));
        Vector3 borderScreen2 = CameraManager.getCamera().project(new Vector3(this.upRight));

        if ( (playerScreen.x - width / 2.0f) < borderScreen.x ) {
            moveScreen.z = 0.0f;
        }
        if ( playerScreen.y - height / 2.0f < borderScreen.y ) {
            moveScreen.x = 0.0f;
        }
        if ( (playerScreen.x + width / 2.0f) > borderScreen2.x ) {
            moveScreen.z = 0.0f;
        }
        if ( playerScreen.y + height / 2.0f > borderScreen2.y ) {
            moveScreen.x = 0.0f;
        }

        Vector3 newWorldMove = new Vector3(moveScreen).mul(this.screenToWorld);
        //System.out.println("NEW MOVE WORLD:_" + newWorldMove);

        this.lastPos.add(newWorldMove);
        //System.out.println("NEW MOVE WORLD PLUS LAST POS:_" + lastPos);
        CameraManager.setCameraPosition(new Vector2(this.lastPos.x, this.lastPos.z));

    }

    @Override
    public void reset() {
        this.playerEntity = LevelManager.getInstance().getPlayerEntity();

        Vector2 backgroundSize = LevelManager.getInstance().getLevel().getBackgroundSize();
        this.downLeft.set((-backgroundSize.x / 2.0f), 0.0f, (backgroundSize.y / 2.0f));
        this.upRight.set((backgroundSize.x / 2.0f), 0.0f, (-backgroundSize.y / 2.0f));

        this.worldToScreen = new Matrix4();
        this.worldToScreen.idt();
        this.worldToScreen.rotate(0.0f, 1.0f, 0.0f, 45.0f); //.rotate(1, 0, 0, 90 - Constants.CAMERA_ANGLE);
        this.screenToWorld = new Matrix4(this.worldToScreen).inv();

        Matrix4 transform = new Matrix4();
        transform.idt();
        transform.rotate(0.0f, 1.0f, 0.0f, 45.0f).rotate(1.0f, 0.0f, 0.0f, 90.0f - Constants.CAMERA_ANGLE);

        this.downLeft.mul(transform);
        this.upRight.mul(transform);

        Gdx.app.log("VIEWPORT", "Oben rechts: " + this.upRight);
        Gdx.app.log("VIEWPORT", "Unten links: " + this.downLeft);

        this.lastPos = new Vector3();

        PositionComponent pos = this.positionMapper.get(this.playerEntity);

        Vector3 playerPos = LevelManager.getInstance().getLevel().getWorldFromPosF(pos.positionF);
        CameraManager.setCameraPosition(new Vector2(0.0f, 0.0f));
        float width = (float) Gdx.graphics.getWidth();
        float height = (float) Gdx.graphics.getHeight();
        Vector3 playerScreen = CameraManager.getCamera().project(new Vector3(playerPos));
        Vector3 borderScreen = CameraManager.getCamera().project(new Vector3(this.downLeft));
        Vector3 borderScreen2 = CameraManager.getCamera().project(new Vector3(this.upRight));

        if ( (playerScreen.x - width / 2.0f) < borderScreen.x ) {
            playerScreen.x = borderScreen.x + width / 2.0f;
        } else if ( (playerScreen.x + width / 2.0f) > borderScreen2.x ) {
            playerScreen.x = borderScreen2.x - width / 2.0f;
        }
        if ( playerScreen.y - height / 2.0f < borderScreen.y ) {
            playerScreen.y = borderScreen2.y - height / 2.0f;
        } else if ( playerScreen.y + height / 2.0f > borderScreen2.y ) {
            playerScreen.y = borderScreen.y + height / 2.0f;
        }

        this.lastPos.set(CameraManager.getWorldFromScreen(CameraManager.getPickRay((int) playerScreen.x, (int) playerScreen.y)));
        //lastPos = LevelManager.getInstance().getLevel().getWorldFromPosF(pos.positionF);
        CameraManager.setCameraPosition(new Vector2(this.lastPos.x, this.lastPos.z));
    }
}
