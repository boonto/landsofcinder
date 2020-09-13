package de.loc.input;

import com.badlogic.gdx.math.collision.Ray;

import de.loc.tools.Position;

public interface IInput {

    void clicked(Ray pickRay, Position position);

    void dragged(Position position);

    void rightClick();
}
