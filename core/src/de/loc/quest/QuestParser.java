package de.loc.quest;

import com.badlogic.gdx.utils.XmlReader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import de.loc.event.Event;
import de.loc.tools.XmlHelper;

public class QuestParser {

    public static QuestComponent parseQuestComponent(String path, String clientName) {
        XmlReader.Element xmlQuest = XmlHelper.getFile(path);
        if ( xmlQuest != null ) {
            String name = xmlQuest.getChildByName("QuestName").getText();
            String dialog = xmlQuest.getChildByName("Dialog").getText();
            QuestComponent q = new QuestComponent(path, name, dialog, clientName);
            return q;
        }

        return null;
    }

    public static Quest loadQuest(String path) {

        XmlReader.Element xmlQuest = XmlHelper.getFile(path);
        if ( xmlQuest != null ) {
            Quest quest = parseQuest(xmlQuest, path);
            return quest;
        } else {
            return null;
        }
    }

    private static Quest parseQuest(XmlReader.Element xmlQuest, String path) {

        // Quest-Header:
        XmlReader.Element nameNode = xmlQuest.getChildByName("QuestName");
        XmlReader.Element clientNode = xmlQuest.getChildByName("Client");
        XmlReader.Element descriptionNode = xmlQuest.getChildByName("Description");
        XmlReader.Element dialogNode = xmlQuest.getChildByName("Dialog");

        String name = nameNode.getText();
        //NameComponent clientName = new NameComponent(clientNode.getText());
        String description = descriptionNode.getText();
        String dialog = dialogNode.getText();
        //-----------------------------------------------------

        // parse all TriggerEvents (optional)
        XmlReader.Element triggerEventsNode = xmlQuest.getChildByName("TriggerEvent");
        ArrayList<Event> triggerEvents = new ArrayList<>();
        if ( triggerEventsNode != null ) {
            for ( XmlReader.Element eventNode : triggerEventsNode.getChildrenByName("Event") ) {
                Event triggerEvent = XmlHelper.parseEventNode(eventNode);
                triggerEvents.add(triggerEvent);
            }
        }

        // parse all QuestEvents:
        XmlReader.Element questEventsNode = xmlQuest.getChildByName("QuestEvents");
        Queue<QuestEvent> questEvents = new LinkedList<>();

        for ( XmlReader.Element questEvent : questEventsNode.getChildrenByName("QuestEvent") ) {
            QuestEvent q = XmlHelper.parseQuestEventNode(questEvent);
            questEvents.add(q);
        }

        // instantiate QuestComponent
        Quest quest = new Quest(path, name, description, dialog, triggerEvents, questEvents);

        return quest;
    }

}
