package de.loc.movement.steering;

import com.badlogic.gdx.ai.steer.Limiter;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class LocLimImp implements Location<Vector2>, Limiter {

    private final Vector2 position;
    private float orientation;

    private float zeroLinearSpeedThreshold;
    private float maxLinearSpeed;
    private float maxLinearAcceleration;
    private float maxAngularSpeed;
    private float maxAngularAcceleration;

    public LocLimImp() {
        this(new Vector2());
    }

    public LocLimImp(float x, float y) {
        this(new Vector2(x, y));
    }

    public LocLimImp(Vector2 position) {
        this.position = new Vector2(position);
        this.orientation = 0.0f;

        this.zeroLinearSpeedThreshold = 0.01f;
        this.maxLinearSpeed = 10.0f;
        this.maxLinearAcceleration = 1.0f;
        this.maxAngularSpeed = 3.0f;
        this.maxAngularAcceleration = 1.0f;
    }

    @Override
    public Vector2 getPosition() {
        return this.position;
    }

    @Override
    public float getOrientation() {
        return this.orientation;
    }

    @Override
    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return MathUtils.atan2(vector.y, -vector.x);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = -MathUtils.sin(angle);
        outVector.y = MathUtils.cos(angle);
        return outVector;
    }

    @Override
    public Location<Vector2> newLocation() {
        return new LocLimImp();
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return this.zeroLinearSpeedThreshold;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        this.zeroLinearSpeedThreshold = value;
    }

    @Override
    public float getMaxLinearSpeed() {
        return this.maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return this.maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return this.maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return this.maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }
}
