package de.loc.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.util.ArrayList;
import java.util.HashMap;

import de.loc.event.Event;
import de.loc.event.EventSystem;
import de.loc.quest.QuestEvent;

public class XmlHelper {

    public static Event parseEventNode(XmlReader.Element eventNode) {
        if ( eventNode == null ) {
            return null;
        }

        XmlReader.Element eventType = eventNode.getChildByName("Type");
        // Cast string to enum EventType
        EventSystem.EventType type = EventSystem.EventType.valueOf(eventType.getText());

        // parse all arguments
        String[] arguments = new String[eventNode.getChildrenByName("Argument").size];
        int i = 0;
        for ( XmlReader.Element argument : eventNode.getChildrenByName("Argument") ) {
            arguments[i] = argument.getText();
            i++;
        }

        Event event = new Event(type, (Object[]) arguments);

        return event;

    }

    public static QuestEvent parseQuestEventNode(XmlReader.Element questEventNode) {
        XmlReader.Element goalEventNode = questEventNode.getChildByName("GoalEvent");
        XmlReader.Element descriptionNode = questEventNode.getChildByName("Description");

        String description = descriptionNode.getText();
        Event goalEvent = parseEventNode(goalEventNode.getChildByName("Event"));

        int goalEventCount = 1;
        try {
            String count = goalEventNode.getAttribute("Anzahl");
            goalEventCount = Integer.parseInt(count);

        } catch ( Exception e ) {
            // tüdelü
        }

        // optional
        ArrayList<Event> triggerEventList = new ArrayList<>();
        for ( XmlReader.Element triggerNode : questEventNode.getChildrenByName("TriggerEvent") ) {
            triggerEventList.add(parseEventNode(triggerNode.getChildByName("Event")));
        }

        QuestEvent questEvent = new QuestEvent(goalEvent, goalEventCount, description, triggerEventList);
        return questEvent;
    }

    public static ListItem parseXml(String path) {
        XmlReader.Element list = getFile(path);
        ListItem listItem = new ListItem(list);
        return listItem;
    }

    public static ArrayList<ListItem> parseXmlList(String path) {
        ArrayList<ListItem> items = new ArrayList<>();
        XmlReader.Element list = getFile(path);

        for ( int i = 0; i < list.getChildCount(); i++ ) {
            XmlReader.Element e = list.getChild(i);
            ListItem listItem = new ListItem(e);
            items.add(listItem);
        }
        return items;
    }

    public static HashMap<String, ListItem> getHashMapXmlList(String path) {
        HashMap<String, ListItem> items = new HashMap<>();
        XmlReader.Element list = getFile(path);

        for ( int i = 0; i < list.getChildCount(); i++ ) {
            XmlReader.Element e = list.getChild(i);
            ListItem listItem = new ListItem(e);
            items.put(listItem.name, listItem);
        }

        return items;
    }

    public static XmlReader.Element getFile(String path) {
        XmlReader xmlReader = new XmlReader();
        FileHandle file = Gdx.files.internal(path);
        XmlReader.Element xmlList;

        xmlList = xmlReader.parse(file);
        return xmlList;
    }

    public static Array<String> parseMusicList(String path) {
        Array<String> items = new Array<String>();

        XmlReader.Element list = getFile(path);

        for ( int i = 0; i < list.getChildCount(); i++ ) {
            XmlReader.Element music = list.getChild(i);
            items.add(music.getText());
        }
        return items;
    }

    /* Parst die Package-Overview.xml und liefert das StartLevel */
    public static String parseStartLevel(String packagePath) {
        XmlReader xmlReader = new XmlReader();
        FileHandle file = Gdx.files.internal(packagePath + "/overview.xml");
        XmlReader.Element xmlScene;
        xmlScene = xmlReader.parse(file);
        return xmlScene.getChildByName("StartLevel").getText() + ".xml";
    }
}
