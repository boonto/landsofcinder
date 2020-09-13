package de.loc.movement.pathfinding;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class GraphPathImp implements GraphPath<Node> {
    private final Array<Node> nodes;

    public GraphPathImp() {
        this.nodes = new Array<>();
    }

    @Override
    public Iterator<Node> iterator() {
        return this.nodes.iterator();
    }

    @Override
    public int getCount() {
        return this.nodes.size;
    }

    @Override
    public Node get(int index) {
        return this.nodes.get(index);
    }

    public Node removeIndex(int index) {
        return this.nodes.removeIndex(index);
    }

    @Override
    public void add(Node node) {
        this.nodes.add(node);
    }

    @Override
    public void clear() {
        this.nodes.clear();
    }

    @Override
    public void reverse() {
        this.nodes.reverse();
    }
}