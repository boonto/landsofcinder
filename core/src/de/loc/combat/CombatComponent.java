package de.loc.combat;

import com.badlogic.ashley.core.Component;

import java.util.HashMap;

public class CombatComponent implements Component {

    public int level;
    public int experience;

    public int strength;
    public int knowledge;
    public int vitality;

    public int maxHealth;
    public int curHealth;
    public int maxActions;
    public int curActions;
    public int maxSteam;
    public int curSteam;

    public int attack; //basisangriff ohne waffen
    public int defense; //basisverteidigung ohne rüstung
    public HashMap<Skill.Type, String> skillList;

    public CombatComponent(
        int level, int strength, int knowledge, int vitality, int maxActions, int maxSteam, int attack, int defense, HashMap<Skill.Type, String> skillList) {
        this.level = level;

        this.strength = strength;
        this.knowledge = knowledge;
        this.vitality = vitality;

        this.maxHealth = vitality * 5;
        this.curHealth = this.maxHealth;
        this.maxActions = maxActions;
        this.curActions = maxActions;
        this.maxSteam = maxSteam;
        this.curSteam = maxSteam;

        this.attack = attack;
        this.defense = defense;
        this.skillList = skillList;
    }

    public CombatComponent(CombatComponent combatComponent) {
        this.level = combatComponent.level;

        this.strength = combatComponent.strength;
        this.knowledge = combatComponent.knowledge;
        this.vitality = combatComponent.vitality;

        this.maxHealth = combatComponent.vitality * 5;
        this.curHealth = combatComponent.maxHealth;
        this.maxActions = combatComponent.maxActions;
        this.curActions = combatComponent.maxActions;
        this.maxSteam = combatComponent.maxSteam;
        this.curSteam = combatComponent.maxSteam;

        this.attack = combatComponent.attack;
        this.defense = combatComponent.defense;
        this.skillList = combatComponent.skillList;
    }
}
