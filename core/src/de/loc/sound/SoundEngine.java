package de.loc.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;
import de.loc.tools.Constants;

public class SoundEngine implements EventListener, Disposable {

    private final float music_volume = 0.7f;
    private float sound_volume = 0.6f;

    // TODO Musik richtig abmischen, dann braucht man die beiden hier nicht mehr!
    public float combat_volume = 0.7f;
    public float ambient_volume = 0.7f;

    private static Music ambientMusic;
    private static Music combatMusic;

    //private static Thread faderThread;
    //private static MusicFader fader;

    private final SoundChache soundChache;

    private SoundEngine() {
        this.soundChache = new SoundChache();
        //fader = new MusicFader();
        //faderThread = new Thread(fader);
        //addToEventSystem();
    }

    public void playAmbientMusic() {
        if ( ambientMusic == null ) {
            return;
        }

        // ist es ein Szenenwechsel? dann spiel einfach wieder die
        // neue ambientMusic ab!
        if ( combatMusic != null && !combatMusic.isPlaying() ) {
            ambientMusic.setVolume(this.music_volume);
            ambientMusic.play();
        }
        // sonst fade von der Kampf zur Ambient-Musik!
        else {
            MusicFader fader = new MusicFader();
            fader.fadeToAmbientMusic = true;
            //faderThread.start();
            Thread t = new Thread(fader);
            t.start();
        }

    }

    public void playCombatMusic() {
        MusicFader fader = new MusicFader();
        fader.fadeToAmbientMusic = false;
        //faderThread.start();
        Thread t = new Thread(fader);
        t.start();

        //combatMusic.setPosition(0);

    }

    public void setMusicVolume(float volume) {
        if ( ambientMusic != null ) {
            ambientMusic.setVolume(volume);
        }

        if ( combatMusic != null ) {
            combatMusic.setVolume(volume);
        }
    }

    public void setSoundEffectVolume(float volume) {
        this.sound_volume = volume;
    }

    public void setAmbientMusic(String path) {

        if ( ambientMusic != null ) {
            ambientMusic.dispose();
        }
        ambientMusic = Gdx.audio.newMusic(Gdx.files.internal(Constants.MUSIC_PATH + path));
        ambientMusic.setLooping(true);
        ambientMusic.setVolume(this.ambient_volume);
    }

    public void setCombatMusic(String path) {
        if ( combatMusic != null ) {
            combatMusic.dispose();
        }
        combatMusic = Gdx.audio.newMusic(Gdx.files.internal(Constants.MUSIC_PATH + path));
        combatMusic.setLooping(true);
        combatMusic.setVolume(this.combat_volume);
    }

    private static final class SoundEngineHolder {
        static final SoundEngine soundEngine = new SoundEngine();
    }

    public static SoundEngine getInstance() {

        return SoundEngineHolder.soundEngine;

    }

    public void addToEventSystem() {
        EventSystem.getInstance().addListener(this, EventSystem.EventType.values());
    }

    @Override
    public void update(Event e) {

        if ( e.eventType == EventSystem.EventType.COMBAT_STARTED ) {
            this.playCombatMusic();
        }

        if ( e.eventType == EventSystem.EventType.COMBAT_ENDED ) {
            this.playAmbientMusic();
        }

        SoundEffect sound = this.soundChache.getSound(e);
        if ( sound != null ) {
            sound.play(this.sound_volume);
        }
    }

    @Override
    public void dispose() {
        //TODO
        //		ambientMusic.dispose();
        //		combatMusic.dispose();
        //      soundChache.dispose();
    }

    class MusicFader implements Runnable {
        public final int FADE_TIME = 2000; // ms
        public final int STEPS = 50;
        public final int PAUSE_TIME = this.FADE_TIME / this.STEPS;

        boolean fadeToAmbientMusic = true; // false means fadeToCombatMusic

        @Override
        public void run() {

            Music fadeToMusic = (this.fadeToAmbientMusic ? ambientMusic : combatMusic);
            Music fadeOfMusic = (this.fadeToAmbientMusic ? combatMusic : ambientMusic);

            fadeToMusic.setVolume(0.0f);
            fadeToMusic.play();

            float volume = fadeOfMusic.getVolume();
            float volumeStep = volume / (float) this.STEPS;
            for ( float f = volume; f > 0.0f; f -= volumeStep ) {
                fadeOfMusic.setVolume(f);
                fadeToMusic.setVolume(volume - f);
                try {
                    Thread.sleep(this.PAUSE_TIME);
                } catch ( InterruptedException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            // Kampfmusik soll immer wieder von vorne anfangen!
            if ( !this.fadeToAmbientMusic ) {
                fadeOfMusic.pause();
            } else {
                fadeOfMusic.stop();
            }
        }
    }
}
