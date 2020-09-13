package de.loc.input.userinterface;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

import de.loc.combat.CombatComponent;
import de.loc.combat.CombatSystem;
import de.loc.combat.Skill;
import de.loc.core.LevelManager;
import de.loc.core.TypeComponent;
import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;
import de.loc.graphics.CustomEffectData;
import de.loc.graphics.ModelComponent;
import de.loc.input.InputHandler;
import de.loc.tools.Constants;
import de.loc.tools.Pair;

public class CombatInterface implements EventListener {

    private final ComponentMapper<CombatComponent> combatMapper;

    private final CombatComponent playerCC;

    private final CombatUI combatUI;

    private final List<Pair<Skill.Type, ArrayList<Entity>>> skills;
    private int currentAction;

    private final List<Entity> enemies;

    public CombatInterface(UserInterface ui, CombatSystem combatSystem) {
        this.combatMapper = ComponentMapper.getFor(CombatComponent.class);

        CombatInputHandler combatInputHandler = new CombatInputHandler();
        this.playerCC = this.combatMapper.get(LevelManager.getInstance().getPlayerEntity());

        this.combatUI = new CombatUI(ui, combatInputHandler, this.playerCC);

        this.skills = new ArrayList<>(this.playerCC.curActions);
        for ( int i = 0; i < this.playerCC.curActions; ++i ) {
            this.skills.add(new Pair<Skill.Type, ArrayList<Entity>>(null, new ArrayList<Entity>(Constants.MAX_ENEMIES)));
        }
        this.currentAction = 0;

        this.enemies = new ArrayList<>(Constants.MAX_ENEMIES);

        //globale Events
        EventSystem.getInstance()
                   .addListener(this, EventSystem.EventType.KILL_EVENT, EventSystem.EventType.ATTACK_EVENT, EventSystem.EventType.COMBAT_ENTITY_CLICKED);

        //lokale Events
        combatSystem.addListener(new EventListener() {
            @Override
            public void update(Event e) {
                switch ( e.eventType ) {
                    case COMBAT_STARTED:
                        CombatInterface.this.combatUI.show();
                        break;
                    case COMBAT_ENEMY:
                        CombatInterface.this.enemies.add((Entity) e.args[0]);
                        break;
                    case COMBAT_ENDED:
                        CombatInterface.this.combatUI.hide();
                        break;
                    case GAME_OVER:
                        CombatInterface.this.showGameOverWindow();
                        CombatInterface.this.combatUI.hide();
                        break;
                }
            }
        });
    }

    private void actionClicked(int action) {
        this.currentAction = action;
        this.combatUI.showSkillWindow(CombatUI.SkillType.ATTACK);
    }

    private void skillClicked(Skill.Type skill) {
        for ( Entity e : this.enemies ) {
            ((CustomEffectData) e.getComponent(ModelComponent.class).model.userData).highlightColor = new Vector3(1.0f, 0.0f, 0.0f);
        }
        this.skills.get(this.currentAction).setLeft(skill);

        this.combatUI.setSkill(this.currentAction, skill);
    }

    private void groupSkillClicked(Skill.Type skill) {
        this.combatUI.hideSkillWindows();

        this.skills.get(this.currentAction).setLeft(skill);
        this.skills.get(this.currentAction).getRight().clear();
        for ( Entity enemy : this.enemies ) {
            this.skills.get(this.currentAction).getRight().add(enemy);
        }

        this.combatUI.setSkill(this.currentAction, skill);
        this.combatUI.setTarget(this.currentAction, "Alle");

        if ( this.currentAction < this.playerCC.curActions ) {
            this.currentAction++;
        }
    }

    private void targetClicked(Entity entity) {
        this.combatUI.hideSkillWindows();

        if ( this.currentAction < this.playerCC.curActions ) {
            if ( (this.skills.get(this.currentAction).getRight().size() <= 1) && (this.skills.get(this.currentAction).getLeft() != null) ) {
                this.skills.get(this.currentAction).getRight().clear();
                this.skills.get(this.currentAction).getRight().add(entity);

                this.combatUI.setTarget(this.currentAction, entity.getComponent(TypeComponent.class).name);
            }

            if ( this.skills.get(this.currentAction).getLeft() == null ) {
                this.skills.get(this.currentAction).setLeft(Skill.Type.ATTACK);
                this.skills.get(this.currentAction).getRight().add(entity);

                this.combatUI.setSkill(this.currentAction, Skill.Type.ATTACK);
                this.combatUI.setTarget(this.currentAction, entity.getComponent(TypeComponent.class).name);
            }

            this.currentAction++;
        }

        for ( Entity e : this.enemies ) {
            ((CustomEffectData) e.getComponent(ModelComponent.class).model.userData).highlightColor = new Vector3(0.0f, 0.0f, 0.0f);
        }
    }

    private void goClicked() {
        boolean valid = true;

        for ( Pair<Skill.Type, ArrayList<Entity>> pair : this.skills ) {
            valid = valid && (pair.getLeft() != null);
            valid = valid && (pair.getRight().get(0) != null);
        }

        if ( valid ) {

            ArrayList<Pair<Skill.Type, ArrayList<Entity>>> copy = new ArrayList<>(this.skills.size());

            for ( Pair<Skill.Type, ArrayList<Entity>> pair : this.skills ) {
                copy.add(new Pair<>(pair.getLeft(), new ArrayList<>(pair.getRight())));
            }

            EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.ATTACK_CLICKED, copy));

            for ( Pair<Skill.Type, ArrayList<Entity>> pair : this.skills ) {
                pair.setLeft(null);
                pair.getRight().clear();
            }
            this.combatUI.resetActionButtons();

            this.currentAction = 0;
        } else {
            System.out.println("da fehlt noch ne aktion oder ein target");
        }
    }

    private void showGameOverWindow() {
        //Label label = new Label("Du bist gestorben...", ui.getSkin());

        //TODO statt gibbetnisch kommt der loadscreen
        // ui.addTwoButtonWindow(label, "Load Game", "Quit", "gibbetnisch", "game_back_menu");
    }

    @Override
    public void update(Event e) {
        switch ( e.eventType ) {

            case KILL_EVENT:
                Entity deceased = ((Entity) e.args[0]);

                this.enemies.remove(deceased);
                break;
            case ATTACK_EVENT:
                //Entity attacker = combatSystem.getEngine().getEntity(Long.valueOf(e.argument[0]));
                //Entity defender = combatSystem.getEngine().getEntity(Long.valueOf(e.argument[1]));

                this.combatUI.updatePlayerHealth(this.playerCC.maxHealth, this.playerCC.curHealth);
                this.combatUI.updatePlayerSteam(this.playerCC.curSteam);
                break;

            case COMBAT_ENTITY_CLICKED:
                this.targetClicked((Entity) e.args[0]);
                break;
        }
    }

    class CombatInputHandler extends InputHandler {
        static final String ACTION = "combat_action_";
        static final String SKILL = "combat_skill_";
        static final String GO = "combat_go";
        static final String OFFENSIVE = "combat_offensive";
        static final String DEFENSIVE = "combat_defensive";
        static final String ITEMS = "combat_items";
        static final String GROUP_SKILL = "combat_group_skill_";

        @Override
        public boolean handle(String handleID) {
            //combat handles
            if ( handleID.startsWith(ACTION) ) {
                int action = Integer.valueOf(handleID.substring(ACTION.length()));
                CombatInterface.this.actionClicked(action);
                return true;
            }

            if ( handleID.startsWith(SKILL) ) {
                Skill.Type skill = Skill.Type.fromString(handleID.substring(SKILL.length()));
                CombatInterface.this.skillClicked(skill);
                return true;
            }

            if ( handleID.startsWith(GROUP_SKILL) ) {
                Skill.Type skill = Skill.Type.fromString(handleID.substring(GROUP_SKILL.length()));
                CombatInterface.this.groupSkillClicked(skill);
                return true;
            }

            if ( handleID.equals(GO) ) {
                CombatInterface.this.goClicked();
                return true;
            }

            if ( handleID.equals(OFFENSIVE) ) {
                CombatInterface.this.combatUI.showSkillWindow(CombatUI.SkillType.ATTACK);
                return true;
            }

            if ( handleID.equals(DEFENSIVE) ) {
                CombatInterface.this.combatUI.showSkillWindow(CombatUI.SkillType.DEFEND);
                return true;
            }

            if ( handleID.equals(ITEMS) ) {
                CombatInterface.this.combatUI.showSkillWindow(CombatUI.SkillType.ITEM);
                return true;
            }

            return false;
        }
    }
}
