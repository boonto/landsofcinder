package de.loc.movement.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;

import de.loc.core.LevelManager;
import de.loc.tools.DimensionHelper;
import de.loc.tools.Position;

public class GraphImp implements IndexedGraph<Node> {

    private final Array<Node> nodes;

    public GraphImp() {
        this(new Array<Node>());
    }

    public GraphImp(Array<Node> nodes) {
        this.nodes = nodes;

        for ( Node node : nodes ) {
            node.setIndex(nodes.indexOf(node, true));
        }
    }

    @Override
    public int getIndex(Node node) {
        return this.nodes.indexOf(node, true);
    }

    @Override
    public int getNodeCount() {
        return this.nodes.size;
    }

    @Override
    public Array<Connection<Node>> getConnections(Node fromNode) {
        return this.nodes.get(this.getIndex(fromNode)).getConnections();
    }

    public Node getNodeByPosition(Position position) {
        return this.nodes.get(DimensionHelper.sub2ind(position, LevelManager.getInstance().getLevel().getGameGrid().getSize().x));
    }
}
