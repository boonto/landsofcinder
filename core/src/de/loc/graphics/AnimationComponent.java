package de.loc.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AnimationComponent implements Component {

    public enum Animation {
        IDLE("Armature|idle"),
        WALK("Armature|walk"),
        RUN("Armature|run"),
        ATTACK("Armature|attack"),
        HURT("Armature|hurt"),
        DIE("Armature|die"),
        UPPERCUT("Armature|uppercut"),
        PICKUP("Armature|pickup"),
        COMBAT_IDLE("Armature|combatidle"),
        DAMPFSTRAHL("Armature|dampfstrahl"),
        VICTORY("Armature|victory"),
        TALK("Armataure|talk");

        private final String text;

        Animation(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }

        public static Animation fromString(String text) {
            for ( Animation a : values() ) {
                if ( a.toString().substring("Armature|".length()).equals(text.toLowerCase()) ) {
                    return a;
                }
            }

            return null;
        }
    }

    public AnimationController animationController;
    public Set<Animation> animations;

    public AnimationComponent(ModelComponent modelComponent, Animation... animations) {
        this.animationController = new AnimationController(modelComponent.model);
        this.animations = new HashSet<>(Arrays.asList(animations));
    }
}
