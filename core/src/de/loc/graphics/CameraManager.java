package de.loc.graphics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import de.loc.core.LevelManager;
import de.loc.tools.Constants;
import de.loc.tools.DimensionHelper;
import de.loc.tools.Position;

public final class CameraManager {

    private static final IsoCamera camera = new IsoCamera();

    private CameraManager() {
    }

    public static void scaleCamera(float scale) {
        camera.scaleViewport(scale);
    }

    public static void moveCamera(Vector2 move) {
        Vector2 translate = new Vector2(move.x, -move.y);

        translate.rotate(45.0f);
        translate.scl(camera.zoom);

        camera.translate(translate.x, 0.0f, translate.y);
        camera.update();
    }

    public static void setCameraPosition(Vector2 move) {
        camera.position.set(move.x + 15.0f, DimensionHelper.getCameraHeight(Constants.CAMERA_DEGREE) * 15.0f, move.y + 15.0f); //mindestens 15m abstand
        camera.lookAt(move.x, 0.0f, move.y);

        camera.update();
    }

    public static Ray getPickRay(int screenX, int screenY) {
        return camera.getPickRay((float) screenX, (float) screenY);
    }

    public static Vector3 getWorldFromScreen(Ray pickRay) {
        float distance = -pickRay.origin.y / pickRay.direction.y;
        Vector3 coords = new Vector3();
        coords.set(pickRay.direction).scl(distance).add(pickRay.origin);
        return coords;
    }

    public static Position getGridFromScreenCoords(int x, int y) {
        return getGridFromScreenCoords(getPickRay(x, y));
    }

    public static Position getGridFromScreenCoords(Ray pickRay) {

        Vector3 worldCoords = getWorldFromScreen(pickRay);

        float gridWidth = (Constants.GRID_WIDTH * (float) LevelManager.getInstance().getLevel().getGameGrid().getSize().x) / 2.0f;
        float gridHeight = (Constants.GRID_WIDTH * (float) LevelManager.getInstance().getLevel().getGameGrid().getSize().y) / 2.0f;

        // außerhalb des Grid geklickt?
        if ( (worldCoords.x > gridWidth) || (worldCoords.x < -gridWidth) || (worldCoords.z > gridHeight) || (worldCoords.z < -gridHeight) ) {
            return null;
        }

        worldCoords.x += gridWidth;
        worldCoords.z += gridHeight;
        int x = (int) (worldCoords.x / Constants.GRID_WIDTH);
        int z = (int) (worldCoords.z / Constants.GRID_WIDTH);

        return new Position(x, z);
    }

    public static Position getScreenCoordsFromGrid(Position position) {
        Vector3 worldPos = LevelManager.getInstance().getLevel().getWorldFromPos(position);

        Vector3 screenPos = camera.project(worldPos);

        return new Position(MathUtils.round(screenPos.x), MathUtils.round(screenPos.y));
    }

    public static IsoCamera getCamera() {
        return camera;
    }
}
