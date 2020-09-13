package de.loc.combat;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import de.loc.tools.IAction;
import de.loc.tools.IState;
import de.loc.tools.Pair;
import de.loc.tools.StateMachine;

public class CombatExecute implements IState {

    private final StateMachine stateMachine;
    private final Deque<IAction> actions;

    public CombatExecute(StateMachine stateMachine, Deque<IAction> actions) {
        this.stateMachine = stateMachine;
        this.actions = actions;
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void onEnter(Object... objects) {
        //die action die ausgeführt wurde
        IAction action = (IAction) objects[0];

        //holt sich die entscheidung, bei null ist es keine entscheidung sondern eine combat action
        List<Pair<Skill.Type, ArrayList<Entity>>> decisions = action.getDecisions();

        //wendet die action an
        action.resolve();

        if ( decisions != null ) {
            for ( int i = decisions.size() - 1; i >= 0; --i ) {
                this.actions.addFirst(new Skill(decisions.get(i).getLeft(), action.getOwner(), decisions.get(i).getRight()));
            }
            this.actions.addLast(action);
        }

        //wechselt den state wieder auf tick
        this.stateMachine.change(CombatState.TICK_STATE);
    }

    @Override
    public void onExit() {

    }
}
