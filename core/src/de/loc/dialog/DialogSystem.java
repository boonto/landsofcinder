package de.loc.dialog;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.loc.core.LandsOfCinderSystem;
import de.loc.core.LevelManager;
import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;
import de.loc.graphics.IconComponent;
import de.loc.input.ClickableComponent;
import de.loc.item.MerchantComponent;
import de.loc.tools.Constants;
import de.loc.tools.EmptyState;
import de.loc.tools.StateMachine;

public class DialogSystem extends LandsOfCinderSystem implements EventListener {

    private ImmutableArray<Entity> dialogEntities;
    private ImmutableArray<Entity> nameEntities;
    private ImmutableArray<Entity> entitiesWithComments;

    private final ComponentMapper<ClickableComponent> clickableMapper;
    private final ComponentMapper<DialogComponent> dialogMapper;
    private final ComponentMapper<NameComponent> nameMapper;
    private final ComponentMapper<IconComponent> iconMapper;

    private final LinkedList<DialogBundle> addDialogList;
    private final HashMap<String, DialogComponent> dialogStateMap;

    private final StateMachine dialogStateMachine;

    public DialogSystem() {
        this.addDialogList = new LinkedList<DialogBundle>();
        this.dialogStateMap = new HashMap<String, DialogComponent>();

        EventSystem.getInstance()
                   .addListener(this, EventSystem.EventType.ADD_DIALOG, EventSystem.EventType.DIALOG_WINDOW_CLICKED, EventSystem.EventType.DIALOG_ENDED);

        this.clickableMapper = ComponentMapper.getFor(ClickableComponent.class);
        this.dialogMapper = ComponentMapper.getFor(DialogComponent.class);
        this.nameMapper = ComponentMapper.getFor(NameComponent.class);
        this.iconMapper = ComponentMapper.getFor(IconComponent.class);

        this.dialogStateMachine = new StateMachine();
        this.dialogStateMachine.add("NoDialog", new EmptyState());
        this.dialogStateMachine.add("Dialog", new DialogState());
    }

    @Override
    public void addedToEngine(Engine engine) {
        // TODO: keine Dialoge bei NPCs die Händler sind (Merchant Component haben)
        // TODO: TODO: das ist eine absolut dümmliche Lösung!
        this.dialogEntities =
            engine.getEntitiesFor(Family.all(DialogComponent.class, ClickableComponent.class, NameComponent.class).exclude(MerchantComponent.class).get());
        this.nameEntities = engine.getEntitiesFor(Family.all(NameComponent.class).get());
        this.entitiesWithComments = engine.getEntitiesFor(Family.all(DialogComponent.class, ClickableComponent.class).exclude(NameComponent.class).get());
    }

    public void update(float deltaTime) {

        for ( DialogBundle d : this.addDialogList ) {
            this.addDialog(d.n, d.d);
        }
        this.addDialogList.clear();

        for ( Entity entity : this.dialogEntities ) {

            ClickableComponent c = this.clickableMapper.get(entity);

            if ( c.clicked ) {
                NameComponent n = this.nameMapper.get(entity);
                DialogComponent d = this.dialogMapper.get(entity);
                IconComponent icon = this.iconMapper.get(entity);

                this.dialogStateMachine.change("Dialog", n.name, icon.iconPath, d, entity);
            }
        }

        for ( Entity entity : this.entitiesWithComments ) {

            ClickableComponent c = this.clickableMapper.get(entity);

            if ( c.clicked ) {
                DialogComponent d = this.dialogMapper.get(entity);
                IconComponent icon = this.iconMapper.get(entity);
                Dialog dialog = d.dialogs.get(0);
                EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.COMMENTARY, "HERO:", dialog.text, icon.iconPath));
            }
        }
    }

    public void addDialog(NameComponent n, DialogComponent d) {
        this.dialogStateMap.put(n.name, d);
        for ( Entity entity : this.dialogEntities ) {

            NameComponent name = this.nameMapper.get(entity);
            Gdx.app.log("Name", name.name);
            if ( name.name.equals(n.name) ) {
                entity.add(d);

            }
        }
    }

    public void applyDialogStateToNpcs() {
        Gdx.app.log("applyDialogStateToNpcs()", "STATE_MAP:");
        for ( Map.Entry<String, DialogComponent> entry : this.dialogStateMap.entrySet() ) {
            Gdx.app.log(entry.getKey(), entry.getValue().id);
        }
        for ( Entity entity : this.nameEntities ) {

            NameComponent name = this.nameMapper.get(entity);

            Gdx.app.log("Name", name.name);
            if ( this.dialogStateMap.containsKey(name.name) ) {
                entity.add(this.dialogStateMap.get(name.name));
            }
        }

    }

    //update mit Events aus dem EventSystem
    @Override
    public void update(Event e) {

        if ( e.eventType == EventSystem.EventType.ADD_DIALOG ) {
            NameComponent n = new NameComponent((String) e.args[0]);
            //DialogComponent d = DialogParser.createDialog(e.argument[0], e.argument[1]);
            DialogComponent d = DialogParser.loadDialog(Constants.PACKAGE_FOLDER + LevelManager.getInstance().getCurrentPackage() + e.args[1]);
            DialogBundle dialogOld = new DialogBundle();
            dialogOld.n = n;
            dialogOld.d = d;
            this.addDialogList.add(dialogOld);
        }

        if ( e.eventType == EventSystem.EventType.DIALOG_WINDOW_CLICKED ) {
            System.out.println("JUHU DU HAST GEKLICKT!!! MIR ABER WURSCHT");
        }

        if ( e.eventType == EventSystem.EventType.DIALOG_ENDED ) {

            this.dialogStateMachine.change("NoDialog", 0);
        }

    }

    @Override
    public void reset() {
        this.applyDialogStateToNpcs();
    }

    private static class DialogBundle {
        NameComponent n;
        DialogComponent d;
    }
}
