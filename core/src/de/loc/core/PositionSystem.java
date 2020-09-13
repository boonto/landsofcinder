package de.loc.core;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import de.loc.game.GameScreen;
import de.loc.game.LevelChangeComponent;
import de.loc.graphics.RotationComponent;
import de.loc.input.ClickableComponent;
import de.loc.tools.Constants;

public class PositionSystem extends LandsOfCinderSystem {

    //TODO Weiß noch nicht wie man das schöner macht; auch Events evtl?
    private final GameScreen gameScreen;

    private final ComponentMapper<PositionComponent> positionMapper;
    private final ComponentMapper<RotationComponent> rotationMapper;
    private final ComponentMapper<LevelChangeComponent> levelChangeMapper;
    private final ComponentMapper<ClickableComponent> clickableMapper;

    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> levelChangeEntities;

    public PositionSystem(GameScreen gameScreen) {
        this.gameScreen = gameScreen;

        this.positionMapper = ComponentMapper.getFor(PositionComponent.class);
        this.rotationMapper = ComponentMapper.getFor(RotationComponent.class);
        this.levelChangeMapper = ComponentMapper.getFor(LevelChangeComponent.class);
        this.clickableMapper = ComponentMapper.getFor(ClickableComponent.class);
    }

    public void addedToEngine(Engine engine) {
        this.entities =
            engine.getEntitiesFor(Family.all(PositionComponent.class, RotationComponent.class).exclude(LevelChangeComponent.class, TileComponent.class).get());
        this.levelChangeEntities = engine.getEntitiesFor(Family.all(ClickableComponent.class, LevelChangeComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {

        GameGrid gameGrid = LevelManager.getInstance().getLevel().getGameGrid();
        gameGrid.softResetGrid();

        for ( Entity entity : this.entities ) {
            PositionComponent positionComponent = this.positionMapper.get(entity);

            RotationComponent rotationComponent = this.rotationMapper.get(entity);

            if ( !gameGrid.isObstructed(positionComponent.position) ) {
                gameGrid.setToOccupied(positionComponent.position, positionComponent.size, rotationComponent.angle);
            }
        }

        for ( Entity entity : this.levelChangeEntities ) {

            ClickableComponent c = this.clickableMapper.get(entity);
            if ( c.clicked ) {
                LevelChangeComponent levelChangeComponent = this.levelChangeMapper.get(entity);
                if ( !levelChangeComponent.levelPath.equals("null") ) {
                    this.gameScreen.saveCurrentLevelState();
                    this.gameScreen.scheduleLoad(Constants.PACKAGE_FOLDER
                                                 + LevelManager.getInstance().getCurrentPackage()
                                                 + "/"
                                                 + Constants.LEVELS_PATH
                                                 + levelChangeComponent.levelPath);
                }
            }
        }
    }

    @Override
    public void reset() {

    }
}
