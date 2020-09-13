package de.loc.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;

import de.loc.tools.Constants;
import de.loc.tools.DimensionHelper;

public class IsoCamera extends OrthographicCamera {

    //private float ratio;
    public float viewportHeight;
    public float viewportWidth;

    public IsoCamera() {
        super(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);

        float cameraHeight = DimensionHelper.getCameraHeight(Constants.CAMERA_DEGREE);
        this.position.set(15.0f, cameraHeight * 15.0f, 15.0f); //mindestens 15 meter abstand
        this.lookAt(0.0f, 0.0f, 0.0f);
        this.near = 1.0f;
        this.far = 80.0f;
        this.update();
        this.viewportHeight = Constants.VIEWPORT_HEIGHT;
        this.viewportWidth = Constants.VIEWPORT_WIDTH;
        //ratio = viewportWidth / viewportHeight;
        //this.cameraHeight = cameraHeight;

    }

    public void setDegree(float degree) {
        float cameraHeight = DimensionHelper.getCameraHeight(degree);
        this.position.set(1.0f, cameraHeight, 1.0f);
        this.lookAt(0.0f, 0.0f, 0.0f);
        this.update();
    }

    public void scaleViewport(float scaleFactor) {

        //this.viewportHeight = (1 / scaleFactor) * viewportHeight;
        //this.viewportWidth = ratio * viewportHeight;
        this.zoom *= scaleFactor;
        this.update();
    }
}