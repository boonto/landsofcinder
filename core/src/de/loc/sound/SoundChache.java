package de.loc.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.XmlReader;

import java.util.ArrayList;
import java.util.HashMap;

import de.loc.event.Event;
import de.loc.event.EventSystem;
import de.loc.tools.Constants;
import de.loc.tools.XmlHelper;

public class SoundChache implements Disposable {

    private final HashMap<String, Sound> soundMapper;
    private final HashMap<String, SoundEffect> eventMapper;
    private final Event eventTemplate = new Event(EventSystem.EventType.ADD_DIALOG, "");

    public SoundChache() {
        this.soundMapper = new HashMap<>();
        this.eventMapper = new HashMap<>();
        this.loadStandardSounds();
    }

    private void loadStandardSounds() {

        XmlReader.Element xmlSoundList = XmlHelper.getFile(Constants.STD_SOUND_LIST_PATH);
        XmlReader.Element soundFiles = xmlSoundList.getChildByName("SoundFileList");

        for ( XmlReader.Element soundFile : soundFiles.getChildrenByName("SoundFile") ) {
            String path = soundFile.getText();
            this.soundMapper.put(soundFile.getAttribute("name"), Gdx.audio.newSound(Gdx.files.internal(Constants.SOUND_PATH + path)));
        }

        for ( XmlReader.Element sound : xmlSoundList.getChildrenByName("Sound") ) {
            this.addSoundEffect(sound);
        }
    }

    private void addSoundEffect(XmlReader.Element sound) {

        Event e = XmlHelper.parseEventNode(sound.getChildByName("Event"));
        SoundEffect soundEffect;
        Array<XmlReader.Element> soundFiles = sound.getChildrenByName("SoundFile");

        if ( soundFiles.size > 1 ) {
            ArrayList<Sound> list = new ArrayList<>();

            for ( XmlReader.Element file : soundFiles ) {
                list.add(this.soundMapper.get(file.getText()));
            }

            soundEffect = new MultiSound(list);
            this.eventMapper.put(e.toString(), soundEffect);
        } else {
            soundEffect = new SingleSound(this.soundMapper.get(soundFiles.get(0).getText()));
            this.eventMapper.put(e.toString(), soundEffect);
        }
    }

    public SoundEffect getSound(Event e) {

        if ( e.args.length == 0 ) {
            return this.eventMapper.get(e.toString());
        } else {
            this.eventTemplate.eventType = e.eventType;
            this.eventTemplate.args[0] = e.args[0];

            SoundEffect s = this.eventMapper.get(this.eventTemplate.toString());
            if ( s == null ) {
                s = this.eventMapper.get(this.eventTemplate.eventType.toString());
            }
            return s;

        }
    }

    @Override
    public void dispose() {
        //TODO
        //        for(Sound s : soundMapper.values()) {
        //            s.dispose();
        //        }
    }
}
