package de.loc.game;

import com.badlogic.ashley.core.Component;

// wird im PositionSystem benutzt!
public class LevelChangeComponent implements Component {
    public String levelPath;
    public String packagePath;

    public LevelChangeComponent(String levelPath, String packagePath) {
        this.levelPath = levelPath;
        this.packagePath = packagePath;
    }
}
