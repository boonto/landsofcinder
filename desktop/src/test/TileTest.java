package test;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.loc.editor.EntityFactory;
import de.loc.main.LandsOfCinder;
import de.loc.tools.Constants;
import de.loc.tools.ListItem;
import de.loc.tools.Position;
import de.loc.tools.XmlHelper;
import de.loc.core.*;

import org.junit.Test;

import java.util.HashMap;
import java.util.logging.Level;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class TileTest implements Runnable {
    private LevelManager levelManager;
    private HashMap<String, ListItem> tileList;
    private Game game;

    public static void main(String[] args) {
        new TileTest();
    }

    public TileTest() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Lands of Cinder";

        config.width = 800;
        config.height = 600;
        config.fullscreen = false;
        config.vSyncEnabled = false;

        game = new TestGame(this);
        new LwjglApplication(game, config);

        game.dispose();
        game = null;
    }

    public void testParsedTile() throws Exception {
        Entity entity = EntityFactory.createEntity(tileList.get("River_Bridge_Tile").xmlElement, new PositionComponent(new Position(0, 0)), 0.f);

        TileComponent t = entity.getComponent(TileComponent.class);

        GameGrid grid = new GameGrid(3, 3);
        for ( int x = 0; x < grid.getSize().x; x++ ) {
            for ( int y = 0; y < grid.getSize().y; y++ ) {
                grid.setType(x, y, GameGrid.Type.EMPTY);
            }
        }

        boolean tilePlaceAble = grid.isTilePlaceable(t, 0, 0);
        assertEquals(tilePlaceAble, true);

        grid.addTile(t, 0, 0);

        byte gridtype = grid.getType(0, 0);
        assertEquals(gridtype, GameGrid.Type.WALKABLE);
        byte gridtype2 = grid.getType(0, 1);
        assertEquals(gridtype2, GameGrid.Type.WALKABLE);
        if ( gridtype2 != GameGrid.Type.WALKABLE ) {
            throw new Exception("Field value not correct!");
        }
        byte gridtype3 = grid.getType(1, 0);
        assertEquals(gridtype3, GameGrid.Type.OBSTRUCTED);
        if ( gridtype3 != GameGrid.Type.OBSTRUCTED ) {
            throw new Exception("Field value not correct!");
        }
        byte gridtype4 = grid.getType(1, 1);
        assertEquals(gridtype4, GameGrid.Type.OBSTRUCTED);
        byte gridtype5 = grid.getType(2, 1);
        assertEquals(gridtype5, GameGrid.Type.EMPTY);
        byte gridtype6 = grid.getType(0, 2);
        assertEquals(gridtype6, GameGrid.Type.EMPTY);
        if ( gridtype6 != GameGrid.Type.EMPTY ) {
            throw new Exception("HEIGHT value not correct!");
        }

        int height = grid.getHeight(0, 0);
        int height2 = grid.getHeight(0, 2);
        if ( height != 10 ) {
            throw new Exception("HEIGHT value not correct!");
        }
        if ( height2 != 0 ) {
            throw new Exception("HEIGHT value not correct!");
        }
    }

    public void testTile() {
        byte tileMap[][] = new byte[2][2];
        byte heightMap[][] = new byte[2][2];
        for ( int x = 0; x < tileMap.length; x++ ) {
            for ( int y = 0; y < tileMap[0].length; y++ ) {
                tileMap[x][y] = GameGrid.Type.OBSTRUCTED;
            }
        }
        for ( int x = 0; x < heightMap.length; x++ ) {
            for ( int y = 0; y < heightMap[0].length; y++ ) {
                heightMap[x][y] = 0x0A;
            }
        }
        tileMap[0][0] = GameGrid.Type.WALKABLE;
        tileMap[0][1] = GameGrid.Type.WALKABLE;

        Entity entity = new Entity();
        entity.add(new TileComponent(tileMap, heightMap));
        TileComponent t = entity.getComponent(TileComponent.class);

        GameGrid grid = new GameGrid(3, 3);
        for ( int x = 0; x < grid.getSize().x; x++ ) {
            for ( int y = 0; y < grid.getSize().y; y++ ) {
                grid.setType(x, y, GameGrid.Type.EMPTY);
            }
        }

        boolean tilePlaceAble = grid.isTilePlaceable(t, 0, 0);
        assertEquals(tilePlaceAble, true);

        grid.addTile(t, 0, 0);

        byte gridtype = grid.getType(0, 0);
        assertEquals(gridtype, GameGrid.Type.WALKABLE);
        byte gridtype2 = grid.getType(0, 1);
        assertEquals(gridtype2, GameGrid.Type.WALKABLE);
        byte gridtype3 = grid.getType(1, 0);
        assertEquals(gridtype3, GameGrid.Type.OBSTRUCTED);
        byte gridtype4 = grid.getType(1, 1);
        assertEquals(gridtype4, GameGrid.Type.OBSTRUCTED);
        byte gridtype5 = grid.getType(2, 1);
        assertEquals(gridtype5, GameGrid.Type.EMPTY);
        byte gridtype6 = grid.getType(0, 2);
        assertEquals(gridtype6, GameGrid.Type.EMPTY);

        int height = grid.getHeight(0, 0);
        int height2 = grid.getHeight(0, 2);
        assertEquals(height, 10);
        assertEquals(height2, 0);
        //PositionComponent p = entity.getComponent(PositionComponent.class);
        //Position size = p.size;
        //GameGrid grid = levelManager.getLevel().getGameGrid();

        //set All Fields to not walkable (obstructed)
        //grid.setToObstructed(new Position(0,0), grid.getSize(),0);

    }

    public void testTileSize() {
        byte tileMap[][] = new byte[3][2];
        byte heightMap[][] = new byte[3][2];
        for ( int x = 0; x < tileMap.length; x++ ) {
            for ( int y = 0; y < tileMap[0].length; y++ ) {
                tileMap[x][y] = GameGrid.Type.OBSTRUCTED;
            }
        }
        for ( int x = 0; x < heightMap.length; x++ ) {
            for ( int y = 0; y < heightMap[0].length; y++ ) {
                heightMap[x][y] = 0x0A;
            }
        }

        heightMap[2][1] = 0x00;
        tileMap[2][1] = GameGrid.Type.WALKABLE;

        TileComponent t = new TileComponent(tileMap, heightMap);

        int sizeX = t.getSizeX();
        int sizeY = t.getSizeY();
        assertEquals(sizeX, 3);
        assertEquals(sizeY, 2);

        int height = t.heightMap[0][0];
        assertEquals(height, 10);

        int type = t.tileMap[0][0];
        assertEquals(type, GameGrid.Type.OBSTRUCTED);

    }

    @Override
    public void run() {
        tileList = XmlHelper.getHashMapXmlList(Constants.TILE_LIST_PATH);
        testTile();
        testTileSize();
        try {
            testParsedTile();
        } catch ( Exception e ) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
