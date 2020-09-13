package de.loc.movement.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

public class Node {
    private final Array<Connection<Node>> connections;
    private final byte type;
    private int index;

    public Node(byte type) {
        this.connections = new Array<>();
        this.type = type;
        this.index = 0;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public byte getType() {
        return this.type;
    }

    public Array<Connection<Node>> getConnections() {
        return this.connections;
    }

    public void createConnection(Node toNode, byte cost) {
        this.connections.add(new ConnectionImp(this, toNode, cost));
    }
}
