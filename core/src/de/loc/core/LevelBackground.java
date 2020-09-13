package de.loc.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import de.loc.graphics.BackgroundComponent;
import de.loc.graphics.GridComponent;
import de.loc.graphics.ModelComponent;
import de.loc.graphics.ModelComponentCreator;
import de.loc.graphics.RenderableComponent;
import de.loc.movement.pathfinding.GraphGenerator;
import de.loc.movement.pathfinding.GraphImp;
import de.loc.tools.Constants;
import de.loc.tools.Position;

public class LevelBackground implements Disposable {

    private Engine engine;

    private static GameGrid gameGrid = new GameGrid(Constants.STANDARD_GRID_SIZE.x, Constants.STANDARD_GRID_SIZE.y);
    private static GraphImp graph;
    private static Position startPosition = new Position();
    private static String backgroundPath;
    private static float backgroundWidth;
    private static Vector2 backgroundSize = new Vector2();

    private static Entity background;
    private static Entity grid;
    private static Entity gridField;

    private static boolean drawGrid = false;

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void scaleGameGrid(int x, int y, boolean isFirstTime) {
        if ( !isFirstTime ) {
            this.engine.removeEntity(grid);
            grid.getComponent(ModelComponent.class).model.model.dispose();
        }
        //Neuberechnung des Grid & Graphen
        gameGrid.setSize(x, y);
        this.generateGraph();

        //Grid
        grid = new Entity();
        grid.add(new RenderableComponent());
        grid.add(new BackgroundComponent());

        grid.add(ModelComponentCreator.getInstance().createGrid(gameGrid, gameGrid.getSize()));

        this.engine.addEntity(grid);
    }

    public void generateGraph() {
        graph = GraphGenerator.generateGraph(gameGrid);
    }

    public void updateBackground(String imagePath) {
        background = new Entity();
        background.add(new RenderableComponent());
        background.add(new BackgroundComponent());
        background.add(ModelComponentCreator.getInstance().createBackground(imagePath, backgroundWidth));
        this.engine.addEntity(background);
    }

    public void toggleGameGrid() {
        if ( drawGrid ) {
            grid.remove(RenderableComponent.class);
            gridField.remove(RenderableComponent.class);
        } else {
            grid.add(new RenderableComponent());
            gridField.add(new RenderableComponent());
        }
        drawGrid = !drawGrid;
    }

    public void updateGameGrid(boolean isFirstTime) {
        if ( !isFirstTime ) {
            this.engine.removeEntity(gridField);
            // dispose the old model - important!
            gridField.getComponent(ModelComponent.class).model.model.dispose();
        }

        gridField = new Entity();
        gridField.add(new RenderableComponent());
        gridField.add(new BackgroundComponent());

        gridField.add(ModelComponentCreator.getInstance().updatedGameGrid(gameGrid));

        gridField.add(new GridComponent());
        this.engine.addEntity(gridField);
    }

    public Vector3 getWorldFromPos(Position pos) {
        return new Vector3(
            (-((float) gameGrid.getSize().x * Constants.GRID_WIDTH_HALF) + ((float) pos.x * Constants.GRID_WIDTH)),
            0.0f,
            (-((float) gameGrid.getSize().y * Constants.GRID_WIDTH_HALF) + ((float) pos.y * Constants.GRID_WIDTH)));
    }

    public Vector3 getWorldFromPosF(Vector2 pos) {
        return new Vector3(
            (-((float) gameGrid.getSize().x * Constants.GRID_WIDTH_HALF) + (pos.x * Constants.GRID_WIDTH)),
            0.0f,
            (-((float) gameGrid.getSize().y * Constants.GRID_WIDTH_HALF) + (pos.y * Constants.GRID_WIDTH)));
    }

    public Entity getBackgroundEntity() {
        return background;
    }

    public Entity getGridEntity() {
        return grid;
    }

    public Entity getGridFieldEntity() {
        return gridField;
    }

    public void addEntities() {

        // Background
        background = new Entity();
        background.add(new RenderableComponent());
        background.add(new BackgroundComponent());
        background.add(ModelComponentCreator.getInstance().createBackground(backgroundPath, backgroundWidth));
        this.engine.addEntity(background);

        //Grid
        grid = new Entity();
        grid.add(new BackgroundComponent());

        grid.add(ModelComponentCreator.getInstance().createGrid(gameGrid, gameGrid.getSize()));

        this.engine.addEntity(grid);

        //GridField
        gridField = new Entity();
        gridField.add(new BackgroundComponent());

        gridField.add(ModelComponentCreator.getInstance().updatedGameGrid(gameGrid));

        gridField.add(new GridComponent());
        this.engine.addEntity(gridField);

        //addTestObject();
    }

    public void addTestObject() {
        Entity imageObjectTest = new Entity();
        imageObjectTest.add(ModelComponentCreator.getInstance().createModelComponent("3D/Models/test_scene/Scene2.g3db"));
        imageObjectTest.add(new BackgroundComponent());
        imageObjectTest.add(new RenderableComponent());
        this.engine.addEntity(imageObjectTest);
    }

    public GameGrid getGameGrid() {
        return gameGrid;
    }

    public void setGameGrid(GameGrid gameGrid) {
        LevelBackground.gameGrid = gameGrid;
    }

    public GraphImp getGraph() {
        return graph;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Position startPosition) {
        LevelBackground.startPosition = startPosition;
    }

    public String getBackgroundPath() {
        return backgroundPath;
    }

    public void setBackgroundPath(String backgroundPath) {
        LevelBackground.backgroundPath = backgroundPath;
    }

    public float getBackgroundWidth() {
        return backgroundWidth;
    }

    public void setBackgroundWidth(float backgroundWidth) {
        LevelBackground.backgroundWidth = backgroundWidth;
    }

    public void scaleBackground(float backgroundWidth) {
        LevelBackground.backgroundWidth = backgroundWidth;

        this.engine.removeEntity(background);
        background.getComponent(ModelComponent.class).model.model.dispose();

        background = new Entity();
        background.add(new RenderableComponent());
        background.add(new BackgroundComponent());
        background.add(ModelComponentCreator.getInstance().createBackground(backgroundPath, backgroundWidth));
        this.engine.addEntity(background);
    }

    public Vector2 getBackgroundSize() {
        return backgroundSize;
    }

    public void setBackgroundSize(Vector2 backgroundSize) {
        LevelBackground.backgroundSize = backgroundSize;
    }

    public boolean isDrawGrid() {
        return drawGrid;
    }

    public void setDrawGrid(boolean drawGrid) {

        if ( drawGrid ) {
            grid.add(new RenderableComponent());
            gridField.add(new RenderableComponent());
        } else {
            grid.remove(RenderableComponent.class);
            gridField.remove(RenderableComponent.class);
        }
        LevelBackground.drawGrid = drawGrid;
    }

    @Override
    public void dispose() {
        this.engine = null;
        gameGrid = null;
        graph = null;
        startPosition = null;
        backgroundPath = null;
        backgroundSize = null;

        background = null;
        grid = null;
        gridField = null;
    }
}