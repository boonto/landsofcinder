package de.loc.combat;

import java.util.Deque;

import de.loc.tools.IAction;
import de.loc.tools.IState;
import de.loc.tools.StateMachine;

public class CombatTick implements IState {

    //TODO timer rausnehmen?
    private float timer;
    private final StateMachine stateMachine;
    private final Deque<IAction> actions;

    public CombatTick(StateMachine stateMachine, Deque<IAction> actions) {
        this.stateMachine = stateMachine;
        this.actions = actions;
    }

    @Override
    public void update(float deltaTime) {
        //updated alle actions
        for ( IAction a : this.actions ) {
            a.update(deltaTime);
        }

        //timer zum anzeigen wer dran ist in der konsole
        this.timer += deltaTime;
        if ( this.timer > 1.5f ) {
            this.timer = 0.0f;
            //checkt ob die oberste action packageListReady ist, wenn ja wechseln in den execute state
            if ( this.actions.peek().isReady() ) {
                this.stateMachine.change(CombatState.EXECUTE_STATE, this.actions.poll());
            }
        }
    }

    @Override
    public void onEnter(Object... objects) {

    }

    @Override
    public void onExit() {

    }
}
