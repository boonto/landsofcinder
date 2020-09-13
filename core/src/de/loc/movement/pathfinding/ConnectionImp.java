package de.loc.movement.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;

public class ConnectionImp implements Connection<Node> {

    private final Node toNode;
    private final Node fromNode;
    private final float cost;

    public ConnectionImp(Node fromNode, Node toNode, float cost) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.cost = cost;
    }

    @Override
    public float getCost() {
        return this.cost;
    }

    @Override
    public Node getFromNode() {
        return this.fromNode;
    }

    @Override
    public Node getToNode() {
        return this.toNode;
    }
}