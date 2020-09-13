package de.loc.movement.steering;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.Face;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.proximities.RadiusProximity;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.EnumMap;
import java.util.Map;

import de.loc.graphics.RotationComponent;
import de.loc.tools.Position;

public class SteeringAgent extends LocLimImp implements Steerable<Vector2> {
    private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());

    private final Vector2 linearVelocity;
    private float angularVelocity;
    private float boundingRadius;
    private boolean tagged;

    private boolean independentFacing;

    private LocLimImp target;
    private LinePath<Vector2> linePath;
    private RadiusProximity<Vector2> proximity;

    private Map<Steering, SteeringBehavior<Vector2>> steeringBehaviors;
    private PrioritySteering<Vector2> prioritySteering;

    public SteeringAgent(Position position, SteeringSetup steeringSetup) {
        this.linearVelocity = new Vector2();
        this.angularVelocity = 0.0f;
        this.tagged = false;

        this.setup(position, steeringSetup);
    }

    private void setup(Position position, SteeringSetup steeringSetup) {
        this.independentFacing = steeringSetup.independentFacing;
        this.setZeroLinearSpeedThreshold(steeringSetup.zeroLinearSpeedThreshold);
        this.setMaxLinearSpeed(steeringSetup.maxLinearSpeed);
        this.setMaxLinearAcceleration(steeringSetup.maxLinearAcceleration);
        this.setMaxAngularSpeed(steeringSetup.maxAngularSpeed);
        this.setMaxAngularAcceleration(steeringSetup.maxAngularAcceleration);
        this.boundingRadius = steeringSetup.boundingRadius;

        //ARRIVE & FACE
        this.target = new LocLimImp((float) position.x, (float) position.y);

        //FOLLOWPATH sehr sehr hässlich aber funktioniert
        Array<Vector2> tmpWaypoints = new Array<Vector2>(2);
        tmpWaypoints.add(new Vector2((float) position.x, (float) position.y));
        tmpWaypoints.add(new Vector2((float) position.x, (float) position.y).add(0.001f, 0.001f));
        this.linePath = new LinePath<Vector2>(tmpWaypoints, true);

        //COLLISIONAVOIDANCE
        this.proximity = new RadiusProximity<Vector2>(this, null, this.boundingRadius);

        this.setupSteeringBehaviors(steeringSetup);
    }

    private void setupSteeringBehaviors(SteeringSetup steeringSetup) {
        this.steeringBehaviors = new EnumMap<Steering, SteeringBehavior<Vector2>>(Steering.class);
        this.prioritySteering = new PrioritySteering<Vector2>(this);

        CollisionAvoidance<Vector2> collisionAvoidance = new CollisionAvoidance<Vector2>(this, this.proximity);
        this.steeringBehaviors.put(Steering.COLLISION_AVOIDANCE, collisionAvoidance);
        this.prioritySteering.add(collisionAvoidance);

        BlendedSteering<Vector2> movement = new BlendedSteering<Vector2>(this);
        this.prioritySteering.add(movement);

        this.steeringBehaviors.put(
            Steering.FOLLOW_PATH,
            new FollowPath<Vector2, LinePath.LinePathParam>(this, this.linePath).setPathOffset(steeringSetup.pathOffset)
                                                                                .setPredictionTime(steeringSetup.predictionTime)
                                                                                .setDecelerationRadius(steeringSetup.decelerationRadiusL)
                                                                                .setArrivalTolerance(steeringSetup.arrivalTolerance));

        this.steeringBehaviors.put(
            Steering.ARRIVE,
            new Arrive<Vector2>(this, this.target).setDecelerationRadius(steeringSetup.decelerationRadiusL)
                                                  .setArrivalTolerance(steeringSetup.arrivalTolerance));

        this.steeringBehaviors.put(
            Steering.FACE,
            new Face<Vector2>(this, this.target).setTimeToTarget(steeringSetup.timeToTarget)
                                                .setAlignTolerance(steeringSetup.alignTolerance)
                                                .setDecelerationRadius(steeringSetup.decelerationRadiusR));

        this.steeringBehaviors.put(
            Steering.LOOK_WHERE_YOU_ARE_GOING,
            new LookWhereYouAreGoing<Vector2>(this).setTimeToTarget(steeringSetup.timeToTarget)
                                                   .setAlignTolerance(steeringSetup.alignTolerance)
                                                   .setDecelerationRadius(steeringSetup.decelerationRadiusR));

        for ( Map.Entry<Steering, SteeringBehavior<Vector2>> entry : this.steeringBehaviors.entrySet() ) {
            if ( entry.getKey() == Steering.FACE ) {
                movement.add(entry.getValue(), 0.8f);
            } else {
                movement.add(entry.getValue(), 1.0f);
            }
        }

        this.enableSteeringBehaviors(steeringSetup.steerings);
    }

    public void enableSteeringBehavior(Steering steering) {
        this.steeringBehaviors.get(steering).setEnabled(true);
    }

    public void disableSteeringBehavior(Steering steering) {
        this.steeringBehaviors.get(steering).setEnabled(false);
    }

    //    public void enableSteeringBehaviors(Steering... steerings) {
    //        ObjectSet<Steering> steeringSet = new ObjectSet<Steering>(steerings.length);
    //
    //        for (Steering s : steerings) {
    //            steeringSet.add(s);
    //        }
    //
    //        enableSteeringBehaviors(steeringSet);
    //    }

    private void enableSteeringBehaviors(Iterable<Steering> steerings) {
        this.disableAllSteeringBehaviors();

        for ( Steering steering : steerings ) {
            this.steeringBehaviors.get(steering).setEnabled(true);
        }
    }

    private void disableAllSteeringBehaviors() {
        for ( SteeringBehavior<Vector2> steeringBehavior : this.steeringBehaviors.values() ) {
            steeringBehavior.setEnabled(false);
        }
    }

    public boolean isEnabled(Steering steering) {
        return this.steeringBehaviors.get(steering).isEnabled();
    }

    public void setProximityAgents(Array<Steerable<Vector2>> agents) {
        this.proximity.setAgents(agents);
    }

    public void setTarget(Vector2 target) {
        this.target.getPosition().set(target);
    }

    public void setPath(Array<Vector2> waypoints) {
        if ( waypoints.size > 1 ) { // damits beim debuggen nicht abstürzt
            this.linePath.createPath(waypoints);
        }
    }

    public void update(float deltaTime, Position position, Vector2 positionF, RotationComponent rotationComponent) {
        this.getPosition().set(positionF);
        this.setOrientation(rotationComponent.angle * MathUtils.degreesToRadians);

        if ( this.prioritySteering != null ) {
            this.prioritySteering.calculateSteering(steeringOutput);
            this.applySteering(steeringOutput, deltaTime, position, positionF, rotationComponent);
        }
    }

    private void applySteering(
        SteeringAcceleration<Vector2> steering, float deltaTime, Position position, Vector2 positionF, RotationComponent rotationComponent) {
        this.getPosition().mulAdd(this.linearVelocity, deltaTime);
        this.linearVelocity.mulAdd(steering.linear, deltaTime).limit(this.getMaxLinearSpeed());

        this.setOrientation(this.getOrientation() + (this.angularVelocity * deltaTime));
        this.angularVelocity += steering.angular * deltaTime;

        position.x = MathUtils.round(this.getPosition().x);
        position.y = MathUtils.round(this.getPosition().y);
        positionF.set(this.getPosition());

        if ( this.independentFacing ) {
            if ( steering.angular == 0.0f ) {
                this.angularVelocity = 0.0f;
            }

            rotationComponent.angle = this.getOrientation() * MathUtils.radiansToDegrees;
        } else {
            if ( !this.linearVelocity.isZero(this.getZeroLinearSpeedThreshold()) ) {
                rotationComponent.angle = this.vectorToAngle(this.linearVelocity) * MathUtils.radiansToDegrees;
            }
        }
    }

    @Override
    public Vector2 getLinearVelocity() {
        return this.linearVelocity;
    }

    @Override
    public float getAngularVelocity() {
        return this.angularVelocity;
    }

    @Override
    public float getBoundingRadius() {
        return this.boundingRadius;
    }

    @Override
    public boolean isTagged() {
        return this.tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    public enum Steering {
        FOLLOW_PATH,
        ARRIVE,
        FACE,
        COLLISION_AVOIDANCE,
        LOOK_WHERE_YOU_ARE_GOING
    }
}