package de.loc.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import de.loc.core.GameGrid;
import de.loc.core.LevelManager;
import de.loc.graphics.ModelComponent;

public class HeightTextureParser {

    private Vector2 PIXEL_DISTANCE;
    private Pixmap HEIGHT_TEXTURE;
    private Position HEIGHTMAP_SIZE;
    private final String backgroundPath;

    public HeightTextureParser(Vector2 sceneSize) {
        this.backgroundPath = LevelManager.getInstance().getLevel().getBackgroundPath();

        try {
            this.HEIGHT_TEXTURE = new Pixmap(Gdx.files.internal(Constants.HEIGHTMAP_PATH + this.backgroundPath));
            this.HEIGHTMAP_SIZE = new Position(this.HEIGHT_TEXTURE.getWidth(), this.HEIGHT_TEXTURE.getHeight());

            float x = sceneSize.x / (float) this.HEIGHTMAP_SIZE.x;
            float y = sceneSize.y / (float) this.HEIGHTMAP_SIZE.y;
            this.PIXEL_DISTANCE = new Vector2(x, y);

            if ( this.HEIGHT_TEXTURE.getFormat() != Pixmap.Format.Intensity ) {
                System.out.println("The specified Height-Texture is not a Grayscale-image!");
            }
        } catch ( Exception e ) {
            this.HEIGHT_TEXTURE = null;
            System.out.println("NO HEIGHT_TEXTURE FOUND!");
        }

    }

    public String currentTexture() {
        return this.backgroundPath;
    }

    public void setSceneSize(Vector2 sceneSize) {
        if ( this.HEIGHT_TEXTURE != null ) {
            float x = sceneSize.x / (float) this.HEIGHTMAP_SIZE.x;
            float y = sceneSize.y / (float) this.HEIGHTMAP_SIZE.y;
            this.PIXEL_DISTANCE.set(x, y);
        }

    }

    public void updateGameGridHeight(GameGrid gameGrid) {

        if ( this.HEIGHT_TEXTURE == null ) {
            byte b = 0x00;
            for ( int x = 0; x < gameGrid.getSize().x; x++ ) {
                for ( int z = 0; z < gameGrid.getSize().y; z++ ) {
                    gameGrid.setHeight(x, z, b);
                }
            }
        } else {
            // Get the model and calculate the bounding box of the backgroundModel for intersection test:
            ModelInstance background = LevelManager.getInstance().getLevel().getBackgroundEntity().getComponent(ModelComponent.class).model;
            BoundingBox bb = new BoundingBox();
            background.calculateBoundingBox(bb);
            bb.mul(background.transform);

            Matrix4 BACKGROUND_INVERS = new Matrix4();
            BACKGROUND_INVERS.idt();
            BACKGROUND_INVERS.translate(-10.0f, -10.0f * DimensionHelper.getCameraHeight(Constants.CAMERA_ANGLE), -10.0f);
            BACKGROUND_INVERS.rotate(0.0f, 1.0f, 0.0f, 45.0f).rotate(1.0f, 0.0f, 0.0f, 90.0f - Constants.CAMERA_ANGLE);
            BACKGROUND_INVERS.inv();

            // set the current Background-Scene-Size to compute appropriate values
            this.setSceneSize(LevelManager.getInstance().getLevel().getBackgroundSize());

            // stores the intersection result
            Vector3 intersectionPoint = new Vector3();
            Vector2 imagePosition = new Vector2();

            // Start XZ muss im Mittelpunkt des Grid sein, nicht am unteren Rand.
            Vector3 startXZ = new Vector3(
                -(((float) gameGrid.getSize().x * Constants.GRID_WIDTH) / 2.0f) + Constants.GRID_WIDTH_HALF,
                0.0f,
                -(((float) gameGrid.getSize().y * Constants.GRID_WIDTH) / 2.0f) + Constants.GRID_WIDTH_HALF);

            for ( int x = 0; x < gameGrid.getSize().x; x++ ) {
                for ( int z = 0; z < gameGrid.getSize().y; z++ ) {

                    Vector3 origin = LevelManager.getInstance().getLevel().getWorldFromPos(new Position(x, z));
                    Vector3 direction = new Vector3(-1.0f, -DimensionHelper.getCameraHeight(Constants.CAMERA_ANGLE), -1.0f);
                    Ray ray = new Ray(origin, direction);

                    boolean intersection = Intersector.intersectRayBounds(ray, bb, intersectionPoint);

                    if ( intersection ) {
                        intersectionPoint.mul(BACKGROUND_INVERS);

                        imagePosition.set(
                            intersectionPoint.x + (LevelManager.getInstance().getLevel().getBackgroundSize().x / 2.0f),
                            intersectionPoint.z + (LevelManager.getInstance().getLevel().getBackgroundSize().y / 2.0f));

                        int height = this.getHeightAt(imagePosition);
                        height += 255;
                        height /= Constants.GRID_HEIGHT_RANGE;
                        byte b = (byte) ((int) (byte) height & (int) GameGrid.Type.HEIGHT_MASK);
                        //System.out.println("Byte b from HeightParser: " + b);
                        gameGrid.setHeight(x, z, b);

                    } else {
                        System.out.println("No intersection.");
                        gameGrid.setHeight(x, z, (byte) 0);
                    }

                }
            }
            //gameGrid.printHeightGrid();
        }

    }

    public void dispose() {
        if ( this.HEIGHT_TEXTURE != null ) {
            this.HEIGHT_TEXTURE.dispose();
        }
    }

    public boolean isDisposable() {
        return (this.HEIGHT_TEXTURE != null);
    }

    public int getAverageHeight(Vector2 imagePosition) {
        Position pixelPerGridWidth = new Position((int) (Constants.GRID_WIDTH / this.PIXEL_DISTANCE.x), (int) (Constants.GRID_WIDTH / this.PIXEL_DISTANCE.y));

        // center pixel of grid postion
        int centerX = (int) (imagePosition.x / this.PIXEL_DISTANCE.x);
        int centerY = (int) (imagePosition.y / this.PIXEL_DISTANCE.y);

        int startX = centerX - (pixelPerGridWidth.x / 4);
        int startY = centerY - (pixelPerGridWidth.y / 4);

        int endX = centerX + (pixelPerGridWidth.x / 4);
        int endY = centerY + (pixelPerGridWidth.y / 4);

        int heightInfo = 0;

        for ( int x = startX; x < endX; x++ ) {
            for ( int y = startY; y < endY; y++ ) {
                heightInfo += this.HEIGHT_TEXTURE.getPixel(x, y);
            }
        }
        int numberOfPixels = (endX - startX) * (endY - startY);
        int average = heightInfo / numberOfPixels;

        return average;
    }

    private int getHeightAt(Vector2 imagePosition) {
        if ( this.HEIGHT_TEXTURE != null ) {
            //			int x = (int) (imagePosition.x / PIXEL_DISTANCE.x);
            //			int y = (int) (imagePosition.y / PIXEL_DISTANCE.y);
            //	
            //			int a = HEIGHT_TEXTURE.getPixel(x, y);

            return this.getAverageHeight(imagePosition);
        } else {
            return 0;
        }
    }

}

