package de.loc.graphics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;

import java.util.Set;

import de.loc.core.LandsOfCinderSystem;
import de.loc.core.LevelManager;
import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;
import de.loc.movement.EntityStateComponent;

public class AnimationSystem extends LandsOfCinderSystem implements EventListener {

    private ImmutableArray<Entity> entities;

    private final ComponentMapper<AnimationComponent> animationMapper;
    private final ComponentMapper<EntityStateComponent> entityStateMapper;

    public AnimationSystem() {
        this.animationMapper = ComponentMapper.getFor(AnimationComponent.class);
        this.entityStateMapper = ComponentMapper.getFor(EntityStateComponent.class);

        EventSystem.getInstance().addListener(this,
                                              EventSystem.EventType.ATTACK_EVENT,
                                              EventSystem.EventType.KILL_EVENT,
                                              EventSystem.EventType.DIALOG_STARTED,
                                              EventSystem.EventType.DIALOG_ENDED);
    }

    public void addedToEngine(Engine engine) {
        this.entities =
            engine.getEntitiesFor(Family.all(AnimationComponent.class, ModelComponent.class, EntityStateComponent.class, RenderableComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for ( Entity entity : this.entities ) {
            AnimationComponent animationComponent = this.animationMapper.get(entity);
            AnimationController animationController = animationComponent.animationController;
            Set<AnimationComponent.Animation> animations = animationComponent.animations;
            EntityStateComponent entityState = this.entityStateMapper.get(entity);

            if ( entityState.isStanding() && animations.contains(AnimationComponent.Animation.IDLE) ) {
                animationController.animate(AnimationComponent.Animation.IDLE.toString(), -1, null, 0.5f);
            } else if ( entityState.isWalking() && animations.contains(AnimationComponent.Animation.WALK) ) {
                animationController.animate(AnimationComponent.Animation.WALK.toString(), -1, null, 0.5f);
            } else if ( entityState.isRunning() && animations.contains(AnimationComponent.Animation.RUN) ) {
                animationController.animate(AnimationComponent.Animation.RUN.toString(), -1, null, 0.75f);
            } else if ( entityState.isFighting() && animations.contains(AnimationComponent.Animation.COMBAT_IDLE) ) {
                animationController.animate(AnimationComponent.Animation.COMBAT_IDLE.toString(), -1, null, 0.5f);
            } else if ( entityState.isFighting() && animations.contains(AnimationComponent.Animation.IDLE) ) {
                animationController.animate(AnimationComponent.Animation.IDLE.toString(), -1, null, 0.5f);
            }

            animationController.update(deltaTime);
        }
    }

    @Override
    public void update(Event e) {
        AnimationComponent animationComponent;

        switch ( e.eventType ) {
            case ATTACK_EVENT:
                Entity attacker = (Entity) e.args[0];
                animationComponent = this.animationMapper.get(attacker);
                AnimationComponent.Animation animation = AnimationComponent.Animation.fromString(e.args[2].toString());
                if ( (animation != null) && animationComponent.animations.contains(animation) ) {
                    animationComponent.animationController.action(animation.toString(), 1, 1.0f, null, 0.5f);
                }

                Entity defender = (Entity) e.args[1];
                animationComponent = this.animationMapper.get(defender);
                Set<AnimationComponent.Animation> animations = animationComponent.animations;

                if ( animations.contains(AnimationComponent.Animation.HURT) ) {
                    animationComponent.animationController.action(AnimationComponent.Animation.HURT.toString(), 1, 1.0f, null, 0.5f);
                }
                break;
            case KILL_EVENT:
                animationComponent = this.animationMapper.get((Entity) e.args[0]);
                if ( animationComponent != null ) {
                    if ( animationComponent.animations.contains(AnimationComponent.Animation.DIE) ) {
                        animationComponent.animationController.action(AnimationComponent.Animation.DIE.toString(), 1, 1.0f, null, 0.5f);
                    }
                }
                break;

            case DIALOG_STARTED:
                animationComponent = this.animationMapper.get((Entity) e.args[2]);
                if ( animationComponent != null ) {
                    if ( animationComponent.animations.contains(AnimationComponent.Animation.TALK) ) {
                        animationComponent.animationController.animate(AnimationComponent.Animation.TALK.toString(), -1, null, 0.5f);
                        this.animationMapper.get(LevelManager.getInstance()
                                                             .getPlayerEntity()).animationController.animate(AnimationComponent.Animation.TALK.toString(),
                                                                                                             -1,
                                                                                                             null,
                                                                                                             0.5f);
                    }
                }
                break;

            case DIALOG_ENDED:
                animationComponent = this.animationMapper.get((Entity) e.args[0]);
                if ( animationComponent != null ) {
                    if ( animationComponent.animations.contains(AnimationComponent.Animation.DIE) ) {
                        animationComponent.animationController.animate(AnimationComponent.Animation.TALK.toString(), 0, null, 0.5f);
                        this.animationMapper.get(LevelManager.getInstance()
                                                             .getPlayerEntity()).animationController.animate(AnimationComponent.Animation.TALK.toString(),
                                                                                                             -1,
                                                                                                             null,
                                                                                                             0.5f);
                    }
                }
                break;
        }
    }

    @Override
    public void reset() {
        //do nothing
    }
}
