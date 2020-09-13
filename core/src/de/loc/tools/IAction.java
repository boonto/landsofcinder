package de.loc.tools;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.List;

import de.loc.combat.Skill;

public interface IAction {

    void update(float deltaTime);

    void resolve();

    boolean isReady();

    //der ausführende der action
    Entity getOwner();

    //die Entscheidungen mit dazugehörigen ziellisten
    List<Pair<Skill.Type, ArrayList<Entity>>> getDecisions();
}
