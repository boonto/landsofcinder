package de.loc.dialog;

import com.badlogic.gdx.utils.XmlReader;

import java.util.ArrayList;

import de.loc.event.Event;
import de.loc.tools.XmlHelper;

public class DialogParser {

    // temporary needed (legacy for ADD_DIALOG Events)
    public static DialogComponent createDialog(String name, String text) {
        ArrayList<Dialog> dialogList = new ArrayList<>();
        Dialog d = new Dialog(name, text);
        dialogList.add(d);
        return new DialogComponent(name, dialogList);
    }

    public static DialogComponent loadDialog(String path) {
        XmlReader.Element xmlDialog = XmlHelper.getFile(path);
        if ( xmlDialog != null ) {
            return parseDialogComponent(xmlDialog, path);
        } else {
            return null;
        }
    }

    private static DialogComponent parseDialogComponent(XmlReader.Element xmlDialog, String path) {

        // Dialog-Header:
        XmlReader.Element idNode = xmlDialog.getChildByName("ID");

        String id = idNode.getText();

        // parse all Dialogs:
        XmlReader.Element dialogListNode = xmlDialog.getChildByName("Dialogs");
        ArrayList<Dialog> dialogList = new ArrayList<>();
        for ( XmlReader.Element dialogNode : dialogListNode.getChildrenByName("Dialog") ) {
            Dialog d = parseDialog(dialogNode);
            dialogList.add(d);
        }

        // instantiate QuestComponent

        return new DialogComponent(id, dialogList);
    }

    private static Dialog parseDialog(XmlReader.Element dialogNode) {

        // Elements needed for the dialog:
        XmlReader.Element textNode = dialogNode.getChildByName("Text");
        String text = textNode.getText();

        //optional elements:
        XmlReader.Element nameNode = dialogNode.getChildByName("Name");
        String name = null;
        if ( nameNode != null ) {
            name = nameNode.getText();
        }

        XmlReader.Element iconNode = dialogNode.getChildByName("Icon");
        String iconPath = null;
        if ( iconNode != null ) {
            iconPath = iconNode.getText();
        }

        XmlReader.Element triggerEventNode = dialogNode.getChildByName("TriggerEvent");
        Event triggerEvent;
        if ( triggerEventNode != null ) {
            triggerEvent = XmlHelper.parseEventNode(triggerEventNode.getChildByName("Event"));
        } else {
            triggerEvent = null;
        }

        return new Dialog(name, text, iconPath, triggerEvent);
    }

}
