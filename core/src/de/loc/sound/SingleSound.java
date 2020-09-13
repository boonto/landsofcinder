package de.loc.sound;

import com.badlogic.gdx.audio.Sound;

public class SingleSound implements SoundEffect {

    Sound sound;

    public SingleSound(Sound sound) {
        this.sound = sound;
    }

    @Override
    public void play(float volume) {
        this.sound.play(volume);
    }

    public void play() {
        this.sound.play();
    }
}
