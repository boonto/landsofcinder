package de.loc.movement.steering;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectSet;

public class SteeringSetup {

    //Steering
    public boolean independentFacing;

    //Limiter
    public float zeroLinearSpeedThreshold;
    public float maxLinearSpeed;
    public float maxLinearAcceleration;
    public float maxAngularSpeed;
    public float maxAngularAcceleration;

    //Arrive & FollowPath
    public float decelerationRadiusL; //linear
    public float arrivalTolerance;

    //FollowPath
    public float pathOffset;
    public float predictionTime;

    //Face & LookWhereYouAreGoing
    public float alignTolerance;
    public float timeToTarget;
    public float decelerationRadiusR; //rotation

    //CollisionAvoidance
    public float boundingRadius;

    public ObjectSet<SteeringAgent.Steering> steerings;

    public SteeringSetup() {
        this(SteeringAgent.Steering.COLLISION_AVOIDANCE, SteeringAgent.Steering.FOLLOW_PATH, SteeringAgent.Steering.LOOK_WHERE_YOU_ARE_GOING);
    }

    public SteeringSetup(SteeringAgent.Steering... steerings) {
        this.independentFacing = true;

        this.zeroLinearSpeedThreshold = 0.001f;
        this.maxLinearSpeed = 2.0f;
        this.maxLinearAcceleration = 5.0f;
        this.maxAngularSpeed = 10.0f;
        this.maxAngularAcceleration = 20.0f;

        this.decelerationRadiusL = 0.6f;
        this.arrivalTolerance = 0.001f;

        this.pathOffset = 0.2f;
        this.predictionTime = 0.2f;

        this.alignTolerance = 0.001f;
        this.timeToTarget = 0.1f;
        this.decelerationRadiusR = MathUtils.PI;

        this.boundingRadius = 0.3f;

        this.steerings = new ObjectSet<SteeringAgent.Steering>();
        for ( SteeringAgent.Steering s : steerings ) {
            this.steerings.add(s);
        }
    }
}
