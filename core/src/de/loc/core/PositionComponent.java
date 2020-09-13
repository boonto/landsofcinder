package de.loc.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import de.loc.tools.Position;

public class PositionComponent implements Component {

    public Position position;
    public Vector2 positionF;
    public Position size;

    public Matrix4 afterPositionTransform;

    public PositionComponent(Position position) {
        this(position.x, position.y);
    }

    public PositionComponent(int x, int y) {
        this(x, y, new Position(1, 1));
    }

    public PositionComponent(int x, int y, Position size) {
        this.position = new Position(x, y);
        this.positionF = new Vector2((float) x, (float) y);
        this.size = size;
        this.afterPositionTransform = new Matrix4();
    }

    public void setPosition(int x, int y) {
        this.position.x = x;
        this.position.y = y;
        this.positionF.x = (float) x;
        this.positionF.y = (float) y;
    }
}
