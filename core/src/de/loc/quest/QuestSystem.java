package de.loc.quest;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import de.loc.core.LandsOfCinderSystem;
import de.loc.core.LevelManager;
import de.loc.core.TypeComponent;
import de.loc.dialog.NameComponent;
import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;
import de.loc.input.ClickableComponent;
import de.loc.item.InventoryComponent;
import de.loc.tools.Constants;

public class QuestSystem extends LandsOfCinderSystem implements EventListener {

    private final QuestLog questLog;

    private final HashMap<String, Quest> questList;
    private final HashMap<String, Integer> killList;

    // alle quests die es in der spielwelt gibt
    private ImmutableArray<Entity> questEntities;

    private final ComponentMapper<ClickableComponent> clickableMapper;
    private final ComponentMapper<QuestComponent> questMapper;

    private InventoryComponent playerInventory;

    public QuestSystem() {

        this.questList = new HashMap<>();
        this.questLog = new QuestLog(this.questList);

        this.killList = new HashMap<>();

        this.clickableMapper = ComponentMapper.getFor(ClickableComponent.class);
        this.questMapper = ComponentMapper.getFor(QuestComponent.class);

        EventSystem.getInstance().addListener(this, EventSystem.EventType.values());
    }

    public void addedToEngine(Engine engine) {

        this.questEntities = engine.getEntitiesFor(Family.all(QuestComponent.class, ClickableComponent.class).get());
        // TODO: ist das wirklich sauber so?
        this.playerInventory = LevelManager.getInstance().getPlayerEntity().getComponent(InventoryComponent.class);
    }

    // zyklisches update der Engine:
    public void update(float deltaTime) {

        // alle inaktiven quests durchschauen ob das entity geclicked wurde
        for ( Entity entity : this.questEntities ) {
            ClickableComponent c = this.clickableMapper.get(entity);
            if ( c.clicked ) {
                QuestComponent qc = this.questMapper.get(entity);
                if ( this.questList.containsKey(qc.NAME) ) {
                    // set the quest status to active, because its already in the Questlog
                    qc.active = true;
                }
                if ( !qc.active ) {
                    qc.active = true;
                    Quest quest = QuestParser.loadQuest(qc.path);
                    // TODO: vergessen init Aufzurufen und damit alle TriggerEvents zu handlen?
                    quest.setClientName(entity.getComponent(NameComponent.class).getName());
                    this.questList.put(quest.NAME, quest);
                    this.setQuestActive(quest);
                }
            }
        }

        //        for(QuestComponent quest : activeQuests.values()) {
        //
        //    		if (quest.isFinished())
        //        	{
        //        		questFinished(quest);
        //        	}
        //        }
    }

    public QuestLog getQuestLog() {
        return this.questLog;
    }

    private void questFinished(QuestComponent q) {
        Gdx.app.log("QUEST", q.NAME + " wurde erfolgreich abgeschlossen. TOLL!");

    }

    private void setQuestActive(Quest quest) {
        EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.QUEST_EVENT));
        Event fetchEvent = new Event(EventSystem.EventType.FETCH_EVENT, "item");
        for ( Entity item : this.playerInventory.contents.values() ) {
            TypeComponent t = item.getComponent(TypeComponent.class);
            fetchEvent.args[0] = t.name;
            quest.update(fetchEvent);
        }

        Event killEvent = new Event(EventSystem.EventType.KILL_EVENT, "mob");
        for ( String mobName : this.killList.keySet() ) {
            killEvent.args[0] = mobName;
            for ( int i = 0; i < this.killList.get(mobName); i++ ) {
                quest.update(killEvent);
            }
        }

        Gdx.app.log("QUEST", "Neue Quest erhalten: " + quest.NAME);
        Gdx.app.log("QUEST", quest.NAME + " : " + quest.description);

    }

    @Override
    public void update(Event e) {

        if ( e.eventType.equals(EventSystem.EventType.KILL_EVENT) ) {
            if ( this.killList.containsKey(((Entity) e.args[0]).getComponent(TypeComponent.class).name) ) //TODO musst du dir nochmal anschauen willi geht bestimmt schlauer aber ich blick da nicht so durch
            {
                int numberOfKills = this.killList.get(((Entity) e.args[0]).getComponent(TypeComponent.class).name);
                numberOfKills++;
                this.killList.put(((Entity) e.args[0]).getComponent(TypeComponent.class).name, numberOfKills);
            } else {
                this.killList.put(((Entity) e.args[0]).getComponent(TypeComponent.class).name, 1);
            }
        }

        for ( Quest quest : this.questList.values() ) {
            quest.update(e);
        }

    }

    @Override
    public void reset() {
        //saveQuestState();
    }

    public void load() {
        XmlReader xmlReader = new XmlReader();
        FileHandle file = null;

        if ( Gdx.app.getType() == Constants.DESKTOP ) {
            file = Gdx.files.internal(Constants.GAME_SAVES_PATH + "questlist.xml");
        } else if ( Gdx.app.getType() == Constants.ANDROID ) {
            file = Gdx.files.internal(Constants.GAME_SAVES_PATH + "questlist.xml");
        } else {
            Gdx.app.log("Fehler", "Unbekannter Application Type!");
        }
        XmlReader.Element xmlQuestList = null;
        if ( !file.exists() ) {
            return;
        }

        xmlQuestList = xmlReader.parse(file);
        this.restoreQuestStatus(xmlQuestList);

    }

    private void restoreQuestStatus(XmlReader.Element xmlQuestList) {
        for ( XmlReader.Element xmlQuest : xmlQuestList.getChildrenByName("Quest") ) {
            Quest quest = QuestParser.loadQuest(xmlQuest.getText());
            // TODO: vergessen init Aufzurufen und damit alle TriggerEvents zu handlen?
            quest.setClientName(xmlQuest.getAttribute("clientName"));
            this.questList.put(quest.NAME, quest);
            quest.setProgress(Integer.parseInt(xmlQuest.getAttribute("progress")));
        }
    }

    public void saveQuestState() {
        StringWriter writer = new StringWriter();
        XmlWriter xmlWriter = new XmlWriter(writer);

        try {
            xmlWriter.element("QuestList");

            for ( Quest quest : this.questList.values() ) {
                xmlWriter.element("Quest").attribute("progress", quest.getProgress()).attribute("clientName", quest.getClientName()).text(quest.path);
                xmlWriter.pop();
            }
            xmlWriter.pop();
            xmlWriter.close();

        } catch ( IOException e ) {
            e.printStackTrace();
        }

        FileHandle file = null;
        if ( Gdx.app.getType() == Constants.DESKTOP ) {
            file = Gdx.files.local(Constants.GAME_SAVES_PATH + "questlist.xml");
        } else if ( Gdx.app.getType() == Constants.ANDROID ) {
            file = Gdx.files.internal(Constants.GAME_SAVES_PATH + "questlist.xml");
        }
        file.writeString(writer.toString(), false);
    }
}
