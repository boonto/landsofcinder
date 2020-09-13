package de.loc.movement.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;

import de.loc.core.LevelManager;

public class HeuristicImp implements Heuristic<Node> {

    @Override
    public float estimate(Node node, Node endNode) {
        int startIndex = node.getIndex();
        int endIndex = endNode.getIndex();

        int gridSizeX = LevelManager.getInstance().getLevel().getGameGrid().getSize().x;

        float startX = (float) (startIndex % gridSizeX);
        float startY = (float) (startIndex / gridSizeX);

        float endX = (float) (endIndex % gridSizeX);
        float endY = (float) (endIndex / gridSizeX);

        float dX = Math.abs(startX - endX);
        float dY = Math.abs(startY - endY);

        float costHorVer = 1.0f;
        float costDiag = 1.414f;

        float distance = (costHorVer * (dX + dY)) + ((costDiag - (2.0f * costHorVer)) * Math.min(dX, dY));

        //tiebreaker
        distance *= (1.0f + (1.0f / 1000.0f));

        return distance;
    }
}