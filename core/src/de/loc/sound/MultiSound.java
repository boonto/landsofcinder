package de.loc.sound;

import com.badlogic.gdx.audio.Sound;

import java.util.List;

public class MultiSound implements SoundEffect {

    private final List<Sound> sounds;
    int currentIndex = 0;
    final int size;

    public MultiSound(List<Sound> soundList) {
        this.sounds = soundList;

        this.size = this.sounds.size();
    }

    @Override
    public void play(float volume) {
        this.sounds.get(this.currentIndex).play(volume);
        this.increaseIndex();
    }

    @Override
    public void play() {
        this.sounds.get(this.currentIndex).play();
        this.increaseIndex();
    }

    private void increaseIndex() {
        if ( this.currentIndex < this.size - 1 ) {
            this.currentIndex++;
        } else {
            this.currentIndex = 0;
        }
    }

}
