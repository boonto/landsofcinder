package de.loc.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

    public Music music;
    public Sound sound;

    private static final class SoundManagerHolder {
        static final SoundManager soundManager = new SoundManager();
    }

    public static SoundManager getInstance() {
        return SoundManagerHolder.soundManager;
    }

    public void playMusic(Music music) {
        this.music = music;
        this.music.setLooping(true);
        this.music.setVolume(0.5f);
        this.music.play();
    }

    public void pauseMusic() {
        this.music.pause();
    }

    public void stopMusic() {
        this.music.stop();
    }

    public void playSound(Sound sound) {
        this.sound = sound;
        this.sound.play(0.5f);
    }

    public static class Type {
        public static final Music MAIN_THEME = Gdx.audio.newMusic(Gdx.files.local("music/main_theme_pianosketch.ogg"));
        public static final Music BATTLE_THEME = Gdx.audio.newMusic(Gdx.files.local("music/epic_battle_against_the_dampfmaschine.ogg"));

        public static final Sound VICTORY = Gdx.audio.newSound(Gdx.files.internal("soundeffects/victory.mp3"));
        public static final Sound WOOD = Gdx.audio.newSound(Gdx.files.internal("soundeffects/Zerbrechendes_Holz.mp3"));
        public static final Sound GAMEOVER = Gdx.audio.newSound(Gdx.files.internal("soundeffects/Game_Over.mp3"));
        public static final Sound BOOO = Gdx.audio.newSound(Gdx.files.internal("soundeffects/Booo.mp3"));
    }
}
