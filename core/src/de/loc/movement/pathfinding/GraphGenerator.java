package de.loc.movement.pathfinding;

import com.badlogic.gdx.utils.Array;

import de.loc.core.GameGrid;
import de.loc.core.LevelManager;
import de.loc.tools.Position;

public final class GraphGenerator {

    private GraphGenerator() {
    }

    public static GraphImp generateGraph(GameGrid grid) {
        Array<Node> nodes = new Array<>();

        Position gridSize = LevelManager.getInstance().getLevel().getGameGrid().getSize();

        for ( int y = 0; y < gridSize.y; ++y ) {
            for ( int x = 0; x < gridSize.x; ++x ) {
                Node node = new Node(GameGrid.Type.WALKABLE);
                nodes.add(node);
            }
        }

        for ( int y = 0; y < gridSize.y; ++y ) {
            for ( int x = 0; x < gridSize.x; ++x ) {
                Node targetNode = nodes.get((gridSize.x * y) + x);
                //Bei OCCUPIED hat der node nur Verbindungen nach außen
                if ( grid.isWalkable(x, y) || grid.isOccupied(x, y) ) { //target
                    if ( grid.isWalkable(x - 1, y - 1) ) { //upLeft
                        Node upLeftNode = nodes.get((gridSize.x * (y - 1)) + (x - 1));
                        targetNode.createConnection(upLeftNode, GameGrid.Type.WALKABLE);
                    }
                    if ( grid.isWalkable(x, y - 1) ) { //up
                        Node upNode = nodes.get((gridSize.x * (y - 1)) + x);
                        targetNode.createConnection(upNode, GameGrid.Type.WALKABLE);
                    }
                    if ( grid.isWalkable(x + 1, y - 1) ) { //upRight
                        Node upRightNode = nodes.get((gridSize.x * (y - 1)) + (x + 1));
                        targetNode.createConnection(upRightNode, GameGrid.Type.WALKABLE);
                    }
                    if ( grid.isWalkable(x - 1, y) ) { //left
                        Node leftNode = nodes.get((gridSize.x * y) + (x - 1));
                        targetNode.createConnection(leftNode, GameGrid.Type.WALKABLE);
                    }
                    if ( grid.isWalkable(x + 1, y) ) { //right
                        Node rightNode = nodes.get((gridSize.x * y) + (x + 1));
                        targetNode.createConnection(rightNode, GameGrid.Type.WALKABLE);
                    }
                    if ( grid.isWalkable(x - 1, y + 1) ) { //downLeft
                        Node downLeftNode = nodes.get((gridSize.x * (y + 1)) + (x - 1));
                        targetNode.createConnection(downLeftNode, GameGrid.Type.WALKABLE);
                    }
                    if ( grid.isWalkable(x, y + 1) ) { //down
                        Node downNode = nodes.get((gridSize.x * (y + 1)) + x);
                        targetNode.createConnection(downNode, GameGrid.Type.WALKABLE);
                    }
                    if ( grid.isWalkable(x + 1, y + 1) ) { //downRight
                        Node downRightNode = nodes.get((gridSize.x * (y + 1)) + (x + 1));
                        targetNode.createConnection(downRightNode, GameGrid.Type.WALKABLE);
                    }
                }
            }
        }

        return new GraphImp(nodes);
    }
}
